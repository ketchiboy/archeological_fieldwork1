package org.wit.placemark.views.placemark

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.activity_placemark.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.placemark.R
import org.wit.placemark.helpers.readImageFromPath
import org.wit.placemark.models.Location
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BaseView
import org.wit.placemark.views.VIEW
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PlacemarkView : BaseView(), AnkoLogger {

  lateinit var presenter: PlacemarkPresenter
  var placemark = PlacemarkModel()
  lateinit var map: GoogleMap
  var checked: Boolean = false

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_placemark)
    presenter = initPresenter (PlacemarkPresenter(this)) as PlacemarkPresenter
    checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
      if (isChecked) {
        placemark.visited = true
        if(placemark.date.isEmpty()){
          val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
          val currentDate = sdf.format(Date())

          placemark.date = currentDate.toString()
        }
      }else{
        placemark.visited = false
        placemark.date = ""
      }
      if(placemark.date == ""){
        visiteddate.text= ""
      }else{
        visiteddate.text = placemark.date
      }

    }

    simpleRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
      placemark.rating = simpleRatingBar.rating
    }

    favorite.setOnCheckedChangeListener { buttonView, isChecked ->

      if (isChecked) {
        placemark.favorite = true
      }else{
        placemark.favorite = false
      }
    }


    super.init(toolbarAdd, true);

    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync {
      map = it
      presenter.doConfigureMap(map)
      it.setOnMapClickListener { presenter.doSetLocation() }
    }



    chooseImage.setOnClickListener {
      presenter.cachePlacemark(placemarkTitle.text.toString(), description.text.toString(), placemark.visited, placemark.date, additionalnotes.text.toString(), simpleRatingBar.rating, placemark.favorite)
      presenter.doSelectImage()
    }
  }

  override fun showPlacemark(placemark: PlacemarkModel) {
    checkBox.isChecked = placemark.visited
    if (placemarkTitle.text.isEmpty()) placemarkTitle.setText(placemark.title)
    if (description.text.isEmpty())  description.setText(placemark.description)
    visiteddate.text = placemark.date
    if(additionalnotes.text.isEmpty()) additionalnotes.setText(placemark.notes)
    if(simpleRatingBar.rating == 0F) simpleRatingBar.rating = placemark.rating
    favorite.isChecked = placemark.favorite

    Glide.with(this).load(placemark.image).into(placemarkImage);

    if (placemark.image != null) {
      chooseImage.setText(R.string.change_placemark_image)
    }
    this.showLocation(placemark.location)
  }

  override fun showLocation (loc : Location) {
    lat.setText("%.6f".format(loc.lat))
    lng.setText("%.6f".format(loc.lng))

  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_placemark, menu)
    if (presenter.edit) menu.getItem(0).setVisible(true)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item?.itemId) {
      R.id.item_delete -> {
        presenter.doDelete()
      }
      R.id.item_save -> {
        if (placemarkTitle.text.toString().isEmpty()) {
          toast(R.string.enter_placemark_title)
        } else {
          presenter.doAddOrSave(placemarkTitle.text.toString(), description.text.toString(), checkBox.isChecked, visiteddate.text.toString(), additionalnotes.text.toString(), simpleRatingBar.rating, favorite.isChecked)
        }
      }
      R.id.item_cancel -> {
        finish()
      }
      R.id.item_settings -> {
        navigateTo(VIEW.SETTINGS)
      }
      R.id.item_favorites -> presenter.doFavorites()

    }
    return super.onOptionsItemSelected(item)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (data != null) {
      presenter.doActivityResult(requestCode, resultCode, data)
    }
  }

  override fun onBackPressed() {
    presenter.doCancel()
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onResume() {
    super.onResume()
    mapView.onResume()
    presenter.doResartLocationUpdates()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
}