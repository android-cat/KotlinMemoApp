package com.example.kotlinmemoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * フォルダエンティティクラス
 * 
 * Room データベースの "folders" テーブルを表現します。
 * メモを分類・整理するためのフォルダ情報を保持します。
 * 
 * @property id フォルダの一意識別子（自動生成）
 * @property name フォルダ名
 * @property createdAt フォルダの作成日時（UNIXタイムスタンプ）
 */
@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
