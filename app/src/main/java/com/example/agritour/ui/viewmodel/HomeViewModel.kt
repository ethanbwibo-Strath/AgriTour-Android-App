package com.example.agritour.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agritour.data.Booking
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
    private val _myBookings = MutableStateFlow<List<Booking>>(emptyList())
    val myBookings: StateFlow<List<Booking>> = _myBookings.asStateFlow()

    private val _currentBooking = MutableStateFlow<Booking?>(null)
    val currentBooking: StateFlow<Booking?> = _currentBooking.asStateFlow()

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

    // ... inside HomeViewModel class ...

    // Helper to save booking
    fun createBooking(
        farmId: String,
        farmName: String,
        date: String,
        time: String,
        groupSize: Int,
        totalPrice: Int,
        paymentMethod: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val booking = Booking(
                farmId = farmId,
                farmName = farmName,
                date = date,
                time = time,
                groupSize = groupSize,
                totalPrice = totalPrice,
                paymentMethod = paymentMethod
            )
            val success = repository.saveBooking(booking)
            onResult(success)
        }
    }

    fun fetchUserBookings() {
        viewModelScope.launch {
            // "test_user_1" matches the hardcoded ID we used in BookingScreen
            _myBookings.value = repository.getBookingsForUser("test_user_1")
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            val success = repository.cancelBooking(bookingId)
            if (success) {
                // Refresh the list so the UI updates immediately
                fetchUserBookings()
            }
        }
    }

    fun getBookingById(bookingId: String): Booking? {
        return _myBookings.value.find { it.id == bookingId }
    }

    fun loadSingleBooking(bookingId: String) {
        viewModelScope.launch {
            // Clear previous data first so we don't show old info
            _currentBooking.value = null
            val booking = repository.getBooking(bookingId)
            _currentBooking.value = booking
        }
    }
}