package com.example.kotlinmemoapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * メモエンティティクラス
 * 
 * Room データベースの "memos" テーブルを表現します。
 * フォルダとの外部キー制約により、フォルダ削除時に関連メモも削除されます（CASCADE）。
 * 
 * @property id メモの一意識別子（自動生成）
 * @property title メモのタイトル
 * @property content メモの本文内容
 * @property folderId 所属するフォルダのID（未分類の場合はnull）
 * @property createdAt メモの作成日時（UNIXタイムスタンプ）
 * @property updatedAt メモの最終更新日時（UNIXタイムスタンプ）
 */
@Entity(
    tableName = "memos",
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE  // フォルダ削除時にメモも削除
        )
    ],
    indices = [Index("folderId")]  // フォルダIDにインデックスを作成してクエリを高速化
)
data class Memo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val folderId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
