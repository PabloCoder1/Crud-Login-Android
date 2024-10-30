package br.unisanta.approom.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val uid:Int,
    @ColumnInfo(name = "first_name")
    var firstName:String?,
    @ColumnInfo(name = "Email")
    var email:String?,
    @ColumnInfo(name = "Image")
    var image: Bitmap,
)
