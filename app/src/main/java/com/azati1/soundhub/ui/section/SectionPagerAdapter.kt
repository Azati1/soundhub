package com.azati1.soundhub.ui.section

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.azati1.soundhub.components.ButtonItem

const val SECTION_SIZE = 4

class SectionPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    private val sectionContents = arrayListOf<SectionPage>()


    override fun getItem(position: Int): Fragment {
        return SectionContentFragment.newInstance(sectionContents[position])
    }

    override fun getCount(): Int {

        return sectionContents.size
    }

    fun addItems(items: List<ButtonItem>) {
        //sectionContents.addAll(items)
        val separated = items.chunked(SECTION_SIZE)

        val pages: ArrayList<SectionPage> = arrayListOf()

        separated.forEach { item ->
            pages.add(SectionPage(item))
        }

        sectionContents.addAll(pages)

    }

    class SectionPage(
        val soundbordItems: List<ButtonItem>
    )

}