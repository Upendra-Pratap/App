package com.pasco.pascocustomer.Driver.DriverDashboard.Ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.transportapp.DriverApp.MarkDuty.MarkDutyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.DriverDashboard.ViewModel.MarkDutyBody
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.DriverOrdersFragment
import com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.DriverMoreFragment
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.Ui.HomeFragment
import com.pasco.pascocustomer.Driver.Fragment.DriverProfile.DriverProfileFragment
import com.pasco.pascocustomer.Driver.Fragment.TripHistoryFragment
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.LoginActivity
import com.pasco.pascocustomer.customer.activity.notificaion.NotificationActivity
import com.pasco.pascocustomer.customer.activity.notificaion.delete.NotificationBody
import com.pasco.pascocustomer.customer.activity.notificaion.notificationcount.NotificationCountViewModel
import com.pasco.pascocustomer.databinding.ActivityDriverDashboardBinding
import com.pasco.pascocustomer.userFragment.logoutmodel.LogOutModelView
import com.pasco.pascocustomer.userFragment.logoutmodel.LogoutBody
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class DriverDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverDashboardBinding
    private lateinit var naview: NavigationView
    private var city: String? = null
    private var address: String? = null
    private var dAdminApprovedId: String? = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler = Handler(Looper.getMainLooper())
    private var lastBackPressTime = 0L
    private val backPressInterval = 2000
    private var shouldLoadHomeFragOnBackPress = true
    private val markDutyViewModel: MarkDutyViewModel by viewModels()
    private val getProfileModelView: GetProfileModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(this) }
    private lateinit var activity: Activity
    private var driverId = ""
    private var navItemIndex = 1
    private var refersh = ""
    private val notificationCountViewModel: NotificationCountViewModel by viewModels()
    private var switchCheck = ""
    private var OnDutyStatus = ""
    private var driverDutyStatus = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.switchbtn.isChecked = false

        activity = this
        driverId = PascoApp.encryptedPrefs.userId
       driverDutyStatus =  PascoApp.encryptedPrefs.CheckedType
        dAdminApprovedId = PascoApp.encryptedPrefs.driverApprovedId
        refersh = PascoApp.encryptedPrefs.token
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()
        if (checkLocationPermission()) {
            requestLocationUpdates()
        } else {
            requestLocationPermission()
        }
        binding.firstConsLayouttt.visibility = View.VISIBLE
        getProfileApi()
        getUserProfileObserver()

        if (dAdminApprovedId == "0") {
            disableAllExceptMore()
            openPopUp()
        } else if (dAdminApprovedId == "1") {
            enableAll()
        }

        //Api and Observer
        getNotificationCountDApi()
        notificationCountDObserver()
        binding.notificationBtnDriver.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        if (driverDutyStatus=="1")
        {
            binding.switchbtn.isChecked = true
            switchCheck = "1"
            markOnDuty()
        }
        else
        {
            binding.switchbtn.isChecked = false
            switchCheck = "0"
            markOnDuty()
        }
        binding.switchbtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switchCheck = "1"
                markOnDuty()
            } else {
                switchCheck = "0"
                markOnDuty()
            }
        }
        markOnObserver()
        val homeFragment = HomeFragment()
        replace_fragment(homeFragment)

        binding.HomeFragmentDri.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            val homeFragment = HomeFragment()
            replace_fragment(homeFragment)
            getProfileApi()
            getUserProfileObserver()
            navItemIndex = 1
            binding.homeIconDri.setImageResource(R.drawable.home_1)
            binding.orderIconDri.setImageResource(R.drawable.order_icon)
            binding.moreIcon.setImageResource(R.drawable.more_icon)
            binding.tripHisIconDri.setImageResource(R.drawable.hostory_icon)
            binding.profileIconDri.setImageResource(R.drawable.profile)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.black))
        }

        binding.OrderFragmentDri.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            val driverOrdersFragment = DriverOrdersFragment()
            replace_fragment(driverOrdersFragment)
            navItemIndex = 2
            binding.homeIconDri.setImageResource(R.drawable.home_icon)
            binding.orderIconDri.setImageResource(R.drawable.order_1)
            binding.moreIcon.setImageResource(R.drawable.more_icon)
            binding.tripHisIconDri.setImageResource(R.drawable.hostory_icon)
            binding.profileIconDri.setImageResource(R.drawable.profile)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.black))
        }
        binding.LinearMoreIcon.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            val driverMoreFragment = DriverMoreFragment()
            replace_fragment(driverMoreFragment)
            navItemIndex = 3
            binding.homeIconDri.setImageResource(R.drawable.home_icon)
            binding.orderIconDri.setImageResource(R.drawable.order)
            binding.moreIcon.setImageResource(R.drawable.more_1)
            binding.tripHisIconDri.setImageResource(R.drawable.hostory_icon)
            binding.profileIconDri.setImageResource(R.drawable.profile)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.black))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))

        }

        binding.tripHistoryFragmentDri.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.VISIBLE
            val tripHistoryFragment = TripHistoryFragment()
            replace_fragment(tripHistoryFragment)
            navItemIndex = 4
            binding.homeIconDri.setImageResource(R.drawable.home_icon)
            binding.orderIconDri.setImageResource(R.drawable.order)
            binding.moreIcon.setImageResource(R.drawable.more)
            binding.tripHisIconDri.setImageResource(R.drawable.history_1)
            binding.profileIconDri.setImageResource(R.drawable.profile)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.black))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
        }

        binding.PrfileDfragment.setOnClickListener {
            binding.firstConsLayouttt.visibility = View.GONE
            val driverProfileFragment = DriverProfileFragment()
            replace_fragment(driverProfileFragment)
            navItemIndex = 5
            binding.homeIconDri.setImageResource(R.drawable.home_icon)
            binding.orderIconDri.setImageResource(R.drawable.order)
            binding.moreIcon.setImageResource(R.drawable.more)
            binding.tripHisIconDri.setImageResource(R.drawable.hostory_icon)
            binding.profileIconDri.setImageResource(R.drawable.profile_1)

            binding.profileTextDri.setTextColor(application.resources.getColor(R.color.black))
            binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.homeTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
            binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
        }


    }

    private fun enableAll() {
        binding.HomeFragmentDri.isEnabled = true
        binding.OrderFragmentDri.isEnabled = true
        binding.tripHistoryFragmentDri.isEnabled = true
        binding.PrfileDfragment.isEnabled = true
        binding.notificationBtnDriver.isEnabled = true
        binding.switchbtn.isEnabled = true
        binding.LinearMoreIcon.isEnabled = true
    }

    private fun disableAllExceptMore() {
        binding.HomeFragmentDri.isEnabled = false
        binding.OrderFragmentDri.isEnabled = false
        binding.tripHistoryFragmentDri.isEnabled = false
        binding.PrfileDfragment.isEnabled = false
        binding.notificationBtnDriver.isEnabled = false
        binding.switchbtn.isEnabled = false
        binding.LinearMoreIcon.isEnabled = true
    }

    private fun openPopUp() {
        val builder =
            AlertDialog.Builder(this, R.style.Style_Dialog_Rounded_Corner)
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

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@DriverDashboardActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@DriverDashboardActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@DriverDashboardActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@DriverDashboardActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationUpdates() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    showAddress(it)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun showAddress(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        GlobalScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(this@DriverDashboardActivity, Locale.getDefault())
            try {
                val addresses: List<Address> = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )!!
                if (addresses.isNotEmpty()) {
                    address = addresses[0].getAddressLine(0)
                    city = addresses[0].locality
                    city?.let { updateUI(it) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUI(city: String) {
        handler.post {
            binding.driverGreeting.text = "$city"
        }
    }

    private fun getProfileApi() {
        getProfileModelView.getProfile(
            activity,
            progressDialog

        )
    }

    private fun getUserProfileObserver() {
        getProfileModelView.progressIndicator.observe(this, Observer {
        })
        getProfileModelView.mRejectResponse.observe(this) { response ->
            val data = response.peekContent().data
            val baseUrl = "http://69.49.235.253:8090"
            val imagePath = data?.image.orEmpty()

            val imageUrl = "$baseUrl$imagePath"
            if (imageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(binding.userIconDashBoard)
            } else {
                binding.userIconDashBoard.setImageResource(R.drawable.ic_launcher_background)
            }

            Log.e("getDetails", "ObservergetUserProfile: ")
            val helloName = data?.fullName.toString()
            var hName = "Hello $helloName"
            binding.driverNameDash.text = hName


        }

        getProfileModelView.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this@DriverDashboardActivity, it)

        }
    }

    private fun markOnDuty() {
        val body = MarkDutyBody(
          mark_status = switchCheck)
        markDutyViewModel.putMarkOn(
            progressDialog,activity,body)
    }

    private fun markOnObserver() {
        markDutyViewModel.mmarkDutyResponse.observe(this) { response ->
            val message = response.peekContent().msg!!
            if (response.peekContent().status == "True") {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                OnDutyStatus = response.peekContent().status.toString()
                PascoApp.encryptedPrefs.CheckedType = OnDutyStatus
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        markDutyViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(this, it)
        }
    }
    private fun getNotificationCountDApi() {
        notificationCountViewModel.getCountNoti()
    }

    private fun notificationCountDObserver() {
        notificationCountViewModel.mNotiCountResponse.observe(this) {
        }
        notificationCountViewModel.mNotiCountResponse.observe(this) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            val countNotification = it.peekContent().count


            binding.countNotificationDri.text = countNotification.toString()


        }

        notificationCountViewModel.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(this, it)
            //errorDialogs()
        }
    }


    override fun onBackPressed() {
        if (shouldLoadHomeFragOnBackPress) {
            when (navItemIndex) {
                5 -> {
                    binding.firstConsLayouttt.visibility = View.GONE
                    val homeFragment = HomeFragment()
                    replace_fragment(homeFragment)
                    binding.profileIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                    binding.tripHisIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                    binding.moreIcon.setColorFilter(application.resources.getColor(R.color.logo_color))
                    binding.orderIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                    binding.homeIconDri.setColorFilter(application.resources.getColor(R.color.black))
                    binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                    binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                    binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                    binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                    binding.homeTextDri.setTextColor(application.resources.getColor(R.color.black))
                }

                4, 3, 2 -> {
                    navItemIndex = 1
                    with(application.resources) {
                        binding.firstConsLayouttt.visibility = View.VISIBLE
                        val homeFragment = HomeFragment()
                        replace_fragment(homeFragment)
                        binding.profileIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                        binding.tripHisIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                        binding.moreIcon.setColorFilter(application.resources.getColor(R.color.logo_color))
                        binding.orderIconDri.setColorFilter(application.resources.getColor(R.color.logo_color))
                        binding.homeIconDri.setColorFilter(application.resources.getColor(R.color.black))
                        binding.profileTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                        binding.tripHistoryTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                        binding.moreTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                        binding.orderTextDri.setTextColor(application.resources.getColor(R.color.logo_color))
                        binding.homeTextDri.setTextColor(application.resources.getColor(R.color.black))
                    }
                }

                else -> {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastBackPressTime < backPressInterval) {
                        super.onBackPressed()
                        finishAffinity() // Closes all activities of the app
                    } else {
                        lastBackPressTime = currentTime
                        Toast.makeText(
                            this@DriverDashboardActivity,
                            "Please click BACK again to exit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun replace_fragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.driverFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this@DriverDashboardActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    openWarningPopUp()
                }
            }
        }
    }

    private fun openWarningPopUp() {
        val builder =
            AlertDialog.Builder(this@DriverDashboardActivity, R.style.Style_Dialog_Rounded_Corner)
        val dialogView = layoutInflater.inflate(R.layout.custom_permission_popup, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val okButtonWarning = dialogView.findViewById<Button>(R.id.okButtonWarning)
        dialog.show()

        okButtonWarning.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        //getProfileApi call
        getProfileApi()
    }

}