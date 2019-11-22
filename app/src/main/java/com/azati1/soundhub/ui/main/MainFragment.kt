package com.azati1.soundhub.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ButtonItem
import com.azati1.soundhub.components.Content
import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.components.ContentItem
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private var content: Content? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
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
        val sectionsAdapter = SectionsRecyclerAdapter()
        sectionsRecyclerView.adapter = sectionsAdapter
        sectionsAdapter.addItems(
            content
        )
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
                                    pageUrl = item.pageUrl,
                                    cost = item.cost
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
