package com.github.pcpl2.animeClient.activities

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.badgeable.secondaryItem
import co.zsmb.materialdrawerkt.draweritems.sectionHeader
import com.github.pcpl2.animeClient.R
import com.github.pcpl2.animeClient.fragments.AnimeListFragment
import com.github.pcpl2.animeClient.managers.CacheManager
import com.mikepenz.materialdrawer.Drawer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by patry on 24.01.2018.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var drawer: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        CacheManager.getInstance().init(this)

        drawer = drawer {
            toolbar = customToolbar
            closeOnClick = true

            primaryItem("Settings") {
                selectable = false
                onClick { _ ->
                    false
                }
            }

            sectionHeader("Services")

            secondaryItem("gogoanime.io") {
                val bundle = Bundle()
                bundle.putString("serviceId", "gogoanimeIo")
                val animeListFragment = AnimeListFragment()
                animeListFragment.arguments = bundle
                replaceFragment(animeListFragment)
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().replace(fragmentContainer.id, fragment).commit()
    }
}
