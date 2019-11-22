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
            sectionPagerAdapter.addItems(it.buttons)
        }

        /*sectionPagerAdapter.addItems(
            arrayListOf(
                SectionPagerAdapter.SoundboardItem(
                    image = "image1",
                    text = "text1",
                    sound = "sound1"
                ),
                SectionPagerAdapter.SoundboardItem(
                    image = "image1",
                    text = "text1",
                    sound = "sound1"
                ),
                SectionPagerAdapter.SoundboardItem(
                    image = "image1",
                    text = "text1",
                    sound = "sound1"
                ),
                SectionPagerAdapter.SoundboardItem(
                    image = "image1",
                    text = "text1",
                    sound = "sound1"
                ),
                SectionPagerAdapter.SoundboardItem(
                    image = "image1",
                    text = "text1",
                    sound = "sound1"
                )
            )


        )*/
        sectionsViewPager.adapter = sectionPagerAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(item: ContentItemDto) : SectionFragment {
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
