package com.example.kotlinmemoapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinmemoapp.databinding.ActivityFolderBinding
import com.example.kotlinmemoapp.viewmodel.FolderViewModel

class FolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFolderBinding
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var folderAdapter: FolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        folderViewModel = ViewModelProvider(this)[FolderViewModel::class.java]

        setupRecyclerView()
        setupFab()
        loadFolders()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        
        folderAdapter = FolderAdapter(
            onFolderClick = { folder ->
                // Navigate back or handle folder selection
                finish()
            },
            onEditClick = { folder ->
                FolderDialogHelper.showEditFolderDialog(this, folder, folderViewModel)
            },
            onDeleteClick = { folder ->
                FolderDialogHelper.showDeleteFolderDialog(this, folder, folderViewModel)
            }
        )
        
        binding.recyclerView.adapter = folderAdapter
    }

    private fun setupFab() {
        binding.fabNewFolder.setOnClickListener {
            FolderDialogHelper.showNewFolderDialog(this, folderViewModel)
        }
    }

    private fun loadFolders() {
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
}
