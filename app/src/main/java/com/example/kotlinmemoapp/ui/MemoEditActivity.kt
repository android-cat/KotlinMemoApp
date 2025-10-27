package com.example.kotlinmemoapp.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.kotlinmemoapp.R
import com.example.kotlinmemoapp.data.model.Folder
import com.example.kotlinmemoapp.data.model.Memo
import com.example.kotlinmemoapp.databinding.ActivityMemoEditBinding
import com.example.kotlinmemoapp.viewmodel.FolderViewModel
import com.example.kotlinmemoapp.viewmodel.MemoViewModel
import kotlinx.coroutines.launch

/**
 * メモ編集アクティビティ
 * 
 * 新規メモの作成、または既存メモの編集・削除を行う画面です。
 * タイトル、内容の入力、フォルダの選択が可能です。
 */
class MemoEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoEditBinding
    private lateinit var memoViewModel: MemoViewModel
    private lateinit var folderViewModel: FolderViewModel
    private var currentMemo: Memo? = null  // 編集中のメモ（新規作成時はnull）
    private var memoId: Long = -1  // Intent から受け取るメモID
    private var folders: List<Folder> = emptyList()  // フォルダ一覧
    private var selectedFolderId: Long? = null  // 選択されたフォルダのID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ViewModel の初期化
        memoViewModel = ViewModelProvider(this)[MemoViewModel::class.java]
        folderViewModel = ViewModelProvider(this)[FolderViewModel::class.java]

        // Intent からメモIDを取得（新規作成の場合は -1）
        memoId = intent.getLongExtra("MEMO_ID", -1)

        setupFolderSpinner()
        setupButtons()

        if (memoId != -1L) {
            // 既存メモの編集
            loadMemo()
        } else {
            // 新規作成時は削除ボタンを非表示
            binding.btnDelete.visibility = View.GONE
        }

        // 戻るボタンで画面を閉じる
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * フォルダ選択スピナーをセットアップ
     * フォルダ一覧を読み込み、選択された値を監視します
     */
    private fun setupFolderSpinner() {
        folderViewModel.allFolders.observe(this) { folderList ->
            folders = folderList
            val folderNames = mutableListOf<String>()
            // 「フォルダなし」を最初の選択肢として追加
            folderNames.add(getString(R.string.no_folder))
            folderNames.addAll(folderList.map { it.name })

            // スピナー用アダプターを設定
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, folderNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFolder.adapter = adapter

            // フォルダ選択時の処理
            binding.spinnerFolder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // position 0 は「フォルダなし」、それ以外は該当フォルダのID
                    selectedFolderId = if (position == 0) null else folders[position - 1].id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedFolderId = null
                }
            }

            // 編集時：現在のメモが所属するフォルダを選択状態にする
            currentMemo?.let { memo ->
                memo.folderId?.let { folderId ->
                    val folderIndex = folders.indexOfFirst { it.id == folderId }
                    if (folderIndex != -1) {
                        binding.spinnerFolder.setSelection(folderIndex + 1)
                    }
                }
            }
        }
    }

    /**
     * 保存ボタンと削除ボタンをセットアップ
     */
    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            saveMemo()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    /**
     * 指定されたIDのメモを読み込んで表示
     */
    private fun loadMemo() {
        lifecycleScope.launch {
            val memo = memoViewModel.getMemoById(memoId)
            memo?.let {
                currentMemo = it
                binding.editTitle.setText(it.title)
                binding.editContent.setText(it.content)
                selectedFolderId = it.folderId
            }
        }
    }

    /**
     * メモを保存（新規作成または更新）
     */
    private fun saveMemo() {
        val title = binding.editTitle.text.toString().trim()
        val content = binding.editContent.text.toString().trim()

        // タイトルが空の場合はエラー
        if (title.isEmpty()) {
            Toast.makeText(this, getString(R.string.memo_title_hint), Toast.LENGTH_SHORT).show()
            return
        }

        // 新規作成か更新かを判定してメモオブジェクトを作成
        val memo = if (currentMemo != null) {
            // 既存メモの更新：IDを保持したままデータを更新
            currentMemo!!.copy(
                title = title,
                content = content,
                folderId = selectedFolderId,
                updatedAt = System.currentTimeMillis()
            )
        } else {
            // 新規メモの作成
            Memo(
                title = title,
                content = content,
                folderId = selectedFolderId
            )
        }

        // データベースに保存
        if (currentMemo != null) {
            memoViewModel.update(memo)
        } else {
            memoViewModel.insert(memo)
        }

        Toast.makeText(this, getString(R.string.save), Toast.LENGTH_SHORT).show()
        finish()
    }

    /**
     * メモ削除の確認ダイアログを表示
     */
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirmation))
            .setMessage(getString(R.string.delete_memo_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                // 削除を実行
                currentMemo?.let {
                    memoViewModel.delete(it)
                    Toast.makeText(this, getString(R.string.delete), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
}
