package com.azati1.soundhub.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.azati1.soundhub.R
import com.azati1.soundhub.ui.section.SectionFragment

class SectionsRecyclerAdapter : RecyclerView.Adapter<SectionsRecyclerAdapter.SectionViewHolder>() {

    private val sections = mutableListOf<Section>()

    fun addItems(items: List<Section>) {
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
        holder.title.text = sections[position].text
        holder.image.setBackgroundColor(sections[position].backgroundColor)
        holder.sectionItemView.setOnClickListener {
            if (it.context is FragmentActivity) {
                (it.context as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container,
                        SectionFragment.newInstance()
                    )
                    .addToBackStack("Section")
                    .commit()
            }
        }
    }

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val sectionItemView = itemView.findViewById<LinearLayout>(R.id.sectionItemView)
        val image = itemView.findViewById<FrameLayout>(R.id.sectionImage)
        val title = itemView.findViewById<TextView>(R.id.sectionTitle)

    }

}