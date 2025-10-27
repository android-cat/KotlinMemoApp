package com.example.kotlinmemoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kotlinmemoapp.data.database.MemoDatabase
import com.example.kotlinmemoapp.data.model.Memo
import com.example.kotlinmemoapp.data.repository.MemoRepository
import kotlinx.coroutines.launch

class MemoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MemoRepository
    val allMemos: LiveData<List<Memo>>
    
    private val _searchResults = MutableLiveData<List<Memo>>()
    val searchResults: LiveData<List<Memo>> = _searchResults

    init {
        val memoDao = MemoDatabase.getDatabase(application).memoDao()
        repository = MemoRepository(memoDao)
        allMemos = repository.allMemos
    }

    fun getMemosByFolder(folderId: Long): LiveData<List<Memo>> {
        return repository.getMemosByFolder(folderId)
    }

    fun getMemosWithoutFolder(): LiveData<List<Memo>> {
        return repository.getMemosWithoutFolder()
    }

    suspend fun getMemoById(id: Long): Memo? {
        return repository.getMemoById(id)
    }

    fun searchMemos(query: String) {
        viewModelScope.launch {
            repository.searchMemos(query).observeForever { memos ->
                _searchResults.postValue(memos)
            }
        }
    }

    fun insert(memo: Memo, callback: ((Long) -> Unit)? = null) = viewModelScope.launch {
        val id = repository.insert(memo)
        callback?.invoke(id)
    }

    fun update(memo: Memo) = viewModelScope.launch {
        repository.update(memo)
    }

    fun delete(memo: Memo) = viewModelScope.launch {
        repository.delete(memo)
    }

    fun deleteById(id: Long) = viewModelScope.launch {
        repository.deleteById(id)
    }
}
