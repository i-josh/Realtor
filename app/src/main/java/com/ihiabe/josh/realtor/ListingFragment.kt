package com.ihiabe.josh.realtor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.viewpagerindicator.CirclePageIndicator


class ListingFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_listing,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageSlide = view.findViewById<ViewPager>(R.id.pager)
        val slideAdapter = SlideAdapter(activity!!.applicationContext)
        imageSlide.adapter = slideAdapter

        val indicator = view.findViewById<CirclePageIndicator>(R.id.indicator)
        indicator.setViewPager(imageSlide)
    }
}
