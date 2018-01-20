package com.wizeline.brainstormingapp.roomsView


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.wizeline.brainstormingapp.R
import com.wizeline.brainstormingapp.nerby.NearbyService
import com.wizeline.brainstormingapp.repository.Room
import kotlinx.android.synthetic.main.fragment_rooms.view.*


/**
 * A simple [Fragment] subclass.
 */
class RoomsFragment : Fragment(), RoomAdapter.RoomClickListener {

    interface InteractionListener {
        fun createRoom()
        fun roomClicked(room: Room)
    }

    lateinit var nearbyService: NearbyService
    lateinit var adapter: RoomAdapter
    var listener: InteractionListener? = null
    var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nearbyService = NearbyService(context)
        adapter = RoomAdapter()
        adapter.setListener(this)
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_rooms, container, false)
        view.add_room.setOnClickListener {
            if (listener != null) {
                listener!!.createRoom()
            }
        }
        view.list_rooms.layoutManager = LinearLayoutManager(context)
        view.list_rooms.adapter = adapter
        return view
    }

    fun addRoom(room: Room) {
        adapter.addRoom(room)
    }

    fun removeRoom(room: Room) {
        adapter.removeRoom(room)
    }

    fun clearData(){
        adapter.clearItems()
    }

    //Item click listener
    override fun itemClicked(room: Room) {
        if (listener != null) {
            listener!!.roomClicked(room)
        }
    }
}
