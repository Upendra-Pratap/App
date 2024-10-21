package com.pasco.pascocustomer.Driver

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityContactWithUsBinding
@AndroidEntryPoint
class ContactWithUsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityContactWithUsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactWithUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backImageCwithUs.setOnClickListener {
            finish()
        }

    }
}