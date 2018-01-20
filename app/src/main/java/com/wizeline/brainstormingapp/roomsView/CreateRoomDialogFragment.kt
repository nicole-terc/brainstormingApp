package com.wizeline.brainstormingapp.roomsView


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wizeline.brainstormingapp.R
import kotlinx.android.synthetic.main.fragment_create_room_dialog.view.*


/**
 * A simple [Fragment] subclass.
 */
class CreateRoomDialogFragment : DialogFragment() {

    interface InteractionListener {
        fun onButtonPressed(text: String)
    }

    var listener: InteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_create_room_dialog, container, true)
        view.btn_create.setOnClickListener {
            if (listener != null) {
                listener!!.onButtonPressed(view.edit_name.text.toString())
            }
            dismiss()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window.setLayout(width, height)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is InteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement InteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        listener = null
    }
}
