package org.wit.placemark.room

import androidx.room.*
import org.wit.placemark.models.PlacemarkModel

@Dao
interface PlacemarkDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun create(placemark: PlacemarkModel)

  @Query("SELECT * FROM PlacemarkModel")
  fun findAll(): List<PlacemarkModel>

  @Query("SELECT * FROM PlacemarkModel where favorite = '1'")
  fun findfavorites():List<PlacemarkModel>


  @Query("select * from PlacemarkModel where id = :id")
  fun findById(id: Long): PlacemarkModel

  @Update
  fun update(placemark: PlacemarkModel)

  @Delete
  fun deletePlacemark(placemark: PlacemarkModel)
}