package com.wizeline.brainstormingapp.repository

import io.reactivex.Observable
import io.reactivex.Single

interface Repository {

    fun createRoom(name: String): Single<Room>

    fun updateRoom(room: Room): Single<Room>

    fun getRoom(roomId: String): Observable<Room>

    fun getRooms(): Observable<Room>

    fun createMessage(roomId: String, texts: List<String>): Single<List<Message>>

    fun getMessages(roomId: String): Single<List<Message>>

    fun getOtherMessages(roomId: String): Single<List<Message>>

    fun getTopMessages(roomId: String): Single<List<ChampionMessage>>

    fun getVotes(roomId: String): Observable<Vote>

    fun vote(votes: List<UserVote>): Single<List<Vote>>

    fun votingFinished(roomId: String): Observable<Boolean>
}
