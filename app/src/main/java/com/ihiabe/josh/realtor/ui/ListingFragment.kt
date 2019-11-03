package com.ihiabe.josh.realtor.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.ihiabe.josh.realtor.R
import com.ihiabe.josh.realtor.adapter.SlideAdapter
import com.ihiabe.josh.realtor.model.Listing
import com.viewpagerindicator.CirclePageIndicator
import java.text.DecimalFormat

class ListingFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var listingRecyclerView: RecyclerView
    private lateinit var showProgress: ProgressBar
    private lateinit var addListingButton: FloatingActionButton
    private val decimalFormat = DecimalFormat("#,##0.00")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listingRecyclerView = view.findViewById(R.id.listing_recycler_view)
        listingRecyclerView.hasFixedSize()
        listingRecyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        showProgress = view.findViewById(R.id.progressBarListing)
        addListingButton = view.findViewById(R.id.add_listing_button)

        listingRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy<0 && !addListingButton.isShown)
                    addListingButton.show()
                else if (dy>0 && addListingButton.isShown)
                    addListingButton.hide()
            }
        })

        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("Listing")

        initFireBase()

        addListingButton.setOnClickListener {
            startActivity(Intent(activity!!.applicationContext,AddListingActivity::class.java))
        }
    }

    private fun initFireBase() {
        val options = FirebaseRecyclerOptions.Builder<Listing>().setQuery(
            databaseReference,
            Listing::class.java
        ).setLifecycleOwner(this).build()

        val adapter = object : FirebaseRecyclerAdapter<Listing, ListingViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(
                holder: ListingViewHolder,
                position: Int,
                model: Listing
            ) {
                val listingImages = arrayOf(model.imageUrl1,model.imageUrl2,model.imageUrl3,
                    model.imageUrl4,model.imageUrl5)
                val slideAdapter = SlideAdapter(activity!!.applicationContext,listingImages)
                holder.imageSlide.adapter = slideAdapter
                holder.indicator.setViewPager(holder.imageSlide)
                holder.listinglocation.text = model.location
                holder.listingDescription.text = model.description
                holder.listingPrice.text = "₦${decimalFormat.format(model.price)}"

                showProgress.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
                val itemView = LayoutInflater.from(activity!!.applicationContext).inflate(
                    R.layout.listing_item,
                    parent, false
                )
                return ListingViewHolder(itemView)
            }
        }

        listingRecyclerView.adapter = adapter
        adapter.startListening()
    }

    class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var imageSlide = itemView.findViewById<ViewPager>(R.id.pager)
        internal var listinglocation = itemView.findViewById<TextView>(R.id.listing_location)
        internal var listingDescription = itemView.findViewById<TextView>(R.id.listing_description)
        internal var listingPrice = itemView.findViewById<TextView>(R.id.listing_price)
        internal var callListing = itemView.findViewById<Button>(R.id.call_listing)
        internal var favListing = itemView.findViewById<Button>(R.id.fav_listing)
        internal var indicator = itemView.findViewById<CirclePageIndicator>(R.id.indicator)
    }

}
