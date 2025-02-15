    
    
    package com.swaraj.kathav1.ui.screens.auth
    
    import com.swaraj.kathav1.ui.theme.Kathav1Theme
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
    import androidx.compose.ui.focus.FocusRequester
    import androidx.compose.ui.focus.focusRequester
    import androidx.compose.ui.focus.onFocusChanged
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.input.key.onKeyEvent
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.platform.LocalSoftwareKeyboardController
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.text.style.TextDecoration
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.PhoneAuthProvider
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.SetOptions
    import com.swaraj.kathav1.R
    import kotlinx.coroutines.delay
    
    @Composable
    fun OTPScreen(
        verificationId: String,
        phoneNumber: String,
        navController: NavController,
        onNavigateBack: () -> Unit
    ) {
        val context = LocalContext.current
        var otp by remember { mutableStateOf(List(6) { "" }) }
        val focusRequesters = List(6) { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val auth = FirebaseAuth.getInstance()
    
        var timer by remember { mutableIntStateOf(30) }
        var isResendEnabled by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }
        var username by remember { mutableStateOf("") }
    
        LaunchedEffect(timer) {
            while (timer > 0) {
                delay(1000L)
                timer--
            }
            isResendEnabled = true
        }
    
        LaunchedEffect(auth.currentUser?.uid) {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                checkUsername(userId, phoneNumber, context) { showDialog = true }
            }
        }
    
        Kathav1Theme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
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
                        modifier = Modifier.fillMaxSize().padding(16.dp),
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
                            text = "Enter the OTP sent to $phoneNumber",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
    
                        Spacer(modifier = Modifier.height(16.dp))
    
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            otp.forEachIndexed { index, value ->
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = { newValue ->
                                        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                            val newOtp = otp.toMutableList()
                                            newOtp[index] = newValue
                                            otp = newOtp
    
                                            if (newValue.isNotEmpty() && index < 5) {
                                                focusRequesters[index + 1].requestFocus()
                                            }
                                            if (index == 5 && newOtp.joinToString("").length == 6) {
                                                keyboardController?.hide()
                                                verifyOTP(newOtp.joinToString(""), verificationId, context, navController)
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                        .focusRequester(focusRequesters[index])
                                        .onFocusChanged { focusState ->
                                            if (focusState.isFocused && index > 0 && otp[index - 1].isEmpty()) {
                                                focusRequesters[otp.indexOfFirst { it.isEmpty() }].requestFocus()
                                            }
                                        }
                                        .onKeyEvent { keyEvent ->
                                            if (keyEvent.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DEL) {
                                                if (otp[index].isNotEmpty()) {
                                                    val newOtp = otp.toMutableList()
                                                    newOtp[index] = ""
                                                    otp = newOtp
                                                } else if (index > 0) {
                                                    val newOtp = otp.toMutableList()
                                                    newOtp[index - 1] = ""
                                                    otp = newOtp
                                                    focusRequesters[index - 1].requestFocus()
                                                }
                                                return@onKeyEvent true
                                            }
                                            false
                                        },
                                    textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                if (index < 5) Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
    
                        Spacer(modifier = Modifier.height(16.dp))
    
                        Text(
                            text = if (timer > 0) "Resend OTP in $timer sec" else "",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
    
                        Spacer(modifier = Modifier.height(16.dp))
    
                        Button(
                            onClick = {
                                val enteredOTP = otp.joinToString("")
                                if (enteredOTP.length == 6) {
                                    verifyOTP(enteredOTP, verificationId, context, navController)
                                } else {
                                    Toast.makeText(context, "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA3D749), contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Verify OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
    
    // Function to check if username exists
    fun checkUsername(userId: String, phoneNumber: String, context: Context, onNoUsername: () -> Unit) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists() && document.contains("username")) {
                saveUsernameToPrefs(context, document.getString("username") ?: "Guest")
            } else {
                onNoUsername()
            }
        }
    }
    
    // Function to verify OTP and navigate accordingly
    private fun verifyOTP(
        otp: String,
        verificationId: String,
        context: Context,
        navController: NavController
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    
    // Function to store username in SharedPreferences
    fun saveUsernameToPrefs(context: Context, username: String) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("username", username).apply()
    }
