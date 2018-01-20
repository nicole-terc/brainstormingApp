package com.wizeline.brainstormingapp.waitingRoom


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wizeline.brainstormingapp.R
import com.wizeline.brainstormingapp.repository.Room
import kotlinx.android.synthetic.main.fragment_waiting_room.view.*


class WaitingRoomFragment : Fragment() {

    interface InteractionListener {
        fun nextSectionButtonPressed()
    }

    lateinit var room: Room
    var listener: InteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_waiting_room, container, false)
        view.btn_continue.setOnClickListener {
            if (listener != null) {
                listener!!.nextSectionButtonPressed()
            }
        }
        return view
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


}
