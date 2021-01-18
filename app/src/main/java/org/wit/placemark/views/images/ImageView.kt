package org.wit.placemark.views.images

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_images.*
import kotlinx.android.synthetic.main.activity_placemark.*
import kotlinx.android.synthetic.main.activity_placemark.toolbarAdd
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.placemark.R
import org.wit.placemark.models.Location
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BaseView
import org.wit.placemark.views.VIEW
import org.wit.placemark.views.placemark.PlacemarkPresenter
import org.wit.placemark.views.settings.settingspresenter
import java.text.SimpleDateFormat
import java.util.*

class ImageView: BaseView(), AnkoLogger {

    lateinit var presenter: ImagePresenter
    var placemark = PlacemarkModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        presenter = initPresenter(ImagePresenter(this)) as ImagePresenter




        super.init(toolbarAdd, true);

            button.setOnClickListener {

                presenter.doSelectImage()

            }

        }

        override fun showPlacemark(placemark: PlacemarkModel) {
            Glide.with(this).load(placemark.image).into(image1);
            Glide.with(this).load(placemark.image).into(image2);
            Glide.with(this).load(placemark.image).into(image3);
            Glide.with(this).load(placemark.image).into(image4);

            if (placemark.image != null) {
                button.setText(R.string.change_placemark_image)
            }
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
                    presenter.gobacktoList()
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

        }

        override fun onLowMemory() {
            super.onLowMemory()

        }

        override fun onPause() {
            super.onPause()

        }

        override fun onResume() {
            super.onResume()
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)

        }



}
