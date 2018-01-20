package com.wizeline.brainstormingapp.repository

import com.wizeline.brainstormingapp.Message
import com.wizeline.brainstormingapp.Room
import io.reactivex.Single

interface Repository {

    fun createRoom(email: String, token: String): Single<Room>

    fun getRooms(): Single<List<Room>>

    fun joinRoom(email: String, room: Room): Single<Boolean>

    fun createMessage(email: String, room: Room, text: String): Single<Message>

    fun getMessages(): Single<List<Message>>

    fun vote(message: Message, vote: Long): Single<Boolean>

}
