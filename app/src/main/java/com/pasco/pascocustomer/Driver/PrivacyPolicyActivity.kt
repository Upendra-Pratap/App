package com.pasco.pascocustomer.activity.Driver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityPrivacyPolicyBinding
@AndroidEntryPoint
class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backImagePrivacy.setOnClickListener {
            finish()
        }
    }
}