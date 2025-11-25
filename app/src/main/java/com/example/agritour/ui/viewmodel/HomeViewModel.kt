package com.example.agritour.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agritour.data.Farm
import com.example.agritour.data.FarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = FarmRepository()

    // State: The list of farms to show in the UI
    private val _farms = MutableStateFlow<List<Farm>>(emptyList())
    val farms: StateFlow<List<Farm>> = _farms.asStateFlow()

    init {
        fetchFarms()
    }

    private fun fetchFarms() {
        viewModelScope.launch {
            val fetchedFarms = repository.getFarms()
            _farms.value = fetchedFarms
        }
    }

    // Call this ONLY ONCE from your MainActivity to populate DB
    fun seedDatabase() {
        repository.addDummyData()
    }
}