package com.github.pcpl2.animeClient.fragments


import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import com.github.pcpl2.animeClient.R
import com.github.pcpl2.animeClient.adapters.EpisodeListAdapter
import com.github.pcpl2.animeClient.callbacks.EpisodeListFragmentElementClicked
import com.github.pcpl2.animeClient.callbacks.EpisodeUpdateDataCallback
import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.domain.EpisodeEntry
import com.github.pcpl2.animeClient.services.GogoanimeIo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_episode_list.*


class EpisodeListFragment : Fragment() {

    enum class EpisodeListFragmentStatus {
        LOADING, LOADED, ERROR, EMPTY
    }

    private var serviceId: String? = null

    private var adapter: EpisodeListAdapter? = null

    private var animeEntry: AnimeEntry? = null

    var currentState = EpisodeListFragmentStatus.LOADING


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (arguments != null) {
            serviceId = arguments.getString("serviceId")
            animeEntry = Gson().fromJson(arguments.getString("animeEntry"), AnimeEntry::class.java)

            if(serviceId == null || animeEntry == null) backToPrevious()

        } else {
            backToPrevious()
        }
        return inflater.inflate(R.layout.fragment_episode_list, container, false)
    }

    private fun backToPrevious() {
        //TODO back to anime list fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(false)

        activity.title = "Select episode"

        adapter = EpisodeListAdapter(this, object : EpisodeListFragmentElementClicked {
            override fun onClicked(episodeEntry: EpisodeEntry) {
                val bundle = Bundle()
                bundle.putString("serviceId", serviceId)
                bundle.putString("animeEntry", Gson().toJson(animeEntry))
                bundle.putString("episodeEntry", Gson().toJson(episodeEntry))
                //val episodeListFragment = EpisodeListFragment()
                //episodeListFragment.arguments = bundle
                //val fm = fragmentManager.beginTransaction()
                //fm.replace(activity.fragmentContainer.id, episodeListFragment)
                //fm.commit()
            }
        })

        episodeListErrorText.text = ""
        episodeRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        episodeRecyclerView.itemAnimator = DefaultItemAnimator()
        episodeRecyclerView.adapter = adapter
        episodeListErrorButtonRetry.setOnClickListener {
            episodeListErrorText.text = ""
            updateData()
        }

        updateData()
    }

    private fun updateData() {
        setState(EpisodeListFragmentStatus.LOADING)
        if(animeEntry?.url == null) {
            backToPrevious()
        } else {
            GogoanimeIo.getInstance().updateEpisodeList(animeEntry?.url, object : EpisodeUpdateDataCallback {
                override fun onComplete(episodeList: ArrayList<EpisodeEntry>) {
                    activity?.runOnUiThread {
                        adapter?.addAll(episodeList)
                        setState(EpisodeListFragmentStatus.LOADED)
                    }
                }

                override fun onError(message: String) {
                    episodeListErrorText.text = message
                    setState(EpisodeListFragmentStatus.ERROR)
                }
            })
        }
    }

    fun setState(state: EpisodeListFragmentStatus) {
        when (state) {
            EpisodeListFragmentStatus.LOADING -> {
                currentState = EpisodeListFragmentStatus.LOADING
                episodeListLoading.visibility = RelativeLayout.VISIBLE

                episodeListEmpty.visibility = RelativeLayout.INVISIBLE
                episodeRecyclerView.visibility = RecyclerView.INVISIBLE
                episodeListError.visibility = RelativeLayout.INVISIBLE
            }
            EpisodeListFragmentStatus.LOADED -> {
                currentState = EpisodeListFragmentStatus.LOADED
                episodeRecyclerView.visibility = RecyclerView.VISIBLE

                episodeListEmpty.visibility = RelativeLayout.INVISIBLE
                episodeListLoading.visibility = RelativeLayout.INVISIBLE
                episodeListError.visibility = RelativeLayout.INVISIBLE
            }
            EpisodeListFragmentStatus.EMPTY -> {
                currentState = EpisodeListFragmentStatus.EMPTY
                episodeListEmpty.visibility = RelativeLayout.VISIBLE

                episodeRecyclerView.visibility = RecyclerView.INVISIBLE
                episodeListLoading.visibility = RelativeLayout.INVISIBLE
                episodeListError.visibility = RelativeLayout.INVISIBLE
            }
            EpisodeListFragmentStatus.ERROR -> {
                currentState = EpisodeListFragmentStatus.ERROR
                episodeListError.visibility = RelativeLayout.VISIBLE

                episodeListEmpty.visibility = RelativeLayout.INVISIBLE
                episodeRecyclerView.visibility = RecyclerView.INVISIBLE
                episodeListLoading.visibility = RelativeLayout.INVISIBLE
            }
        }
    }
}
