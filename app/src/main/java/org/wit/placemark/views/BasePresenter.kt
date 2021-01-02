package org.wit.placemark.views

import android.content.Intent
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import org.wit.placemark.main.MainApp

open class BasePresenter(var view: BaseView?) {

  var app: MainApp =  view?.application as MainApp
  var logout : Boolean = false


  open fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
  }

  open fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
  }

  open fun onDestroy() {
    view = null
  }


  fun doLogout() {
    logout = true
    FirebaseAuth.getInstance().signOut()
    app.placemarks.clear()
    view?.navigateTo(VIEW.LOGIN)
  }

  fun doAddPlacemark() {
    view?.navigateTo(VIEW.PLACEMARK)
  }

  fun doShowPlacemarksMap() {
    view?.navigateTo(VIEW.MAPS)
  }

  fun doSettings(){
    view?.navigateTo(VIEW.SETTINGS)
  }

  fun doFavorites(){
    view?.navigateTo(VIEW.FAVORITE)
  }
  fun gobacktoList(){
    view?.navigateTo(VIEW.LIST)
  }



}