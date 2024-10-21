package com.pasco.pascocustomer.Driver.NotesRemainders.Ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.NotesRemainders.ViewModel.NotesRViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.databinding.ActivityNotesRemainderBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@AndroidEntryPoint
class NotesRemainderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesRemainderBinding
    private lateinit var activity: Activity
    private val notesRViewModel: NotesRViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var formattedDate=""
    private var formattedDateString=""
    private var formattedTime=""
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesRemainderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backArrowAddNotes.setOnClickListener {
            finish()
        }
        activity = this

        binding.startDateTxtNotes.setOnClickListener {
            val calendar = Calendar.getInstance()

            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@NotesRemainderActivity, R.style.MyTimePicker,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val formattedMonth = String.format("%02d", monthOfYear + 1)
                    val formatDay = String.format("%02d", dayOfMonth)
                    formattedDate = "$year-$formattedMonth-$formatDay"

                    val originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val desiredFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

                    // Parse the original date string to a LocalDate
                    val date = LocalDate.parse(formattedDate, originalFormatter)

                    // Format the LocalDate to the desired string format
                   formattedDateString = date.format(desiredFormatter)
                    binding.startDateTxtNotes.text = formattedDate
                },
                year, month, day
            )

            datePickerDialog.show()
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }

        binding.startTimetxtNotes.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog

            mTimePicker = TimePickerDialog(
                this@NotesRemainderActivity, R.style.MyTimePicker,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    val formattedHour = String.format("%02d", selectedHour)
                    val formattedMinutes = String.format("%02d", selectedMinute)
                    val formattedTime = "$formattedHour:$formattedMinutes"
                    binding.startTimetxtNotes.text = formattedTime
                },
                hour, minute, true
            )
            mTimePicker.show()
        }

        binding.saveBtnAddNotes.setOnClickListener {
            //call validation
            validation()
        }
        //call Observer
        notesReminderObserver()
    }

    private fun notesReminderObserver() {
        notesRViewModel.progressIndicator.observe(this, Observer {
        })
        notesRViewModel.mGetNotesResponse.observe(this) { response ->
            val message = response.peekContent().msg
            val success = response.peekContent().status
            if (success == "False") {
                // Handle unsuccessful login
                Toast.makeText(this@NotesRemainderActivity, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@NotesRemainderActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        notesRViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this@NotesRemainderActivity, it)
        }
    }

    private fun validation() {
       if (binding.startDateTxtNotes.text.toString().isNullOrBlank())
       {
           Toast.makeText(this@NotesRemainderActivity, "Please select date", Toast.LENGTH_SHORT).show()
       }
        else if (binding.startTimetxtNotes.text.toString().isNullOrBlank())
       {
            Toast.makeText(this@NotesRemainderActivity, "Please select time", Toast.LENGTH_SHORT).show()
       }
        else if (binding.addSubjectEdittext.text.toString().isNullOrBlank())
       {
           Toast.makeText(this@NotesRemainderActivity, "Please add the subject", Toast.LENGTH_SHORT).show()
       }
        else if (binding.commentAddNotesReminder.text.isNullOrBlank())
       {
           Toast.makeText(this@NotesRemainderActivity, "Please add the description", Toast.LENGTH_SHORT).show()
       }
        else
       {
          //call api
           notesReminderApi()
       }
    }

    private fun notesReminderApi() {
        val title = binding.addSubjectEdittext.text.toString()
        val desp = binding.commentAddNotesReminder.text.toString()
        val reminderDate = "${formattedDateString}${formattedTime}"
        notesRViewModel.getNotesReminderData(
            progressDialog,
            activity,
            title,
            desp,
            reminderDate
        )
    }

}