package com.example.agritour.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.agritour.R // Ensure R is imported for drawable resources if you have a google icon
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
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isFarmer by remember { mutableStateOf(false) }

    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repository = remember { AuthRepository() }

    // Helper for Input Colors (Reusable)
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextBlack,          // Text you type is BLACK
        unfocusedTextColor = TextBlack,
        focusedLabelColor = AgriGreen,         // Label turns Green when active
        unfocusedLabelColor = TextGrey,
        focusedBorderColor = AgriGreen,
        unfocusedBorderColor = TextGrey.copy(alpha = 0.5f),
        cursorColor = AgriGreen,
        focusedLeadingIconColor = TextGrey,
        unfocusedLeadingIconColor = TextGrey,
        focusedTrailingIconColor = TextGrey,
        unfocusedTrailingIconColor = TextGrey
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                // 1. BOLD TITLE
                title = {
                    Text(
                        "Sign Up / Log In",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle Close */ }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextBlack)
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
            Text("Welcome Back!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextBlack)
            Text("Login to access your personalized experience.", color = TextGrey, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (isLoginMode) AgriGreen else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { isLoginMode = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Log In", color = if (isLoginMode) Color.White else TextGrey, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (!isLoginMode) AgriGreen else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { isLoginMode = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sign Up", color = if (!isLoginMode) Color.White else TextGrey, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            if (!isLoginMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = inputColors // <--- Applying Colors
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = inputColors // <--- Applying Colors
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (it.length >= 6) passwordError = null
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        // 2. DARKER ICON
                        Icon(image, contentDescription = null, tint = TextGrey)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = inputColors // <--- Applying Colors
            )

            // Options Row
            if (!isLoginMode) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isFarmer,
                        onCheckedChange = { isFarmer = it },
                        colors = CheckboxDefaults.colors(checkedColor = AgriGreen, uncheckedColor = TextGrey)
                    )
                    Text("Register as a Farmer / Host", color = TextGrey) // Darker Grey
                }
            } else {
                // 3. FORGOT PASSWORD LOGIC
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = {
                        if (email.isNotEmpty()) {
                            scope.launch {
                                Toast.makeText(context, "Sending reset email...", Toast.LENGTH_SHORT).show()
                                val success = repository.sendPasswordReset(email)
                                if (success) {
                                    Toast.makeText(context, "Reset link sent to $email", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Error sending email. Check address.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please enter your email first", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Forgot Password?", color = AgriGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Button
            Button(
                onClick = {
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

                    isLoading = true
                    scope.launch {
                        val error = if (isLoginMode) {
                            repository.login(email, password)
                        } else {
                            repository.signUp(name, email, password, isFarmer)
                        }
                        isLoading = false
                        if (error == null) onLoginSuccess() else Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text(if (isLoginMode) "Log In" else "Sign Up", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f))
                Text("  or  ", color = TextGrey)
                Divider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. GOOGLE BUTTON (Styled)
            OutlinedButton(
                onClick = { /* TODO: Google Auth */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray), // Grey Border
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White) // White Background
            ) {
                // Simulated Google Colors text
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // If you have a Google Icon in drawable, use:
                    // Image(painterResource(id = R.drawable.ic_google), null, modifier = Modifier.size(20.dp))
                    // Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue with Google", color = TextBlack, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}