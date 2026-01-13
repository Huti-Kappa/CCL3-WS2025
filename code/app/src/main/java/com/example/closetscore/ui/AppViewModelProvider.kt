package com.example.closetscore.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.closetscore.ClosetApplication
import com.example.closetscore.ui.viewmodel.ItemViewModel
import com.example.closetscore.ui.viewmodels.TemplateViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {


        initializer {
            val application = this[APPLICATION_KEY] as ClosetApplication
            ItemViewModel(application.itemRepository)
        }


        initializer {
            val application = this[APPLICATION_KEY] as ClosetApplication
            TemplateViewModel(application.templateRepository)
        }
    }
}