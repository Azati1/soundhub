package com.azati1.soundhub.ui.section

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ButtonItem
import com.azati1.soundhub.ui.main.OnBackPressed
import com.azati1.soundhub.ui.main.OnSoundAction
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_section_content.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.xml.datatype.DatatypeConstants.SECONDS

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


class SectionContentFragment : Fragment(){




    lateinit var player: MediaPlayer
    lateinit var sectionPage: SectionPagerAdapter.SectionPage

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_section_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildPageContent()
        player = MediaPlayer()
        Log.d("MSG", "VIEW CREATED")
    }

    private fun buildPageContent() {

        for (i in sectionPage.soundbordItems.chunked(2)) {
            buildContentLine(i)
        }

        if (sectionPage.soundbordItems.size <= 2)
            buildEmptyLine()
    }

    private fun buildEmptyLine() {
        val horizontalLinearLayout = LinearLayout(context)
        val linearLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        linearLayoutParams.weight = 1.0f
        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
        horizontalLinearLayout.layoutParams = linearLayoutParams
        horizontalLinearLayout.addView(
            buildSoundboardItem(
                ButtonItem(
                    name = "stub",
                    sound = "stub",
                    picture = "stub"
                )
            )
        )
        horizontalLinearLayout.visibility = View.INVISIBLE
        content.addView(horizontalLinearLayout)
    }

    private fun buildContentLine(items: List<ButtonItem>) {
        if (items.size == 2) {

            val horizontalLinearLayout = LinearLayout(context)
            val linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            linearLayoutParams.weight = 1.0f
            horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
            horizontalLinearLayout.layoutParams = linearLayoutParams

            horizontalLinearLayout.addView(buildSoundboardItem(items[0]))
            horizontalLinearLayout.addView(buildSoundboardItem(items[1]))

            content.addView(horizontalLinearLayout)

        } else {
            val horizontalLinearLayout = LinearLayout(context)
            val linearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            linearLayoutParams.weight = 1.0f
            horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
            horizontalLinearLayout.layoutParams = linearLayoutParams

            horizontalLinearLayout.addView(buildSoundboardItem(items[0]))

            val layout = LayoutInflater.from(context).inflate(R.layout.soundboard_item, null, false)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.weight = 1.0f
            layout.layoutParams = layoutParams
            layout.visibility = View.INVISIBLE
            horizontalLinearLayout.addView(layout)

            content.addView(horizontalLinearLayout)
        }
    }

    private fun buildSoundboardItem(item: ButtonItem): View {
        val layout = LayoutInflater.from(context).inflate(R.layout.soundboard_item, null, false)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.weight = 1.0f
        layout.layoutParams = layoutParams
        layout.findViewById<TextView>(R.id.item_text).text = item.name
        layout.findViewById<ConstraintLayout>(R.id.item_container).setOnClickListener {
            Toast.makeText(context, item.picture, Toast.LENGTH_SHORT).show()
        }

        Picasso.get()
            .load(item.picture)
            .centerCrop()
            .fit()
            .placeholder(R.drawable.ic_music)
            .into(layout.findViewById<ImageView>(R.id.view))

        layout.setOnClickListener {

            (context as? OnSoundAction)?.onSoundStarted(
                context!!.applicationInfo.dataDir + "/" + Uri.parse(
                    item.sound
                ).lastPathSegment
            )


        }

        return layout
    }

    companion object {
        @JvmStatic
        fun newInstance(sectionPage: SectionPagerAdapter.SectionPage): SectionContentFragment {
            val fragment = SectionContentFragment()
            fragment.sectionPage = sectionPage
            return fragment
        }
    }
}
