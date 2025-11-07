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
import com.aitsuki.compose.form.sample.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val controller = rememberFormController()
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { CenterAlignedTopAppBar(title = { Text("登录") }) }
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
                        v.isNullOrBlank() -> "请输入邮箱"
                        !Patterns.EMAIL_ADDRESS.matcher(v).matches() -> "邮箱格式不正确"
                        else -> null
                    }
                },
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value.orEmpty(),
                    onValueChange = onValueChange,
                    label = { Text("邮箱") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                )
            }
            FormField<String>(
                controller = controller,
                name = "password",
                validator = { v ->
                    when {
                        v.isNullOrBlank() -> "请输入密码"
                        v.length < 6 -> "密码长度最小为6"
                        v.length > 20 -> "密码长度最大为20"
                        else -> null
                    }
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value.orEmpty(),
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
                    if (controller.validate()) {
                        Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show()
                        val user = User(email = controller.value<String>("email").orEmpty())
                        backStack.add(Routes.Profile(user))
                        backStack.removeAll { it !is Routes.Profile }
                    } else {
                        controller.autoValidate = true
                        Toast.makeText(context, "请检查表单错误", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                Text("登录")
            }
        }
    }
}