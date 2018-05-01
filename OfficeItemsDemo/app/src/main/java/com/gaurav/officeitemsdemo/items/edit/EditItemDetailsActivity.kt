package com.gaurav.officeitemsdemo.items.edit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.gaurav.officeitemsdemo.R
import com.gaurav.officeitemsdemo.data.SqlLiteDbHelper
import com.gaurav.officeitemsdemo.items.IFragmentInteractionListener

class EditItemDetailsActivity : AppCompatActivity(), IFragmentInteractionListener {

    override fun onFragmentInteraction(viewId: Int) {
        when(viewId) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item_details)

        val dbHelper = SqlLiteDbHelper(this)

        openFragment(DisplayItemDetailsFragment.newInstance(dbHelper))

    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.root_fragment_container, fragment)
                .commitAllowingStateLoss()
    }
}
