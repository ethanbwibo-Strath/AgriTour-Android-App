package com.example.agritour.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agritour.data.Farm
import com.example.agritour.data.FarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = FarmRepository()
    private var allFarmsCache = listOf<Farm>()

    // Tracking active filters (null means "All")
    private val _typeFilter = MutableStateFlow<String?>(null)
    private val _locationFilter = MutableStateFlow<String?>(null)

    // The final filtered list
    private val _farms = MutableStateFlow<List<Farm>>(emptyList())
    val farms: StateFlow<List<Farm>> = _farms.asStateFlow()

    init {
        fetchFarms()
    }

    private fun fetchFarms() {
        viewModelScope.launch {
            allFarmsCache = repository.getFarms()
            applyFilters() // Initial load
        }
    }

    // Called when user selects a specific Type (e.g., "Coffee")
    fun setTypeFilter(type: String?) {
        _typeFilter.value = type
        applyFilters()
    }

    // Called when user selects a specific Location (e.g., "Nairobi")
    fun setLocationFilter(location: String?) {
        _locationFilter.value = location
        applyFilters()
    }

    // The Master Filter Logic
    private fun applyFilters() {
        val currentType = _typeFilter.value
        val currentLocation = _locationFilter.value

        _farms.value = allFarmsCache.filter { farm ->
            val matchType = currentType == null || farm.type.equals(currentType, ignoreCase = true)
            // Simple "contains" check for location (e.g., "Nairobi" matches "Nairobi, Kenya")
            val matchLocation = currentLocation == null || farm.location.contains(currentLocation, ignoreCase = true)

            matchType && matchLocation
        }
    }

    fun getFarmById(id: String): Farm? = allFarmsCache.find { it.id == id }
}