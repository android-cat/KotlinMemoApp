package com.example.kotlinmemoapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kotlinmemoapp.data.model.Memo

/**
 * メモデータアクセスオブジェクト（DAO）
 * 
 * Room データベースに対するメモの CRUD 操作と検索機能を提供します。
 * LiveData を使用してデータの変更を自動的に UI に反映します。
 */
@Dao
interface MemoDao {
    /**
     * すべてのメモを最終更新日時の降順で取得
     * @return メモリストの LiveData（更新日時が新しい順）
     */
    @Query("SELECT * FROM memos ORDER BY updatedAt DESC")
    fun getAllMemos(): LiveData<List<Memo>>

    /**
     * 指定されたフォルダに属するメモを取得
     * @param folderId フォルダID
     * @return 指定フォルダ内のメモリストの LiveData
     */
    @Query("SELECT * FROM memos WHERE folderId = :folderId ORDER BY updatedAt DESC")
    fun getMemosByFolder(folderId: Long): LiveData<List<Memo>>

    /**
     * フォルダに未分類のメモを取得
     * @return フォルダIDがnullのメモリストの LiveData
     */
    @Query("SELECT * FROM memos WHERE folderId IS NULL ORDER BY updatedAt DESC")
    fun getMemosWithoutFolder(): LiveData<List<Memo>>

    /**
     * IDでメモを1件取得
     * @param id メモID
     * @return 該当するメモ、または存在しない場合はnull
     */
    @Query("SELECT * FROM memos WHERE id = :id")
    suspend fun getMemoById(id: Long): Memo?

    /**
     * タイトルまたは内容で部分一致検索
     * @param query 検索キーワード
     * @return 検索条件に一致するメモリストの LiveData
     */
    @Query("SELECT * FROM memos WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchMemos(query: String): LiveData<List<Memo>>

    /**
     * メモを挿入または置換
     * @param memo 挿入するメモ
     * @return 挿入されたメモのID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: Memo): Long

    /**
     * 既存のメモを更新
     * @param memo 更新するメモ
     */
    @Update
    suspend fun updateMemo(memo: Memo)

    /**
     * メモを削除
     * @param memo 削除するメモ
     */
    @Delete
    suspend fun deleteMemo(memo: Memo)

    /**
     * IDでメモを削除
     * @param id 削除するメモのID
     */
    @Query("DELETE FROM memos WHERE id = :id")
    suspend fun deleteMemoById(id: Long)
}
