package com.pasco.pascocustomer.userFragment.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.FragmentProfileBinding
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileModelView
import com.pasco.pascocustomer.userFragment.profile.updatemodel.UpdateProfileModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity: Activity

    private var selectedImageFile: File? = null
    private val CAMERA_PERMISSION_CODE = 101

    private val getProfileModelView: GetProfileModelView by viewModels()
    private val updateProfileModelView: UpdateProfileModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(activity) }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap

                if (imageBitmap != null) {
                    // Convert Bitmap to File
                    selectedImageFile = bitmapToFile(imageBitmap)
                    setImageOnImageView(selectedImageFile)
                    // Now you can use the 'imageFile' as needed
                    binding.profileImg.setImageBitmap(imageBitmap)
                } else {
                    Toast.makeText(requireActivity(), "Failed to capture image", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireActivity(), "Image capture cancelled", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
            )
        } else {
            // Permission already granted, proceed with camera operation
            selectImage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        activity = requireActivity()

        binding.editProfileBtn.setOnClickListener {
            requestCameraPermission()
        }

        binding.updateBtn.setOnClickListener { updateProfile() }

        getProfileApi()
        getProfileObserver()
        updateProfileObserver()
        return view
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Select Image")
        builder.setItems(options) { dialog, item ->

            when {
                options[item] == "Take Photo" -> openCamera()
                options[item] == "Choose from Gallery" -> openGallery()
                options[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    @SuppressLint("IntentReset")
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*" // Allow all image types
        selectImageLauncher.launch(galleryIntent)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            takePictureLauncher.launch(cameraIntent)
        }
    }

    private fun setImageOnImageView(imageFile: File?) {
        if (imageFile != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver, Uri.fromFile(imageFile)
            )
            binding.profileImg.setImageBitmap(bitmap)
        }
    }


    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val selectedImageUri = data.data
                    if (selectedImageUri != null) {
                        selectedImageFile = convertUriToFile(selectedImageUri)
                        if (selectedImageFile != null) {
                            // Now you have the image file in File format, you can use it as needed.
                            setImageOnImageView(selectedImageFile)
                        } else {
                            // Handle the case where conversion to File failed
                            showToast("Error converting URI to File")
                        }
                    } else {
                        // Handle the case where the URI is null
                        showToast("Selected image URI is null")
                    }
                } else {
                    // Handle the case where data is null
                    showToast("No data received")
                }
            } else {
                // Handle the case where the result code is not RESULT_OK
                showToast("Action canceled")
            }
        }

    private fun convertUriToFile(uri: Uri): File? {
        try {
            val inputStream: InputStream? = requireActivity().contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val fileName = getFileNameFromUri(uri)
                val outputFile = File(requireActivity().cacheDir, fileName)
                val outputStream = FileOutputStream(outputFile)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                return outputFile
            }
        } catch (e: Exception) {
            // Log the exception for debugging purposes
            Log.e("ConversionError", "Error converting URI to File: ${e.message}", e)
        }
        return null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }


    private fun getFileNameFromUri(uri: Uri): String {
        var result: String? = null
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    result = it.getString(displayNameIndex)
                }
            }
        }
        return result ?: "file"
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        // Create a file in the cache directory
        val file = File(requireActivity().cacheDir, "image.jpg")

        // Convert the bitmap to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bitmapData = byteArrayOutputStream.toByteArray()

        // Write the bytes into the file
        FileOutputStream(file).use { fileOutputStream ->
            fileOutputStream.write(bitmapData)
            fileOutputStream.flush()
        }

        return file
    }


    private fun getProfileApi() {
        getProfileModelView.getProfile(activity, progressDialog)
    }

    private fun getProfileObserver() {
        getProfileModelView.progressIndicator.observe(this) {
        }
        getProfileModelView.mRejectResponse.observe(this) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            val users = it.peekContent().data
            if (users?.fullName == null) {

            } else {
                binding.userName.text = Editable.Factory.getInstance().newEditable(users?.fullName)
            }

            if (users?.email == null) {

            } else {
                binding.emailTxtA.text = Editable.Factory.getInstance().newEditable(users?.email)
            }

            if (users?.currentCity == null) {

            } else {
                binding.currentCityTxt.text =
                    Editable.Factory.getInstance().newEditable(users?.currentCity)
            }


            if (users?.image == null) {

            } else {
                val url = it.peekContent().data?.image
                Glide.with(this).load(BuildConfig.IMAGE_KEY + url).into(binding.profileImg)
            }


        }

        getProfileModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
            //errorDialogs()
        }
    }

    private fun updateProfile() {


        val userName = binding.userName.text.toString().toRequestBody(MultipartBody.FORM)
        val email = binding.emailTxtA.text.toString().toRequestBody(MultipartBody.FORM)
        val currentCity = binding.currentCityTxt.text.toString().toRequestBody(MultipartBody.FORM)


        var profileImage: MultipartBody.Part? = null

        profileImage = if (selectedImageFile == null) {

            MultipartBody.Part.createFormData(
                "", selectedImageFile?.name, "".toRequestBody("*image/*".toMediaTypeOrNull())
            )

        } else {
            MultipartBody.Part.createFormData(
                "image",
                selectedImageFile?.name,
                selectedImageFile!!.asRequestBody("*image/*".toMediaTypeOrNull())
            )

        }



        updateProfileModelView.updateProfile(
            progressDialog,
            activity,
            userName,
            email,
            currentCity,
            profileImage

        )
    }

    private fun updateProfileObserver() {
        updateProfileModelView.progressIndicator.observe(this, androidx.lifecycle.Observer {
        })
        updateProfileModelView.mCustomerResponse.observe(
            this
        ) {

            var message = it.peekContent().msg!!
            PascoApp.encryptedPrefs.profileUpdate = it.peekContent().profile.toString()
            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
            getProfileApi()
        }

        updateProfileModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }
}