package com.example.sectionedrecyclerview

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class ListItem (
    val type: Int
        ): Parcelable {
    companion object {
        const val TYPE_DATE = 0
        const val TYPE_GENERAL = 1
    }
}