package com.azati1.soundhub.ui.section


import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ButtonItem
import com.azati1.soundhub.components.ContentItem
import com.azati1.soundhub.components.ContentItemDto
import com.azati1.soundhub.ui.main.OnBackPressed
import com.azati1.soundhub.ui.main.OnPageShow
import com.azati1.soundhub.ui.main.OnSoundAction
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.fragment_section.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

const val SECTION_CONTENT_FRAGMENT = "SECTION_CONTENT"
class SectionFragment : Fragment(), OnBackPressed {

    private var contentItem: ContentItem? = null

    var canPop: Boolean = false
    lateinit var timerSubscribe: Disposable

    override fun onBackPressed(): Boolean {
        return canPop
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_section, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        timerSubscribe = Observable.interval(420, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                canPop = true
                timerSubscribe.dispose()
            }
    }

    override fun onPause() {
        super.onPause()
        (context as? OnSoundAction)?.onSoundStopped()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sectionPagerAdapter = SectionPagerAdapter(childFragmentManager)

        createAdBanner()

        contentItem = arguments?.getSerializable("item") as ContentItem?

        contentItem?.let {
            sectionName.text = it.name
            val buttons = mutableListOf<ButtonItem>()
            buttons.addAll(it.buttons)
            buttons.addAll(it.buttons)
            sectionPagerAdapter.addItems(buttons)
        }

        sectionsViewPager.adapter = sectionPagerAdapter

        sectionsViewPager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onPageSelected(position: Int) {
                    (context as? OnPageShow)?.onPageShowed()
                }

            }
        )

        prevPageButton.setOnClickListener {
            if (sectionsViewPager.currentItem > 0) {
                sectionsViewPager.setCurrentItem(sectionsViewPager.currentItem - 1, true)
            }
        }

        nextPageButton.setOnClickListener {
            if (sectionPagerAdapter.count > sectionsViewPager.currentItem + 1) {
                sectionsViewPager.setCurrentItem(sectionsViewPager.currentItem + 1, true)
            }
        }

        stopButton.setOnClickListener {
            (context as? OnSoundAction)?.onSoundStopped()
        }
    }

    private fun createAdBanner() {

        val size = getAdSize(ad_container)

        val adContainerParams = ad_container.layoutParams
        adContainerParams.height = dpToPx(size.height)

        ad_container.layoutParams = adContainerParams

        ad_container.visibility = View.VISIBLE

        val adView = AdView(context)
        adView.adSize = size
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        ad_container.addView(adView, params)
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = context!!.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    private fun getAdSize(adViewContainer: View) : AdSize {
        val display = activity!!.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = adViewContainer.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    companion object {
        @JvmStatic
        fun newInstance(item: ContentItem): SectionFragment {
            val bundle = Bundle()
            bundle.putSerializable(
                "item", ContentItem(
                    name = item.name,
                    buttons = item.buttons.map {
                        ButtonItem(
                            name = it.name,
                            picture = it.picture,
                            sound = it.sound
                        )
                    },
                    pictureUrl = item.pictureUrl
                )
            )
            val fragment = SectionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}
