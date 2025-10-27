package com.example.kotlinmemoapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kotlinmemoapp.R
import com.example.kotlinmemoapp.data.model.Folder
import com.example.kotlinmemoapp.databinding.DialogFolderBinding
import com.example.kotlinmemoapp.viewmodel.FolderViewModel

object FolderDialogHelper {

    fun showNewFolderDialog(context: Context, folderViewModel: FolderViewModel) {
        val binding = DialogFolderBinding.inflate(LayoutInflater.from(context))
        binding.dialogTitle.text = context.getString(R.string.new_folder)

        AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton(context.getString(R.string.save)) { _, _ ->
                val name = binding.editFolderName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val folder = Folder(name = name)
                    folderViewModel.insert(folder)
                    Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, context.getString(R.string.folder_name_hint), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }

    fun showEditFolderDialog(context: Context, folder: Folder, folderViewModel: FolderViewModel) {
        val binding = DialogFolderBinding.inflate(LayoutInflater.from(context))
        binding.dialogTitle.text = context.getString(R.string.edit_folder)
        binding.editFolderName.setText(folder.name)

        AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton(context.getString(R.string.save)) { _, _ ->
                val name = binding.editFolderName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val updatedFolder = folder.copy(name = name)
                    folderViewModel.update(updatedFolder)
                    Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, context.getString(R.string.folder_name_hint), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }

    fun showDeleteFolderDialog(context: Context, folder: Folder, folderViewModel: FolderViewModel) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.delete_confirmation))
            .setMessage(context.getString(R.string.delete_folder_message))
            .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                folderViewModel.delete(folder)
                Toast.makeText(context, context.getString(R.string.delete), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(context.getString(R.string.no), null)
            .show()
    }
}
