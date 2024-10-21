package com.pasco.pascocustomer.userFragment.history

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripViewModel
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryViewModel
import com.pasco.pascocustomer.Driver.adapter.CompletedTripHistoryAdapter
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.databinding.FragmentHistoryBinding
import com.pasco.pascocustomer.databinding.FragmentMoreBinding
import com.pasco.pascocustomer.userFragment.history.complete.CompleteModelView
import com.pasco.pascocustomer.userFragment.history.complete.CompletedHistoryAdapter
import com.pasco.pascocustomer.userFragment.history.model.CustBookingCancelViewModel
import com.pasco.pascocustomer.userFragment.logoutmodel.LogOutModelView
import com.pasco.pascocustomer.userFragment.order.acceptedadapter.AcceptedAdapter
import com.pasco.pascocustomer.userFragment.order.acceptedmodel.AcceptedModelView
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val logoutViewModel: LogOutModelView by viewModels()
    private var refresh = ""
    private lateinit var activity: Activity
    private var driverTripHistory: List<CompletedTripHistoryResponse.DriverTripHistoryData> =
        ArrayList()
    private var refersh = ""
    private val acceptedModelView: AcceptedModelView by viewModels()
    private val cancelledTripViewModel: CustBookingCancelViewModel by viewModels()
    private val completedTripViewModel: CompleteModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(activity) }
    private var acceptedList: List<AllBiddsDetailResponse.Datum> = ArrayList()
    private var acceptedAdapter: AcceptedAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val view = binding.root

        activity = requireActivity()


        binding.ordersConst.setOnClickListener {
            binding.ordersConst.setBackgroundResource(R.drawable.orders_tab_back)
            binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.allBiddsRecycler.visibility = View.GONE
            binding.acceptRecycler.visibility = View.GONE
            binding.asAcceptConst.setBackgroundResource(0)
            binding.allBiddsConst.setBackgroundResource(0)
            getAcceptedApi()

        }

        binding.allBiddsConst.setOnClickListener {
            binding.allBiddsConst.setBackgroundResource(R.drawable.all_bidds_back)
            binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.oderRecycler.visibility = View.GONE
            binding.acceptRecycler.visibility = View.GONE
            binding.asAcceptConst.setBackgroundResource(0)
            binding.ordersConst.setBackgroundResource(0)

            completedApi()
        }

        binding.asAcceptConst.setOnClickListener {
            binding.asAcceptConst.setBackgroundResource(R.drawable.accept_back)
            binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.oderRecycler.visibility = View.GONE
            binding.allBiddsRecycler.visibility = View.GONE
            binding.allBiddsConst.setBackgroundResource(0)
            binding.ordersConst.setBackgroundResource(0)

            cancelledApi()
        }
        getAcceptedApi()
        acceptedObserver()
        completedObserver()
        cancelledObserver()
        return view
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
            driverTripHistory = response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                binding.noDataFoundTxt.visibility = View.VISIBLE
                binding.noDataFoundTxt.text = "You have not cancelled any trips yet."
                binding.oderRecycler.visibility = View.GONE
                //Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                binding.noDataFoundTxt.visibility = View.GONE
                binding.oderRecycler.visibility = View.VISIBLE
                binding.oderRecycler.isVerticalScrollBarEnabled = true
                binding.oderRecycler.isVerticalFadingEdgeEnabled = true
                binding.oderRecycler.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.oderRecycler.adapter =
                    CompletedTripHistoryAdapter(requireContext(), driverTripHistory)
                // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()

            }
        }

        cancelledTripViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    private fun completedApi() {
        completedTripViewModel.driverTripCancelData(
            progressDialog,
            requireActivity()
        )
    }

    private fun completedObserver() {
        completedTripViewModel.progressIndicator.observe(requireActivity(), Observer {
            // Handle progress indicator changes if needed
        })

        completedTripViewModel.mCancelledHis.observe(requireActivity()) { response ->
            val message = response.peekContent().msg!!
            driverTripHistory = response.peekContent().data ?: emptyList()

            if (response.peekContent().status == "False") {
                binding.noDataFoundTxt.visibility = View.VISIBLE
                binding.allBiddsRecycler.visibility = View.GONE
                //Toast.makeText(requireActivity(), "$message", Toast.LENGTH_LONG).show()
            } else {
                binding.noDataFoundTxt.visibility = View.GONE
                binding.allBiddsRecycler.visibility = View.VISIBLE
                binding.allBiddsRecycler.isVerticalScrollBarEnabled = true
                binding.allBiddsRecycler.isVerticalFadingEdgeEnabled = true
                binding.allBiddsRecycler.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.allBiddsRecycler.adapter =
                    CompletedHistoryAdapter(requireContext(), driverTripHistory)
                // Toast.makeText(this@BiddingDetailsActivity, message, Toast.LENGTH_SHORT).show()

            }
        }

        completedTripViewModel.errorResponse.observe(requireActivity()) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
        }
    }

    private fun getAcceptedApi() {
        acceptedModelView.acceptedBids(activity, progressDialog)
    }

    private fun acceptedObserver() {
        acceptedModelView.progressIndicator.observe(this) {
        }
        acceptedModelView.mRejectResponse.observe(this) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            if (success == "True") {
                acceptedList = it.peekContent().data!!
                if (acceptedList.isEmpty()) {
                    binding.noDataFoundTxt.visibility = View.VISIBLE
                    binding.noDataFoundTxt.text = "No Accepted"
                } else {
                    binding.acceptRecycler.visibility = View.VISIBLE
                    binding.acceptRecycler.isVerticalScrollBarEnabled = true
                    binding.acceptRecycler.isVerticalFadingEdgeEnabled = true
                    binding.acceptRecycler.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    acceptedAdapter = AcceptedAdapter(requireContext(), acceptedList)
                    binding.acceptRecycler.adapter = acceptedAdapter
                }

            }
        }

        acceptedModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
            //errorDialogs()
        }
    }
}