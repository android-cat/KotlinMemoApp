package com.example.kotlinmemoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.kotlinmemoapp.data.database.MemoDatabase
import com.example.kotlinmemoapp.data.model.Folder
import com.example.kotlinmemoapp.data.repository.FolderRepository
import kotlinx.coroutines.launch

class FolderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FolderRepository
    val allFolders: LiveData<List<Folder>>

    init {
        val folderDao = MemoDatabase.getDatabase(application).folderDao()
        repository = FolderRepository(folderDao)
        allFolders = repository.allFolders
    }

    suspend fun getFolderById(id: Long): Folder? {
        return repository.getFolderById(id)
    }

    fun insert(folder: Folder, callback: ((Long) -> Unit)? = null) = viewModelScope.launch {
        val id = repository.insert(folder)
        callback?.invoke(id)
    }

    fun update(folder: Folder) = viewModelScope.launch {
        repository.update(folder)
    }

    fun delete(folder: Folder) = viewModelScope.launch {
        repository.delete(folder)
    }

    fun deleteById(id: Long) = viewModelScope.launch {
        repository.deleteById(id)
    }
}
