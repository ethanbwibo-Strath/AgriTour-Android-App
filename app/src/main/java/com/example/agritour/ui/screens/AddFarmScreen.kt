package com.example.agritour.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import com.example.agritour.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFarmScreen(
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    // Form State
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Image Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Add New Farm", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextBlack,
                    navigationIconContentColor = TextBlack
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // 1. Image Picker Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable {
                        // Launch Gallery
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, tint = TextGrey, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap to upload farm photo", color = TextGrey)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Form Fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Farm Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location (e.g. Nakuru)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Type Dropdown (Simplified as text input for now, or use filter chips logic)
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Farm Type (e.g. Coffee, Dairy)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price per Person (Ksh)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description / Activities") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Submit Button
            Button(
                onClick = {
                    if (name.isNotEmpty() && price.isNotEmpty() && imageUri != null) {
                        isUploading = true
                        viewModel.addNewFarm(
                            name = name,
                            location = location,
                            price = price.toIntOrNull() ?: 0,
                            type = type,
                            description = description,
                            imageUri = imageUri,
                            context
                        ) { success ->
                            isUploading = false
                            if (success) {
                                Toast.makeText(context, "Farm Listed Successfully!", Toast.LENGTH_SHORT).show()
                                onBackClick()
                            } else {
                                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all details and add a photo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Publish Listing", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}