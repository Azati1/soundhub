package com.azati1.soundhub.ui.section

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

import com.azati1.soundhub.R
import kotlinx.android.synthetic.main.fragment_section_content.*
import kotlinx.android.synthetic.main.soundboard_item.*

class SectionContentFragment : Fragment() {

    lateinit var sectionPage: SectionPagerAdapter.SectionPage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_section_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildPageContent()
    }

    private fun buildPageContent() {
        for (i in sectionPage.soundbordItems.indices step 2) {
            buildContentLine(sectionPage.soundbordItems.subList(i, i + 2))
        }
    }

    private fun buildContentLine(items: List<SectionPagerAdapter.SoundboardItem>) {
        if (items.size == 2) {

            val horizontalLinearLayout = LinearLayout(context)
            val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            linearLayoutParams.weight = 1.0f
            horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
            horizontalLinearLayout.layoutParams = linearLayoutParams

            horizontalLinearLayout.addView(buildSoundboardItem(items[0]))
            horizontalLinearLayout.addView(buildSoundboardItem(items[1]))

            content.addView(horizontalLinearLayout)

        } else {
            val horizontalLinearLayout = LinearLayout(context)
            val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            linearLayoutParams.weight = 1.0f
            horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
            horizontalLinearLayout.layoutParams = linearLayoutParams

            horizontalLinearLayout.addView(buildSoundboardItem(items[0]))

            content.addView(horizontalLinearLayout)
        }
    }

    private fun buildSoundboardItem(item: SectionPagerAdapter.SoundboardItem) : View {
        val layout = LayoutInflater.from(context).inflate(R.layout.soundboard_item, null, false)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layoutParams.weight = 1.0f
        layout.layoutParams = layoutParams
        layout.findViewById<TextView>(R.id.item_text).text = item.text
        layout.findViewById<ConstraintLayout>(R.id.item_container).setOnClickListener {
            Toast.makeText(context, item.sound, Toast.LENGTH_SHORT).show()
        }
        return layout
    }

    companion object {
        @JvmStatic
        fun newInstance(sectionPage: SectionPagerAdapter.SectionPage) : SectionContentFragment {
            val fragment = SectionContentFragment()
            fragment.sectionPage = sectionPage
            return fragment
        }
    }
}
