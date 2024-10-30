package br.unisanta.approom.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.unisanta.approom.dao.UserDao
import br.unisanta.approom.model.User

@Database(entities = [User::class], version = 5)
@TypeConverters(Converters::class)

abstract class AppDataBase:RoomDatabase() {
    abstract fun userDao():UserDao
}