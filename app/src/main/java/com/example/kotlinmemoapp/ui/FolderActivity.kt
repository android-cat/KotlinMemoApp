package com.example.kotlinmemoapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinmemoapp.databinding.ActivityFolderBinding
import com.example.kotlinmemoapp.viewmodel.FolderViewModel

/**
 * フォルダ管理アクティビティ
 * 
 * フォルダ一覧を表示し、フォルダの作成・編集・削除を行う専用画面です。
 * MainActivity のフォルダタブとは別に、独立した画面としてフォルダ管理を提供します。
 * 
 * 主な機能：
 * - フォルダ一覧の表示
 * - 新規フォルダの作成（FAB）
 * - フォルダ名の編集
 * - フォルダの削除（関連メモも削除）
 */
class FolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFolderBinding
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var folderAdapter: FolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ツールバーを設定し、戻るボタンを表示
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ViewModel の初期化
        folderViewModel = ViewModelProvider(this)[FolderViewModel::class.java]

        // 各コンポーネントのセットアップ
        setupRecyclerView()
        setupFab()
        loadFolders()

        // 戻るボタンで画面を閉じる
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * RecyclerView とアダプターをセットアップ
     * フォルダリストの表示と、各フォルダに対する操作を設定します
     */
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        
        // フォルダアダプター：各種操作のコールバックを設定
        folderAdapter = FolderAdapter(
            onFolderClick = { folder ->
                // フォルダをタップ：画面を閉じて前の画面に戻る
                // （MainActivity でフォルダ内のメモを表示する想定）
                finish()
            },
            onEditClick = { folder ->
                // 編集ボタン：フォルダ編集ダイアログを表示
                FolderDialogHelper.showEditFolderDialog(this, folder, folderViewModel)
            },
            onDeleteClick = { folder ->
                // 削除ボタン：フォルダ削除確認ダイアログを表示
                FolderDialogHelper.showDeleteFolderDialog(this, folder, folderViewModel)
            }
        )
        
        binding.recyclerView.adapter = folderAdapter
    }

    /**
     * FAB（Floating Action Button）をセットアップ
     * 新規フォルダ作成ダイアログを表示します
     */
    private fun setupFab() {
        binding.fabNewFolder.setOnClickListener {
            FolderDialogHelper.showNewFolderDialog(this, folderViewModel)
        }
    }

    /**
     * フォルダ一覧を読み込んで表示
     * LiveData を監視し、データベースの変更を自動的に UI に反映します
     */
    private fun loadFolders() {
        folderViewModel.allFolders.observe(this) { folders ->
            folderAdapter.submitList(folders)
            updateEmptyView(folders.isEmpty())
        }
    }

    /**
     * 空の状態表示を更新
     * フォルダが存在しない場合は空の状態メッセージを表示します
     * 
     * @param isEmpty フォルダリストが空かどうか
     */
    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }
    }
}
