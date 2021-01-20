package org.wit.placemark.views.images

import android.annotation.SuppressLint
import android.content.Intent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.placemark.helpers.checkLocationPermissions
import org.wit.placemark.helpers.createDefaultLocationRequest
import org.wit.placemark.helpers.isPermissionGranted
import org.wit.placemark.helpers.showImagePicker
import org.wit.placemark.models.Location
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.*

class ImagePresenter(view: BaseView) : BasePresenter(view) {


    var placemark = PlacemarkModel()
    var edit = false;

    init {
        if (view.intent.hasExtra("placemark_edit")) {
            edit = true
            placemark = view.intent.extras?.getParcelable<PlacemarkModel>("placemark_edit")!!
            view.showPlacemark(placemark)
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

    fun doSelectImage() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST)
        }
    }



    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                if(placemark.images.size < 4)
                placemark.images.add(data.data.toString())
                view?.showPlacemark(placemark)
                }

            }

        }
    }

