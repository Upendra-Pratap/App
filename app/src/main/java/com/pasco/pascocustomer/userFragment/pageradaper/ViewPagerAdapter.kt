package com.pasco.pascocustomer.userFragment.pageradaper

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.BuildConfig
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeResponse
import java.util.ArrayList

class ViewPagerAdapter(
    private val context: Context,
   private var sliderList: ArrayList<SliderHomeResponse.Datum>
) :
    PagerAdapter() {

    override fun getCount(): Int {
        return sliderList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.landing_viewpager, container, false)
        val imageView = view.findViewById<ImageView>(R.id.viewPagerImg)
        val textView = view.findViewById<TextView>(R.id.headingTxt)
        val subHeading = view.findViewById<TextView>(R.id.subHeading)
         val url = sliderList?.get(position)?.slideimage
        Glide.with(context).load(BuildConfig.IMAGE_KEY+url).into(imageView)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}