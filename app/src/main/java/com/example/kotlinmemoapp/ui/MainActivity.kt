package com.example.kotlinmemoapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinmemoapp.databinding.ActivityMainBinding
import com.example.kotlinmemoapp.viewmodel.FolderViewModel
import com.example.kotlinmemoapp.viewmodel.MemoViewModel
import com.google.android.material.tabs.TabLayout

/**
 * メインアクティビティ
 * 
 * アプリのメイン画面を表示します。
 * - メモ一覧タブ：すべてのメモを表示・検索
 * - フォルダタブ：フォルダ一覧を表示・管理
 * タブの切り替え、検索機能、FAB による新規作成を提供します。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var memoViewModel: MemoViewModel
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var memoAdapter: MemoAdapter
    private lateinit var folderAdapter: FolderAdapter
    private var currentTab = 0  // 現在選択中のタブ（0: メモ, 1: フォルダ）

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // ViewModel の初期化
        memoViewModel = ViewModelProvider(this)[MemoViewModel::class.java]
        folderViewModel = ViewModelProvider(this)[FolderViewModel::class.java]

        // 各コンポーネントのセットアップ
        setupRecyclerView()
        setupTabs()
        setupSearch()
        setupFab()

        // 初期表示としてメモ一覧を読み込み
        loadMemos()
    }

    /**
     * RecyclerView とアダプターをセットアップ
     * メモとフォルダの2種類のアダプターを用意します
     */
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        
        // メモアダプター：メモをタップしたら編集画面を開く
        memoAdapter = MemoAdapter { memo ->
            val intent = Intent(this, MemoEditActivity::class.java)
            intent.putExtra("MEMO_ID", memo.id)
            startActivity(intent)
        }
        
        // フォルダアダプター：各種操作のコールバックを設定
        folderAdapter = FolderAdapter(
            onFolderClick = { folder ->
                // フォルダをタップ：そのフォルダ内のメモを表示
                memoViewModel.getMemosByFolder(folder.id).observe(this) { memos ->
                    memoAdapter.submitList(memos)
                    updateEmptyView(memos.isEmpty())
                }
                currentTab = 0
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
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
    }

    /**
     * タブレイアウトをセットアップ
     * メモタブとフォルダタブの切り替えを処理します
     */
    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                when (currentTab) {
                    0 -> loadMemos()      // メモタブが選択された
                    1 -> loadFolders()    // フォルダタブが選択された
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    /**
     * 検索機能をセットアップ
     * SearchView による、メモのタイトル・内容のリアルタイム検索を実装
     */
    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // メモタブでのみ検索を有効化
                if (currentTab == 0) {
                    if (newText.isNullOrEmpty()) {
                        // 検索キーワードが空の場合はすべてのメモを表示
                        loadMemos()
                    } else {
                        // 検索キーワードに一致するメモを表示
                        memoViewModel.searchMemos(newText)
                        memoViewModel.searchResults.observe(this@MainActivity) { memos ->
                            memoAdapter.submitList(memos)
                            updateEmptyView(memos.isEmpty())
                        }
                    }
                }
                return true
            }
        })
    }

    /**
     * FAB（Floating Action Button）をセットアップ
     * 現在のタブに応じて新規メモ作成またはフォルダ作成を実行
     */
    private fun setupFab() {
        binding.fabNewMemo.setOnClickListener {
            when (currentTab) {
                0 -> {
                    // メモタブ：新規メモ作成画面を開く
                    val intent = Intent(this, MemoEditActivity::class.java)
                    startActivity(intent)
                }
                1 -> {
                    // フォルダタブ：新規フォルダ作成ダイアログを表示
                    FolderDialogHelper.showNewFolderDialog(this, folderViewModel)
                }
            }
        }
    }

    /**
     * メモ一覧を読み込んで表示
     */
    private fun loadMemos() {
        binding.recyclerView.adapter = memoAdapter
        memoViewModel.allMemos.observe(this) { memos ->
            memoAdapter.submitList(memos)
            updateEmptyView(memos.isEmpty())
        }
    }

    /**
     * フォルダ一覧を読み込んで表示
     */
    private fun loadFolders() {
        binding.recyclerView.adapter = folderAdapter
        folderViewModel.allFolders.observe(this) { folders ->
            folderAdapter.submitList(folders)
            updateEmptyView(folders.isEmpty())
        }
    }

    /**
     * 空の状態表示を更新
     * @param isEmpty リストが空かどうか
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

    /**
     * 画面再表示時にデータを再読み込み
     */
    override fun onResume() {
        super.onResume()
        when (currentTab) {
            0 -> loadMemos()
            1 -> loadFolders()
        }
    }
}
