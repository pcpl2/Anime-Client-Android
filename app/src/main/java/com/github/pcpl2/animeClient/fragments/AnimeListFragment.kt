package com.github.pcpl2.animeClient.fragments

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.*
import com.github.pcpl2.animeClient.R
import com.github.pcpl2.animeClient.callbacks.animeUpdateDataCallback
import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.services.GogoanimeIo
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import com.github.pcpl2.animeClient.adapters.AnimeListAdapter
import kotlinx.android.synthetic.main.fragment_anime_list.*


/**
 * Created by patry on 24.01.2018.
 */
class AnimeListFragment : Fragment() {
    var serviceId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (arguments != null) {
            serviceId = arguments.getString("serviceId")
        }
//        arguments.getString("Service")
        return inflater.inflate(R.layout.fragment_anime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(false)

        val adapter = AnimeListAdapter()

        val linearLayoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        animeRecyclerView.layoutManager = linearLayoutManager
        animeRecyclerView.itemAnimator = DefaultItemAnimator()
        animeRecyclerView.adapter = adapter

        GogoanimeIo.getInstance().updateAnimeList(object : animeUpdateDataCallback {
            override fun onComplete(animeList: ArrayList<AnimeEntry>) {
                activity?.runOnUiThread {
                    adapter.addAll(animeList)
                    animeListEmpty.visibility = RelativeLayout.INVISIBLE
                    animeRecyclerView.visibility = RecyclerView.VISIBLE
                }
            }

            override fun onError(message: String) {
                Log.e("AnimeListFragment", message)
            }
        })
    }
}

