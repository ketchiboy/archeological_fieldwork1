package org.wit.placemark.views.placemark

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.activity_placemark.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.placemark.BuildConfig
import org.wit.placemark.R
import org.wit.placemark.models.Location
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


class PlacemarkView : BaseView(), AnkoLogger, HillfortClickListener {

  lateinit var presenter: PlacemarkPresenter
  var placemark = PlacemarkModel()
  lateinit var map: GoogleMap
  lateinit var currentPhotoPath: String



  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_placemark)

    val layoutManager = LinearLayoutManager(this)
    recyclerView2.layoutManager = layoutManager

    presenter = initPresenter(PlacemarkPresenter(this)) as PlacemarkPresenter
    super.init(toolbarAdd, true);

    checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
      if (isChecked) {
        placemark.visited = true
        if (placemark.date.isEmpty()) {
          val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
          val currentDate = sdf.format(Date())

          placemark.date = currentDate.toString()
        }
      } else {
        placemark.visited = false
        placemark.date = ""
      }
      if (placemark.date == "") {
        visiteddate.text = ""
      } else {
        visiteddate.text = placemark.date
      }

    }

    simpleRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
      placemark.rating = simpleRatingBar.rating
    }


    favorite.setOnCheckedChangeListener { buttonView, isChecked ->

      if (isChecked) {
        placemark.favorite = true
      } else {
        placemark.favorite = false
      }
    }

    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync {
      map = it
      presenter.doConfigureMap(map)
      it.setOnMapClickListener { presenter.doSetLocation() }
    }




    chooseImage.setOnClickListener {
      presenter.cachePlacemark(placemarkTitle.text.toString(), description.text.toString(), checkBox.isChecked, visiteddate.text.toString(), additionalnotes.text.toString(), simpleRatingBar.rating, favorite.isChecked)
      presenter.doSelectImage(IMAGE_REQUEST)

    }



    shareinfo.setOnClickListener {
      val emailadress: String = emailadressshare.text.toString()
      val information: String = "link to coordinates: " + "http://maps.google.com?q=" + lat.text.toString() + "," + lng.text.toString()
      presenter.doSendEmail(emailadress, placemarkTitle.text.toString() + " " + description.text.toString(), information)

    }

    openCamera.setOnClickListener {
      openCamera()
    }
  }

  fun openCamera(){
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
      intent.resolveActivity(packageManager)?.also {
        val photoFile: File? = try {
          createCapturedPhoto()
        } catch (ex: IOException) {
          // If there is error while creating the File, it will be null
          null
        }
        photoFile?.also {
          val photoURI = FileProvider.getUriForFile(
                  this,
                  "${BuildConfig.APPLICATION_ID}.fileprovider",
                  it
          )
          intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
          startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
      }
    }

  }

  @Throws(IOException::class)
  private fun createCapturedPhoto(): File {
    val timestamp: String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("PHOTO_${timestamp}",".jpg", storageDir).apply {
      currentPhotoPath = absolutePath
    }
  }


  override fun showPlacemark(placemark: PlacemarkModel) {

    recyclerView2.adapter = HillfortAdapter(placemark.images, this)
    recyclerView2.adapter?.notifyDataSetChanged()

    checkBox.isChecked = placemark.visited
    if (placemarkTitle.text.isEmpty()) placemarkTitle.setText(placemark.title)
    if (description.text.isEmpty()) description.setText(placemark.description)
    visiteddate.text = placemark.date
    if (additionalnotes.text.isEmpty()) additionalnotes.setText(placemark.notes)
    if (simpleRatingBar.rating == 0F) simpleRatingBar.rating = placemark.rating
    favorite.isChecked = placemark.favorite

    if (placemark.images.size == 4) {
      chooseImage.visibility = View.INVISIBLE
    }
    this.showLocation(placemark.location)
  }

  override fun showLocation(loc: Location) {
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
      R.id.item_search -> presenter.doSearch()
      R.id.item_navigateto -> presenter.doNavigatetoSite()

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
    checkCameraPermission()
  }
  private fun checkCameraPermission() {
    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
              arrayOf(android.Manifest.permission.CAMERA),
              REQUEST_PERMISSION)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }



  override fun onImageClick(image: String, index: Int) {
    presenter.cachePlacemark(placemarkTitle.text.toString(), description.text.toString(), checkBox.isChecked, visiteddate.text.toString(), additionalnotes.text.toString(), simpleRatingBar.rating, favorite.isChecked)
    presenter.doSelectImage(index)


  }

}