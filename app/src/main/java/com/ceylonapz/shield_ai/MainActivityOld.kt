package com.ceylonapz.shield_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ceylonapz.shield_ai.ui.theme.ShieldAITheme

class MainActivityOld : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShieldAITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*WelcomeScreen(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )*/
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(uiState: LoginUiState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            LoginUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.testTag("loading")
                )
            }

            is LoginUiState.Success -> {
                Text(
                    text = "Welcome, ${uiState.email}"
                )
            }

            is LoginUiState.Error -> {
                Text("Error: ${uiState.message}")
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("inputEml")
        )

        Button(
            onClick = {
                message = if (email.isBlank()) {
                    "Email cannot be empty"
                } else {
                    "Welcome $email"
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("loginBtn")
        ) {
            Text("Login")
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun WelcomeScreen(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome $name",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {},
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShieldAITheme {
        WelcomeScreen("Android")
    }
}