package org.wit.placemark.views.search

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_placemark_list.*
import kotlinx.android.synthetic.main.activity_placemark_list.recyclerView
import kotlinx.android.synthetic.main.activity_placemark_list.toolbar
import kotlinx.android.synthetic.main.activity_searchview.*
import org.wit.placemark.R
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BaseView
import org.wit.placemark.views.favorites.FavoriteListPresenter
import org.wit.placemark.views.placemarklist.PlacemarkAdapter
import org.wit.placemark.views.placemarklist.PlacemarkListPresenter
import org.wit.placemark.views.placemarklist.PlacemarkListener

class SearchListView :  BaseView(), PlacemarkListener {

    lateinit var presenter: SearchListPresenter
    var searchstring: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchview)
        setSupportActionBar(toolbar)
        super.init(toolbar, false)

        presenter = initPresenter(SearchListPresenter(this)) as SearchListPresenter

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        presenter.loadallPlacemarks()

        buttonsearch.setOnClickListener {
            searchstring = searchforwords.text.toString()
            presenter.loadsearchPlacemarks(searchstring)
        }




    }

    override fun showPlacemarks(placemarks: List<PlacemarkModel>) {
        recyclerView.adapter = PlacemarkAdapter(placemarks, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_add -> presenter.doAddPlacemark()
            R.id.item_map -> presenter.doShowPlacemarksMap()
            R.id.item_logout ->presenter.doLogout()
            R.id.item_settings -> presenter.doSettings()
            R.id.item_up -> recyclerView.scrollToPosition(0)
            R.id.item_allsites -> presenter.gobacktoList()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPlacemarkClick(placemark: PlacemarkModel) {
        presenter.doEditPlacemark(placemark)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadsearchPlacemarks(searchforwords.text.toString())
        super.onActivityResult(requestCode, resultCode, data)
    }
}