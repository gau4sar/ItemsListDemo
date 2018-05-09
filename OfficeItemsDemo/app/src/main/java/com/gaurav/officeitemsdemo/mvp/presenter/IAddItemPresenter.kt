package com.gaurav.officeitemsdemo.mvp.presenter

interface IAddItemPresenter {
    fun addItem(itemName: String, itemDescription: String, itemImagePath: String?,
                itemLocation: String, itemCost: String)
}