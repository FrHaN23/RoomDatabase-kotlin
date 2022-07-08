package com.example.roomdatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WordViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: WordRepository
    val allWords: LiveData<List<Word>>

    init{
        val wordsDAO = WordRoomDatabase.getDatabase(application,viewModelScope).WordDAO()

        repository = WordRepository(wordsDAO)
        allWords = repository.allWords
    }

    fun insert(word: Word) = viewModelScope.launch{
        repository.insert(word)
    }
}