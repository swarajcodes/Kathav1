package com.swaraj.kathav1.ui.screens.auth
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.swaraj.kathav1.ui.theme.Kathav1Theme
//
//
//@Composable
//fun GuestHomeScreen(
//    onNavigateBack: () -> Unit
//) {
//    Kathav1Theme {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = Color(0xFFA3D749),)
//            {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // Title
//                    Text(
//                        text = "Welcome, Guest!",
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White
//                    )
//
//                    Spacer(modifier = Modifier.height(32.dp))
//
//                    // Description
//                    Text(
//                        text = "Explore our webtoons without signing in.",
//                        fontSize = 16.sp,
//                        color = Color.White
//                    )
//
//                    Spacer(modifier = Modifier.height(32.dp))
//
//                    // Webtoon List
//                    WebtoonList(webtoons = getSampleWebtoons())
//
//                    Spacer(modifier = Modifier.height(32.dp))
//
//                    // Back Button
//                    Button(
//                        onClick = onNavigateBack,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 32.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.White,
//                            contentColor = Color(0xFFA3D749)
//                        )
//                    ) {
//                        Text(
//                            text = "Go Back",
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//    }
//}