package org.wit.placemark.views.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_placemark.*
import kotlinx.android.synthetic.main.activity_placemark_list.*
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.toast
import org.wit.placemark.R
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BaseView
import org.wit.placemark.views.login.LoginView
import org.wit.placemark.views.placemark.PlacemarkPresenter
import org.wit.placemark.views.placemarklist.PlacemarkListPresenter
import org.wit.placemark.views.placemarklist.PlacemarkListener

class settingsview : BaseView(), PlacemarkListener {


    lateinit var presenter: settingspresenter
    var user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        super.init(toolbarSettings, upEnabled = true)


        presenter = initPresenter(settingspresenter(this)) as settingspresenter

        email2.setText("E-Mail Adress: ")
        password2.setText("Password: ")
        numberofsights.setText("Number of sights: " + presenter.countsites().toString())
        numberofvisited.setText("Number of visited sights: " + presenter.countifchecked())


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_add -> presenter.doAddPlacemark()
            R.id.item_map -> presenter.doShowPlacemarksMap()
            R.id.item_logout -> presenter.doLogout()
            R.id.item_save -> {
                if (emailupdate.text.toString().equals(user?.email.toString()) ) {
                    toast("Email wurde nicht geändert")
                } else{
                    user?.updateEmail(emailupdate.text.toString())
                }
                if(passwordupdate.text.toString().isNotEmpty()){
                    user?.updatePassword(passwordupdate.text.toString())
                }
                presenter.doLogout()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPlacemarkClick(placemark: PlacemarkModel) {
    }
}


