package org.wit.placemark.views.settings

import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.models.PlacemarkStore
import org.wit.placemark.models.firebase.PlacemarkFireStore
import org.wit.placemark.views.BasePresenter
import org.wit.placemark.views.BaseView
import org.wit.placemark.views.VIEW
import org.wit.placemark.views.placemarklist.PlacemarkAdapter

class settingspresenter(view: BaseView) : BasePresenter(view) {

    fun countsites (): Int {
        return app.placemarks.findAll().size
    }
    fun countifchecked(): Int{

        var i:Int = 0
        app.placemarks.findAll().forEach {
            if(it.visited == true){
                i++
            }
        }
        return i
    }

}