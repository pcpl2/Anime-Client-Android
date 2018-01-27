package com.github.pcpl2.animeClient.fragments

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.*
import com.github.pcpl2.animeClient.R
import com.github.pcpl2.animeClient.callbacks.AnimeUpdateDataCallback
import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.services.GogoanimeIo
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import com.github.pcpl2.animeClient.adapters.AnimeListAdapter
import com.github.pcpl2.animeClient.callbacks.AnimeListFragmentElementClicked
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_anime_list.*


/**
 * Created by patry on 24.01.2018.
 */
class AnimeListFragment : Fragment() {

    enum class AnimeListFragmentStatus {
        LOADING, LOADED, ERROR, EMPTY
    }

    private var serviceId: String? = null

    private var adapter: AnimeListAdapter? = null

    var currentState = AnimeListFragmentStatus.LOADING

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (arguments != null) {
            serviceId = arguments.getString("serviceId")
        }
        return inflater.inflate(R.layout.fragment_anime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(false)

        adapter = AnimeListAdapter(this, object : AnimeListFragmentElementClicked {
            override fun onClicked(animeEntry: AnimeEntry) {
                val bundle = Bundle()
                bundle.putString("serviceId", serviceId)
                bundle.putString("animeEntry", Gson().toJson(animeEntry))
                val episodeListFragment = EpisodeListFragment()
                episodeListFragment.arguments = bundle
                val fm = fragmentManager.beginTransaction()
                fm.replace(activity.fragmentContainer.id, episodeListFragment)
                fm.addToBackStack(null)
                fm.commit()
            }
        })

        animeListErrorText.text = ""
        animeRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        animeRecyclerView.itemAnimator = DefaultItemAnimator()
        animeRecyclerView.adapter = adapter
        animeListErrorButtonRetry.setOnClickListener {
            animeListErrorText.text = ""
            updateData()
        }

        updateData()

    }

    private fun updateData() {
        setState(AnimeListFragmentStatus.LOADING)
        GogoanimeIo.getInstance().updateAnimeList(object : AnimeUpdateDataCallback {
            override fun onComplete(animeList: ArrayList<AnimeEntry>) {
                activity?.runOnUiThread {
                    adapter?.addAll(animeList)
                    setState(AnimeListFragmentStatus.LOADED)
                }
            }

            override fun onError(message: String) {
                animeListErrorText.text = message
                setState(AnimeListFragmentStatus.ERROR)
            }
        }, force = false)
    }

    fun setState(state: AnimeListFragmentStatus) {
        when (state) {
            AnimeListFragmentStatus.LOADING -> {
                currentState = AnimeListFragmentStatus.LOADING
                animeListLoading.visibility = RelativeLayout.VISIBLE

                animeListEmpty.visibility = RelativeLayout.INVISIBLE
                animeRecyclerView.visibility = RecyclerView.INVISIBLE
                animeListError.visibility = RelativeLayout.INVISIBLE
            }
            AnimeListFragmentStatus.LOADED -> {
                currentState = AnimeListFragmentStatus.LOADED
                animeRecyclerView.visibility = RecyclerView.VISIBLE

                animeListEmpty.visibility = RelativeLayout.INVISIBLE
                animeListLoading.visibility = RelativeLayout.INVISIBLE
                animeListError.visibility = RelativeLayout.INVISIBLE
            }
            AnimeListFragmentStatus.EMPTY -> {
                currentState = AnimeListFragmentStatus.EMPTY
                animeListEmpty.visibility = RelativeLayout.VISIBLE

                animeRecyclerView.visibility = RecyclerView.INVISIBLE
                animeListLoading.visibility = RelativeLayout.INVISIBLE
                animeListError.visibility = RelativeLayout.INVISIBLE
            }
            AnimeListFragmentStatus.ERROR -> {
                currentState = AnimeListFragmentStatus.ERROR
                animeListError.visibility = RelativeLayout.VISIBLE

                animeListEmpty.visibility = RelativeLayout.INVISIBLE
                animeRecyclerView.visibility = RecyclerView.INVISIBLE
                animeListLoading.visibility = RelativeLayout.INVISIBLE
            }
        }
    }
}
