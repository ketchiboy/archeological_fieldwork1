package org.wit.placemark.views.search


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_placemark.*
import kotlinx.android.synthetic.main.card_placemark.view.*
import org.wit.placemark.R
import org.wit.placemark.helpers.readImageFromPath
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.placemarklist.PlacemarkListener
import java.text.SimpleDateFormat
import java.util.*

interface PlacemarkListener {
    fun onPlacemarkClick(placemark: PlacemarkModel)
}

class SearchAdapter constructor(
        private var placemarks: List<PlacemarkModel>,
        private val listener: PlacemarkListener
) : RecyclerView.Adapter<SearchAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
                LayoutInflater.from(parent?.context).inflate(
                        R.layout.card_placemark,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val placemark = placemarks[holder.adapterPosition]

        holder.bind(placemark, listener)
    }



    override fun getItemCount(): Int = placemarks.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(placemark: PlacemarkModel, listener: PlacemarkListener) {

            itemView.latitude.setText("%.4f".format(placemark.location.lat))
            itemView.longitude.setText("%.4f".format(placemark.location.lng))
            itemView.placemarkTitle.text = placemark.title
            itemView.description.text = placemark.description
            itemView.checkBox2.isChecked = placemark.visited
            itemView.checkBox2.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    placemark.visited = true
                    if (placemark.date == "") {
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
                        val currentDate = sdf.format(Date())

                        placemark.date = currentDate.toString()
                    }

                } else {
                    placemark.visited = false
                    placemark.date = ""
                }
            }
            Glide.with(itemView.context).load(placemark.image).into(itemView.imageIcon);
            itemView.setOnClickListener { listener.onPlacemarkClick(placemark) }
        }
    }

}