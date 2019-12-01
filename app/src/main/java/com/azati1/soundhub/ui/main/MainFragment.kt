package com.azati1.soundhub.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ButtonItem
import com.azati1.soundhub.components.Content
import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.components.ContentItem
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), SectionRecyclerViewEvents {
    override fun onItemSelected() {
        rootView?.findViewById<RecyclerView>(R.id.sectionsRecyclerView)?.visibility = View.INVISIBLE
    }

    override fun onImagesLoaded() {
        (context as SectionRecyclerViewEvents)?.let {
            it.onImagesLoaded()
        }
        rootView?.findViewById<RecyclerView>(R.id.sectionsRecyclerView)?.visibility = View.VISIBLE
    }

    private var content: Content? = null
    private var mainFragmentCallbacks: SectionRecyclerViewEvents? = null
    private var rootView: View? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(rootView != null){
            Log.d("MSG", "view restored")
            return rootView
        } else {
            Log.d("MSG", "view inflated")
            rootView = inflater.inflate(R.layout.fragment_main, container, false)
            return rootView
        }

    }

    override fun onDetach() {
        super.onDetach()
        Log.d("MSG", "onDetach is called")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        content = arguments?.getSerializable("content") as Content?
        content?.let {
            initRecyclerView(it.content)
        }
    }

    private fun initRecyclerView(content: List<ContentItem>) {
        sectionsRecyclerView.layoutManager = LinearLayoutManager(context)
        sectionsRecyclerView.setHasFixedSize(true)
        sectionsRecyclerView.setItemViewCacheSize(20)

        val sectionsAdapter = SectionsRecyclerAdapter()
        sectionsRecyclerView.adapter = sectionsAdapter
        sectionsAdapter.addItems(
            content
        )
        sectionsAdapter.setOnDataLoadedListener(this)

    }

    override fun onPause() {
        super.onPause()
        Log.d("MSG", "onPause is called")

    }

    companion object {
        @JvmStatic
        fun create(content: ContentDto): MainFragment {
            val bundle = Bundle()
            bundle.putSerializable(
                "content",
                Content(
                    mainPicture = content.mainPicture,
                    content = content.content.map {
                        ContentItem(
                            name = it.name,
                            pictureUrl = it.pictureUrl,
                            buttons = it.buttons.map { item ->
                                ButtonItem(
                                    name = item.name,
                                    sound = item.sound,
                                    picture = item.picture
                                )
                            })
                    }
                )
            )
            val fragment = MainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}

interface SectionRecyclerViewEvents {
    fun onImagesLoaded()
    fun onItemSelected()

}