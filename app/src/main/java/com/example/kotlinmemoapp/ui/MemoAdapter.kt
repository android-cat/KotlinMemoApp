package com.example.kotlinmemoapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmemoapp.data.model.Memo
import com.example.kotlinmemoapp.databinding.ItemMemoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * メモ一覧用 RecyclerView アダプター
 * 
 * メモのリストを効率的に表示するためのアダプターです。
 * DiffUtil を使用してリストの変更を自動検出し、必要な部分のみを更新します。
 * 
 * @property onItemClick メモがタップされた時に呼ばれるコールバック
 */
class MemoAdapter(
    private val onItemClick: (Memo) -> Unit
) : ListAdapter<Memo, MemoAdapter.MemoViewHolder>(MemoDiffCallback()) {

    /**
     * ViewHolder を作成
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = ItemMemoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemoViewHolder(binding)
    }

    /**
     * ViewHolder にデータをバインド
     */
    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * メモアイテムの ViewHolder
     * 
     * 個々のメモカードを保持し、データを表示します。
     */
    inner class MemoViewHolder(
        private val binding: ItemMemoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * メモデータをビューにバインド
         * @param memo 表示するメモ
         */
        fun bind(memo: Memo) {
            binding.textTitle.text = memo.title
            binding.textContent.text = memo.content
            binding.textDate.text = formatDate(memo.updatedAt)
            binding.textFolder.text = ""  // フォルダ名は必要に応じて MainActivity で設定

            // メモカードをタップしたら編集画面を開く
            binding.root.setOnClickListener {
                onItemClick(memo)
            }
        }

        /**
         * UNIXタイムスタンプを日付文字列にフォーマット
         * @param timestamp UNIXタイムスタンプ（ミリ秒）
         * @return yyyy/MM/dd 形式の日付文字列
         */
        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    /**
     * メモリストの差分を計算するためのコールバック
     * 
     * DiffUtil がリストの変更を効率的に検出するために使用します。
     */
    class MemoDiffCallback : DiffUtil.ItemCallback<Memo>() {
        /**
         * アイテムが同一かどうかを判定（IDで比較）
         */
        override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * アイテムの内容が同一かどうかを判定（全フィールドで比較）
         */
        override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
            return oldItem == newItem
        }
    }
}
