package com.ihiabe.josh.realtor.adapter

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import com.ihiabe.josh.realtor.R
import com.squareup.picasso.Picasso

class ImageGridAdapter(private val context: Context,
                       private val images: List<Uri>): BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = if (convertView == null){
            ImageView(context)
        }else
            convertView as ImageView
        imageView.layoutParams = ViewGroup.LayoutParams(250,250)
        Picasso.with(context).load(images[position]).fit().centerCrop().into(imageView)
        return imageView
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return images.size
    }

}