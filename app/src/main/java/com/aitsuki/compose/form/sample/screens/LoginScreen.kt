package com.aitsuki.compose.form.sample.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aitsuki.compose.form.FormField
import com.aitsuki.compose.form.rememberFormController
import com.aitsuki.compose.form.sample.LocalBackStack
import com.aitsuki.compose.form.sample.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val controller = rememberFormController()
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { CenterAlignedTopAppBar(title = { Text("Login") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(24.dp),
        ) {
            FormField<String>(
                controller = controller,
                name = "email",
                validator = { v ->
                    when {
                        v.isNullOrBlank() -> "Email is required"
                        !Patterns.EMAIL_ADDRESS.matcher(v).matches() -> "Invalid email format"
                        else -> null
                    }
                },
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value.orEmpty(),
                    onValueChange = onValueChange,
                    label = { Text("Email") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                )
            }
            FormField<String>(
                controller = controller,
                name = "password",
                validator = { v ->
                    when {
                        v.isNullOrBlank() -> "Password is required"
                        v.length < 6 -> "Password must be at least 6 characters"
                        v.length > 20 -> "Password must be at most 20 characters"
                        else -> null
                    }
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value.orEmpty(),
                    onValueChange = onValueChange,
                    label = { Text("Password") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } }
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (controller.validate()) {
                        Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
                        backStack.add(Routes.Home)
                        backStack.removeAll { it != Routes.Home }
                    } else {
                        controller.autoValidate = true
                        Toast.makeText(context, "Please check form error", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                Text("Login")
            }
        }
    }
}