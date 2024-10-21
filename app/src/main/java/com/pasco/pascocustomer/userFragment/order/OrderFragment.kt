package com.pasco.pascocustomer.userFragment.order

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.databinding.FragmentOrderBinding
import com.pasco.pascocustomer.userFragment.allbidds.AllBiddsAdapter
import com.pasco.pascocustomer.userFragment.allbidds.AllBiddsModelView
import com.pasco.pascocustomer.userFragment.order.acceptedadapter.AcceptedAdapter
import com.pasco.pascocustomer.userFragment.order.acceptedmodel.AcceptedModelView
import com.pasco.pascocustomer.userFragment.order.adapter.OrderAdapter
import com.pasco.pascocustomer.userFragment.order.odermodel.OrderModelView
import com.pasco.pascocustomer.userFragment.order.odermodel.OrderResponse
import com.pasco.pascocustomer.utils.ErrorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity: Activity

    private var customAdapter: OrderAdapter? = null
    private var allBiddsAdapter: AllBiddsAdapter? = null
    private var acceptedAdapter: AcceptedAdapter? = null
    private val orderModelView: OrderModelView by viewModels()
    private val acceptedModelView: AcceptedModelView by viewModels()
    private val allBiddsModelView: AllBiddsModelView by viewModels()
    private val progressDialog by lazy { CustomProgressDialog(activity) }
    private var isValue = false
    private var bookMarkList: List<OrderResponse.Datum> = ArrayList()
    private var allBiddsList: List<OrderResponse.Datum> = ArrayList()
    private var acceptedList: List<AllBiddsDetailResponse.Datum> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        val view = binding.root

        activity = requireActivity()

        binding.ordersConst.setOnClickListener {
            binding.ordersConst.setBackgroundResource(R.drawable.orders_tab_back)
            binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.oderRecycler.visibility = View.VISIBLE
            binding.allBiddsRecycler.visibility = View.GONE
            binding.acceptRecycler.visibility = View.GONE
            binding.noDataFoundTxt.visibility = View.GONE
            binding.asAcceptConst.setBackgroundResource(0)
            binding.allBiddsConst.setBackgroundResource(0)
            getOrderApi()

        }

        binding.allBiddsConst.setOnClickListener {
            binding.allBiddsConst.setBackgroundResource(R.drawable.all_bidds_back)
            binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.oderRecycler.visibility = View.GONE
            binding.allBiddsRecycler.visibility = View.VISIBLE
            binding.acceptRecycler.visibility = View.GONE
            binding.asAcceptConst.setBackgroundResource(0)
            binding.ordersConst.setBackgroundResource(0)
            getAllBiddsApi()
        }

        binding.asAcceptConst.setOnClickListener {
            binding.asAcceptConst.setBackgroundResource(R.drawable.accept_back)
            binding.acceptTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.orderTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.biddsTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.oderRecycler.visibility = View.GONE
            binding.allBiddsRecycler.visibility = View.GONE
            binding.acceptRecycler.visibility = View.VISIBLE
            binding.allBiddsConst.setBackgroundResource(0)
            binding.ordersConst.setBackgroundResource(0)
            getAcceptedApi()

        }
        getOrderApi()
        orderObserver()
        allBiddsObserver()
        acceptedObserver()
        return view
    }


    private fun getOrderApi() {
        orderModelView.otpCheck(activity, progressDialog)
    }

    private fun orderObserver() {
        orderModelView.progressIndicator.observe(this) {
        }
        orderModelView.mRejectResponse.observe(this) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            if (success == "True") {
                bookMarkList = it.peekContent().data!!
                if (bookMarkList.isEmpty()) {
                    binding.oderRecycler.visibility = View.GONE
                    binding.noDataFoundTxt.visibility = View.VISIBLE
                    binding.noDataFoundTxt.text = "No Orders"
                } else {
                    binding.oderRecycler.visibility = View.VISIBLE
                    binding.oderRecycler.isVerticalScrollBarEnabled = true
                    binding.oderRecycler.isVerticalFadingEdgeEnabled = true
                    binding.oderRecycler.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    customAdapter = OrderAdapter(requireContext(), bookMarkList)
                    binding.oderRecycler.adapter = customAdapter
                }

            }
        }

        orderModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
            //errorDialogs()
        }
    }


    private fun getAllBiddsApi() {
        allBiddsModelView.otpCheck(activity, progressDialog)
    }

    private fun allBiddsObserver() {
        allBiddsModelView.progressIndicator.observe(this) {
        }
        allBiddsModelView.mRejectResponse.observe(this) {
            val message = it.peekContent().msg
            val success = it.peekContent().status
            if (success == "True") {
                allBiddsList = it.peekContent().data!!
                if (allBiddsList.isEmpty()) {
                    binding.noDataFoundTxt.visibility = View.VISIBLE
                    binding.noDataFoundTxt.text = "No bids"
                } else {
                    binding.allBiddsRecycler.visibility = View.VISIBLE
                    binding.allBiddsRecycler.isVerticalScrollBarEnabled = true
                    binding.allBiddsRecycler.isVerticalFadingEdgeEnabled = true
                    binding.allBiddsRecycler.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    allBiddsAdapter = AllBiddsAdapter(requireContext(), allBiddsList)
                    binding.allBiddsRecycler.adapter = allBiddsAdapter
                }

            }
        }

        allBiddsModelView.errorResponse.observe(this) {
            ErrorUtil.handlerGeneralError(requireActivity(), it)
            //errorDialogs()
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