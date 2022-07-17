package com.layrin.smsapp.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.layrin.smsapp.repository.DaoRepository
import com.layrin.smsapp.repository.ProviderRepository

class MainViewModelFactory(
    private val providerRepository: ProviderRepository,
    private val daoRepository: DaoRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(providerRepository, daoRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}