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

/**
 * メモ ViewModel クラス
 * 
 * MVVM アーキテクチャの ViewModel 層を担当します。
 * UI（Activity/Fragment）とデータ層（Repository）の橋渡しを行い、
 * UI に表示するためのデータとビジネスロジックを管理します。
 * 
 * @property application アプリケーションコンテキスト
 */
class MemoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MemoRepository
    
    /** すべてのメモを監視可能な LiveData として提供 */
    val allMemos: LiveData<List<Memo>>
    
    /** 検索結果を保持する内部用 MutableLiveData */
    private val _searchResults = MutableLiveData<List<Memo>>()
    
    /** 検索結果を外部に公開する読み取り専用 LiveData */
    val searchResults: LiveData<List<Memo>> = _searchResults

    init {
        // データベースインスタンスとリポジトリを初期化
        val memoDao = MemoDatabase.getDatabase(application).memoDao()
        repository = MemoRepository(memoDao)
        allMemos = repository.allMemos
    }

    /**
     * 指定されたフォルダに属するメモを取得
     * @param folderId フォルダID
     * @return フォルダ内のメモリストの LiveData
     */
    fun getMemosByFolder(folderId: Long): LiveData<List<Memo>> {
        return repository.getMemosByFolder(folderId)
    }

    /**
     * フォルダに未分類のメモを取得
     * @return 未分類メモリストの LiveData
     */
    fun getMemosWithoutFolder(): LiveData<List<Memo>> {
        return repository.getMemosWithoutFolder()
    }

    /**
     * IDでメモを1件取得
     * @param id メモID
     * @return 該当するメモ、または存在しない場合はnull
     */
    suspend fun getMemoById(id: Long): Memo? {
        return repository.getMemoById(id)
    }

    /**
     * タイトルまたは内容で検索し、結果を searchResults に格納
     * @param query 検索キーワード
     */
    fun searchMemos(query: String) {
        viewModelScope.launch {
            repository.searchMemos(query).observeForever { memos ->
                _searchResults.postValue(memos)
            }
        }
    }

    /**
     * 新しいメモを挿入
     * @param memo 挿入するメモ
     * @param callback 挿入完了時に呼ばれるコールバック（挿入されたIDを受け取る）
     */
    fun insert(memo: Memo, callback: ((Long) -> Unit)? = null) = viewModelScope.launch {
        val id = repository.insert(memo)
        callback?.invoke(id)
    }

    /**
     * 既存のメモを更新
     * @param memo 更新するメモ
     */
    fun update(memo: Memo) = viewModelScope.launch {
        repository.update(memo)
    }

    /**
     * メモを削除
     * @param memo 削除するメモ
     */
    fun delete(memo: Memo) = viewModelScope.launch {
        repository.delete(memo)
    }

    /**
     * IDでメモを削除
     * @param id 削除するメモのID
     */
    fun deleteById(id: Long) = viewModelScope.launch {
        repository.deleteById(id)
    }
}
