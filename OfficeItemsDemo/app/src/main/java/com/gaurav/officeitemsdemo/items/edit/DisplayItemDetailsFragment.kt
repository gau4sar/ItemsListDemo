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
import kotlinx.android.synthetic.main.fragment_display_item_details.*
import java.io.File


private const val ARG_ITEM = "item"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [IFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DisplayItemDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DisplayItemDetailsFragment : Fragment() {

    private lateinit var itemDetails: ListItemModel
    private var interactionListener: IFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemDetails = it.getParcelable(ARG_ITEM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_item_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Picasso.get()
                .load(File(itemDetails.image))
                .resize(GeneralUtils.IMG_WIDTH * 4, GeneralUtils.IMG_HEIGHT * 4)
                .centerCrop().into(imageViewItem)

        textViewName.text = itemDetails.name
        textViewDescription.text = itemDetails.description
        textViewCost.text = itemDetails.cost
        textViewLocation.text = itemDetails.location

        buttonEditItem.setOnClickListener {
            interactionListener?.onFragmentInteraction(it.id, itemDetails)
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
         * @param ListItemModel Parameter.
         * @return A new instance of fragment DisplayItemDetailsFragment.
         */
        @JvmStatic
        fun newInstance(itemList: ListItemModel) =
                DisplayItemDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_ITEM, itemList)
                    }
                }
    }
}
