package com.wizeline.brainstormingapp.repository

import com.google.firebase.FirebaseApp
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.wizeline.brainstormingapp.App
import com.wizeline.brainstormingapp.Message
import com.wizeline.brainstormingapp.Room
import com.wizeline.brainstormingapp.Vote
import com.wizeline.brainstormingapp.ext.getUserEmail
import io.reactivex.Single

class RepositoryImpl(private val app: App) : Repository {

    private val roomsTable by lazy { FirebaseDatabase.getInstance().getReference("rooms") }
    private val rooms = hashMapOf<String, Room>()

    private val roomChildListener: ChildEventListener

    init {
        FirebaseApp.initializeApp(app)

        roomChildListener = roomsTable.orderByKey().addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                cacheData(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                cacheData(snapshot)
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                cacheData(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                rooms.remove(snapshot.key)
            }

            fun cacheData(snapshot: DataSnapshot) {
                val key = snapshot.key
                val email = snapshot.child("email")?.value as String?
                val name = snapshot.child("name")?.value as String?
                val timestamp = snapshot.child("timestamp")?.value as Long?
                if (key != null && email != null && name != null && timestamp != null) {
                    rooms[key] = Room(key, email, name, timestamp)
                }
            }
        })
    }

    override fun createRoom(name: String): Single<Room> {
        return Single.fromPublisher {
            val room = Room(roomsTable.push().key, app.getUserEmail(), "A name", System.currentTimeMillis())
            roomsTable.child(room.id).setValue(mapOf(
                    "email" to room.hostEmail,
                    "name" to room.name,
                    "timestamp" to room.timestamp))
            it.onNext(room)
            it.onComplete()
        }
    }

    override fun getRoom(roomId: String): Single<Room> {
        return app.repository.getRoom(roomId)
    }

    override fun getRooms(): Single<List<Room>> {
        return Single.just(rooms.values.toList())
    }

    override fun joinRoom(room: Room): Single<Boolean> {
        return app.repository.joinRoom(room)
    }

    override fun createMessage(room: Room, text: String): Single<Message> {
        return app.repository.createMessage(room, text)
    }

    override fun getMessages(): Single<List<Message>> {
        return app.repository.getMessages()
    }

    override fun getOtherMessages(): Single<List<Message>> {
        return app.repository.getOtherMessages()
    }

    override fun getTopMessages(): Single<List<Message>> {
        return app.repository.getTopMessages()
    }

    override fun vote(message: Message, vote: Long): Single<Vote> {
        return app.repository.vote(message, vote)
    }

}
