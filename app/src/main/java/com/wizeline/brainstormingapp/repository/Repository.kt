package com.wizeline.brainstormingapp.repository

import com.wizeline.brainstormingapp.Message
import com.wizeline.brainstormingapp.Room
import com.wizeline.brainstormingapp.Vote
import io.reactivex.Observable
import io.reactivex.Single

interface Repository {

    fun createRoom(name: String): Single<Room>

    fun getRoom(roomId: String): Single<Room>

    fun getRooms(): Observable<Room>

    fun createMessage(roomId: String, texts: List<String>): Single<List<Message>>

    fun getOtherMessages(): Single<List<Message>>

    fun getTopMessages(): Single<List<Message>>

    fun vote(message: Message, vote: Long): Single<Vote>

}
