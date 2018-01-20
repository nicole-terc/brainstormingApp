package com.wizeline.brainstormingapp.repository

import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.wizeline.brainstormingapp.App
import com.wizeline.brainstormingapp.ext.getUserEmail
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

class RepositoryImpl(private val app: App) : Repository {

    private val roomsTable by lazy { FirebaseDatabase.getInstance().getReference("rooms") }
    private val messagesTable by lazy { FirebaseDatabase.getInstance().getReference("messages") }
    private val votesTable by lazy { FirebaseDatabase.getInstance().getReference("votes") }

    init {
        FirebaseApp.initializeApp(app)
    }

    override fun createRoom(name: String): Single<Room> {
        return Single.fromPublisher {
            val room = Room(roomsTable.push().key, app.getUserEmail(), name, System.currentTimeMillis(), 0L)
            roomsTable.child(room.id).setValue(mapOf(
                    "email" to room.email,
                    "name" to room.name,
                    "timestamp" to room.timestamp,
                    "startTime" to room.startTime
            ))
            it.onNext(room)
            it.onComplete()
        }
    }

    override fun updateRoom(room: Room): Single<Room> {
        return Single.fromPublisher {
            roomsTable.child(room.id).updateChildren(mapOf(
                    "email" to room.email,
                    "name" to room.name,
                    "timestamp" to room.timestamp,
                    "startTime" to System.currentTimeMillis()
            ))
        }
    }

    override fun getRoom(roomId: String): Observable<Room> {
        return Observable.fromPublisher {
            roomsTable.child(roomId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    parse(snapshot)
                }

                fun parse(snapshot: DataSnapshot) {
                    val key = snapshot.key
                    val email = snapshot.child("email")?.value as String?
                    val name = snapshot.child("name")?.value as String?
                    val timestamp = snapshot.child("timestamp")?.value as Long?
                    val startTime = snapshot.child("startTime")?.value as Long?
                    if (key != null && email != null && name != null && timestamp != null) {
                        it.onNext(Room(key, email, name, timestamp, startTime ?: 0L))
                    }
                }

            })
        }
    }

    override fun getRooms(): Observable<Room> {
        return Observable.fromPublisher {
            roomsTable.orderByKey().addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    parse(snapshot)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    parse(snapshot)
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    parse(snapshot)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                fun parse(snapshot: DataSnapshot) {
                    val key = snapshot.key
                    val email = snapshot.child("email")?.value as String?
                    val name = snapshot.child("name")?.value as String?
                    val timestamp = snapshot.child("timestamp")?.value as Long?
                    val startTime = snapshot.child("startTime")?.value as Long?
                    if (key != null && email != null && name != null && timestamp != null && startTime != null) {
                        it.onNext(Room(key, email, name, timestamp, startTime))
                    }
                }
            })
        }
    }

    override fun createMessage(roomId: String, texts: List<String>): Single<List<Message>> {
        return Single.fromPublisher {
            it.onNext(texts.map {
                Message(messagesTable.push().key, roomId, app.getUserEmail(), it)
            }.onEach {
                messagesTable.child(it.id).setValue(mapOf(
                        "id_room" to it.idRoom,
                        "email" to it.email,
                        "text" to it.text
                ))
            })
            it.onComplete()
        }
    }

    override fun getMessages(roomId: String): Single<List<Message>> {
        return Single.fromPublisher {
            messagesTable.orderByChild("id_room").equalTo(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    it.onError(error.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = arrayListOf<Message>()
                    snapshot.children.filterNotNull().forEach {
                        val key = it.key
                        val email = it.child("email")?.value as String?
                        val text = it.child("text")?.value as String?
                        if (key != null && email != null && text != null) {
                            messages.add(Message(key, roomId, email, text))
                        }
                    }
                    it.onNext(messages)
                }
            })
        }
    }


    override fun getOtherMessages(roomId: String): Single<List<Message>> {
        return Single.fromPublisher {
            messagesTable.orderByChild("id_room").equalTo(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    it.onError(error.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = arrayListOf<Message>()
                    snapshot.children.filterNotNull().forEach {
                        val key = it.key
                        val email = it.child("email")?.value as String?
                        val text = it.child("text")?.value as String?
                        if (key != null && email != null && text != null) {
                            messages.add(Message(key, roomId, email, text))
                        }
                    }
                    val userEmail = app.getUserEmail()
                    it.onNext(messages.filter { it.email != userEmail })
                    it.onComplete()
                }
            })
        }
    }

    override fun getTopMessages(roomId: String): Single<List<ChampionMessage>> {
        return Single.fromPublisher { publisher ->
            messagesTable.orderByChild("id_room").equalTo(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    publisher.onError(error.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = arrayListOf<Message>()
                    snapshot.children.filterNotNull().forEach {
                        val key = it.key
                        val email = it.child("email")?.value as String?
                        val text = it.child("text")?.value as String?
                        if (key != null && email != null && text != null) {
                            messages.add(Message(key, roomId, email, text))
                        }
                    }
                    val messagesMap = hashMapOf<Message, ChampionMessage>()
                    messages.forEach { message ->
                        votesTable.orderByChild("id_message").equalTo(message.id).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                publisher.onError(error.toException())
                            }

                            override fun onDataChange(voteSnapshot: DataSnapshot) {
                                var upVotes = 0
                                var downVotes = 0
                                voteSnapshot.children.filterNotNull().forEach {
                                    val vote = it.child("vote")?.value as Long?
                                    if (vote != null) {
                                        val voteValue = vote.toInt()
                                        if (voteValue > 0) {
                                            upVotes += voteValue
                                        } else {
                                            downVotes += voteValue
                                        }
                                    }
                                }
                                messagesMap[message] = ChampionMessage(message, upVotes, downVotes)
                                if (messagesMap.keys.size == messages.size) {
                                    publisher.onNext(messagesMap.toList()
                                            .sortedByDescending { (_, value) -> value.upVotes + value.downVotes }
                                            .map { it.second }
                                            .take(5))
                                    publisher.onComplete()
                                }
                            }
                        })
                    }
                }
            })
        }
    }

    override fun getVotes(roomId: String): Observable<Vote> {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun vote(votes: List<UserVote>): Single<List<Vote>> {
        return Single.fromPublisher {
            val remoteVotes = arrayListOf<Vote>()
            votes.forEach {
                val remoteVote = Vote(votesTable.push().key, it.idMessage, app.getUserEmail(), it.vote)
                votesTable.child(remoteVote.id).setValue(mapOf(
                        "id_message" to remoteVote.idMessage,
                        "email" to remoteVote.email,
                        "vote" to remoteVote.vote))
                remoteVotes.add(remoteVote)
            }
            it.onNext(remoteVotes)
            it.onComplete()
        }
    }

    override fun votingFinished(roomId: String): Observable<Boolean> {
        val emailList = ArrayList<String>()
        return getMessages(roomId)
                .map { messages ->
                    messages.forEach {
                        if (!emailList.contains(it.email)) {
                            emailList.add(it.email)
                        }
                    }
                    emailList
                }
                .toObservable()
                .flatMap { _ ->
                    getVotes(roomId)
                }
                .map { vote ->
                    if (emailList.contains(vote.email)) {
                        emailList.remove(vote.email)
                    }
                    emailList.isEmpty()
                }
    }

}
