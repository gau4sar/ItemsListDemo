package com.gaurav.officeitemsdemo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ListItemModel(
        val id: String, val name: String, val description: String,
        val image: String, val location: String, val cost: String) : Parcelable