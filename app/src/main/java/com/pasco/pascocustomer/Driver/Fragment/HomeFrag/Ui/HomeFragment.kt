package com.pasco.pascocustomer.Driver.Fragment.HomeFrag.Ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel.ShowBookingReqResponse
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel.ShowBookingReqViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.pasco.pascocustomer.Driver.DriverMessageActivity
import com.pasco.pascocustomer.Driver.EmergencyResponse.Ui.EmergencyCallActivity
import com.pasco.pascocustomer.Driver.NotesRemainders.Ui.NotesRemainderActivity
import com.pasco.pascocustomer.Driver.UpdateLocation.Ui.UpdateLocationActivity
import com.pasco.pascocustomer.Driver.adapter.AcceptRideAdapter
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.FragmentHomeDriverBinding
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeBody
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeModelView
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeResponse
import com.pasco.pascocustomer.userFragment.pageradaper.ViewPagerAdapter
import com.pasco.pascocustomer.utils.ErrorUtil

import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeDriverBinding
    private var dAdminApprovedId: String? = ""
    private var userType = ""
    private var currentPage = 0
    private var isLastPage = false
    private val NUM_PAGES = 3
    private val sliderViewModel: SliderHomeModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(activity) }
    private var sliderList: ArrayList<SliderHomeResponse.Datum>? = null
    private var rideRequestList: List<ShowBookingReqResponse.ShowBookingReqData> = ArrayList()
    @Inject lateinit var activity: Activity // Injecting activity
    private val showBookingReqViewModel: ShowBookingReqViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeDriverBinding.inflate(inflater, container, false)
        userType = PascoApp.encryptedPrefs.userType

        binding.viewPagerDriver.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentPage = position

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        val handler = Handler()
        val update: Runnable = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            binding.viewPagerDriver.setCurrentItem(currentPage++, true)
        }

        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 2000, 2000)
        dAdminApprovedId = PascoApp.encryptedPrefs.driverApprovedId
        if (dAdminApprovedId == "0") {
            disableAll()
        } else if (dAdminApprovedId == "1") {
            enableAll()
            showRideRequestApi()
            setupObservers()
        }
        binding.NotesDriHome.setOnClickListener {
            val intent = Intent(requireContext(), NotesRemainderActivity::class.java)
            startActivity(intent)
        }

        binding.supportsLinearDri.setOnClickListener {
            val intent = Intent(requireContext(), DriverMessageActivity::class.java)
            startActivity(intent)
        }
        binding.linearDriHEmergency.setOnClickListener {
            val intent = Intent(requireContext(), EmergencyCallActivity::class.java)
            startActivity(intent)
        }
        binding.LinearWallHomeF.setOnClickListener {
            val intent = Intent(requireContext(), UpdateLocationActivity::class.java)
            startActivity(intent)
        }
        sliderPageApi()
        sliderPageObserver()
        return binding.root
    }


    private fun sliderPageApi() {

        val bookingBody = SliderHomeBody(
            user_type = userType
        )
        sliderViewModel.otpCheck(bookingBody, requireActivity())
    }

    private fun sliderPageObserver() {

        sliderViewModel.mRejectResponse.observe(requireActivity()) { response ->
            sliderList = response.peekContent().data
            binding.viewPagerDriver.adapter = ViewPagerAdapter(requireContext(), sliderList!!)
            binding.indicator.setViewPager(binding.viewPagerDriver)

            binding.indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageSelected(position: Int) {
                    currentPage = position
                }

                override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {}
                override fun onPageScrollStateChanged(pos: Int) {}
            })
        }

        sliderViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }

    private fun enableAll() {
        binding.NotesDriHome.isEnabled = true
        binding.supportsLinearDri.isEnabled = true
        binding.LinearWallHomeF.isEnabled = true
        binding.linearDriHEmergency.isEnabled = true
    }

    private fun disableAll() {
        binding.NotesDriHome.isEnabled = false
        binding.supportsLinearDri.isEnabled = false
        binding.LinearWallHomeF.isEnabled = false
        binding.linearDriHEmergency.isEnabled = false
    }

    private fun showRideRequestApi() {
        showBookingReqViewModel.getShowBookingRequestsData(
            activity
        )
    }

    private fun setupObservers() {
        showBookingReqViewModel.mShowBookingReq.observe(viewLifecycleOwner) { response ->
            val message = response.peekContent().msg
            rideRequestList = response.peekContent().data ?: emptyList()

            if (rideRequestList.isEmpty()) {
                binding.orderBidsHomeFragTextView.visibility = View.VISIBLE
                binding.recycerRideRequest.visibility = View.GONE
            } else {
                binding.orderBidsHomeFragTextView.visibility = View.GONE
                binding.recycerRideRequest.visibility = View.VISIBLE
                setupRecyclerView()
            }

            if (response.peekContent().status == "False") {
                Toast.makeText(requireContext(), "Failed: $message", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.recycerRideRequest) {
            isVerticalScrollBarEnabled = true
            isVerticalFadingEdgeEnabled = true
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = AcceptRideAdapter(requireContext(), rideRequestList)
        }
    }

    override fun onResume() {
        super.onResume()
        //call api
        showRideRequestApi()
    }
}
