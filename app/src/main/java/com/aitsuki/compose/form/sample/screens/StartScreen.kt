package com.aitsuki.compose.form.sample.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aitsuki.compose.form.sample.LocalBackStack
import com.aitsuki.compose.form.sample.Routes


@Composable
fun StartScreen() {
    val backStack = LocalBackStack.current
    Scaffold(modifier = Modifier.imePadding()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "登录页面演示了表单的基础使用方式，注册页面额外演示了以下特性：\n" +
                        "1. 表单联合校验（密码和确认密码）\n" +
                        "2. 动态表单（根据选择的职业，动态的添加或移除\"收入\"输入框）",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { backStack.add(Routes.Login) }) {
                Text("登录")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { backStack.add(Routes.Register) }
            ) {
                Text("注册")
            }
        }
    }
}