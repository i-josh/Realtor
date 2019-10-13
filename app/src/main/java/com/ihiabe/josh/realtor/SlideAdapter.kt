package com.ihiabe.josh.realtor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter

class SlideAdapter(private val context: Context): PagerAdapter() {

    private val images = intArrayOf(R.drawable.hailey_baldwin_4k_2017,R.drawable.gal_gadot_2)

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageResource(images[position])
        container.addView(imageView,0)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }

}
