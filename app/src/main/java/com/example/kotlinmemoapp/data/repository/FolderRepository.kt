package com.example.kotlinmemoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.kotlinmemoapp.data.database.FolderDao
import com.example.kotlinmemoapp.data.model.Folder

class FolderRepository(private val folderDao: FolderDao) {
    
    val allFolders: LiveData<List<Folder>> = folderDao.getAllFolders()
    
    suspend fun getFolderById(id: Long): Folder? {
        return folderDao.getFolderById(id)
    }
    
    suspend fun insert(folder: Folder): Long {
        return folderDao.insertFolder(folder)
    }
    
    suspend fun update(folder: Folder) {
        folderDao.updateFolder(folder)
    }
    
    suspend fun delete(folder: Folder) {
        folderDao.deleteFolder(folder)
    }
    
    suspend fun deleteById(id: Long) {
        folderDao.deleteFolderById(id)
    }
}
