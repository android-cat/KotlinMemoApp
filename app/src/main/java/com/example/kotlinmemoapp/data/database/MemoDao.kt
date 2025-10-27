package com.example.kotlinmemoapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kotlinmemoapp.data.model.Memo

@Dao
interface MemoDao {
    @Query("SELECT * FROM memos ORDER BY updatedAt DESC")
    fun getAllMemos(): LiveData<List<Memo>>

    @Query("SELECT * FROM memos WHERE folderId = :folderId ORDER BY updatedAt DESC")
    fun getMemosByFolder(folderId: Long): LiveData<List<Memo>>

    @Query("SELECT * FROM memos WHERE folderId IS NULL ORDER BY updatedAt DESC")
    fun getMemosWithoutFolder(): LiveData<List<Memo>>

    @Query("SELECT * FROM memos WHERE id = :id")
    suspend fun getMemoById(id: Long): Memo?

    @Query("SELECT * FROM memos WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchMemos(query: String): LiveData<List<Memo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: Memo): Long

    @Update
    suspend fun updateMemo(memo: Memo)

    @Delete
    suspend fun deleteMemo(memo: Memo)

    @Query("DELETE FROM memos WHERE id = :id")
    suspend fun deleteMemoById(id: Long)
}
