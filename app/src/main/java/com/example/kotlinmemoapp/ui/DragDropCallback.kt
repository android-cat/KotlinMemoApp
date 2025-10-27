package com.example.kotlinmemoapp.ui

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmemoapp.data.model.ListItem

/**
 * ドラッグ&ドロップ機能を提供する ItemTouchHelper.Callback
 * 
 * メモをドラッグして別のフォルダに移動したり、フォルダから出したりできます。
 * 
 * @property onMemoMoved メモが移動された時のコールバック（メモ、新しいフォルダID）
 */
class DragDropCallback(
    private val adapter: HierarchicalAdapter,
    private val onMemoMoved: (memo: com.example.kotlinmemoapp.data.model.Memo, newFolderId: Long?) -> Unit
) : ItemTouchHelper.Callback() {

    private var draggedItem: ListItem.MemoItem? = null
    private var targetFolderId: Long? = null

    /**
     * ドラッグとドロップの方向を指定
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // メモのみドラッグ可能
        val position = viewHolder.adapterPosition
        if (position == RecyclerView.NO_POSITION) return 0
        
        val item = adapter.currentList.getOrNull(position)
        if (item is ListItem.MemoItem) {
            // 上下にドラッグ可能
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }
        return 0
    }

    /**
     * アイテムが移動された時の処理
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        
        if (fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION) {
            return false
        }

        val fromItem = adapter.currentList.getOrNull(fromPosition)
        val toItem = adapter.currentList.getOrNull(toPosition)

        // メモをドラッグ中
        if (fromItem is ListItem.MemoItem) {
            draggedItem = fromItem
            
            // ドロップ先がフォルダの場合、そのフォルダIDを記録
            when (toItem) {
                is ListItem.FolderItem -> {
                    targetFolderId = toItem.folder.id
                }
                is ListItem.MemoItem -> {
                    // 同じフォルダ内のメモにドロップした場合
                    targetFolderId = toItem.memo.folderId
                }
                null -> {
                    targetFolderId = null
                }
            }
        }
        
        return true
    }

    /**
     * スワイプ操作の処理（使用しない）
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // スワイプは使用しない
    }

    /**
     * ドラッグ中はアイテムを半透明にする
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
        }
    }

    /**
     * ドラッグ終了時の処理
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        
        viewHolder.itemView.alpha = 1.0f
        
        // ドラッグしたメモを新しいフォルダに移動
        draggedItem?.let { memoItem ->
            if (memoItem.memo.folderId != targetFolderId) {
                onMemoMoved(memoItem.memo, targetFolderId)
            }
        }
        
        draggedItem = null
        targetFolderId = null
    }

    /**
     * 長押しでドラッグを開始
     */
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    /**
     * スワイプは無効化
     */
    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }
}
