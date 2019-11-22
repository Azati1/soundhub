package com.azati1.soundhub.ui.section


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ButtonItem
import com.azati1.soundhub.components.ContentItem
import com.azati1.soundhub.components.ContentItemDto
import kotlinx.android.synthetic.main.fragment_section.*

class SectionFragment : Fragment() {

    private var contentItem: ContentItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_section, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sectionPagerAdapter = SectionPagerAdapter(childFragmentManager)

        contentItem = arguments?.getSerializable("item") as ContentItem?

        contentItem?.let {
            sectionName.text = it.name
            val buttons = mutableListOf<ButtonItem>()
            buttons.addAll(it.buttons)
            buttons.addAll(it.buttons)
            sectionPagerAdapter.addItems(buttons)
        }

        sectionsViewPager.adapter = sectionPagerAdapter

        prevPageButton.setOnClickListener {
            if (sectionsViewPager.currentItem > 0)
                sectionsViewPager.setCurrentItem(sectionsViewPager.currentItem - 1, true)
        }

        nextPageButton.setOnClickListener {
            if (sectionPagerAdapter.count > sectionsViewPager.currentItem + 1)
                sectionsViewPager.setCurrentItem(sectionsViewPager.currentItem + 1, true)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(item: ContentItem) : SectionFragment {
            val bundle = Bundle()
            bundle.putSerializable("item", ContentItem(
                name = item.name,
                buttons = item.buttons.map { ButtonItem(name = it.name, pageUrl = it.pageUrl, cost = it.cost) },
                pictureUrl = item.pictureUrl
            ))
            val fragment = SectionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}
