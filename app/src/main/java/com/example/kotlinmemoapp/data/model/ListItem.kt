package com.example.kotlinmemoapp.data.model

/**
 * リストアイテムの基底クラス
 * 
 * RecyclerView で階層構造を表現するため、フォルダとメモを統一的に扱います。
 * sealed class により、型安全にフォルダとメモを区別できます。
 */
sealed class ListItem {
    /**
     * フォルダアイテム
     * 
     * @property folder フォルダデータ
     * @property memos このフォルダに含まれるメモのリスト
     * @property isExpanded フォルダが展開されているかどうか
     */
    data class FolderItem(
        val folder: Folder,
        val memos: List<Memo> = emptyList(),
        var isExpanded: Boolean = false
    ) : ListItem()
    
    /**
     * メモアイテム
     * 
     * @property memo メモデータ
     * @property isChild フォルダの子要素かどうか（インデント表示用）
     */
    data class MemoItem(
        val memo: Memo,
        val isChild: Boolean = false
    ) : ListItem()
}
