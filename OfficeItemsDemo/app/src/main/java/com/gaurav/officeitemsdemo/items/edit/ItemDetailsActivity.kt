package com.gaurav.officeitemsdemo.items.edit

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.gaurav.officeitemsdemo.MainActivity
import com.gaurav.officeitemsdemo.R
import com.gaurav.officeitemsdemo.data.SqlLiteDbHelper
import com.gaurav.officeitemsdemo.items.IFragmentInteractionListener
import com.gaurav.officeitemsdemo.model.ListItemModel
import com.gaurav.officeitemsdemo.utils.GeneralUtils
import com.gaurav.officeitemsdemo.utils.GeneralUtils.openFragment
import com.gaurav.officeitemsdemo.utils.ImageUtils
import com.gaurav.officeitemsdemo.utils.MPermissionChecker
import com.gaurav.officeitemsdemo.utils.SelectImageDialog
import com.squareup.picasso.Picasso
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.create_item.*
import java.io.File


class ItemDetailsActivity : AppCompatActivity(), IFragmentInteractionListener {

    private val TAG = "ItemDetailsActivity"
    private lateinit var dbHelper : SqlLiteDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item_details)

        GeneralUtils.setToolbarTitle(this@ItemDetailsActivity, "Edit Item Details")
        GeneralUtils.setToolbarBackButton(this@ItemDetailsActivity)

        // Get list item id from intent extra
        val listItemId = intent.getIntExtra(GeneralUtils.BUNDLE_ITEM_ID, -1)

        // Initialize db helper class
        dbHelper = SqlLiteDbHelper(this)

        // Get the item details for the listItemId
        val cursor = dbHelper.getItemById(listItemId)
        Log.d(Fabric.TAG, "Item Id :  $listItemId, Cursor : $cursor")

        try {
            if (cursor.moveToFirst()) {
                Log.d(Fabric.TAG, "Item Id :  $listItemId, Cursor position : ${cursor.position}")

                val name = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_NAME))
                val description = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_DESCRIPTION))
                val imagePath = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_IMAGE_PATH))
                val cost = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_COST))
                val location = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_LOCATION))

                // Add item details to the list
                val itemList = ListItemModel(listItemId.toString(), name, description, imagePath, location, cost)
                openFragment(this, R.id.edit_item_fragment_container, DisplayItemDetailsFragment.newInstance(itemList))
            }
        } finally {
            if (cursor != null && !cursor.isClosed)
                cursor.close()
        }
    }


    override fun onFragmentInteraction(viewId: Int, listItem: ListItemModel) {
        when (viewId) {
            R.id.buttonEditItem -> {
                openFragment(this, R.id.edit_item_fragment_container, EditItemFragment.newInstance(listItem))
            }

            R.id.buttonAddItem -> {

                // Check if image was changed else use from same image
                val itemImagePath = if(imagePath == null) listItem.image else imagePath

                // Update the list item details
                if(dbHelper.updateItem((listItem.id.toInt()), listItem.name, listItem.description, itemImagePath,
                        listItem.cost.toInt(), listItem.location)) {
                    Toast.makeText(applicationContext, "Item ${(listItem.id.toInt())} " +
                            "updated successfully", Toast.LENGTH_SHORT).show()

                    Log.d(TAG, "Item updated routing to main activity !!!")

                } else {
                    Toast.makeText(applicationContext, "Item added successfully", Toast.LENGTH_SHORT).show()
                }

                // Route back to Item List View
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onFragmentInteraction(viewId: Int) {
        when(viewId) {
            R.id.imageViewAddItem -> {
                val fm = fragmentManager
                val dialogFragment = SelectImageDialog(selectImageItemSrcListener)
                dialogFragment.show(fm, "SelectImageDialogFragment")
            }
        }
    }

    private var selectedPhotoPath: String? = null
    private var imagePath: String? = null

    // Listener which handle the mode (from camera/gallery) selected for adding item image
    var selectImageItemSrcListener = { openCamera: Boolean ->
        if (openCamera) {
            if (MPermissionChecker.grantCameraAccess(this, ImageUtils.REQUEST_CAMERA))
                selectedPhotoPath = ImageUtils.intentCamera(this, ImageUtils.CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE)
        } else {
            if (MPermissionChecker.grantGalleryAccess(this, ImageUtils.REQUEST_GALLERY))
                ImageUtils.intentGallery(this, ImageUtils.SELECT_IMAGE_FROM_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            // Handle callback from image selected from Gallery/Camera
            if (requestCode == ImageUtils.CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE ||
                    requestCode == ImageUtils.SELECT_IMAGE_FROM_GALLERY) {
                var imageUri: Uri? = null
                // get image path
                when (requestCode) {
                    ImageUtils.CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE -> imagePath = selectedPhotoPath

                    ImageUtils.SELECT_IMAGE_FROM_GALLERY -> try {
                        imageUri = intent?.data
                        imagePath = GeneralUtils.getRealPathFromURI(this, imageUri!!)
                        Crashlytics.log(Log.DEBUG, TAG, "Image Path : " + imagePath!!)

                    } catch (e: Exception) {
                        Crashlytics.log(Log.ERROR, TAG, "Caught Exception message : " + e.message)
                        imagePath = GeneralUtils.getRealPathFromURI_API19(this, imageUri!!)
                        Crashlytics.log(Log.DEBUG, TAG, "Image Path :  $imagePath")
                    }
                }

                if (imagePath != null) {
                    GeneralUtils.fixImageOrientation(imagePath.toString())
                    Picasso.get()
                            .load(File(imagePath))
                            .resize(GeneralUtils.IMG_WIDTH * 2, GeneralUtils.IMG_HEIGHT)
                            .centerCrop().into(imageViewAddItem)
                } else {
                    // TODO :: Handle image path is not null
                }
            }
        }
    }

    // Handle runtime permission callback
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ImageUtils.REQUEST_CAMERA ->
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectedPhotoPath = ImageUtils.intentCamera(this, ImageUtils.CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE)
                } else {
                    Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show()
                }
            ImageUtils.REQUEST_GALLERY -> ImageUtils.intentGallery(this, ImageUtils.SELECT_IMAGE_FROM_GALLERY)
        }
    }

}
