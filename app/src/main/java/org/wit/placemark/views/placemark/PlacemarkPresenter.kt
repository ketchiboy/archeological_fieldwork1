package org.wit.placemark.views.placemark

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.wit.placemark.R
import org.wit.placemark.helpers.checkLocationPermissions
import org.wit.placemark.helpers.createDefaultLocationRequest
import org.wit.placemark.helpers.isPermissionGranted
import org.wit.placemark.helpers.showImagePicker
import org.wit.placemark.models.Location
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.*
import kotlin.math.absoluteValue
import android.widget.Toast.makeText as makeText1

class PlacemarkPresenter(view: BaseView) : BasePresenter(view) {

  var map: GoogleMap? = null
  var placemark = PlacemarkModel()
  var defaultLocation = Location(52.245696, -7.139102, 15f)
  var edit = false;
  var locationManualyChanged = false;
  var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
  val locationRequest = createDefaultLocationRequest()
  var currentlocation = Location(defaultLocation.lat, defaultLocation.lng)

  init {
    if (view.intent.hasExtra("placemark_edit")) {
      edit = true
      placemark = view.intent.extras?.getParcelable<PlacemarkModel>("placemark_edit")!!
      view.showPlacemark(placemark)
    } else {
      if (checkLocationPermissions(view)) {
        doSetCurrentLocation()
      }
    }
  }

  @SuppressLint("MissingPermission")
  fun doSetCurrentLocation() {
    locationService.lastLocation.addOnSuccessListener {
      locationUpdate(Location(it.latitude, it.longitude))
      currentlocation.lat = it.latitude
      currentlocation.lng = it.longitude
    }
  }

  @SuppressLint("MissingPermission")
  fun doResartLocationUpdates() {
    var locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult != null && locationResult.locations != null) {
          val l = locationResult.locations.last()
          if (!locationManualyChanged) {
            locationUpdate(Location(l.latitude, l.longitude))
            currentlocation.lat = l.latitude
            currentlocation.lng = l.longitude
          }
        }
      }
    }
    if (!edit) {
      locationService.requestLocationUpdates(locationRequest, locationCallback, null)
    }
  }

  override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (isPermissionGranted(requestCode, grantResults)) {
      doSetCurrentLocation()
    } else {
      locationUpdate(defaultLocation)
    }
  }



  fun cachePlacemark (title: String, description: String, checked: Boolean, time: String, notes: String, rating: Float, favorite: Boolean) {
    placemark.title = title;
    placemark.description = description
    placemark.visited = checked
    placemark.date = time
    placemark.notes = notes
    placemark.rating = rating
    placemark.favorite = favorite
  }

  fun doConfigureMap(m: GoogleMap) {
    map = m
    locationUpdate(placemark.location)
  }

  fun locationUpdate(location: Location) {
    placemark.location = location
    placemark.location.zoom = 15f
    map?.clear()
    map?.uiSettings?.setZoomControlsEnabled(true)
    val options = MarkerOptions().title(placemark.title).position(LatLng(placemark.location.lat, placemark.location.lng))
    map?.addMarker(options)
    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(placemark.location.lat, placemark.location.lng), placemark.location.zoom))
    view?.showLocation(placemark.location)
  }

  fun doAddOrSave(title: String, description: String, checked: Boolean, time: String, notes: String, rating: Float, favorite: Boolean) {
    placemark.title = title
    placemark.description = description
    placemark.visited = checked
    placemark.date = time
    placemark.notes = notes
    placemark.rating = rating
    placemark.favorite = favorite
    doAsync {
      if (edit) {
        app.placemarks.update(placemark)
      } else {
        app.placemarks.create(placemark)
      }
      uiThread {
        view?.finish()
      }
    }
  }

  fun doCancel() {
    view?.finish()
  }

  fun doDelete() {
    doAsync {
      app.placemarks.delete(placemark)
      
      uiThread {
        view?.finish()
      }
    }
  }

  fun doSelectImage(requestCode: Int) {

      view?.let {
        showImagePicker(view!!, requestCode)
      }


  }

  fun doSetLocation() {
    locationManualyChanged = true;
    view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", Location(placemark.location.lat, placemark.location.lng, placemark.location.zoom))
  }

 fun doNavigatetoSite(){

   val Diff: Double = 0.001
   val longitudeDifference = (currentlocation.lng - placemark.location.lng).absoluteValue
   val latitudeDifference = (currentlocation.lat - placemark.location.lat).absoluteValue

   if( latitudeDifference < Diff && longitudeDifference < Diff){
     view?.toast(R.string.samelocation.toString())
     gobacktoList()
   }
   val IntentUriNavigation = Uri.parse("google.navigation:q=${placemark.location.lat},${placemark.location.lng}")
   val mapIntent = Intent(Intent.ACTION_VIEW, IntentUriNavigation)
   mapIntent.setPackage("com.google.android.apps.maps")
   view?.startActivity(mapIntent)


 }

  fun doSendEmail(recipient: String, subject: String, message: String) {

    val mIntent = Intent(Intent.ACTION_SEND)

    mIntent.data = Uri.parse("mailto:")
    mIntent.type = "text/plain"

    mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))

    mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)

    mIntent.putExtra(Intent.EXTRA_TEXT, message)


    try {

      view?.startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
    }
    catch (e: Exception){
      view?.toast(R.string.couldnotsendemail.toString())
    }

  }


  override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    when (requestCode) {
      IMAGE_REQUEST -> {
        if(placemark.images.size <4){
          if(!placemark.images.contains(data.data.toString())){
            placemark.images.add(data.data.toString())
          }

            }

        view?.showPlacemark(placemark)
      }
      LOCATION_REQUEST -> {
        val location = data.extras?.getParcelable<Location>("location")!!
        placemark.location = location
        locationUpdate(location)
      }
      IMAGE_REQUEST_ADAPTER_CLICK_1 -> {
        placemark.images.set(requestCode, data.data.toString())
        view?.showPlacemark(placemark)

      }
      IMAGE_REQUEST_ADAPTER_CLICK_2 -> {
        placemark.images.set(requestCode, data.data.toString())
        view?.showPlacemark(placemark)
      }
      IMAGE_REQUEST_ADAPTER_CLICK_3 -> {
        placemark.images.set(requestCode, data.data.toString())
        view?.showPlacemark(placemark)
      }
      IMAGE_REQUEST_ADAPTER_CLICK_4 -> {
        placemark.images.set(requestCode, data.data.toString())
        view?.showPlacemark(placemark)

      }
      REQUEST_IMAGE_CAPTURE -> {
        if(placemark.images.size == 4)
        placemark.images.add(data.data.toString())
        

      }
    }
  }

}