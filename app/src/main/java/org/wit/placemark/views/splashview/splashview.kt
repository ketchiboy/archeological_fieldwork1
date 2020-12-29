package org.wit.placemark.views.splashview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_placemark_list.*
import org.wit.placemark.R
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BaseView
import org.wit.placemark.views.VIEW
import org.wit.placemark.views.placemarklist.PlacemarkListPresenter
import org.wit.placemark.views.placemarklist.PlacemarkListener
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class splashview :  BaseView(), PlacemarkListener {

    lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashview)
        presenter = initPresenter(SplashPresenter(this)) as SplashPresenter
        val backgroundExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        backgroundExecutor.schedule({
            navigateTo(VIEW.LOGIN)
        }, 3, TimeUnit.SECONDS)
    }

    override fun onPlacemarkClick(placemark: PlacemarkModel) {

    }


}
