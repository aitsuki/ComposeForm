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
import com.aitsuki.compose.form.Form
import com.aitsuki.compose.form.FormField
import com.aitsuki.compose.form.rememberFieldState
import com.aitsuki.compose.form.rememberFormController
import com.aitsuki.compose.form.sample.LocalBackStack
import com.aitsuki.compose.form.sample.Routes
import com.aitsuki.compose.form.sample.models.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    val backStack = LocalBackStack.current
    val coroutineScope = rememberCoroutineScope()

    val fieldPositions = remember { mutableMapOf<String, Float>() }
    val controller = rememberFormController()
    var autoValidate by remember { mutableStateOf(false) }
    val emailState = rememberFieldState("email", "") {
        when {
            it.isBlank() -> "请输入邮箱"
            !Patterns.EMAIL_ADDRESS.matcher(it).matches() -> "邮箱格式不正确"
            else -> null
        }
    }
    val occupationState = rememberFieldState<String?>("occupation", null) {
        if (it == null) "请选择职业" else null
    }
    val annualIncomeState = rememberFieldState("annual", "") {
        when {
            it.isBlank() -> "年收入不能为空"
            it.toDoubleOrNull() == null -> "请输入有效金额"
            it.toDouble() < 0 -> "不能为负数"
            else -> null
        }
    }
    val passwordState = rememberFieldState("password", "") {
        when {
            it.isBlank() -> "请输入确认密码"
            it.length < 6 -> "密码长度最小为6"
            it.length > 20 -> "密码长度最大为20"
            else -> null
        }
    }
    val confirmPasswordState = rememberFieldState("confirmPassword", "") {
        when {
            it.isBlank() -> "请输入确认密码"
            it.length < 6 -> "密码长度最小为6"
            it.length > 20 -> "密码长度最大为20"
            passwordState.value.isNotBlank() && it != passwordState.value -> "密码不匹配"
            else -> null
        }
    }


    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { CenterAlignedTopAppBar(title = { Text("注册") }) }
    ) { innerPadding ->
        Form(controller, autoValidate) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(24.dp),
            ) {
                FormField(emailState) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                fieldPositions[emailState.key] = it.positionInParent().y
                            },
                        value = value,
                        onValueChange = onValueChange,
                        label = { Text("邮箱") },
                        isError = error != null,
                        supportingText = { error?.let { Text(it) } },
                    )
                }
                FormField(occupationState) {
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
                if (occupationState.value != null && occupationState.value != "学生") {
                    FormField(annualIncomeState) {
                        OutlinedTextField(
                            value = value,
                            onValueChange = onValueChange,
                            label = { Text("年收入") },
                            isError = error != null,
                            supportingText = { error?.let { Text(it) } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned {
                                    fieldPositions[annualIncomeState.key] = it.positionInParent().y
                                },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }

                FormField(passwordState) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = { Text("Password") },
                        isError = error != null,
                        supportingText = { error?.let { Text(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                fieldPositions[passwordState.key] = it.positionInParent().y
                            }
                    )
                }

                FormField(confirmPasswordState) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = { Text("确认密码") },
                        isError = error != null,
                        supportingText = { error?.let { Text(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                fieldPositions[confirmPasswordState.key] = it.positionInParent().y
                            }
                    )
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (controller.validateAll()) {
                            Toast.makeText(context, "注册成功！", Toast.LENGTH_SHORT).show()
                            val user = User(
                                email = emailState.value,
                                occupation = occupationState.value.orEmpty(),
                                annualIncome = annualIncomeState.value,
                            )
                            backStack.add(Routes.Profile(user))
                            backStack.removeAll { it !is Routes.Profile }
                        } else {
                            autoValidate = true
                            // 自动滚动到第一个错误
                            controller.firstInvalid()?.key?.let { errorKey ->

                                fieldPositions[errorKey]?.let { y ->
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
}