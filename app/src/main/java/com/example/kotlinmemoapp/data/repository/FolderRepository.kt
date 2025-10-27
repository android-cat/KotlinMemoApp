package com.example.kotlinmemoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.kotlinmemoapp.data.database.FolderDao
import com.example.kotlinmemoapp.data.model.Folder

/**
 * フォルダリポジトリクラス
 * 
 * リポジトリパターンを実装し、フォルダデータソース（DAO）を抽象化します。
 * ViewModel とデータベース間の仲介役となり、データアクセスロジックを管理します。
 * 
 * @property folderDao フォルダデータアクセスオブジェクト
 */
class FolderRepository(private val folderDao: FolderDao) {
    
    /** すべてのフォルダを監視可能な LiveData として提供 */
    val allFolders: LiveData<List<Folder>> = folderDao.getAllFolders()
    
    /**
     * IDでフォルダを1件取得
     * @param id フォルダID
     * @return 該当するフォルダ、または存在しない場合はnull
     */
    suspend fun getFolderById(id: Long): Folder? {
        return folderDao.getFolderById(id)
    }
    
    /**
     * 新しいフォルダを挿入
     * @param folder 挿入するフォルダ
     * @return 挿入されたフォルダのID
     */
    suspend fun insert(folder: Folder): Long {
        return folderDao.insertFolder(folder)
    }
    
    /**
     * 既存のフォルダを更新
     * @param folder 更新するフォルダ
     */
    suspend fun update(folder: Folder) {
        folderDao.updateFolder(folder)
    }
    
    /**
     * フォルダを削除（関連するメモも削除される）
     * @param folder 削除するフォルダ
     */
    suspend fun delete(folder: Folder) {
        folderDao.deleteFolder(folder)
    }
    
    /**
     * IDでフォルダを削除（関連するメモも削除される）
     * @param id 削除するフォルダのID
     */
    suspend fun deleteById(id: Long) {
        folderDao.deleteFolderById(id)
    }
}
