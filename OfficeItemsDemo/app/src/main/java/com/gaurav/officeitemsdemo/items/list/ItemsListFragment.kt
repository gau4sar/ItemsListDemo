package com.gaurav.officeitemsdemo.items.list

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gaurav.officeitemsdemo.R
import com.gaurav.officeitemsdemo.mvp.data.SqlLiteDbHelper
import com.gaurav.officeitemsdemo.model.ListItemModel
import io.fabric.sdk.android.Fabric.TAG
import kotlinx.android.synthetic.main.fragment_items_list.*

/**
 * A simple fragment containing simple recyclerView displaying list items form SqlLiteDatabase.
 */
class ItemsListFragment : Fragment() {

    private lateinit var dbHelper: SqlLiteDbHelper
    private var itemList: MutableList<ListItemModel> = mutableListOf()

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_items_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dbHelper = SqlLiteDbHelper(activity)

        val cursor = dbHelper.getAllItems()

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val id = (cursor.position + 1).toString()
                Log.d(TAG, "Inserting list Item Id : $id")
                val name = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_NAME))
                val description = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_DESCRIPTION))
                val imagePath = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_IMAGE_PATH))
                val cost = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_COST))
                val location = cursor.getString(cursor.getColumnIndex(SqlLiteDbHelper.ITEM_COLUMN_LOCATION))

                itemList.add(ListItemModel(id, name, description, imagePath, location, cost))
                cursor.moveToNext()
            }
        }

        cursor.close()

        Log.d(TAG, "Items list size :  ${itemList.size} ")


        linearLayoutManager = LinearLayoutManager(activity!!.baseContext)
        recyclerView.layoutManager = linearLayoutManager
        val itemListAdapter = ItemsListAdapter(activity!!.baseContext, itemList)
        recyclerView.adapter = itemListAdapter

        recyclerView.setEmptyView(emptyListView)
    }

}
