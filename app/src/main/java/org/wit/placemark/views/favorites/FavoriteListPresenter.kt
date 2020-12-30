package org.wit.placemark.views.favorites

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BasePresenter
import org.wit.placemark.views.BaseView
import org.wit.placemark.views.VIEW


class FavoriteListPresenter(view: BaseView) : BasePresenter(view) {


    fun doEditPlacemark(placemark: PlacemarkModel) {
        view?.navigateTo(VIEW.PLACEMARK, 0, "placemark_edit", placemark)
    }



    fun loadfavoritePlacemarks() {
        val favoriteplacemarks = ArrayList<PlacemarkModel>()
        doAsync {
            val placemarks = app.placemarks.findfavorites().forEach{
                if(it.favorite == true){
                    favoriteplacemarks.add(it)
                }

            }
            uiThread {
                view?.showPlacemarks(favoriteplacemarks)
            }
        }
    }



}