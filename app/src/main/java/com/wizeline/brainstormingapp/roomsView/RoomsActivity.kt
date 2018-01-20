package com.wizeline.brainstormingapp.roomsView

import android.Manifest.permission.GET_ACCOUNTS
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.wizeline.brainstormingapp.App
import com.wizeline.brainstormingapp.R
import com.wizeline.brainstormingapp.create_ideas.CreateIdeasActivity
import com.wizeline.brainstormingapp.ext.getUserEmail
import com.wizeline.brainstormingapp.nerby.NearbyService
import com.wizeline.brainstormingapp.repository.Repository
import com.wizeline.brainstormingapp.repository.RepositoryImpl
import com.wizeline.brainstormingapp.repository.Room
import com.wizeline.brainstormingapp.util.ParserUtil
import com.wizeline.brainstormingapp.util.ParserUtil.Companion.roomToJson
import com.wizeline.brainstormingapp.waitingRoom.WaitingRoomFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RoomsActivity : AppCompatActivity(), RoomsFragment.InteractionListener, WaitingRoomFragment.InteractionListener, CreateRoomDialogFragment.InteractionListener {
    val ROOMS_FRAGMENT_TAG = "roomFragmentTag"
    val WAITING_FRAGMENT_TAG = "waitingFragmentTag"
    val CREATE_FRAGMENT_TAG = "createFragmentTag"
    lateinit var roomsFragment: RoomsFragment

    lateinit var repository: Repository
    lateinit var nearbyService: NearbyService
    lateinit var room: Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)
        nearbyService = NearbyService(this)
        repository = RepositoryImpl(applicationContext as App)

        //Adds roomsFragment
        roomsFragment = supportFragmentManager.findFragmentByTag(ROOMS_FRAGMENT_TAG) as RoomsFragment? ?: RoomsFragment()
        supportFragmentManager.beginTransaction()
                .add(R.id.rooms_container, roomsFragment, ROOMS_FRAGMENT_TAG)
                .commit()

        if (ContextCompat.checkSelfPermission(this, GET_ACCOUNTS) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(GET_ACCOUNTS), 1)
        }
    }

    //Create room flow
    fun createRoom(roomName: String) {
        repository.createRoom(roomName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { room -> roomCreated(room) }
    }

    fun roomCreated(room: Room) {
        this.room = room
        nearbyService.startBroadcasting(roomToJson(room))
        goToWaitingRoom()
    }

    fun goToWaitingRoom() {
        var userEmail = (applicationContext as App).getUserEmail()
        var fragment = WaitingRoomFragment.getInstance(room, room.email == userEmail)
        supportFragmentManager.beginTransaction()
                .add(R.id.rooms_container, fragment, WAITING_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit()
    }

    // Interaction listeners -----

    //Rooms fragment
    override fun createRoom() {
        var fragment = CreateRoomDialogFragment()
        fragment.show(supportFragmentManager, CREATE_FRAGMENT_TAG)
    }

    override fun roomClicked(room: Room) {
        this.room = room
        goToWaitingRoom()
    }

    //Create dialog
    override fun onButtonPressed(text: String) {
        Toast.makeText(this, "SUPER ROOM: " + text, Toast.LENGTH_SHORT).show()
        createRoom(text)
    }


    //Waiting room fragment
    override fun nextSectionButtonPressed(room: Room) {
        this.room = room
        nearbyService.stopBroadcasting()
        repository.updateRoom(room)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { item ->
                    Log.d("ROOMS ACTIVITY", "Room updated: " + item.name)
                }

    }

    override fun startNextSection(room: Room) {
        this.room = room
        Log.d("RoomsActivity", "Room to load: " + ParserUtil.roomToJson(room))
        var i = Intent(this, CreateIdeasActivity::class.java)
        i.putExtra("room", ParserUtil.roomToJson(this.room))
        startActivity(i)
    }

    //Nearby flow
    override fun onResume() {
        super.onResume()
        nearbyService.startListening(getMessageListener())
    }

    override fun onPause() {
        super.onPause()
        nearbyService.stop()
        roomsFragment.clearData()
    }

    fun getMessageListener(): MessageListener {
        return object : MessageListener() {
            override fun onFound(message: Message?) {
                if (message != null) {
                    Log.d("Rooms fragment", "found message: -" + message.content.toString() + "-")
                    val room = ParserUtil.jsonToRoom(String(message.content))
                    if (!room.name.isEmpty())
                        roomsFragment.addRoom(room)
                }
            }

            override fun onLost(message: Message?) {
                if (message != null) {
                    Log.d("Rooms fragment", "lost message: -" + message.content.toString() + "-")
                    roomsFragment.removeRoom(ParserUtil.jsonToRoom(String(message.content)))
                }
            }
        }
    }

}
