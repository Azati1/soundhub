package com.azati1.soundhub.ui.section

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.azati1.soundhub.R
import com.azati1.soundhub.RateAppDialogFragment
import com.azati1.soundhub.components.AppComponent
import com.azati1.soundhub.components.ButtonItem
import com.azati1.soundhub.components.ContentItem
import com.azati1.soundhub.ui.main.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_section.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

const val SECTION_CONTENT_FRAGMENT = "SECTION_CONTENT"

class SectionFragment : Fragment(), OnBackPressed {

    private var contentItem: ContentItem? = null
    private var rateUsDisposable: Disposable? = null

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
        context.applicationContext
        timerSubscribe = Observable.interval(900, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                canPop = true
                timerSubscribe.dispose()
            }, { Log.d("CDA", "err") })
    }

    override fun onPause() {
        super.onPause()
        (context as? OnSoundAction)?.onSoundStopped()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionPagerAdapter = SectionPagerAdapter(childFragmentManager)

        val adsDto = (context?.applicationContext as AppComponent).getAdsDto()
        adsDto?.let {
            createAdBanner(adsDto.admobBannerId)
        }

        contentItem = arguments?.getSerializable("item") as ContentItem?

        contentItem?.let {
            sectionName.text = it.name
            val buttons = mutableListOf<ButtonItem>()
            buttons.addAll(it.buttons)
            sectionPagerAdapter.addItems(buttons)
        }

        sectionsViewPager.adapter = sectionPagerAdapter

        sectionsViewPager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                    Log.d("SC12345", "PAGE SCROLL STATE CHANGED " + state)

                    if(state == ViewPager.SCROLL_STATE_IDLE){
                        if (sectionsViewPager?.currentItem == sectionsViewPager?.adapter!!.count - 1){
                            sectionName?.alpha = 0f
                        } else {
                            sectionName?.alpha = 1f
                        }
                    }


                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                    sectionsViewPager?.adapter?.let {
                        var lastPage = it.count - 2
                            if(position == lastPage){
                            sectionName?.alpha = 1 - positionOffset
                        } else if (position == lastPage + 1){
                            sectionName?.alpha = 0f
                        } else {
                            sectionName?.alpha = 1f
                        }
                    }




                }

                override fun onPageSelected(position: Int) {
                    Log.d("MSG", "PAGE SELECTED " + position)




                    (context as? OnPageShow)?.onPageShowed()
                    if (position == sectionPagerAdapter.count - 1) {
                        showRateUsDialogIfNeed()
                    } else {
                        rateUsDisposable?.dispose()
                    }
                }

            }
        )

        prevPageButton.setOnClickListener {
            Log.d("MSG_CANPOP", canPop.toString())
            if (sectionsViewPager.currentItem == 0 && canPop)
                fragmentManager?.popBackStack()
            else if (sectionsViewPager.currentItem > 0) {
                sectionsViewPager.setCurrentItem(sectionsViewPager.currentItem - 1, true)
            }
            animateButton(prevPageButton)
        }

        nextPageButton.setOnClickListener {
            if (sectionPagerAdapter.count > sectionsViewPager.currentItem + 1) {
                sectionsViewPager.setCurrentItem(sectionsViewPager.currentItem + 1, true)
            }
            animateButton(nextPageButton)
        }

        stopButton.setOnClickListener {
            (context as? OnSoundAction)?.onSoundStopped()
            animateButton(stopButton)
        }
    }

    private fun animateButton(button: View) {
        val scale = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.9f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.9f, 1f)
        )
        scale.duration = 400
        scale.start()
    }

    private fun showRateUsDialogIfNeed() {
        rateUsDisposable = Observable.just(1)
            .delay(10L, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                val isMarketPageShowed = context
                    ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    ?.getBoolean(IS_MARKET_PAGE_SHOWED, false) ?: false
                val isRateUsDialogShowNow = (context?.applicationContext as? AppComponent)?.isRateUsDialogShowed ?: false
                if (!isRateUsDialogShowNow && !isMarketPageShowed) {
                    Log.d("CDA123", "show rate dialog from section")
                    fragmentManager?.let { fragmentManager ->
                        val rateAppDialogFragment = RateAppDialogFragment.create()
                        rateAppDialogFragment.show(fragmentManager, "")
                    }
                    (context?.applicationContext as? AppComponent)?.isRateUsDialogShowed = true
                }
            }, { Log.d("CDA", "err")})
    }

    private fun createAdBanner(id: String) {

        val size = getAdSize(ad_container)

        val adContainerParams = ad_container.layoutParams
        adContainerParams.height = dpToPx(size.height)

        ad_container.layoutParams = adContainerParams

        ad_container.visibility = View.VISIBLE

        val adView = AdView(context)
        adView.adSize = size
        adView.adUnitId = id

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
