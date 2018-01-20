package com.wizeline.brainstormingapp

import android.app.Application
import com.wizeline.brainstormingapp.repository.Repository
import io.reactivex.Single

class App : Application() {

    val repository: Repository by lazy {
        object : Repository {
            override fun createRoom(email: String, name: String): Single<Room> {
                return Single.just(Room("mockId", "mock@email.com", "Mock Name"))
            }

            override fun getRooms(): Single<List<Room>> {
                return Single.just(listOf(Room("mockId", "mock@email.com", "Mock Name")))
            }

            override fun joinRoom(email: String, room: Room): Single<Boolean> {
                return Single.just(true)
            }

            override fun createMessage(email: String, room: Room, text: String): Single<Message> {
                return Single.just(Message("mockId", "mockRoomId", "moc@email.com", "Mock text"))
            }

            override fun getMessages(): Single<List<Message>> {
                return Single.just(listOf(Message("mockId", "mockRoomId", "moc@email.com", "Mock text")))
            }

            override fun vote(message: Message, vote: Long): Single<Boolean> {
                return Single.just(true)
            }
        }
    }

}
