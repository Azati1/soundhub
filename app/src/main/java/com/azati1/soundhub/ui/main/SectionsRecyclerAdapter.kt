package com.azati1.soundhub.ui.main

import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ContentItem
import com.azati1.soundhub.ui.section.SECTION_CONTENT_FRAGMENT
import com.azati1.soundhub.ui.section.SectionFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class SectionsRecyclerAdapter : RecyclerView.Adapter<SectionsRecyclerAdapter.SectionViewHolder>() {

    private val sections = mutableListOf<ContentItem>()
    private var dataLoadedListener: SectionRecyclerViewEvents? = null

    private var loadedCount: Int = 0
    private var createdTasks: Int = 0

    fun setOnDataLoadedListener(callback: SectionRecyclerViewEvents) {
        dataLoadedListener = callback
    }

    fun addItems(items: List<ContentItem>) {
        sections.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.section_item, parent, false)
        return SectionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sections.size
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {

        Log.d("MSG", "onBindViewHolder")

        if (holder.isInitialized) {
            Log.d("MSG", "holder is initialized")
            return
        } else {
            Log.d("MSG", "holder is NOT initialized")
        }

        holder.setIsRecyclable(false)

        holder.title.text = sections[position].name
        createdTasks++


        Picasso.get()
            .load(sections[position].pictureUrl)

            .centerCrop()
            .fit()
            .into(holder.image, object : Callback {
                override fun onSuccess() {
                    Log.d("MSG", "onSuccess")
                    if (++loadedCount == createdTasks)
                        dataLoadedListener!!.onImagesLoaded()

                }

                override fun onError(e: Exception?) {
                    Log.d("MSG", "onError")
                }
            })



        holder.title.setOnClickListener {
            if (it.context is FragmentActivity) {

                val fragment = SectionFragment.newInstance(sections[position])
                fragment.sharedElementEnterTransition =
                    TransitionInflater.from(holder.itemView.context)
                        .inflateTransition(R.transition.name_transition)

                (it.context as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .addSharedElement(holder.title, holder.title.transitionName)
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(
                        R.id.container,
                        fragment,
                        SECTION_CONTENT_FRAGMENT
                    )

                    .addToBackStack(null)
                    .commit()
                dataLoadedListener?.onItemSelected()
            }
            (it.context as? OnPageShow)?.onPageShowed()
        }

        holder.isInitialized = true
    }

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var isInitialized: Boolean = false
        val image = itemView.findViewById<ImageView>(R.id.sectionImage)
        val title = itemView.findViewById<TextView>(R.id.sectionTitle)

    }

}