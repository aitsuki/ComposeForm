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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aitsuki.compose.form.Form
import com.aitsuki.compose.form.FormField
import com.aitsuki.compose.form.rememberFieldState
import com.aitsuki.compose.form.rememberFormController
import com.aitsuki.compose.form.sample.LocalBackStack
import com.aitsuki.compose.form.sample.Routes
import com.aitsuki.compose.form.sample.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    val controller = rememberFormController()
    var autoValidate by remember { mutableStateOf(false) }
    val emailState = rememberFieldState("email", "") {
        when {
            it.isBlank() -> "请输入邮箱"
            !Patterns.EMAIL_ADDRESS.matcher(it).matches() -> "邮箱格式不正确"
            else -> null
        }
    }
    val passwordState = rememberFieldState("password", "") {
        when {
            it.isBlank() -> "请输入密码"
            it.length < 6 -> "密码长度最小为6"
            it.length > 20 -> "密码长度最大为20"
            else -> null
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { CenterAlignedTopAppBar(title = { Text("登录") }) }
    ) { innerPadding ->
        Form(controller,  autoValidate) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(24.dp),
            ) {
                FormField(emailState) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = value,
                        onValueChange = onValueChange,
                        label = { Text("邮箱") },
                        isError = error != null,
                        supportingText = { error?.let { Text(it) } },
                    )
                }
                FormField(passwordState) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = value,
                        onValueChange = onValueChange,
                        label = { Text("密码") },
                        isError = error != null,
                        supportingText = { error?.let { Text(it) } }
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (controller.validateAll()) {
                            Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show()
                            val user = User(email = emailState.value)
                            backStack.add(Routes.Profile(user))
                            backStack.removeAll { it !is Routes.Profile }
                        } else {
                            autoValidate = true
                            Toast.makeText(context, "请检查表单错误", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }) {
                    Text("登录")
                }
            }
        }
    }
}