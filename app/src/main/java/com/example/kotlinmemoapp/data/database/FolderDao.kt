package com.example.kotlinmemoapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kotlinmemoapp.data.model.Folder

/**
 * フォルダデータアクセスオブジェクト（DAO）
 * 
 * Room データベースに対するフォルダの CRUD 操作を提供します。
 * フォルダ削除時はカスケード設定により、関連するメモも自動削除されます。
 */
@Dao
interface FolderDao {
    /**
     * すべてのフォルダを作成日時の降順で取得
     * @return フォルダリストの LiveData（作成日時が新しい順）
     */
    @Query("SELECT * FROM folders ORDER BY createdAt DESC")
    fun getAllFolders(): LiveData<List<Folder>>

    /**
     * IDでフォルダを1件取得
     * @param id フォルダID
     * @return 該当するフォルダ、または存在しない場合はnull
     */
    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getFolderById(id: Long): Folder?

    /**
     * フォルダを挿入または置換
     * @param folder 挿入するフォルダ
     * @return 挿入されたフォルダのID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder): Long

    /**
     * 既存のフォルダを更新
     * @param folder 更新するフォルダ
     */
    @Update
    suspend fun updateFolder(folder: Folder)

    /**
     * フォルダを削除（関連するメモも削除される）
     * @param folder 削除するフォルダ
     */
    @Delete
    suspend fun deleteFolder(folder: Folder)

    /**
     * IDでフォルダを削除（関連するメモも削除される）
     * @param id 削除するフォルダのID
     */
    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: Long)
}
