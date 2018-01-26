package com.github.pcpl2.animeClient.fragments

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.*
import com.github.pcpl2.animeClient.R
import com.github.pcpl2.animeClient.callbacks.animeUpdateDataCallback
import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.services.GogoanimeIo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers



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



        GogoanimeIo.getInstance().updateAnimeList(object : animeUpdateDataCallback {
            override fun onComplete(animeList: ArrayList<AnimeEntry>) {
                for (anime in animeList) {
                    Log.i("AnimeListFragment", anime.toString())
                }
            }

            override fun onError(message: String) {
                Log.e("AnimeListFragment", message)
            }
        })
    }
}