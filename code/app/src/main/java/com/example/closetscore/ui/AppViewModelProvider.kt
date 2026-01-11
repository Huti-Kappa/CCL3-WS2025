package com.example.closetscore.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.closetscore.ClosetApplication
import com.example.closetscore.ui.viewmodel.ItemViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        // Item ViewModel
        initializer {
            val itemApplication = this[APPLICATION_KEY] as ClosetApplication
            ItemViewModel(itemApplication.itemRepository)
        }


    }
}