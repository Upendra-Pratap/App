package com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.ContactWithUsActivity
import com.pasco.pascocustomer.Driver.DriverWallet.DriverWalletActivity
import com.pasco.pascocustomer.Driver.NotesRemainders.Ui.NotesRemainderActivity
import com.pasco.pascocustomer.Driver.adapter.TermsAndConditionsActivity
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.customer.activity.updatevehdetails.UpdateVehicleDetailsActivity
import com.pasco.pascocustomer.databinding.FragmentDriverMoreBinding
import com.pasco.pascocustomer.userFragment.logoutmodel.LogOutModelView
import com.pasco.pascocustomer.userFragment.logoutmodel.LogoutBody
import com.pasco.pascocustomer.utils.ErrorUtil


@AndroidEntryPoint
class DriverMoreFragment : Fragment() {
    private lateinit var binding: FragmentDriverMoreBinding
    private val logoutViewModel: LogOutModelView by viewModels()
    private var dAdminApprovedId: String? = ""
    private val progressDialog by lazy { CustomProgressDialog(requireContext()) }
    private var refersh = ""

//hello
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDriverMoreBinding.inflate(inflater, container, false)

        refersh = PascoApp.encryptedPrefs.token
        dAdminApprovedId = PascoApp.encryptedPrefs.driverApprovedId

        if (dAdminApprovedId == "0") {
            disableAllExceptFour()
            openPopUp()
        } else if (dAdminApprovedId == "1") {
            enableAll()
        }

        binding.consUpdateVehDetails.setOnClickListener {
            val intent = Intent(requireActivity(), UpdateVehicleDetailsActivity::class.java)
            startActivity(intent)
        }
        binding.consContactAndSupportInside.setOnClickListener {
            val intent = Intent(requireContext(), ContactWithUsActivity::class.java)
            startActivity(intent)
        }
        binding.consMyWalletVehDetails.setOnClickListener {
            val intent = Intent(requireContext(), DriverWalletActivity::class.java)
            startActivity(intent)
        }
        binding.consTermsCondInside.setOnClickListener {
            val intent = Intent(requireContext(), TermsAndConditionsActivity::class.java)
            startActivity(intent)
        }

        binding.consPrivacyPolicyInside.setOnClickListener {
            val intent = Intent(requireContext(), TermsAndConditionsActivity::class.java)
            startActivity(intent)
        }
        binding.consNotesReminderDri.setOnClickListener {
            val intent = Intent(requireContext(), NotesRemainderActivity::class.java)
            startActivity(intent)
        }


        binding.consLogout.setOnClickListener {
            openLogoutPop()
            //logout observer
            logOutObserver()
        }

        return binding.root
    }

    private fun enableAll() {
        binding.consContactAndSupportInside.isEnabled = true
        binding.consMyWalletVehDetails.isEnabled = true
        binding.consTermsCondInside.isEnabled = true
        binding.consPrivacyPolicyInside.isEnabled = true
    }

    private fun openPopUp() {
        val builder =
            AlertDialog.Builder(requireContext(), R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.admin_approval_status, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val okButtonAdminA = dialogView.findViewById<TextView>(R.id.okButtonAdminA)
        dialog.show()
        okButtonAdminA.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun disableAllExceptFour() {
        binding.consNotesReminderDri.isEnabled = false
        binding.consMyWalletVehDetails.isEnabled = false
        binding.consNotesReminderDri.isEnabled = false
        binding.consUpdateVehDetails.isEnabled = false
    }

    @SuppressLint("MissingInflatedId")
    private fun openLogoutPop() {
        val builder = AlertDialog.Builder(
            requireContext(),
            R.style.Style_Dialog_Rounded_Corner
        )
        val dialogView = layoutInflater.inflate(R.layout.logout_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cancelLogBtn = dialogView.findViewById<TextView>(R.id.cancelLogBtn)
        val yesLogoutBtn = dialogView.findViewById<TextView>(R.id.yesLogoutBtn)
        dialog.show()
        cancelLogBtn.setOnClickListener {
            dialog.dismiss()
        }
        yesLogoutBtn.setOnClickListener {
            logOutApi()
        }
    }

    private fun logOutApi() {
        val bookingBody = LogoutBody(
            refresh = refersh
        )
        logoutViewModel.otpCheck(bookingBody,requireActivity())
    }

    private fun logOutObserver() {
        logoutViewModel.mRejectResponse.observe(requireActivity()) { response ->
            val message = response.peekContent().msg
            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()

            if (response.peekContent().status == "True")
            {
                PascoApp.encryptedPrefs.bearerToken = ""
                PascoApp.encryptedPrefs.userId = ""
                PascoApp.encryptedPrefs.driverApprovedId = ""
                PascoApp.encryptedPrefs.CheckedType=""
                PascoApp.encryptedPrefs.isFirstTime = true
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }



        }

        logoutViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }

}