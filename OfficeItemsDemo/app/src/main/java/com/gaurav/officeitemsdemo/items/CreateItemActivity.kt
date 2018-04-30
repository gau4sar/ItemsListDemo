package com.gaurav.officeitemsdemo.items

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.gaurav.officeitemsdemo.MainActivity
import com.gaurav.officeitemsdemo.R
import com.gaurav.officeitemsdemo.data.SqlLiteDbHelper
import com.gaurav.officeitemsdemo.utils.GeneralUtils
import com.gaurav.officeitemsdemo.utils.ImageUtils
import com.gaurav.officeitemsdemo.utils.MPermissionChecker
import com.gaurav.officeitemsdemo.utils.SelectImageDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_item.*
import java.io.File

class CreateItemActivity : AppCompatActivity() {

    private val TAG = "CreateItemActivity"
    private var selectedPhotoPath: String? = null

    private lateinit var dbHelper: SqlLiteDbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)

        GeneralUtils.setToolbarTitle(this@CreateItemActivity, "Create New Item")
        GeneralUtils.setToolbarBackButton(this@CreateItemActivity)

        imageViewAddItem.setOnClickListener {
            val fm = fragmentManager
            val dialogFragment = SelectImageDialog(imageSelectListener)
            dialogFragment.show(fm, "SelectImageDialogFragment")
        }

        // Initialize db Helper
        dbHelper = SqlLiteDbHelper(this@CreateItemActivity)

        // Add/Save new item to the db
        buttonAddItem.setOnClickListener {

            //Front end validation check
            if(editTextName.text.isNotEmpty() && editTextDescription.text.isNotEmpty() && editTextCost.text.isNotEmpty()
                && editTextLocation.text.isNotEmpty() && !imagePath.isNullOrEmpty()) {

                // Insert item into the db
                if (dbHelper.insertItem(
                                editTextName.text.toString(),
                                editTextDescription.text.toString(),
                                imagePath,
                                Integer.parseInt(editTextCost.text.toString()),
                                editTextLocation.text.toString())) {

                    Toast.makeText(applicationContext, "Item added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Could not add item", Toast.LENGTH_LONG).show()
                }

                // Route back to Item List View
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

            } else {
                Toast.makeText(applicationContext, "Please enter all details", Toast.LENGTH_SHORT).show()
            }

        }
    }


    private var imagePath: String? = null

    private var imageSelectListener = { openCamera: Boolean ->
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
            if (requestCode == ImageUtils.CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE || requestCode == ImageUtils.SELECT_IMAGE_FROM_GALLERY) {
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
