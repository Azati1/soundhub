package com.azati1.soundhub.ui.section


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.azati1.soundhub.R
import kotlinx.android.synthetic.main.fragment_section.*

class SectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_section, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sectionPagerAdapter = SectionPagerAdapter(childFragmentManager)




        sectionPagerAdapter.addItems(
            arrayListOf(
                SectionPagerAdapter.SoundboardItem(
                    image = "image1",
                    text = "text1",
                    sound = "sound1"
                ),
                SectionPagerAdapter.SoundboardItem(
                    image = "image2",
                    text = "text2",
                    sound = "sound2"
                ),
                SectionPagerAdapter.SoundboardItem(
                    image = "image1",
                    text = "text3",
                    sound = "sound1"
                ),
                SectionPagerAdapter.SoundboardItem(
                    image = "image2",
                    text = "text4",
                    sound = "sound2"
                ),
                SectionPagerAdapter.SoundboardItem(
                    image = "image2",
                    text = "text5",
                    sound = "sound2"
                ),
                SectionPagerAdapter.SoundboardItem(
                    image = "image2",
                    text = "text5",
                    sound = "sound2"
                )
            )


        )
        sectionsViewPager.adapter = sectionPagerAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance() : SectionFragment {
            return SectionFragment()
        }
    }

}
