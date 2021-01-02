package org.wit.placemark.views.search

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BasePresenter
import org.wit.placemark.views.BaseView
import org.wit.placemark.views.VIEW


class SearchListPresenter(view: BaseView) : BasePresenter(view) {


    fun doEditPlacemark(placemark: PlacemarkModel) {
        view?.navigateTo(VIEW.PLACEMARK, 0, "placemark_edit", placemark)
    }

    fun loadallPlacemarks() {
        doAsync {
            val placemarks = app.placemarks.findAll()
            uiThread {
                view?.showPlacemarks(placemarks)
            }
        }
    }



    fun loadsearchPlacemarks(word: String) {
        val searchplacemarks = ArrayList<PlacemarkModel>()
        doAsync {
           val placemarks = app.placemarks.findAll().forEach{
                if(it.description == "hallofg"){
                    searchplacemarks.add(it)
                }
                else if(word.toString() in it.description.toString()){
                    searchplacemarks.add(it)
                }else if(word.toString() in it.notes.toString()){
                    searchplacemarks.add(it)
                }else if(word.toString() in it.title.toString()){
                    searchplacemarks.add(it)
                }

            }
            uiThread {
                view?.showPlacemarks(searchplacemarks)
            }
        }
    }



}