package com.scientists.happy.botanist.controller

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.scientists.happy.botanist.R
import com.scientists.happy.botanist.data.Plant
import za.co.riggaroo.materialhelptutorial.TutorialItem
import java.util.*

class EditPlantController(activity: AppCompatActivity) : ActivityController(activity) {

    private val mPlantId: String = activity.intent.extras.getString("plant_id")
    private var mPlantName: String? = null
    private var mPhotoNum: Int = 0
    private var mPhotoPointer = -1

    private val mPlantReference = databaseManager.getPlantReference(mPlantId)
    private val mUserPhotosReference = databaseManager.userPhotosReference
    private val mUserStorage = databaseManager.userStorage

    init {
        mPlantReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val plant = dataSnapshot.getValue(Plant::class.java)
                    if (plant != null) {
                        val nameTextView = activity.findViewById<TextView>(R.id.name_text_view)
                        mPlantName = plant.name
                        nameTextView.text = mPlantName
                        mPhotoNum = plant.photoNum
                        mPhotoPointer = plant.photoPointer
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    override fun load() {
        showProgressDialog(activity.getString(R.string.loading_text))
        loadNameSection()
        val grid = activity.findViewById<GridView>(R.id.photo_grid_view)
        grid.emptyView = activity.findViewById(R.id.empty_grid_view)
        populatePhotoGrid(activity)
        hideProgressDialog()
    }

    override fun loadTutorialItems(): ArrayList<TutorialItem>? {
        return null
    }

    /**
     * populate a grid with plant photos
     * @param activity - the current activity
     */
    private fun populatePhotoGrid(activity: Activity) {
        val grid = activity.findViewById<GridView>(R.id.photo_grid_view)
        val emptyGridView = activity.findViewById<TextView>(R.id.empty_grid_view)
        val loadingProgressBar = activity.findViewById<ProgressBar>(R.id.loading_indicator)
        loadingProgressBar.visibility = View.VISIBLE
        if (mUserPhotosReference != null) {
            // An SQL-like hack to retrieve only data with values that matches the query: "plantId*"
            // This is needed to query only images that correspond to the specific plant being edited
            val query = mUserPhotosReference.orderByValue().startAt(mPlantId).endAt(mPlantId + "\uf8ff")
            val adapter = object : FirebaseListAdapter<String>(activity, String::class.java, R.layout.grid_item_photo, query) {
                /**
                 * Populate a photo grid item
                 * @param view - the current view
                 * *
                 * @param photoName - the photo to display
                 * *
                 * @param position - the position
                 */
                override fun populateView(view: View, photoName: String, position: Int) {
                    val profilePhotoRef = mPlantReference.child("profilePhoto")
                    val storageReference = mUserStorage.child(photoName)
                    val picture = view.findViewById<ImageView>(R.id.photo_image_view)
                    val isProfilePicture = BooleanArray(1)
                    Glide.with(activity).using(FirebaseImageLoader()).load(storageReference).dontAnimate()
                            .placeholder(R.drawable.flowey).into(picture)
                    val setButton = view.findViewById<View>(R.id.set_photo_btn)
                    setButton.setOnClickListener {
                        profilePhotoRef.setValue(photoName)
                        notifyDataSetChanged()
                    }
                    view.findViewById<ImageView>(R.id.delete_photo_btn).setOnClickListener {
                        buildDeletePhotoDialog(storageReference,
                                profilePhotoRef, position, isProfilePicture[0]).show()
                    }
                    profilePhotoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val profilePhoto = dataSnapshot.value as String?
                            val isSetIndicator = view.findViewById<View>(R.id.is_set_indicator)
                            isProfilePicture[0] = profilePhoto != null && profilePhoto == photoName
                            if (isProfilePicture[0]) {
                                setButton.visibility = View.GONE
                                isSetIndicator.visibility = View.VISIBLE
                            } else {
                                setButton.visibility = View.VISIBLE
                                isSetIndicator.visibility = View.GONE
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })
                }

                override fun onDataChanged() {
                    super.onDataChanged()
                    val photoNumRef = mPlantReference.child("photoNum")
                    // Keep photoNum up to date. (photoNum is zero-based)
                    photoNumRef.setValue(count - 1)
                }

                /**
                 * Delete Photo dialog
                 * @return Return the dialog window warning user of photo removal
                 */
                private fun buildDeletePhotoDialog(storageReference: StorageReference,
                                                   profilePhotoRef: DatabaseReference, position: Int,
                                                   isProfilePicture: Boolean): AlertDialog {
                    //Instantiate an AlertDialog.Builder with its constructor
                    val builder = AlertDialog.Builder(activity)
                    //Chain together various setter methods to set the dialog characteristics
                    builder.setTitle(R.string.dialog_delete_photo_title).setMessage(R.string.dialog_delete_photo_text)
                    // Add the buttons
                    builder.setPositiveButton(R.string.yes) { _, _ ->
                        storageReference.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                getRef(position).removeValue()
                                if (isProfilePicture) {
                                    profilePhotoRef.setValue("default")
                                }
                                // Keep photoCount up-to-date
                                databaseManager.photoCount = databaseManager.photoCount - 1
                            }
                        }
                    }
                    builder.setNegativeButton(R.string.no) { _, _ -> }
                    // Get the AlertDialog from create()
                    return builder.create()
                }
            }

            // After digging deep, I discovered that Firebase keeps some local information in ".info"
            val connectedRef = databaseManager.userConnectionReference
            connectedRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.value as Boolean
                    if (connected) {
                        emptyGridView.setText(R.string.loading_text)
                        loadingProgressBar.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(activity, R.string.msg_network_error, Toast.LENGTH_SHORT).show()
                        emptyGridView.setText(R.string.msg_network_error)
                        loadingProgressBar.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            mUserPhotosReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    loadingProgressBar.visibility = View.GONE
                    emptyGridView.setText(R.string.grid_photos_empty_text)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    emptyGridView.setText(R.string.msg_unexpected_error)
                    loadingProgressBar.visibility = View.GONE
                }
            })
            grid.adapter = adapter
        }
    }

    private fun loadNameSection() {
        activity.findViewById<Button>(R.id.rename_button).setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            val dialogView = View.inflate(activity, R.layout.dialog_name_input, null)
            val inputEditText = dialogView.findViewById<EditText>(R.id.name_edit_text)
            inputEditText.setText(mPlantName)
            builder.setView(dialogView).setTitle(R.string.rename)
            // Set up the buttons
            builder.setPositiveButton(R.string.mdtp_ok) { _, _ ->
                mPlantReference.child("name").setValue(inputEditText.text.toString())
            }
            builder.setNegativeButton(R.string.mdtp_cancel) { dialog, _ ->
                dialog.cancel()
            }
            builder.create().show()
        }
    }
}
