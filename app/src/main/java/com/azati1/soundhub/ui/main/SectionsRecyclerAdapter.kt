package com.azati1.soundhub.ui.main

import android.graphics.Bitmap
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ContentItemDto
import com.azati1.soundhub.ui.section.SectionFragment
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class SectionsRecyclerAdapter : RecyclerView.Adapter<SectionsRecyclerAdapter.SectionViewHolder>() {

    private val sections = mutableListOf<ContentItemDto>()

    fun addItems(items: List<ContentItemDto>) {
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

        holder.title.text = sections[position].name
        Picasso.get()
            .load(sections[position].pictureUrl)
            .resize(5000, 5000)
            .onlyScaleDown()
            .into(holder.image)
        holder.sectionItemView.setOnClickListener {
            if (it.context is FragmentActivity) {
                (it.context as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                        SectionFragment.newInstance(sections[position])
                    )
                    .addSharedElement(holder.title, holder.title.transitionName)
                    .addToBackStack("Section")
                    .commit()
            }
        }
    }

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val sectionItemView = itemView.findViewById<LinearLayout>(R.id.sectionItemView)
        val image = itemView.findViewById<ImageView>(R.id.sectionImage)
        val title = itemView.findViewById<TextView>(R.id.sectionTitle)

    }

}