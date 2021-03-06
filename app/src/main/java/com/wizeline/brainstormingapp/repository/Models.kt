package com.wizeline.brainstormingapp.repository

data class Room(
        val id: String,
        val email: String,
        val name: String,
        val timestamp: Long,
        val startTime: Long)

data class Message(
        val id: String,
        val idRoom: String,
        val email: String,
        val text: String)

data class ChampionMessage(
        val message: Message,
        val upVotes: Int,
        val downVotes: Int)

data class Vote(
        val id: String,
        val idMessage: String,
        val email: String,
        val vote: Int)

data class UserVote(
        val idMessage: String,
        val vote: Int)
