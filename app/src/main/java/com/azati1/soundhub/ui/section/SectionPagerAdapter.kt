package com.azati1.soundhub.ui.section

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val sectionContents = arrayListOf<SectionPage>()

    override fun getItem(position: Int): Fragment {
        return SectionContentFragment.newInstance(sectionContents[position])
    }

    override fun getCount(): Int {
        return sectionContents.size
    }

    fun addItems(items: ArrayList<SectionPage>) {
        sectionContents.addAll(items)
    }

    class SectionPage(
        val soundbordItems: List<SoundboardItem>
    )

    class SoundboardItem(
        val image: String,
        val text: String,
        val sound: String
    )

}