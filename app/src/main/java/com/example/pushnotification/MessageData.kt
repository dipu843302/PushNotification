package com.example.pushnotification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageData(
    val title: String,
    val body: String,
    val image: String,
    val id: Int
) : Parcelable