package com.wizeline.brainstormingapp.repository

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.wizeline.brainstormingapp.App
import com.wizeline.brainstormingapp.Message
import com.wizeline.brainstormingapp.Room
import io.reactivex.Single

class RepositoryImpl(private val app: App) : Repository {

    private val roomsTable by lazy { FirebaseDatabase.getInstance().getReference("rooms") }

    init {
        FirebaseApp.initializeApp(app)
    }

    override fun createRoom(email: String, token: String, name: String): Single<Room> {
        return Single.fromPublisher {
            val room = Room(token, email, name)
            roomsTable.child(roomsTable.push().key).setValue(room)
            it.onNext(room)
            it.onComplete()
        }
    }

    override fun getRooms(): Single<List<Room>> {
        return Single.fromPublisher {
            roomsTable.orderByKey().addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    Log.d("Wizeline", "onCancelled %s".format(p0))
                }

                override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                    Log.d("Wizeline", "onChildMoved %s %s".format(p0, p1))
                }

                override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                    Log.d("Wizeline", "onChildChanged %s %s".format(p0, p1))
                }

                override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                    Log.d("Wizeline", "onChildAdded %s %s".format(p0, p1))
                }

                override fun onChildRemoved(p0: DataSnapshot?) {
                    Log.d("Wizeline", "onChildRemoved %s".format(p0))
                }
            })
        }
    }

    override fun joinRoom(email: String, room: Room): Single<Boolean> {
        return app.repository.joinRoom(email, room)
    }

    override fun createMessage(email: String, room: Room, text: String): Single<Message> {
        return app.repository.createMessage(email, room, text)
    }

    override fun getMessages(): Single<List<Message>> {
        return app.repository.getMessages()
    }

    override fun vote(message: Message, vote: Long): Single<Boolean> {
        return app.repository.vote(message, vote)
    }

}