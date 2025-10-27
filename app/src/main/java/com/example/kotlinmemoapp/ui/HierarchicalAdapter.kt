package com.example.kotlinmemoapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmemoapp.data.model.ListItem
import com.example.kotlinmemoapp.databinding.ItemFolderExpandableBinding
import com.example.kotlinmemoapp.databinding.ItemMemoExpandableBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 階層構造リスト用アダプター
 * 
 * フォルダとメモを入れ子構造で表示します。
 * フォルダはタップで展開/折りたたみでき、メモはドラッグ&ドロップで移動できます。
 * 
 * @property onFolderClick フォルダがクリックされた時のコールバック
 * @property onMemoClick メモがクリックされた時のコールバック
 * @property onFolderLongClick フォルダが長押しされた時のコールバック
 * @property onMemoLongClick メモが長押しされた時のコールバック
 */
class HierarchicalAdapter(
    private val onFolderClick: (ListItem.FolderItem) -> Unit,
    private val onMemoClick: (ListItem.MemoItem) -> Unit,
    private val onFolderLongClick: (ListItem.FolderItem) -> Unit,
    private val onMemoLongClick: (ListItem.MemoItem) -> Unit
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_FOLDER = 0
        private const val VIEW_TYPE_MEMO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.FolderItem -> VIEW_TYPE_FOLDER
            is ListItem.MemoItem -> VIEW_TYPE_MEMO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FOLDER -> {
                val binding = ItemFolderExpandableBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FolderViewHolder(binding)
            }
            VIEW_TYPE_MEMO -> {
                val binding = ItemMemoExpandableBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MemoViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListItem.FolderItem -> (holder as FolderViewHolder).bind(item)
            is ListItem.MemoItem -> (holder as MemoViewHolder).bind(item)
        }
    }

    /**
     * フォルダアイテムの ViewHolder
     */
    inner class FolderViewHolder(
        private val binding: ItemFolderExpandableBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(folderItem: ListItem.FolderItem) {
            binding.textFolderName.text = folderItem.folder.name
            binding.textMemoCount.text = "${folderItem.memos.size}件"
            
            // 展開状態に応じてアイコンを変更
            binding.iconExpand.rotation = if (folderItem.isExpanded) 90f else 0f
            
            // フォルダをタップで展開/折りたたみ
            binding.root.setOnClickListener {
                onFolderClick(folderItem)
            }
            
            // 長押しで編集・削除メニューを表示
            binding.root.setOnLongClickListener {
                onFolderLongClick(folderItem)
                true
            }
        }
    }

    /**
     * メモアイテムの ViewHolder
     */
    inner class MemoViewHolder(
        private val binding: ItemMemoExpandableBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(memoItem: ListItem.MemoItem) {
            binding.textTitle.text = memoItem.memo.title
            binding.textContent.text = memoItem.memo.content
            binding.textDate.text = formatDate(memoItem.memo.updatedAt)
            
            // 子要素の場合はインデントを追加
            val indent = if (memoItem.isChild) 48 else 16
            binding.root.setPadding(
                dpToPx(indent),
                binding.root.paddingTop,
                binding.root.paddingRight,
                binding.root.paddingBottom
            )
            
            // メモをタップで編集画面を開く
            binding.root.setOnClickListener {
                onMemoClick(memoItem)
            }
            
            // 長押しでドラッグ開始のハンドルとして機能
            binding.root.setOnLongClickListener {
                onMemoLongClick(memoItem)
                true
            }
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        private fun dpToPx(dp: Int): Int {
            val density = binding.root.context.resources.displayMetrics.density
            return (dp * density).toInt()
        }
    }

    /**
     * リストアイテムの差分を計算するためのコールバック
     */
    class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is ListItem.FolderItem && newItem is ListItem.FolderItem ->
                    oldItem.folder.id == newItem.folder.id
                oldItem is ListItem.MemoItem && newItem is ListItem.MemoItem ->
                    oldItem.memo.id == newItem.memo.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }
}
