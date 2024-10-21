package com.pasco.pascocustomer.Driver

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityCreateDriverAvailabilityBinding
import java.util.Calendar
@AndroidEntryPoint
class CreateDriverAvailabilityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateDriverAvailabilityBinding
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDriverAvailabilityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowAddAv.setOnClickListener {
            finish()
        }

        binding.startDateTxt.setOnClickListener {
            val calendar = Calendar.getInstance()

            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@CreateDriverAvailabilityActivity, R.style.MyTimePicker,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val formattedMonth = String.format("%02d", monthOfYear + 1)
                    val formatDay = String.format("%02d", dayOfMonth)
                    val date = "$year-$formattedMonth-$formatDay"

                    binding.startDateTxt.text = date
                },
                year, month, day
            )

            datePickerDialog.show()
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }

        binding.endDateTxt.setOnClickListener {
            val calendar = Calendar.getInstance()

            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@CreateDriverAvailabilityActivity, R.style.MyTimePicker,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val formattedMonth = String.format("%02d", monthOfYear + 1)
                    val formatDay = String.format("%02d", dayOfMonth)
                    val date = "$year-$formattedMonth-$formatDay"

                    binding.endDateTxt.text = date
                },
                year, month, day
            )

            datePickerDialog.show()
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }

        binding.startTimetxt.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog

            mTimePicker = TimePickerDialog(
                this@CreateDriverAvailabilityActivity, R.style.MyTimePicker,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    val formattedHour = String.format("%02d", selectedHour)
                    val formatMinutes = String.format("%02d", selectedMinute)

                    binding.startTimetxt.text = "$formattedHour:$formatMinutes"
                },
                hour, minute, true
            )
            mTimePicker.show()
        }
        binding.endTimetxt.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog

            mTimePicker = TimePickerDialog(
                this@CreateDriverAvailabilityActivity, R.style.MyTimePicker,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    val formattedHour = String.format("%02d", selectedHour)
                    val formatMinutes = String.format("%02d", selectedMinute)

                    binding.endTimetxt.text = "$formattedHour:$formatMinutes"
                },
                hour, minute, true
            )
            mTimePicker.show()
        }


    }
}
