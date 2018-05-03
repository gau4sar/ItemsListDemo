package com.gaurav.officeitemsdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gaurav.officeitemsdemo.items.CreateItemActivity
import com.gaurav.officeitemsdemo.items.list.ItemsListFragment
import com.gaurav.officeitemsdemo.utils.GeneralUtils.openFragment
import com.gaurav.officeitemsdemo.utils.GeneralUtils.setToolbarTitle

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setToolbarTitle(this@MainActivity, "Items List")
        openFragment(this, R.id.root_fragment_container, ItemsListFragment())

        fab.setOnClickListener { view ->
            startActivity(Intent(this@MainActivity, CreateItemActivity::class.java))
        }
    }

}
