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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aitsuki.compose.form.FormField
import com.aitsuki.compose.form.rememberFormController
import com.aitsuki.compose.form.sample.LocalBackStack
import com.aitsuki.compose.form.sample.Routes
import com.aitsuki.compose.form.sample.models.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen() {
    val controller = rememberFormController()
    val context = LocalContext.current
    val backStack = LocalBackStack.current
    val coroutineScope = rememberCoroutineScope()

    val fieldPositions = remember { mutableMapOf<String, Float>() }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { CenterAlignedTopAppBar(title = { Text("注册") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            fieldPositions["email"] = it.positionInParent().y
                        },
                    value = value.orEmpty(),
                    onValueChange = onValueChange,
                    label = { Text("邮箱") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                )
            }
            FormField<String>(
                controller = controller,
                name = "occupation",
                validator = { if (it.isNullOrBlank()) "请选择职业" else null }
            ) {
                val occupations = listOf("学生", "教师", "工程师", "医生", "律师", "其他")
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.onGloballyPositioned {
                        fieldPositions["occupation"] = it.positionInParent().y
                    }
                ) {
                    OutlinedTextField(
                        value = value ?: "",
                        onValueChange = {},
                        label = { Text("职业") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        readOnly = true,
                        isError = error != null,
                        supportingText = { error?.let { Text(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                        occupations.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    onValueChange(it)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 年收入：动态显示
            FormField<String>(
                controller = controller,
                name = "annualIncome",
                visible = controller.value<String>("occupation")
                    .let { !it.isNullOrEmpty() && it != "学生" },
                validator = {
                    when {
                        it.isNullOrBlank() -> "年收入不能为空"
                        it.toDoubleOrNull() == null -> "请输入有效金额"
                        it.toDouble() < 0 -> "不能为负数"
                        else -> null
                    }
                }
            ) {
                OutlinedTextField(
                    value = value ?: "",
                    onValueChange = onValueChange,
                    label = { Text("年收入") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            fieldPositions["annualIncome"] = it.positionInParent().y
                        },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            FormField<String>(
                controller = controller,
                name = "password",
                validator = {
                    when {
                        it.isNullOrBlank() -> "请输入密码"
                        it.length < 6 -> "密码长度最小为6"
                        it.length > 20 -> "密码长度最大为20"
                        else -> null
                    }
                }
            ) {
                OutlinedTextField(
                    value = value ?: "",
                    onValueChange = onValueChange,
                    label = { Text("Password") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            fieldPositions["password"] = it.positionInParent().y
                        }
                )
            }

            FormField<String>(
                controller = controller,
                name = "confirmPassword",
                dependencies = setOf("password"),
                validator = {
                    val password = controller.value<String>("password").orEmpty()
                    when {
                        it.isNullOrBlank() -> "请输入确认密码"
                        it.length < 6 -> "密码长度最小为6"
                        it.length > 20 -> "密码长度最大为20"
                        password.isNotEmpty() && it != password -> "密码不匹配"
                        else -> null
                    }
                }
            ) {
                OutlinedTextField(
                    value = value ?: "",
                    onValueChange = onValueChange,
                    label = { Text("确认密码") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            fieldPositions["confirmPassword"] = it.positionInParent().y
                        }
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (controller.validate()) {
                        Toast.makeText(context, "注册成功！", Toast.LENGTH_SHORT).show()
                        val values = controller.values()
                        val user = User(
                            email = values["email"] as? String ?: "",
                            occupation = values["occupation"] as? String ?: "",
                            annualIncome = values["annualIncome"] as? String ?: "",
                        )
                        backStack.add(Routes.Profile(user))
                        backStack.removeAll { it !is Routes.Profile }
                    } else {
                        controller.autoValidate = true
                        // 自动滚动到第一个错误
                        controller.firstErrorFieldName()?.let {
                            fieldPositions[it]?.let { y ->
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(y.toInt())
                                }
                            }
                        }
                        Toast.makeText(context, "请检查表单错误", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                Text("注册")
            }
        }
    }
}