package com.wizeline.brainstormingapp

import android.app.Application
import com.wizeline.brainstormingapp.ext.getUserEmail
import com.wizeline.brainstormingapp.repository.Repository
import io.reactivex.Observable
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

            override fun getRooms(): Observable<Room> {
                return Observable.just(Room("mockId", "aRemote@email.com", "Mock Name", System.currentTimeMillis()))
            }

            override fun createMessage(roomId: String, texts: List<String>): Single<List<Message>> {
                return Single.just(listOf(Message("mockId", "mockRoomId", getUserEmail(), "Mock text")))
            }

            override fun getOtherMessages(): Single<List<Message>> {
                return Single.just(listOf(
                        Message("mockId0", "mockRoomId", "moc@email.com", "Mock text0000"),
                        Message("mockId1", "mockRoomId", "moc@email.com", "Mock text11111"),
                        Message("mockId2", "mockRoomId", "moc@email.com", "Mock text22222"),
                        Message("mockId3", "mockRoomId", "moc@email.com", "Mock text333333"))
                )
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
