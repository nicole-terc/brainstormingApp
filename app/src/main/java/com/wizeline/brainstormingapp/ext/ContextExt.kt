package com.wizeline.brainstormingapp.ext

import android.accounts.AccountManager
import android.content.Context
import android.util.Patterns.EMAIL_ADDRESS

fun Context.getUserEmail() = AccountManager.get(this).accounts
        .map { it.name }
        .firstOrNull { EMAIL_ADDRESS.matcher(it).matches() }
        ?: throw IllegalStateException("It is mandatory to have an email")
