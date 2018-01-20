package com.wizeline.brainstormingapp

data class Room(
        val id: String,
        val hostEmail: String,
        val name: String)

data class Message(
        val id: String,
        val idRoom: String,
        val email: String,
        val text: String)

data class Vote(
        val id: String,
        val idMessage: String,
        val voterEmail: String,
        val vote: Int)
