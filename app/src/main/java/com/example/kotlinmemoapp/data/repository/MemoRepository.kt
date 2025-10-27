package com.example.kotlinmemoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.kotlinmemoapp.data.database.MemoDao
import com.example.kotlinmemoapp.data.model.Memo

class MemoRepository(private val memoDao: MemoDao) {
    
    val allMemos: LiveData<List<Memo>> = memoDao.getAllMemos()
    
    fun getMemosByFolder(folderId: Long): LiveData<List<Memo>> {
        return memoDao.getMemosByFolder(folderId)
    }
    
    fun getMemosWithoutFolder(): LiveData<List<Memo>> {
        return memoDao.getMemosWithoutFolder()
    }
    
    suspend fun getMemoById(id: Long): Memo? {
        return memoDao.getMemoById(id)
    }
    
    fun searchMemos(query: String): LiveData<List<Memo>> {
        return memoDao.searchMemos(query)
    }
    
    suspend fun insert(memo: Memo): Long {
        return memoDao.insertMemo(memo)
    }
    
    suspend fun update(memo: Memo) {
        memoDao.updateMemo(memo)
    }
    
    suspend fun delete(memo: Memo) {
        memoDao.deleteMemo(memo)
    }
    
    suspend fun deleteById(id: Long) {
        memoDao.deleteMemoById(id)
    }
}
