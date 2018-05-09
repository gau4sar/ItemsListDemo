package com.gaurav.officeitemsdemo.mvp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.gaurav.officeitemsdemo.R
import com.gaurav.officeitemsdemo.mvp.data.SqlLiteDbHelper
import com.gaurav.officeitemsdemo.utils.GeneralUtils
import com.gaurav.officeitemsdemo.utils.ImageUtils
import com.gaurav.officeitemsdemo.utils.MPermissionChecker
import com.gaurav.officeitemsdemo.utils.SelectImageDialog
import com.gaurav.officeitemsdemo.mvp.activities.view.IAddItemView
import com.gaurav.officeitemsdemo.mvp.model.AddItemModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.create_item.*
import java.io.File

class AddItemActivity : AppCompatActivity(), View.OnClickListener, IAddItemView {

    override fun validateItem() {
        Toast.makeText(applicationContext, "Please enter all details", Toast.LENGTH_SHORT).show()
    }

    override fun addItemSuccess() {
        Toast.makeText(applicationContext, "Item added successfully", Toast.LENGTH_SHORT).show()

        // Route back to Item List View
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun addItemError() {
        Toast.makeText(applicationContext, "Error : Couldn't add item", Toast.LENGTH_LONG).show()
    }

    override fun onClick(view: View?) {
        Log.d(TAG, "Item details : ${editTextName.text} ${editTextDescription.text} $imagePath, ${editTextLocation.text} ${editTextCost.text}")

        addItemModel.addItem(editTextName.text.toString(), editTextDescription.text.toString(),
                imagePath, editTextLocation.text.toString(), editTextCost.text.toString())
    }

    private val TAG = "AddItemActivity"
    private var selectedPhotoPath: String? = null

    private lateinit var dbHelper: SqlLiteDbHelper
    private lateinit var addItemModel: AddItemModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_item)

        // Setup toolbar
        GeneralUtils.setToolbarTitle(this@AddItemActivity, "Create New Item")
        GeneralUtils.setToolbarBackButton(this@AddItemActivity)

        buttonAddItem.text = "Add Item"

        imageViewAddItem.setOnClickListener {
            val fm = fragmentManager
            val dialogFragment = SelectImageDialog(selectImageItemSrcListener)
            dialogFragment.show(fm, "SelectImageDialogFragment")
        }

        // Initialize db Helper
        dbHelper = SqlLiteDbHelper(this@AddItemActivity)

        // Initialize presenter
        addItemModel = AddItemModel(this, dbHelper)

        // Set Click listener for Add/Save new item button
        buttonAddItem.setOnClickListener(this)
    }


    // Get imagePath for the selected / captured image

    private var imagePath: String? = null

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
