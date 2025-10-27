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

class MemoEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoEditBinding
    private lateinit var memoViewModel: MemoViewModel
    private lateinit var folderViewModel: FolderViewModel
    private var currentMemo: Memo? = null
    private var memoId: Long = -1
    private var folders: List<Folder> = emptyList()
    private var selectedFolderId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        memoViewModel = ViewModelProvider(this)[MemoViewModel::class.java]
        folderViewModel = ViewModelProvider(this)[FolderViewModel::class.java]

        memoId = intent.getLongExtra("MEMO_ID", -1)

        setupFolderSpinner()
        setupButtons()

        if (memoId != -1L) {
            loadMemo()
        } else {
            binding.btnDelete.visibility = View.GONE
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupFolderSpinner() {
        folderViewModel.allFolders.observe(this) { folderList ->
            folders = folderList
            val folderNames = mutableListOf<String>()
            folderNames.add(getString(R.string.no_folder))
            folderNames.addAll(folderList.map { it.name })

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, folderNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFolder.adapter = adapter

            binding.spinnerFolder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedFolderId = if (position == 0) null else folders[position - 1].id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedFolderId = null
                }
            }

            // Set selected folder if editing
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

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            saveMemo()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

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

    private fun saveMemo() {
        val title = binding.editTitle.text.toString().trim()
        val content = binding.editContent.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, getString(R.string.memo_title_hint), Toast.LENGTH_SHORT).show()
            return
        }

        val memo = if (currentMemo != null) {
            currentMemo!!.copy(
                title = title,
                content = content,
                folderId = selectedFolderId,
                updatedAt = System.currentTimeMillis()
            )
        } else {
            Memo(
                title = title,
                content = content,
                folderId = selectedFolderId
            )
        }

        if (currentMemo != null) {
            memoViewModel.update(memo)
        } else {
            memoViewModel.insert(memo)
        }

        Toast.makeText(this, getString(R.string.save), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirmation))
            .setMessage(getString(R.string.delete_memo_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
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
