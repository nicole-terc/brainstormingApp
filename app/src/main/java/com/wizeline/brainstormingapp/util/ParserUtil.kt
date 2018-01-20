package com.wizeline.brainstormingapp.util

import com.google.gson.Gson
import com.wizeline.brainstormingapp.repository.Room

/**
 * Created by Nicole Terc on 1/20/18.
 */
class ParserUtil {
    companion object {
        fun jsonToRoom(roomJson: String): Room {
            return Gson().fromJson(roomJson, Room::class.java)
        }

        fun roomToJson(room: Room): String {
            return Gson().toJson(room)
        }
    }
}