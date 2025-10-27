package com.example.kotlinmemoapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmemoapp.data.model.Folder
import com.example.kotlinmemoapp.databinding.ItemFolderBinding

/**
 * フォルダ一覧用 RecyclerView アダプター
 * 
 * フォルダのリストを表示し、各フォルダに対する操作（クリック、編集、削除）を処理します。
 * DiffUtil を使用してリストの変更を自動検出し、必要な部分のみを更新します。
 * 
 * @property onFolderClick フォルダがクリックされた時のコールバック
 * @property onEditClick 編集ボタンがクリックされた時のコールバック
 * @property onDeleteClick 削除ボタンがクリックされた時のコールバック
 */
class FolderAdapter(
    private val onFolderClick: (Folder) -> Unit,
    private val onEditClick: (Folder) -> Unit,
    private val onDeleteClick: (Folder) -> Unit
) : ListAdapter<Folder, FolderAdapter.FolderViewHolder>(FolderDiffCallback()) {

    /**
     * ViewHolder を作成
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FolderViewHolder(binding)
    }

    /**
     * ViewHolder にデータをバインド
     */
    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * フォルダアイテムの ViewHolder
     * 
     * 個々のフォルダカードを保持し、データと操作ボタンを表示します。
     */
    inner class FolderViewHolder(
        private val binding: ItemFolderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * フォルダデータをビューにバインド
         * @param folder 表示するフォルダ
         */
        fun bind(folder: Folder) {
            binding.textFolderName.text = folder.name

            // フォルダカードをタップ：そのフォルダ内のメモを表示
            binding.root.setOnClickListener {
                onFolderClick(folder)
            }

            // 編集ボタン：フォルダ名編集ダイアログを表示
            binding.btnEditFolder.setOnClickListener {
                onEditClick(folder)
            }

            // 削除ボタン：フォルダ削除確認ダイアログを表示
            binding.btnDeleteFolder.setOnClickListener {
                onDeleteClick(folder)
            }
        }
    }

    /**
     * フォルダリストの差分を計算するためのコールバック
     * 
     * DiffUtil がリストの変更を効率的に検出するために使用します。
     */
    class FolderDiffCallback : DiffUtil.ItemCallback<Folder>() {
        /**
         * アイテムが同一かどうかを判定（IDで比較）
         */
        override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * アイテムの内容が同一かどうかを判定（全フィールドで比較）
         */
        override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem == newItem
        }
    }
}
