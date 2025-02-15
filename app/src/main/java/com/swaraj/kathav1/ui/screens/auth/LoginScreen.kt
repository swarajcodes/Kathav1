package com.swaraj.kathav1.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.swaraj.kathav1.R
import com.swaraj.kathav1.ui.theme.Kathav1Theme
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(
    onNavigateToOTP: (String, String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var mobileNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // List of country codes
    val countryList = listOf(
        "🇮🇳 India (+91)" to "+91",
        "🇺🇸 USA (+1)" to "+1",
        "🇬🇧 UK (+44)" to "+44",
        "🇦🇺 Australia (+61)" to "+61",
        "🇨🇦 Canada (+1)" to "+1"
    )

    var selectedCountry by remember { mutableStateOf(countryList[0]) }
    var expanded by remember { mutableStateOf(false) }

    // Auto-login if user is already signed in
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            checkUsername(userId, context, onNavigateToHome)
        }
    }

    Kathav1Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .imePadding(),
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(Color.DarkGray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.katha_dark),
                        contentDescription = "Katha Logo",
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Enter your mobile number",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                                .clickable { expanded = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = selectedCountry.first, color = Color.White)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.DarkGray)
                        ) {
                            countryList.forEach { country ->
                                DropdownMenuItem(
                                    text = { Text(country.first, color = Color.White) },
                                    onClick = {
                                        selectedCountry = country
                                        expanded = false
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextField(
                            value = mobileNumber,
                            onValueChange = { mobileNumber = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Mobile Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.DarkGray,
                                unfocusedContainerColor = Color.DarkGray,
                                focusedLabelColor = Color(0xFFA3D749),
                                unfocusedLabelColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFFA3D749))
                    } else {
                        Button(
                            onClick = {
                                if (mobileNumber.length in 8..15) {
                                    isLoading = true
                                    sendOTP(selectedCountry.second + mobileNumber, FirebaseAuth.getInstance(), context) { verificationId ->
                                        isLoading = false
                                        onNavigateToOTP(verificationId, mobileNumber)
                                    }
                                } else {
                                    Toast.makeText(context, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFA3D749),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Request OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun sendOTP(
    mobileNumber: String,
    auth: FirebaseAuth,
    context: Context,
    onNavigateToOTP: (String) -> Unit
) {
    val phoneNumber = mobileNumber
    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(context as android.app.Activity)
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Toast.makeText(context, "Auto-verification successful!", Toast.LENGTH_SHORT).show()
                signInWithCredential(credential, context) { success ->
                    if (success) {
                        onNavigateToOTP("auto_verification")
                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onNavigateToOTP(verificationId)
            }
        })
        .build()

    PhoneAuthProvider.verifyPhoneNumber(options)
}

private fun signInWithCredential(
    credential: PhoneAuthCredential,
    context: Context,
    onComplete: (Boolean) -> Unit
) {
    FirebaseAuth.getInstance().signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
}

private fun checkUsername(userId: String, context: Context, onNavigateToHome: () -> Unit) {
    val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
    userRef.get().addOnSuccessListener { document ->
        if (document.exists() && document.contains("username")) {
            onNavigateToHome()
        }
    }
}
