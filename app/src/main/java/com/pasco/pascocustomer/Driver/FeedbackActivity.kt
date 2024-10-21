package com.pasco.pascocustomer.Driver

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityFeedbackBinding
@AndroidEntryPoint
class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowFeedback.setOnClickListener {
            finish()
        }
    }
}