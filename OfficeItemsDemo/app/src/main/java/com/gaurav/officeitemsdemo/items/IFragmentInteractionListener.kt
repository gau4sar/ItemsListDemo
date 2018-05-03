package com.gaurav.officeitemsdemo.items

import android.net.Uri
import com.gaurav.officeitemsdemo.model.ListItemModel

interface IFragmentInteractionListener {

    fun onFragmentInteraction(viewId: Int)
    fun onFragmentInteraction(viewId: Int, listItem : ListItemModel)
}