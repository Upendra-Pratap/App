package com.pasco.pascocustomer.customer.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.databinding.ActivityChooseLanguageBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
@AndroidEntryPoint
class ChooseLanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLanguageBinding
    private val chooseLanguageList = ArrayList<String>()
    private val strLangList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChooseLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        chooseLanguageList.add("English")
        chooseLanguageList.add("Italian")

        //Spinner Adapter
        val dAdapter = spinnerAdapter(this, R.layout.custom_spinner_two, strLangList)
        dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dAdapter.add("Select  Language")
        dAdapter.addAll(chooseLanguageList)
        binding.englishLanguage.adapter = dAdapter

        binding.continueBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }


    //Add backButton Popup method
    class spinnerAdapter constructor(
        context: Context, textViewResourceId: Int, strInterestedList: List<String>
    ) : ArrayAdapter<String?>(context, textViewResourceId)

}