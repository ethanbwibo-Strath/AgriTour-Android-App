package com.example.agritour.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agritour.data.Booking
import com.example.agritour.data.ChatMessage
import com.example.agritour.data.Conversation
import com.example.agritour.data.Farm
import com.example.agritour.data.FarmRepository
import com.example.agritour.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = FarmRepository()
    private var allFarmsCache = listOf<Farm>()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()
    private val _typeFilter = MutableStateFlow<String?>(null)
    private val _locationFilter = MutableStateFlow<String?>(null)
    private val _farms = MutableStateFlow<List<Farm>>(emptyList())
    val farms: StateFlow<List<Farm>> = _farms.asStateFlow()
    private val _myBookings = MutableStateFlow<List<Booking>>(emptyList())
    val myBookings: StateFlow<List<Booking>> = _myBookings.asStateFlow()
    private val _currentBooking = MutableStateFlow<Booking?>(null)
    val currentBooking: StateFlow<Booking?> = _currentBooking.asStateFlow()
    private val _myFarms = MutableStateFlow<List<Farm>>(emptyList())
    val myFarms: StateFlow<List<Farm>> = _myFarms.asStateFlow()
    private val _incomingBookings = MutableStateFlow<List<Booking>>(emptyList())
    val incomingBookings: StateFlow<List<Booking>> = _incomingBookings.asStateFlow()
    private val _currentFarmOwner = MutableStateFlow<UserProfile?>(null)
    val currentFarmOwner: StateFlow<UserProfile?> = _currentFarmOwner.asStateFlow()
    private val _availableTypes = MutableStateFlow<List<String>>(listOf("All"))
    val availableTypes: StateFlow<List<String>> = _availableTypes.asStateFlow()

    private val _availableLocations = MutableStateFlow<List<String>>(listOf("All"))
    val availableLocations: StateFlow<List<String>> = _availableLocations.asStateFlow()

    private val _priceRange = MutableStateFlow(0f..Float.MAX_VALUE)
    val priceRange: StateFlow<ClosedFloatingPointRange<Float>> = _priceRange.asStateFlow()

    private val _maxPriceInDb = MutableStateFlow(5000f) // Default fallback
    val maxPriceInDb: StateFlow<Float> = _maxPriceInDb.asStateFlow()

    private val _minRating = MutableStateFlow(0.0)
    val minRating: StateFlow<Double> = _minRating.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    init {
        fetchFarms()
        fetchCurrentUser()
        fetchUserProfile()
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

    fun fetchUserProfile() {
        val currentUser = auth.currentUser ?: return

        // Listen for real-time updates from Firestore
        db.collection("users").document(currentUser.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    _userProfile.value = snapshot.toObject(UserProfile::class.java)
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

            // Calculate Unique Options
            val types = listOf("All") + allFarmsCache.map { it.type }.distinct().sorted()
            val locations = listOf("All") + allFarmsCache.map { it.location }.distinct().sorted()

            _availableTypes.value = types
            _availableLocations.value = locations

            val maxPrice = allFarmsCache.maxOfOrNull { it.price }?.toFloat() ?: 5000f
            _maxPriceInDb.value = maxPrice
            _priceRange.value = 0f..maxPrice

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

    fun setPriceRange(range: ClosedFloatingPointRange<Float>) {
        _priceRange.value = range
        applyFilters()
    }

    fun setMinRating(rating: Double) {
        _minRating.value = rating
        applyFilters()
    }

    private fun applyFilters() {
        val currentType = _typeFilter.value
        val currentLocation = _locationFilter.value
        val currentPrice = _priceRange.value
        val currentRating = _minRating.value

        _farms.value = allFarmsCache.filter { farm ->
            // 1. String Matches
            val matchType = currentType == null || farm.type.equals(currentType, ignoreCase = true)
            val matchLocation = currentLocation == null || farm.location.contains(currentLocation, ignoreCase = true)

            // 2. Number Matches
            val matchPrice = farm.price.toFloat() >= currentPrice.start && farm.price.toFloat() <= currentPrice.endInclusive
            val matchRating = farm.rating >= currentRating

            matchType && matchLocation && matchPrice && matchRating
        }
    }

    fun getFarmById(id: String): Farm? = allFarmsCache.find { it.id == id }

    // --- BOOKING LOGIC ---
    fun createBooking(
        farmId: String,
        farmName: String,
        farmOwnerId: String,
        date: String,
        time: String,
        groupSize: Int,
        totalPrice: Int,
        paymentMethod: String,
        onResult: (Boolean) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            val booking = Booking(
                userId = userId,
                farmId = farmId,
                farmOwnerId = farmOwnerId,
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

    fun fetchIncomingBookings() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _incomingBookings.value = repository.getBookingsForOwner(userId)
        }
    }

    fun fetchFarmOwner(userId: String) {
        viewModelScope.launch {
            _currentFarmOwner.value = null // Clear previous owner
            val owner = repository.getUserProfile(userId)
            _currentFarmOwner.value = owner
        }
    }

    private fun getConversationId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) {
            "${userId1}_${userId2}"
        } else {
            "${userId2}_${userId1}"
        }
    }

    fun loadMessages(otherUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val conversationId = getConversationId(currentUserId, otherUserId)

        viewModelScope.launch {
            repository.getMessages(conversationId).collect { messages ->
                _chatMessages.value = messages
            }
        }
    }

    fun sendMessage(text: String, otherUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        if (text.isBlank()) return

        val conversationId = getConversationId(currentUserId, otherUserId)
        val message = ChatMessage(
            senderId = currentUserId,
            text = text
        )

        viewModelScope.launch {
            repository.sendMessage(conversationId, message)
        }
    }

    fun loadConversations() {
        val currentUserId = auth.currentUser?.uid ?: return
        val chatRef = FirebaseDatabase.getInstance().reference.child("chats")

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Conversation>()

                snapshot.children.forEach { doc ->
                    val roomId = doc.key ?: ""
                    if (roomId.contains(currentUserId)) {
                        val lastMsg = doc.children.lastOrNull()?.getValue(ChatMessage::class.java)
                        val otherId = roomId.split("_").find { it != currentUserId } ?: ""

                        // We create the conversation with a placeholder name first
                        val convo = Conversation(
                            peerId = otherId,
                            peerName = "Loading...", // Placeholder
                            lastMessage = lastMsg?.text ?: "No messages",
                            roomId = roomId,
                            timestamp = lastMsg?.timestamp ?: 0
                        )
                        list.add(convo)

                        // Fetch the real name from Firestore
                        fetchPeerName(otherId) { realName ->
                            updateConversationName(otherId, realName)
                        }
                    }
                }
                _conversations.value = list.sortedByDescending { it.timestamp }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Helper to fetch name from Firestore
    private fun fetchPeerName(uid: String, onResult: (String) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "Unknown User"
                onResult(name)
            }
    }

    // Helper to update the name in our state list
    private fun updateConversationName(peerId: String, name: String) {
        _conversations.value = _conversations.value.map {
            if (it.peerId == peerId) it.copy(peerName = name) else it
        }
    }
}