package com.aitsuki.compose.form.sample.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aitsuki.compose.form.sample.LocalBackStack
import com.aitsuki.compose.form.sample.Routes
import com.aitsuki.compose.form.sample.models.User

@Composable
fun ProfileScreen(user: User) {
    val backStack = LocalBackStack.current
    Scaffold { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("邮箱：${user.email}")
            Text("职业: ${user.occupation}")
            Text("收入: ${user.annualIncome}")
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                backStack.add(0, Routes.Start)
                backStack.removeAll({ it != Routes.Start })
            }) {
                Text("退出登录")
            }
        }
    }
}