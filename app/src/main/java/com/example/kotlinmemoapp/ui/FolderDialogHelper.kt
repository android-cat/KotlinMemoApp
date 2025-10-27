package com.example.kotlinmemoapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kotlinmemoapp.R
import com.example.kotlinmemoapp.data.model.Folder
import com.example.kotlinmemoapp.databinding.DialogFolderBinding
import com.example.kotlinmemoapp.viewmodel.FolderViewModel

/**
 * フォルダダイアログヘルパー
 * 
 * フォルダの作成、編集、削除に関するダイアログを表示するユーティリティクラスです。
 * シングルトンオブジェクトとして実装し、アプリ全体で共通のダイアログロジックを提供します。
 */
object FolderDialogHelper {

    /**
     * 新規フォルダ作成ダイアログを表示
     * 
     * フォルダ名を入力するダイアログを表示し、保存ボタンで新しいフォルダを作成します。
     * 
     * @param context コンテキスト
     * @param folderViewModel フォルダ操作用の ViewModel
     */
    fun showNewFolderDialog(context: Context, folderViewModel: FolderViewModel) {
        val binding = DialogFolderBinding.inflate(LayoutInflater.from(context))
        binding.dialogTitle.text = context.getString(R.string.new_folder)

        AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton(context.getString(R.string.save)) { _, _ ->
                val name = binding.editFolderName.text.toString().trim()
                if (name.isNotEmpty()) {
                    // 入力されたフォルダ名で新規フォルダを作成
                    val folder = Folder(name = name)
                    folderViewModel.insert(folder)
                    Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
                } else {
                    // フォルダ名が空の場合はエラーメッセージを表示
                    Toast.makeText(context, context.getString(R.string.folder_name_hint), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }

    /**
     * フォルダ編集ダイアログを表示
     * 
     * 既存のフォルダ名を編集するダイアログを表示します。
     * 現在のフォルダ名がテキストフィールドに表示され、変更を保存できます。
     * 
     * @param context コンテキスト
     * @param folder 編集対象のフォルダ
     * @param folderViewModel フォルダ操作用の ViewModel
     */
    fun showEditFolderDialog(context: Context, folder: Folder, folderViewModel: FolderViewModel) {
        val binding = DialogFolderBinding.inflate(LayoutInflater.from(context))
        binding.dialogTitle.text = context.getString(R.string.edit_folder)
        binding.editFolderName.setText(folder.name)

        AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton(context.getString(R.string.save)) { _, _ ->
                val name = binding.editFolderName.text.toString().trim()
                if (name.isNotEmpty()) {
                    // フォルダ名を更新
                    val updatedFolder = folder.copy(name = name)
                    folderViewModel.update(updatedFolder)
                    Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show()
                } else {
                    // フォルダ名が空の場合はエラーメッセージを表示
                    Toast.makeText(context, context.getString(R.string.folder_name_hint), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }

    /**
     * フォルダ削除確認ダイアログを表示
     * 
     * フォルダを削除する前に確認ダイアログを表示します。
     * 注意：フォルダを削除すると、そのフォルダに含まれるすべてのメモも削除されます（カスケード削除）。
     * 
     * @param context コンテキスト
     * @param folder 削除対象のフォルダ
     * @param folderViewModel フォルダ操作用の ViewModel
     */
    fun showDeleteFolderDialog(context: Context, folder: Folder, folderViewModel: FolderViewModel) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.delete_confirmation))
            .setMessage(context.getString(R.string.delete_folder_message))
            .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                // フォルダと関連するメモを削除
                folderViewModel.delete(folder)
                Toast.makeText(context, context.getString(R.string.delete), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(context.getString(R.string.no), null)
            .show()
    }
}
