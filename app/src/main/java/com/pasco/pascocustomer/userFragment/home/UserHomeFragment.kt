package com.pasco.pascocustomer.userFragment.home

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.AddVehicle.ServiceListViewModel.ServicesViewModel
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.adddetailsmodel.ServicesResponse
import com.pasco.pascocustomer.databinding.FragmentUserHomeBinding
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeBody
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeModelView
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeResponse
import com.pasco.pascocustomer.userFragment.pageradaper.VehicleTypeAdapter
import com.pasco.pascocustomer.userFragment.pageradaper.ViewPagerAdapter
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UserHomeFragment : Fragment() {
    private var currentPage = 0
    private var isLastPage = false
    private val NUM_PAGES = 3
    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity: Activity
    private var vehicleTypeAdapter: VehicleTypeAdapter? = null
    private val servicesViewModel: ServicesViewModel by viewModels()
    private val sliderViewModel: SliderHomeModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(activity) }

    private var vehicleList: ArrayList<ServicesResponse.ServicesResponseData>? = null
    private var sliderList: ArrayList<SliderHomeResponse.Datum>? = null

    private var userType = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        activity = requireActivity()
        userType = PascoApp.encryptedPrefs.userType


        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
            binding.viewPager.setCurrentItem(currentPage++, true)
        }

        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 2000, 2000)



        servicesList()
        servicesObserver()
        sliderPageApi()
        sliderPageObserver()
        return view
    }

    private fun servicesList() {
        servicesViewModel.getServicesData(
            progressDialog,
            activity
        )
    }

    private fun servicesObserver() {
        servicesViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        servicesViewModel.mGetServices.observe(this) { response ->
            val content = response.peekContent()
            val message = content.msg ?: return@observe
            vehicleList = response.peekContent().data

            if (response.peekContent().status.equals("False")) {

            } else {
                binding.vehicleRecycler.isVerticalScrollBarEnabled = true
                binding.vehicleRecycler.isVerticalFadingEdgeEnabled = true
                binding.vehicleRecycler.layoutManager =
                    GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                vehicleTypeAdapter =
                    vehicleList?.let { it1 -> VehicleTypeAdapter(activity, it1) }
                binding.vehicleRecycler.adapter = vehicleTypeAdapter
            }
        }

        servicesViewModel.errorResponse.observe(this) {
            // Handle general errors
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
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
                binding.viewPager.adapter = ViewPagerAdapter(requireContext(), sliderList!!)
            binding.indicator.setViewPager(binding.viewPager)

            binding.indicator.setOnPageChangeListener(object : OnPageChangeListener {
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

}