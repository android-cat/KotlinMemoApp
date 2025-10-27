package com.example.kotlinmemoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.kotlinmemoapp.data.database.MemoDatabase
import com.example.kotlinmemoapp.data.model.Folder
import com.example.kotlinmemoapp.data.repository.FolderRepository
import kotlinx.coroutines.launch

/**
 * フォルダ ViewModel クラス
 * 
 * MVVM アーキテクチャの ViewModel 層を担当します。
 * UI（Activity/Fragment）とデータ層（Repository）の橋渡しを行い、
 * フォルダ管理に関するデータとビジネスロジックを管理します。
 * 
 * @property application アプリケーションコンテキスト
 */
class FolderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FolderRepository
    
    /** すべてのフォルダを監視可能な LiveData として提供 */
    val allFolders: LiveData<List<Folder>>

    init {
        // データベースインスタンスとリポジトリを初期化
        val folderDao = MemoDatabase.getDatabase(application).folderDao()
        repository = FolderRepository(folderDao)
        allFolders = repository.allFolders
    }

    /**
     * IDでフォルダを1件取得
     * @param id フォルダID
     * @return 該当するフォルダ、または存在しない場合はnull
     */
    suspend fun getFolderById(id: Long): Folder? {
        return repository.getFolderById(id)
    }

    /**
     * 新しいフォルダを挿入
     * @param folder 挿入するフォルダ
     * @param callback 挿入完了時に呼ばれるコールバック（挿入されたIDを受け取る）
     */
    fun insert(folder: Folder, callback: ((Long) -> Unit)? = null) = viewModelScope.launch {
        val id = repository.insert(folder)
        callback?.invoke(id)
    }

    /**
     * 既存のフォルダを更新
     * @param folder 更新するフォルダ
     */
    fun update(folder: Folder) = viewModelScope.launch {
        repository.update(folder)
    }

    /**
     * フォルダを削除（関連するメモも削除される）
     * @param folder 削除するフォルダ
     */
    fun delete(folder: Folder) = viewModelScope.launch {
        repository.delete(folder)
    }

    /**
     * IDでフォルダを削除（関連するメモも削除される）
     * @param id 削除するフォルダのID
     */
    fun deleteById(id: Long) = viewModelScope.launch {
        repository.deleteById(id)
    }
}
