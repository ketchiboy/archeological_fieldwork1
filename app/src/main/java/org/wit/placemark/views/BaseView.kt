package org.wit.placemark.views

import android.content.Intent

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.wit.placemark.models.Location

import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.favorites.FavoriteListView
import org.wit.placemark.views.location.EditLocationView
import org.wit.placemark.views.map.PlacemarkMapView
import org.wit.placemark.views.placemark.PlacemarkView
import org.wit.placemark.views.placemarklist.PlacemarkListView
import org.wit.placemark.views.login.LoginView
import org.wit.placemark.views.search.SearchListView
import org.wit.placemark.views.settings.settingsview
import org.wit.placemark.views.splashview.splashview

val IMAGE_REQUEST = 5
val LOCATION_REQUEST = 6
val IMAGE_REQUEST_ADAPTER_CLICK_1 = 0
val IMAGE_REQUEST_ADAPTER_CLICK_2 = 1
val IMAGE_REQUEST_ADAPTER_CLICK_3 = 2
val IMAGE_REQUEST_ADAPTER_CLICK_4 = 3

enum class VIEW {
  LOCATION, PLACEMARK, MAPS, LIST, LOGIN, SPLASH, SETTINGS, FAVORITE, SEARCH
}

open abstract class BaseView() : AppCompatActivity(), AnkoLogger {

  var basePresenter: BasePresenter? = null







  fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null) {
    var intent = Intent(this, PlacemarkListView::class.java)
    when (view) {
      VIEW.LOCATION -> intent = Intent(this, EditLocationView::class.java)
      VIEW.PLACEMARK -> intent = Intent(this, PlacemarkView::class.java)
      VIEW.MAPS -> intent = Intent(this, PlacemarkMapView::class.java)
      VIEW.LIST -> intent = Intent(this, PlacemarkListView::class.java)
      VIEW.LOGIN -> intent = Intent(this, LoginView::class.java)
      VIEW.SPLASH -> intent = Intent(this, splashview::class.java)
      VIEW.SETTINGS -> intent = Intent(this, settingsview::class.java)
      VIEW.FAVORITE -> intent = Intent(this, FavoriteListView::class.java)
      VIEW.SEARCH -> intent = Intent(this, SearchListView::class.java)
    }
    if (key != "") {
      intent.putExtra(key, value)
    }

    startActivityForResult(intent, code)

  }

  fun init(toolbar: Toolbar, upEnabled: Boolean) {
    toolbar.title = title
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(upEnabled)
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
      toolbar.title = "${title}: ${user.email}"
    }
  }

  fun initPresenter(presenter: BasePresenter): BasePresenter {
    basePresenter = presenter
    return presenter
  }

  fun init(toolbar: Toolbar) {
    toolbar.title = title
    setSupportActionBar(toolbar)
  }


  override fun onDestroy() {
    basePresenter?.onDestroy()
    super.onDestroy()
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (data != null) {
      basePresenter?.doActivityResult(requestCode, resultCode, data)
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  open fun showPlacemark(placemark: PlacemarkModel) {}
  open fun showPlacemarks(placemarks: List<PlacemarkModel>) {}
  open fun showLocation(location : Location) {}
  open fun showProgress() {}
  open fun hideProgress() {}
}