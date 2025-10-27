package com.example.kotlinmemoapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinmemoapp.R
import com.example.kotlinmemoapp.data.model.ListItem
import com.example.kotlinmemoapp.databinding.ActivityMainBinding
import com.example.kotlinmemoapp.viewmodel.FolderViewModel
import com.example.kotlinmemoapp.viewmodel.MemoViewModel

/**
 * メインアクティビティ（階層構造版）
 * 
 * フォルダとメモを入れ子構造で表示します。
 * - フォルダをタップで展開/折りたたみ
 * - メモをドラッグ&ドロップでフォルダ間を移動
 * - 検索機能でメモを絞り込み
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var memoViewModel: MemoViewModel
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var hierarchicalAdapter: HierarchicalAdapter
    
    private val expandedFolderIds = mutableSetOf<Long>()  // 展開中のフォルダIDを記録

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
        setupDragAndDrop()
        setupSearch()
        setupFabs()

        // データを読み込んで表示
        loadHierarchicalList()
    }

    /**
     * RecyclerView と階層構造アダプターをセットアップ
     */
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        
        hierarchicalAdapter = HierarchicalAdapter(
            onFolderClick = { folderItem ->
                // フォルダをタップで展開/折りたたみ
                toggleFolder(folderItem)
            },
            onMemoClick = { memoItem ->
                // メモをタップで編集画面を開く
                val intent = Intent(this, MemoEditActivity::class.java)
                intent.putExtra("MEMO_ID", memoItem.memo.id)
                startActivity(intent)
            },
            onFolderLongClick = { folderItem ->
                // フォルダを長押しで編集・削除メニューを表示
                showFolderMenu(folderItem)
            },
            onMemoLongClick = { memoItem ->
                // メモを長押し（ドラッグ開始のヒント）
                // 実際のドラッグはItemTouchHelperが処理
            }
        )
        
        binding.recyclerView.adapter = hierarchicalAdapter
    }

    /**
     * ドラッグ&ドロップ機能をセットアップ
     */
    private fun setupDragAndDrop() {
        val dragDropCallback = DragDropCallback(hierarchicalAdapter) { memo, newFolderId ->
            // メモが新しいフォルダに移動された
            val updatedMemo = memo.copy(
                folderId = newFolderId,
                updatedAt = System.currentTimeMillis()
            )
            memoViewModel.update(updatedMemo)
            
            // リストを再読み込み
            loadHierarchicalList()
        }
        
        val itemTouchHelper = ItemTouchHelper(dragDropCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    /**
     * 検索機能をセットアップ
     */
    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // 検索キーワードが空の場合は全件表示
                    loadHierarchicalList()
                } else {
                    // 検索キーワードに一致するメモのみ表示
                    searchMemos(newText)
                }
                return true
            }
        })
    }

    /**
     * FABボタンをセットアップ
     */
    private fun setupFabs() {
        // 新規メモ作成
        binding.fabNewMemo.setOnClickListener {
            val intent = Intent(this, MemoEditActivity::class.java)
            startActivity(intent)
        }
        
        // 新規フォルダ作成
        binding.fabNewFolder.setOnClickListener {
            FolderDialogHelper.showNewFolderDialog(this, folderViewModel)
        }
    }

    /**
     * フォルダとメモを階層構造でリスト表示
     */
    private fun loadHierarchicalList() {
        folderViewModel.allFolders.observe(this) { folders ->
            memoViewModel.allMemos.observe(this) { allMemos ->
                val listItems = mutableListOf<ListItem>()
                
                // 各フォルダとそのメモを追加
                folders.forEach { folder ->
                    val folderMemos = allMemos.filter { it.folderId == folder.id }
                    val isExpanded = expandedFolderIds.contains(folder.id)
                    
                    // フォルダアイテムを追加
                    listItems.add(
                        ListItem.FolderItem(
                            folder = folder,
                            memos = folderMemos,
                            isExpanded = isExpanded
                        )
                    )
                    
                    // 展開されている場合はメモも表示
                    if (isExpanded) {
                        folderMemos.forEach { memo ->
                            listItems.add(
                                ListItem.MemoItem(
                                    memo = memo,
                                    isChild = true
                                )
                            )
                        }
                    }
                }
                
                // フォルダに属さないメモを追加
                val uncategorizedMemos = allMemos.filter { it.folderId == null }
                uncategorizedMemos.forEach { memo ->
                    listItems.add(
                        ListItem.MemoItem(
                            memo = memo,
                            isChild = false
                        )
                    )
                }
                
                hierarchicalAdapter.submitList(listItems)
                updateEmptyView(listItems.isEmpty())
            }
        }
    }

    /**
     * メモを検索して表示
     */
    private fun searchMemos(query: String) {
        memoViewModel.searchMemos(query)
        memoViewModel.searchResults.observe(this) { memos ->
            val listItems = memos.map { memo ->
                ListItem.MemoItem(memo = memo, isChild = false)
            }
            hierarchicalAdapter.submitList(listItems)
            updateEmptyView(listItems.isEmpty())
        }
    }

    /**
     * フォルダの展開/折りたたみを切り替え
     */
    private fun toggleFolder(folderItem: ListItem.FolderItem) {
        if (expandedFolderIds.contains(folderItem.folder.id)) {
            expandedFolderIds.remove(folderItem.folder.id)
        } else {
            expandedFolderIds.add(folderItem.folder.id)
        }
        loadHierarchicalList()
    }

    /**
     * フォルダのメニューを表示（編集・削除）
     */
    private fun showFolderMenu(folderItem: ListItem.FolderItem) {
        val options = arrayOf("編集", "削除")
        AlertDialog.Builder(this)
            .setTitle(folderItem.folder.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> FolderDialogHelper.showEditFolderDialog(
                        this,
                        folderItem.folder,
                        folderViewModel
                    )
                    1 -> FolderDialogHelper.showDeleteFolderDialog(
                        this,
                        folderItem.folder,
                        folderViewModel
                    )
                }
            }
            .show()
    }

    /**
     * 空の状態表示を更新
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
        loadHierarchicalList()
    }
}
