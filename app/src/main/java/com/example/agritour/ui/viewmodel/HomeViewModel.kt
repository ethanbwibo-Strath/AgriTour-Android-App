package com.example.agritour.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agritour.data.Booking
import com.example.agritour.data.Farm
import com.example.agritour.data.FarmRepository
import com.example.agritour.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = FarmRepository()
    private var allFarmsCache = listOf<Farm>()

    // --- STATE ---
    // User Profile
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Farms & Filters
    private val _typeFilter = MutableStateFlow<String?>(null)
    private val _locationFilter = MutableStateFlow<String?>(null)
    private val _farms = MutableStateFlow<List<Farm>>(emptyList())
    val farms: StateFlow<List<Farm>> = _farms.asStateFlow()

    // Bookings
    private val _myBookings = MutableStateFlow<List<Booking>>(emptyList())
    val myBookings: StateFlow<List<Booking>> = _myBookings.asStateFlow()

    private val _currentBooking = MutableStateFlow<Booking?>(null)
    val currentBooking: StateFlow<Booking?> = _currentBooking.asStateFlow()

    private val _myFarms = MutableStateFlow<List<Farm>>(emptyList())
    val myFarms: StateFlow<List<Farm>> = _myFarms.asStateFlow()

    init {
        fetchFarms()
        fetchCurrentUser() // Fetch user immediately on start
    }

    // --- USER LOGIC ---
    fun fetchCurrentUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            viewModelScope.launch {
                val profile = repository.getUserProfile(firebaseUser.uid)
                _currentUser.value = profile

                // If visitor, load their bookings automatically
                if (profile?.role == "visitor") {
                    fetchUserBookings()
                }
            }
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        _currentUser.value = null
        _myBookings.value = emptyList() // Clear data
    }

    // --- FARM LOGIC ---
    private fun fetchFarms() {
        viewModelScope.launch {
            allFarmsCache = repository.getFarms()
            applyFilters()
        }
    }

    fun setTypeFilter(type: String?) {
        _typeFilter.value = type
        applyFilters()
    }

    fun setLocationFilter(location: String?) {
        _locationFilter.value = location
        applyFilters()
    }

    private fun applyFilters() {
        val currentType = _typeFilter.value
        val currentLocation = _locationFilter.value

        _farms.value = allFarmsCache.filter { farm ->
            val matchType = currentType == null || farm.type.equals(currentType, ignoreCase = true)
            val matchLocation = currentLocation == null || farm.location.contains(currentLocation, ignoreCase = true)
            matchType && matchLocation
        }
    }

    fun getFarmById(id: String): Farm? = allFarmsCache.find { it.id == id }

    // --- BOOKING LOGIC ---
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
        // Get the REAL User ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            onResult(false) // Not logged in
            return
        }

        viewModelScope.launch {
            val booking = Booking(
                userId = userId, // Use real ID
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
        // Get the REAL User ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _myBookings.value = repository.getBookingsForUser(userId)
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            val success = repository.cancelBooking(bookingId)
            if (success) {
                fetchUserBookings()
            }
        }
    }

    fun getBookingById(bookingId: String): Booking? {
        return _myBookings.value.find { it.id == bookingId }
    }

    fun loadSingleBooking(bookingId: String) {
        viewModelScope.launch {
            _currentBooking.value = null
            val booking = repository.getBooking(bookingId)
            _currentBooking.value = booking
        }
    }

    fun fetchMyFarms() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _myFarms.value = repository.getFarmsByOwner(userId)
        }
    }

    // ... inside HomeViewModel ...

    fun addNewFarm(
        name: String,
        location: String,
        price: Int,
        type: String,
        description: String,
        imageUri: android.net.Uri?,
        onResult: (Boolean) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            // 1. Upload Image (if exists)
            var imageUrl = ""
            if (imageUri != null) {
                imageUrl = repository.uploadImage(imageUri) ?: ""
                if (imageUrl.isEmpty()) {
                    onResult(false) // Upload failed
                    return@launch
                }
            }

            // 2. Create Farm Object
            val newFarm = Farm(
                ownerId = userId,
                name = name,
                location = location,
                price = price,
                type = type,
                description = description,
                imageUrl = imageUrl,
                rating = 0.0
            )

            // 3. Save to Database
            val success = repository.addFarm(newFarm)
            if (success) fetchMyFarms() // Refresh the list
            onResult(success)
        }
    }

}