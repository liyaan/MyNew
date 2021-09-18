package com.liyaan.mynew

import android.os.Parcel
import android.os.Parcelable

data class BookInfo(
    var bookName: String?,
    var bookAuthor:String?,
    var bookYear:String?):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(bookName)
        parcel?.writeString(bookAuthor)
        parcel?.writeString(bookYear)
    }
    override fun describeContents(): Int {
        return 0
    }
    fun readFromParcel(parcel: Parcel){
        this.bookName = parcel.readString()
        this.bookAuthor = parcel.readString()
        this.bookYear = parcel.readString()
    }
    companion object CREATOR : Parcelable.Creator<BookInfo> {
        override fun createFromParcel(parcel: Parcel): BookInfo {
            return BookInfo(parcel)
        }

        override fun newArray(size: Int): Array<BookInfo?> {
            return arrayOfNulls(size)
        }
    }
}

