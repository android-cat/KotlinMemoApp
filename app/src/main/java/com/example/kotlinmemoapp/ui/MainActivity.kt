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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var memoViewModel: MemoViewModel
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var memoAdapter: MemoAdapter
    private lateinit var folderAdapter: FolderAdapter
    private var currentTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        memoViewModel = ViewModelProvider(this)[MemoViewModel::class.java]
        folderViewModel = ViewModelProvider(this)[FolderViewModel::class.java]

        setupRecyclerView()
        setupTabs()
        setupSearch()
        setupFab()

        loadMemos()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        
        memoAdapter = MemoAdapter { memo ->
            val intent = Intent(this, MemoEditActivity::class.java)
            intent.putExtra("MEMO_ID", memo.id)
            startActivity(intent)
        }
        
        folderAdapter = FolderAdapter(
            onFolderClick = { folder ->
                // Show memos in folder
                memoViewModel.getMemosByFolder(folder.id).observe(this) { memos ->
                    memoAdapter.submitList(memos)
                    updateEmptyView(memos.isEmpty())
                }
                currentTab = 0
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
            },
            onEditClick = { folder ->
                FolderDialogHelper.showEditFolderDialog(this, folder, folderViewModel)
            },
            onDeleteClick = { folder ->
                FolderDialogHelper.showDeleteFolderDialog(this, folder, folderViewModel)
            }
        )
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                when (currentTab) {
                    0 -> loadMemos()
                    1 -> loadFolders()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (currentTab == 0) {
                    if (newText.isNullOrEmpty()) {
                        loadMemos()
                    } else {
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

    private fun setupFab() {
        binding.fabNewMemo.setOnClickListener {
            when (currentTab) {
                0 -> {
                    val intent = Intent(this, MemoEditActivity::class.java)
                    startActivity(intent)
                }
                1 -> {
                    FolderDialogHelper.showNewFolderDialog(this, folderViewModel)
                }
            }
        }
    }

    private fun loadMemos() {
        binding.recyclerView.adapter = memoAdapter
        memoViewModel.allMemos.observe(this) { memos ->
            memoAdapter.submitList(memos)
            updateEmptyView(memos.isEmpty())
        }
    }

    private fun loadFolders() {
        binding.recyclerView.adapter = folderAdapter
        folderViewModel.allFolders.observe(this) { folders ->
            folderAdapter.submitList(folders)
            updateEmptyView(folders.isEmpty())
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        when (currentTab) {
            0 -> loadMemos()
            1 -> loadFolders()
        }
    }
}
