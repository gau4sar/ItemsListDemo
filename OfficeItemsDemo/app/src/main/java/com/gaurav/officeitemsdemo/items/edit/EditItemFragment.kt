package com.gaurav.officeitemsdemo.items.edit

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gaurav.officeitemsdemo.R
import com.gaurav.officeitemsdemo.items.IFragmentInteractionListener
import com.gaurav.officeitemsdemo.model.ListItemModel
import com.gaurav.officeitemsdemo.utils.GeneralUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.create_item.*
import java.io.File

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val LIST_ITEM = "param1"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [IFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EditItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EditItemFragment : Fragment() {

    private lateinit var listItem: ListItemModel

    private var interactionListener: IFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listItem = it.getParcelable(LIST_ITEM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.create_item, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Picasso.get()
                .load(File(listItem.image))
                .resize(GeneralUtils.IMG_WIDTH * 2, GeneralUtils.IMG_HEIGHT)
                .centerCrop().into(imageViewAddItem)

        editTextName.setText(listItem.name)
        editTextDescription.setText(listItem.description)
        editTextCost.setText(listItem.cost)
        editTextLocation.setText(listItem.location)

        buttonAddItem.text = "SAVE"

        buttonAddItem.setOnClickListener {
            if(editTextName.text.isNotEmpty() && editTextDescription.text.isNotEmpty() && editTextCost.text.isNotEmpty()
                    && editTextLocation.text.isNotEmpty()) {

                val itemModel = ListItemModel(
                        listItem.id,
                        editTextName.text.toString(),
                        editTextDescription.text.toString(),
                        listItem.image,
                        editTextLocation.text.toString(),
                        editTextCost.text.toString()
                )

                interactionListener?.onFragmentInteraction(it.id, itemModel)
            }
        }

        imageViewAddItem.setOnClickListener {
            interactionListener?.onFragmentInteraction(it.id)
        }

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
         * @param param1 Parameter 1.
         * @return A new instance of fragment EditItemFragment.
         */
        @JvmStatic
        fun newInstance(listItem : ListItemModel) =
                EditItemFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(LIST_ITEM, listItem)
                    }
                }
    }
}
