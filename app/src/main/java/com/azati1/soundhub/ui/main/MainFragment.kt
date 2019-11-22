package com.azati1.soundhub.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ContentItemDto
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val model = MainModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable.add(
            model.getAds()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    Log.d("CDA123", it.admobAppId)
                }, {t ->
                    Log.d("CDA123", t.message)
                }))

        compositeDisposable.add(
            model.getContent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    initRecyclerView(it.content)
                }, {t ->
                    Log.d("CDA123", t.message)
                }))
    }

    private fun initRecyclerView(content: List<ContentItemDto>) {
        sectionsRecyclerView.layoutManager = LinearLayoutManager(context)
        val sectionsAdapter = SectionsRecyclerAdapter()
        sectionsRecyclerView.adapter = sectionsAdapter
        sectionsAdapter.addItems(
            content
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        @JvmStatic
        fun create() : MainFragment {
            return MainFragment()
        }
    }

    interface MainFragmentListener {
        fun onDataLoaded()
    }
}
