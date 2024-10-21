package com.pasco.pascocustomer.Driver.Fragment
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripResponse.CancelledData
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripViewModel
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryViewModel
import com.pasco.pascocustomer.Driver.adapter.CancelledTripHistoryAdapter
import com.pasco.pascocustomer.Driver.adapter.CompletedTripHistoryAdapter
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.databinding.FragmentTripHistoryBinding
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TripHistoryFragment : Fragment() {
    private lateinit var binding:FragmentTripHistoryBinding
    private var driverTripHistory:List<CompletedTripHistoryResponse.DriverTripHistoryData> = ArrayList()
    private var cancelledTrips:List<CancelledTripResponse.CancelledData> = ArrayList()
    private var refersh = ""
    private val completedTripHistoryViewModel: CompletedTripHistoryViewModel by viewModels()
    private val cancelledTripViewModel: CancelledTripViewModel by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTripHistoryBinding.inflate(inflater, container, false)
        refersh = PascoApp.encryptedPrefs.token

        completedApi()
        completedObserver()
        binding.completedHisTextview.setOnClickListener {
            binding.completedHisTextview.background = ContextCompat.getDrawable(requireActivity(), R.drawable.order_bidding_yellow)
            binding.cancelledHisTextview.background = null
            binding.completedHisTextview.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.cancelledHisTextview.setTextColor(Color.parseColor("#FF000000"))
            completedApi()
            completedObserver()

        }
        binding.cancelledHisTextview.setOnClickListener {
            binding.completedHisTextview.background = null
            binding.cancelledHisTextview.background = ContextCompat.getDrawable(requireActivity(), R.drawable.accept_bidd_background)
            binding.completedHisTextview.setTextColor(Color.parseColor("#FF000000"))
            binding.cancelledHisTextview.setTextColor(Color.parseColor("#FFFFFFFF"))
            cancelledApi()
            cancelledObserver()
        }
        return binding.root
    }



    private fun cancelledApi() {
        cancelledTripViewModel.driverTripCancelData(
            progressDialog,
            requireActivity()
        )
    }
    private fun cancelledObserver() {
        cancelledTripViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        cancelledTripViewModel.mCancelledHis.observe(requireActivity()) { response ->
            val message = response.peekContent().msg!!
            cancelledTrips =  response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                binding.staticCTextview.visibility = View.VISIBLE
                binding.staticCTextview.text = "You have not cancelled any trips yet."
                binding.recycerHistoryDriverList.visibility = View.VISIBLE
                //Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                binding.staticCTextview.visibility = View.GONE
                binding.recycerHistoryDriverList.visibility = View.VISIBLE
                binding.recycerHistoryDriverList.isVerticalScrollBarEnabled = true
                binding.recycerHistoryDriverList.isVerticalFadingEdgeEnabled = true
                binding.recycerHistoryDriverList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.recycerHistoryDriverList.adapter = CancelledTripHistoryAdapter(requireContext(), cancelledTrips)

            }
        }

        cancelledTripViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }
    private fun completedApi() {
        completedTripHistoryViewModel.driverTripHisData(
            progressDialog,
            requireActivity()
        )
    }
    private fun completedObserver() {
        completedTripHistoryViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        completedTripHistoryViewModel.mGetServices.observe(requireActivity()) { response ->
            val message = response.peekContent().msg!!
            driverTripHistory = response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                binding.staticCTextview.visibility = View.VISIBLE
                binding.recycerHistoryDriverList.visibility = View.GONE
                //Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                binding.staticCTextview.visibility = View.GONE
                binding.recycerHistoryDriverList.visibility = View.VISIBLE
                binding.recycerHistoryDriverList.isVerticalScrollBarEnabled = true
                binding.recycerHistoryDriverList.isVerticalFadingEdgeEnabled = true
                binding.recycerHistoryDriverList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.recycerHistoryDriverList.adapter = CompletedTripHistoryAdapter(requireContext(), driverTripHistory)
                // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()

            }
        }

        completedTripHistoryViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }



    override fun onResume() {
        super.onResume()
        completedApi()
    }

}