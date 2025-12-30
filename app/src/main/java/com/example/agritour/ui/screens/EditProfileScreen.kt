package com.example.agritour.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritour.data.AuthRepository
import com.example.agritour.ui.components.AgriAvatar
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextGrey
import com.example.agritour.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onHomeClick: () -> Unit,
    onExploreClick: () -> Unit,
    onLearnClick: () -> Unit,
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    authRepository: AuthRepository = remember { AuthRepository() }
) {
    // 1. Collect the userProfile from ViewModel
    val userProfile by viewModel.userProfile.collectAsState()

    // 2. Local states for the input fields
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    // 3. Sync local state when userProfile is loaded from Firestore
    LaunchedEffect(userProfile) {
        userProfile?.let {
            name = it.name
            email = it.email
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = AgriBackground,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onHomeClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = false,
                    onClick = onExploreClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = false,
                    onClick = onLearnClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = true,
                    onClick = onProfileClick,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AgriGreen,
                        selectedTextColor = AgriGreen,
                        indicatorColor = Color.White)
                )
            }
        }
    ) { padding ->
        // 4. Handle the "Blank Screen" by showing a loader if data hasn't arrived
        if (userProfile == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AgriGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // If the user picked a local image, we could show a preview here
                // For now, we use the initials avatar
                AgriAvatar(
                    name = name,
                    imageUrl = userProfile?.profileImageUrl,
                    size = 100.dp,
                    fontSize = 36.sp
                )

                TextButton(onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text(
                        text = if (selectedImageUri != null) "Image Selected!" else "Change Profile Picture",
                        color = AgriGreen
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank()) {
                            Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            // 1. Update text profile
                            val textError = authRepository.updateProfile(name, email)

                            // 2. Upload image if user picked a new one
                            var imageError: String? = null
                            if (selectedImageUri != null) {
                                imageError = authRepository.uploadProfileImage(selectedImageUri!!)
                            }

                            isLoading = false

                            if (textError == null && imageError == null) {
                                Toast.makeText(context, "Profile fully updated!", Toast.LENGTH_SHORT).show()
                                onBackClick()
                            } else {
                                Toast.makeText(context, textError ?: imageError, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}