package org.wit.placemark.views.placemark



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
import java.text.SimpleDateFormat
import java.util.*

interface HillfortClickListener {
    fun onImageClick(image: String, index: Int)
}

class HillfortAdapter constructor(
        private var images: List<String>,
        private val listener: HillfortClickListener
) : RecyclerView.Adapter<HillfortAdapter.MainHolder>() {

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
        val image = images[holder.adapterPosition]
        holder.bind(image, holder.adapterPosition, listener)
    }



    override fun getItemCount(): Int {
        if (images.size > 4){
            return 4
        }
        return images.size
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(image: String, index:Int, listener: HillfortClickListener) {
            Glide.with(itemView.context).load(image).into(itemView.imageIcon);
            itemView.setOnClickListener { listener.onImageClick(image, index) }
        }
    }
}