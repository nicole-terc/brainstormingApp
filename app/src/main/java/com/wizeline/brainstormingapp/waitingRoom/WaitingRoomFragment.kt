package com.wizeline.brainstormingapp.waitingRoom


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wizeline.brainstormingapp.App
import com.wizeline.brainstormingapp.R
import com.wizeline.brainstormingapp.repository.Repository
import com.wizeline.brainstormingapp.repository.Room
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_waiting_room.view.*


class WaitingRoomFragment : Fragment() {

    interface InteractionListener {
        fun nextSectionButtonPressed(room: Room)
        fun startNextSection(room: Room)
    }

    companion object {
        fun getInstance(room: Room, isHost: Boolean): WaitingRoomFragment {
            var fragment = WaitingRoomFragment()
            fragment.room = room
            fragment.isHost = isHost
            return fragment
        }
    }

    var listener: InteractionListener? = null
    var isHost = false
    lateinit var room: Room
    lateinit var repository: Repository
    lateinit var roomSubscription: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = (context.applicationContext as App).repository
        getRoomData()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_waiting_room, container, false)
        if (isHost) {
            view.wait_title.text = "Broadcasting"
            view.wait_text.text = resources.getText(R.string.waiting_host)
            view.btn_continue.setOnClickListener {
                if (listener != null) {
                    listener!!.nextSectionButtonPressed(room)
                }
            }
        } else {
            view.wait_title.text = "Receiving"
            view.wait_text.text = resources.getText(R.string.waiting_all)
            view.btn_continue.visibility = View.GONE
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
        if (!roomSubscription.isDisposed) {
            roomSubscription.dispose()
        }
    }

    fun getRoomData() {
        roomSubscription = repository.getRoom(room.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { item ->
                    if (item.startTime != 0L && listener != null) {
                        listener!!.startNextSection(item)
                    }
                }
    }

}
