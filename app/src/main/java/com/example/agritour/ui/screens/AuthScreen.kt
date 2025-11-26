package com.example.agritour.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.agritour.data.AuthRepository
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    // UI State
    var isLoginMode by remember { mutableStateOf(true) } // Toggle between Login/Signup
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") } // For Signup only
    var isFarmer by remember { mutableStateOf(false) } // For Signup only

    // Validation & Loading State
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Tools
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repository = remember { AuthRepository() }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Sign Up / Log In", fontSize = MaterialTheme.typography.titleMedium.fontSize) },
                navigationIcon = {
                    IconButton(onClick = { /* Optional: Handle Close/Back */ }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Greeting
            Text("Welcome Back!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Login to access your personalized experience.", color = TextGrey, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Custom Toggle (Login / Sign Up)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                // Log In Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (isLoginMode) AgriGreen else Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { isLoginMode = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Log In",
                        color = if (isLoginMode) Color.White else TextGrey,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Sign Up Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (!isLoginMode) AgriGreen else Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { isLoginMode = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sign Up",
                        color = if (!isLoginMode) Color.White else TextGrey,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Form Fields
            if (!isLoginMode) {
                // Name Field (Only for Signup)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field with Error State
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    // Clear error as user types valid length
                    if (it.length >= 6) passwordError = null
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // 4. Role Selection (Farmer Checkbox) - Only on Sign Up
            if (!isLoginMode) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isFarmer,
                        onCheckedChange = { isFarmer = it },
                        colors = CheckboxDefaults.colors(checkedColor = AgriGreen)
                    )
                    Text("Register as a Farmer / Host")
                }
            } else {
                // Forgot Password (Login only)
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = {}) {
                        Text("Forgot Password?", color = AgriGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Action Button
            Button(
                onClick = {
                    // --- CLIENT SIDE VALIDATION ---
                    if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"
                        return@Button
                    }

                    if (email.isEmpty()) {
                        Toast.makeText(context, "Please enter an email address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!isLoginMode && name.isEmpty()) {
                        Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // --- SERVER SIDE CALL ---
                    isLoading = true
                    scope.launch {
                        // The repository now returns a String? (Error message or null)
                        val error = if (isLoginMode) {
                            repository.login(email, password)
                        } else {
                            repository.signUp(name, email, password, isFarmer)
                        }

                        isLoading = false

                        if (error == null) {
                            // Success!
                            onLoginSuccess()
                        } else {
                            // Failure: Show the actual Firebase error
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (isLoginMode) "Log In" else "Sign Up", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Social Buttons (Visual only)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f))
                Text("  or  ", color = TextGrey)
                Divider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { /* TODO: Google Auth */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue with Google", color = TextBlack)
            }
        }
    }
}