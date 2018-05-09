package com.gaurav.officeitemsdemo.utils

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.transition.Slide
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.gaurav.officeitemsdemo.R
import io.fabric.sdk.android.Fabric.TAG
import java.io.*

object GeneralUtils {

    val BUNDLE_ITEM_ID = "item_id"

    val IMG_WIDTH = 250
    val IMG_HEIGHT = 250

    fun setToolbarTitle(activity: AppCompatActivity, title: String) {
        val headerTitle: TextView
        val toolbar = activity.findViewById<View>(R.id.app_bar) as Toolbar
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
        headerTitle = toolbar.findViewById<View>(R.id.toolbar_title) as TextView
        headerTitle.text = title
    }

    fun setToolbarBackButton(currentActivity: AppCompatActivity) {
        val toolbar = currentActivity.findViewById<View>(R.id.app_bar) as Toolbar
        currentActivity.setSupportActionBar(toolbar)
        currentActivity.supportActionBar!!.setDisplayShowTitleEnabled(false)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            if (currentActivity.supportFragmentManager.backStackEntryCount == 0)
                currentActivity.finish()
            else
                currentActivity.supportFragmentManager.popBackStack()
        }
    }

    fun attachFragment(activity: AppCompatActivity, containerViewId: Int, fragment: Fragment, addToBackStack: Boolean, isEntryTransitionSlide: Boolean) {

        val slideTransition: Slide?

        if (isEntryTransitionSlide) {
            slideTransition = Slide(Gravity.END)
            slideTransition.duration = 250
        } else {
            slideTransition = Slide(Gravity.START)
            slideTransition.duration = 250
        }

        fragment.enterTransition = slideTransition

        if (addToBackStack) {
            activity.supportFragmentManager.beginTransaction().replace(containerViewId, fragment)
                    .addToBackStack(null)
                    .commit()
        } else {
            activity.supportFragmentManager.beginTransaction().replace(containerViewId, fragment)
                    .commit()
        }
    }

    // IMAGE PROCESSING methods

    fun fixImageOrientation(imagePath: String) {
        var orientation = 0
        try {
            Crashlytics.log(Log.DEBUG, TAG, "Image Path inside fixImageOrientation : $imagePath")
            orientation = GeneralUtils.getImageOrientation(imagePath)
            // Handling special case for phones which takes photos in landscape mode
            val imageBitmap = GeneralUtils.rotateImage(imagePath, orientation)
            Crashlytics.log(Log.DEBUG, TAG, "Image Bitmap : $imageBitmap")
            GeneralUtils.writeBitmap(imageBitmap, imagePath, 85)
        } catch (e: IOException) {
            Crashlytics.log(Log.ERROR, TAG, "Exif Info not available. " + e.message)
        }
    }


    @Throws(IOException::class)
    fun getImageOrientation(photoPath: String): Int {
        val ei = ExifInterface(photoPath)
        return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
    }

    fun rotateImage(path: String, orientation: Int): Bitmap {
        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(path, bmOptions)
        return rotateImage(bitmap, orientation)
    }

    fun rotateImage(source: Bitmap, orientation: Int): Bitmap {

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return source
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return source
        }

        val retVal: Bitmap
        matrix.postRotate(orientation.toFloat())
        retVal = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        return retVal
    }

    fun nativeRotate(bitmap: Bitmap, degree: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height

        val mtx = Matrix()
        mtx.postRotate(degree.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
    }

    fun writeBitmap(source: Bitmap, path: String, compression: Int) {
        var fOut: OutputStream? = null
        val file = File(path)
        try {
            fOut = FileOutputStream(file)
            // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            source.compress(Bitmap.CompressFormat.JPEG, compression, fOut)
            fOut.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (fOut != null)
                    fOut.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    @Throws(IllegalArgumentException::class)
    fun getRealPathFromURI(context: Context, imageUri: Uri): String {
        val result: String
        val cursor = context.contentResolver.query(imageUri, null, null, null, null)
        if (cursor == null) {
            result = imageUri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
            Crashlytics.log(Log.DEBUG, TAG, " Move first curesor : " + cursor.moveToFirst() + " Idx column : " + idx)
            if (idx == -1) {
                result = imageUri.path
            } else {
                result = cursor.getString(idx)
                cursor.close()
            }
        }
        return result
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getRealPathFromURI_API19(context: Context, uri: Uri): String {
        var filePath = ""
        var wholeID: String? = null

        wholeID = DocumentsContract.getDocumentId(uri)

        // Split at colon, use second item in the array
        val id = wholeID!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null)

        var columnIndex = 0
        if (cursor != null) {
            columnIndex = cursor.getColumnIndex(column[0])

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }

            cursor.close()
        }

        return filePath
    }
}