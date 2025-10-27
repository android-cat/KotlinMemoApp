package com.example.kotlinmemoapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotlinmemoapp.data.model.Folder
import com.example.kotlinmemoapp.data.model.Memo

/**
 * メモアプリの Room データベースクラス
 * 
 * Memo と Folder の2つのエンティティを管理します。
 * シングルトンパターンで実装し、アプリ全体で単一のデータベースインスタンスを使用します。
 * 
 * @property memoDao メモデータにアクセスするための DAO
 * @property folderDao フォルダデータにアクセスするための DAO
 */
@Database(
    entities = [Memo::class, Folder::class],  // データベースに含まれるエンティティ
    version = 1,  // データベースのバージョン番号
    exportSchema = false  // スキーマのエクスポートを無効化
)
abstract class MemoDatabase : RoomDatabase() {
    /** メモデータアクセスオブジェクトを取得 */
    abstract fun memoDao(): MemoDao
    
    /** フォルダデータアクセスオブジェクトを取得 */
    abstract fun folderDao(): FolderDao

    companion object {
        // マルチスレッドからの可視性を保証
        @Volatile
        private var INSTANCE: MemoDatabase? = null

        /**
         * データベースインスタンスを取得
         * 
         * シングルトンパターンで実装し、既存のインスタンスがあればそれを返し、
         * なければ新しく作成します。スレッドセーフな実装です。
         * 
         * @param context アプリケーションコンテキスト
         * @return データベースインスタンス
         */
        fun getDatabase(context: Context): MemoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoDatabase::class.java,
                    "memo_database"  // データベースファイル名
                )
                    .fallbackToDestructiveMigration()  // マイグレーション失敗時はデータベースを再作成
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
