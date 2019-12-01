package com.azati1.soundhub.ui.section


import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ButtonItem
import com.azati1.soundhub.components.ContentItem
import com.azati1.soundhub.components.ContentItemDto
import com.azati1.soundhub.ui.main.OnBackPressed
import com.azati1.soundhub.ui.main.OnSoundAction
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_section.*
import java.util.concurrent.TimeUnit

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

        stopButton.setOnClickListener {
            (context as? OnSoundAction)?.onSoundStopped()
        }
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
