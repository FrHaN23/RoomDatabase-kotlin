package com.example.roomdatabase

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Word::class), version = 1, exportSchema = false)

public abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun WordDAO() : WordDAO

    companion object {
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null
        fun getDatabase(context: Context, scope: CoroutineScope): WordRoomDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            else synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                ).addCallback(WordDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }

        private class WordDatabaseCallback (private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let{
                        database -> scope.launch { populateDatabase(database.WordDAO()) }
                }
            }
        }

        suspend fun populateDatabase(wordDAO: WordDAO){
            wordDAO.deleteAll()

            var word = Word("Hello")
            wordDAO.insert(word)
            word = Word("World!")
            wordDAO.insert(word)
        }
    }
}

