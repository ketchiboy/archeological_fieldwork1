package org.wit.placemark.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
@Entity
data class PlacemarkModel(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                          var fbId: String ="",
                          var title: String = "",
                          var description: String = "",
                          var image: String = "",
                          var images: List<String> = ArrayList<String>(),
                          var visited: Boolean = false,
                          var date: String = "",
                          var notes: String="",
                          var rating: Float = 0F,
                          var favorite: Boolean = false,
                          @Embedded var location: Location = Location()) : Parcelable
@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable

