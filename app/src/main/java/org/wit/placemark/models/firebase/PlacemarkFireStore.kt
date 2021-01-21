package org.wit.placemark.models.firebase

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.jetbrains.anko.AnkoLogger
import org.wit.placemark.helpers.readImageFromPath
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.models.PlacemarkStore
import java.io.ByteArrayOutputStream
import java.io.File

class PlacemarkFireStore(val context: Context) : PlacemarkStore, AnkoLogger {

    val placemarks = ArrayList<PlacemarkModel>()
    lateinit var userId: String
    lateinit var db: DatabaseReference
    lateinit var st: StorageReference



    override fun findAll(): List<PlacemarkModel> {
        return placemarks
    }

    override fun findfavorites(): List<PlacemarkModel> {
        return placemarks
    }


    override fun findById(id: Long): PlacemarkModel? {
        val foundPlacemark: PlacemarkModel? = placemarks.find { p -> p.id == id }
        return foundPlacemark
    }

    override fun create(placemark: PlacemarkModel) {
        val key = db.child("users").child(userId).child("placemarks").push().key
        key?.let {
            placemark.fbId = key
            placemarks.add(placemark)
            db.child("users").child(userId).child("placemarks").child(key).setValue(placemark)


            var index: Int =0
            placemark.images.forEach {
                updateImage(placemark,placemark.images.get(index), index) }
            index++
        }
    }

    override fun update(placemark: PlacemarkModel) {
        var foundPlacemark: PlacemarkModel? = placemarks.find { p -> p.fbId == placemark.fbId }
        if (foundPlacemark != null) {
            foundPlacemark.title = placemark.title
            foundPlacemark.description = placemark.description
            foundPlacemark.image = placemark.image
            foundPlacemark.images = placemark.images
            foundPlacemark.location = placemark.location
            foundPlacemark.visited = placemark.visited
            foundPlacemark.date = placemark.date
            foundPlacemark.notes = placemark.notes
            foundPlacemark.rating = placemark.rating
            foundPlacemark.favorite = placemark.favorite


            db.child("users").child(userId).child("placemarks").child(placemark.fbId).setValue(placemark)

            var index:Int =0
            placemark.images.forEach{
                if((it.length) > 0 && (it[0] != 'h')){
                    updateImage(placemark, placemark.images.get(index), index)
                }
                index++
            }

        }
    }

        override fun delete(placemark: PlacemarkModel) {
            db.child("users").child(userId).child("placemarks").child(placemark.fbId).removeValue()
            placemarks.remove(placemark)
        }

        override fun clear() {
            placemarks.clear()
        }

        fun updateImage(placemark: PlacemarkModel, image: String, index: Int) {
            if(index < placemark.images.size){
                if(image != ""){
                    val fileName = File(image)
                    val imageName = fileName.getName()

                    var imageRef = st.child(userId + '/' + imageName)
                    val baos = ByteArrayOutputStream()
                    val bitmap = readImageFromPath(context, image)

                    bitmap?.let {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        val uploadTask = imageRef.putBytes(data)
                        uploadTask.addOnFailureListener {
                            println(it.message)
                        }.addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                                if(!placemark.images.contains(it.toString())){
                                    placemark.images.set(index, it.toString())
                                }

                                db.child("users").child(userId).child("placemarks").child(placemark.fbId).setValue(placemark)
                            }
                        }
                    }
                }



            }

            }
           /* while (i < placemark.images.size){
                if (placemark.images.get(i) != "") {
                    val fileName = File(placemark.images.get(i))
                    val imageName = fileName.getName()

                    var imageRef = st.child(userId + '/' + imageName)
                    val baos = ByteArrayOutputStream()
                    val bitmap = readImageFromPath(context, placemark.images.get(i))

                    bitmap?.let {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        val uploadTask = imageRef.putBytes(data)
                        uploadTask.addOnFailureListener {
                            println(it.message)
                        }.addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                                if(!placemark.images.contains(it.toString())){
                                    placemark.images.add(it.toString())
                                }

                                db.child("users").child(userId).child("placemarks").child(placemark.fbId).setValue(placemark)
                            }
                        }
                    }
                }
                i++
            } */


        fun fetchPlacemarks(placemarksReady: () -> Unit) {

            val valueEventListener = object : ValueEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot!!.children.mapNotNullTo(placemarks) { it.getValue<PlacemarkModel>(PlacemarkModel::class.java) }
                    placemarksReady()
                }
            }


            db = FirebaseDatabase.getInstance().reference

            userId = FirebaseAuth.getInstance().currentUser!!.uid
            st = FirebaseStorage.getInstance().reference
            placemarks.clear()
            db.child("users").child(userId).child("placemarks").addListenerForSingleValueEvent(valueEventListener)

        }
    }