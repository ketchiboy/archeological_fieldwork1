package org.wit.placemark.views.placemarklist

import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BasePresenter
import org.wit.placemark.views.BaseView
import org.wit.placemark.views.VIEW

class PlacemarkListPresenter(view: BaseView) : BasePresenter(view) {


  fun doEditPlacemark(placemark: PlacemarkModel) {
    view?.navigateTo(VIEW.PLACEMARK, 0, "placemark_edit", placemark)
  }



  fun loadPlacemarks() {
    doAsync {
      val placemarks = app.placemarks.findAll()
      uiThread {
        view?.showPlacemarks(placemarks)
      }
    }
  }


}