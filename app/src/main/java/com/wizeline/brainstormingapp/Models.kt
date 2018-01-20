package com.wizeline.brainstormingapp

data class Room(
        val id: Long,
        val hostEmail: String,
        val name: String)

data class Message(
        val id: Long,
        val idRoom: Long,
        val email: String,
        val text: String)

data class Vote(
        val id: Long,
        val idMessage: Long,
        val voterEmail: String,
        val vote: Int)
