package com.azati1.soundhub.ui.section

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.azati1.soundhub.R
import com.azati1.soundhub.components.AppComponent
import com.azati1.soundhub.components.ButtonItem
import com.azati1.soundhub.components.InnerContentItem
import com.azati1.soundhub.ui.main.OnSoundAction
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_section_content.*

class SectionContentFragment : Fragment(){

    lateinit var player: MediaPlayer
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

    @SuppressLint("InflateParams")
    private fun buildContentLine(items: List<InnerContentItem>) {
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

    @SuppressLint("InflateParams")
    private fun buildSoundboardItem(item: InnerContentItem): View {
        val layout = LayoutInflater.from(context).inflate(R.layout.soundboard_item, null, false)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.weight = 1.0f
        layout.layoutParams = layoutParams

        if (item is ButtonItem) {

            layout.findViewById<AppCompatImageView>(R.id.view).visibility = View.VISIBLE
            layout.findViewById<TextView>(R.id.item_text).text = item.name
            layout.findViewById<ConstraintLayout>(R.id.item_container).setOnClickListener {
                Toast.makeText(context, item.picture, Toast.LENGTH_SHORT).show()
            }

            Picasso.get()
                .load(item.picture)
                .centerCrop()
                .fit()
                .placeholder(R.drawable.placeholder)
                .into(layout.findViewById<ImageView>(R.id.view))

            layout.setOnClickListener {

                (context as? OnSoundAction)?.onSoundStarted(
                    context!!.applicationInfo.dataDir + "/" + Uri.parse(
                        item.sound
                    ).lastPathSegment
                )


            }
        } else {

            val adContainer = layout.findViewById<LinearLayout>(R.id.ad_container)
            adContainer.visibility = View.VISIBLE
            val adsDto = (context?.applicationContext as AppComponent).getAdsDto()
            adsDto?.let {
                val adLoader = AdLoader.Builder(context, adsDto.admobNativeId)
                    .forUnifiedNativeAd {

                        if (isAdded) {
                            val unifiedNativeAdView =
                                layoutInflater.inflate(R.layout.native_ad_layout, null)

                            if (unifiedNativeAdView is UnifiedNativeAdView) {
                                mapUnifiedNativeAdToLayout(it, unifiedNativeAdView)
                                adContainer.removeAllViews()
                                adContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                                adContainer.addView(unifiedNativeAdView)
                            }
                        }

                    }.build()
                adLoader.loadAd(AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build())
            }

        }

        return layout
    }

    private fun mapUnifiedNativeAdToLayout(adFromGoogle: UnifiedNativeAd, myAdView: UnifiedNativeAdView) {

        myAdView.iconView = myAdView.findViewById(R.id.ad_icon)

        if (adFromGoogle.icon == null) {
            myAdView.iconView.visibility = View.GONE
        } else {
            (myAdView.iconView as ImageView).setImageDrawable(adFromGoogle.icon.drawable)
        }

        myAdView.setNativeAd(adFromGoogle)
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
