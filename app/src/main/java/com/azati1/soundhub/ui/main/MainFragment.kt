package com.azati1.soundhub.ui.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ApiService
import com.azati1.soundhub.components.AppComponent
import com.azati1.soundhub.repo.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        loadData()
    }

    private fun loadData() {
        val repository = Repository(AppComponent.getOrCreateApiService())
        repository.getAdsData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe ({
                Log.d("CDA123", it.admobAppId)
            }, {t ->
                Log.d("CDA123", t.message)
            })
        repository.getContent()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe ({
                Log.d("CDA123", it.mainPicture)
            }, {t ->
                Log.d("CDA123", t.message)
            })
    }

    private fun initRecyclerView() {
        sectionsRecyclerView.layoutManager = LinearLayoutManager(context)
        val sectionsAdapter = SectionsRecyclerAdapter()
        sectionsRecyclerView.adapter = sectionsAdapter
        sectionsAdapter.addItems(
            listOf(
                Section(Color.BLUE, "skrt"),
                Section(Color.CYAN, "mmbb"),
                Section(Color.GREEN, "ablm"),
                Section(Color.LTGRAY, "qtry"),
                Section(Color.MAGENTA, "maxx")
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() : MainFragment {
            return MainFragment()
        }
    }
}
