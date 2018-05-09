package com.gaurav.officeitemsdemo.mvp.activities.view

interface IAddItemView {
    fun validateItem()
    fun addItemSuccess()
    fun addItemError()
}