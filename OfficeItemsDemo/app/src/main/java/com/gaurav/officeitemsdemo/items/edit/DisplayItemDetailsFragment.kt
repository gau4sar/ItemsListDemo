package com.gaurav.officeitemsdemo.items.edit


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gaurav.officeitemsdemo.R
import com.gaurav.officeitemsdemo.data.SqlLiteDbHelper
import com.gaurav.officeitemsdemo.items.IFragmentInteractionListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DisplayItemDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DisplayItemDetailsFragment : Fragment() {
    private var dbHelper: SqlLiteDbHelper? = null

    private var interactionListener: IFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dbHelper = it.get(ARG_PARAM1) as SqlLiteDbHelper?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_item_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IFragmentInteractionListener) {
            interactionListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement IFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        interactionListener = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param dbHelper Parameter.
         * @return A new instance of fragment DisplayItemDetailsFragment.
         */
        @JvmStatic
        fun newInstance(dbHelper: SqlLiteDbHelper) =
                DisplayItemDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, dbHelper.toString())
                    }
                }
    }
}
