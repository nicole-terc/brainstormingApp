package com.wizeline.brainstormingapp

import android.app.Application
import com.wizeline.brainstormingapp.ext.getUserEmail
import com.wizeline.brainstormingapp.repository.Repository
import io.reactivex.Single

class App : Application() {

    val repository: Repository by lazy {
        object : Repository {
            override fun createRoom(name: String): Single<Room> {
                return Single.just(Room("mockId", getUserEmail(), "Mock Name", System.currentTimeMillis()))
            }

            override fun getRoom(roomId: String): Single<Room> {
                return Single.just(Room("mockId", "aRemote@email.com", "Mock Name", System.currentTimeMillis()))
            }

            override fun getRooms(): Single<List<Room>> {
                return Single.just(listOf(Room("mockId", "aRemote@email.com", "Mock Name", System.currentTimeMillis())))
            }

            override fun joinRoom(room: Room): Single<Boolean> {
                return Single.just(true)
            }

            override fun createMessage(room: Room, text: String): Single<Message> {
                return Single.just(Message("mockId", "mockRoomId", getUserEmail(), "Mock text"))
            }

            override fun getMessages(): Single<List<Message>> {
                return Single.just(listOf(Message("mockId", "mockRoomId", "aRemote@email.com", "Mock text")))
            }

            override fun getOtherMessages(): Single<List<Message>> {
                return Single.just(listOf(Message("mockId", "mockRoomId", "aRemote@email.com", "Mock text")))
            }

            override fun getTopMessages(): Single<List<Message>> {
                return Single.just(listOf(Message("mockId", "mockRoomId", "aRemote@email.com", "Mock text")))
            }

            override fun vote(message: Message, vote: Long): Single<Vote> {
                return Single.just(Vote("anId", "aMessageId", "aVoter@email.com", 1))
            }
        }
    }

}
