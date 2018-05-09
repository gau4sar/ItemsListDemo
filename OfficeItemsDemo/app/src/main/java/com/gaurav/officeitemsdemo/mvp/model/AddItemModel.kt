package com.gaurav.officeitemsdemo.mvp.model

import com.gaurav.officeitemsdemo.mvp.data.SqlLiteDbHelper
import com.gaurav.officeitemsdemo.mvp.activities.view.IAddItemView
import com.gaurav.officeitemsdemo.mvp.presenter.IAddItemPresenter

class AddItemModel(val addItemView: IAddItemView, val dbHelper: SqlLiteDbHelper) : IAddItemPresenter {

    override fun addItem(itemName: String, itemDescription: String, itemImagePath: String?,
                         itemLocation: String, itemCost: String) {
        //Front end validation check
        if (itemName.isNotEmpty() && itemDescription.isNotEmpty() &&
                itemCost.isNotEmpty() && itemLocation.isNotEmpty() &&
                itemImagePath != null) {


            // Insert item into the db
            if (dbHelper.insertItem(itemName, itemDescription, itemImagePath,
                            Integer.parseInt(itemCost), itemLocation)) {

                addItemView.addItemSuccess()
            } else {
                addItemView.addItemError()
            }

        } else {
            addItemView.validateItem()
        }
    }
}