package com.example.kotlinmemoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.kotlinmemoapp.data.database.MemoDao
import com.example.kotlinmemoapp.data.model.Memo

/**
 * メモリポジトリクラス
 * 
 * リポジトリパターンを実装し、データソース（DAO）を抽象化します。
 * ViewModel とデータベース間の仲介役となり、データアクセスロジックを管理します。
 * 
 * @property memoDao メモデータアクセスオブジェクト
 */
class MemoRepository(private val memoDao: MemoDao) {
    
    /** すべてのメモを監視可能な LiveData として提供 */
    val allMemos: LiveData<List<Memo>> = memoDao.getAllMemos()
    
    /**
     * 指定されたフォルダに属するメモを取得
     * @param folderId フォルダID
     * @return フォルダ内のメモリストの LiveData
     */
    fun getMemosByFolder(folderId: Long): LiveData<List<Memo>> {
        return memoDao.getMemosByFolder(folderId)
    }
    
    /**
     * フォルダに未分類のメモを取得
     * @return 未分類メモリストの LiveData
     */
    fun getMemosWithoutFolder(): LiveData<List<Memo>> {
        return memoDao.getMemosWithoutFolder()
    }
    
    /**
     * IDでメモを1件取得
     * @param id メモID
     * @return 該当するメモ、または存在しない場合はnull
     */
    suspend fun getMemoById(id: Long): Memo? {
        return memoDao.getMemoById(id)
    }
    
    /**
     * タイトルまたは内容で検索
     * @param query 検索キーワード
     * @return 検索結果のメモリストの LiveData
     */
    fun searchMemos(query: String): LiveData<List<Memo>> {
        return memoDao.searchMemos(query)
    }
    
    /**
     * 新しいメモを挿入
     * @param memo 挿入するメモ
     * @return 挿入されたメモのID
     */
    suspend fun insert(memo: Memo): Long {
        return memoDao.insertMemo(memo)
    }
    
    /**
     * 既存のメモを更新
     * @param memo 更新するメモ
     */
    suspend fun update(memo: Memo) {
        memoDao.updateMemo(memo)
    }
    
    /**
     * メモを削除
     * @param memo 削除するメモ
     */
    suspend fun delete(memo: Memo) {
        memoDao.deleteMemo(memo)
    }
    
    /**
     * IDでメモを削除
     * @param id 削除するメモのID
     */
    suspend fun deleteById(id: Long) {
        memoDao.deleteMemoById(id)
    }
}
