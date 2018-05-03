package com.gaurav.officeitemsdemo.items.list

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics.TAG
import com.gaurav.officeitemsdemo.R
import com.gaurav.officeitemsdemo.items.edit.ItemDetailsActivity
import com.gaurav.officeitemsdemo.model.ListItemModel
import com.gaurav.officeitemsdemo.utils.GeneralUtils
import com.gaurav.officeitemsdemo.utils.inflate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_layout.view.*
import java.io.File

class ItemsListAdapter(private val context: Context, private val itemsList: List<ListItemModel>) :
        RecyclerView.Adapter<ItemsListAdapter.ItemsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsListViewHolder {
        val inflatedView = parent.inflate(R.layout.item_layout, false)
        return ItemsListViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ItemsListViewHolder, position: Int) {
        val listItem = itemsList[position]
        holder.bind(context, listItem)
    }


    class ItemsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(v: View?) {

        }

        fun bind(context: Context, listItem: ListItemModel) {

            GeneralUtils.fixImageOrientation(listItem.image)
            Picasso.get()
                    .load(File(listItem.image))
                    .resize(GeneralUtils.IMG_WIDTH * 2, GeneralUtils.IMG_HEIGHT)
                    .centerCrop().into(itemView.imageViewListItem)

            itemView.textViewName.text = listItem.name
            itemView.textViewDescription.text = listItem.description
            itemView.textViewLocation.text = listItem.location
            itemView.textViewCost.text = listItem.cost

            itemView.cardViewItem.setOnClickListener {
                Log.d(TAG, "List Item Id : ${listItem.id}")
                val intent = Intent(context, ItemDetailsActivity::class.java)
                intent.putExtra(GeneralUtils.BUNDLE_ITEM_ID, listItem.id.toInt())
                context.startActivity(intent)
            }
        }


    }
}