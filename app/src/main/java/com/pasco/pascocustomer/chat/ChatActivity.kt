package com.pasco.pascocustomer.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.chat.chatadapter.ChatAdapter
import com.pasco.pascocustomer.databinding.ActivityChatBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private lateinit var chatAdapter: ChatAdapter
    private val messageList: MutableList<Message> = mutableListOf()

    private val IMAGE_PICK_CODE = 1000
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = PascoApp.encryptedPrefs.userId
        database = FirebaseDatabase.getInstance().reference.child("messages")
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        chatAdapter = ChatAdapter(messageList,userId)
        binding.chatRecycler.layoutManager = LinearLayoutManager(this)
        binding.chatRecycler.adapter = chatAdapter

        binding.sendBtn.setOnClickListener { sendMessage() }
        binding.selectImg.setOnClickListener { pickImageFromGallery() }
        binding.placeRecord.setOnClickListener { startRecording() }

        listenForMessages()

    }

    private fun sendMessage() {
        val text = binding.messageInput.text.toString().trim()
        if (text.isNotEmpty()) {
            val message = Message(text = text, senderId = userId)
            database.push().setValue(message)
            binding.messageInput.text.clear()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun startRecording() {
        val storageDir: File = externalCacheDir!!
        try {
            audioFile = File.createTempFile("audio", ".3gp", storageDir)
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
            binding.placeRecord.text = "Stop"
            binding.placeRecord.setOnClickListener { stopRecording() }
        } catch (e: IOException) {
            Log.e("AudioRecording", "prepare() failed")
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        uploadAudio()
    }

    private fun uploadAudio() {
        val audioUri = Uri.fromFile(audioFile)
        val storageRef = storage.reference.child("audio/${UUID.randomUUID()}.3gp")
        storageRef.putFile(audioUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val message =
                        Message(voiceUrl = uri.toString(), senderId = userId)
                    database.push().setValue(message)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            val imageUri = data?.data ?: return
            val storageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val message =
                            Message(imageUrl = uri.toString(), senderId = userId)
                        database.push().setValue(message)
                    }
                }
        }
    }

    private fun listenForMessages() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messageList.add(it)
                    chatAdapter.notifyItemInserted(messageList.size - 1)
                    binding.chatRecycler.scrollToPosition(messageList.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}