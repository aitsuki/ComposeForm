@file:Suppress("SpellCheckingInspection")

package com.aitsuki.compose.form

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.IdentityHashMap

class FieldState<T>(
    val key: String,
    initial: T,
    val validator: (T) -> String?,
) {
    var value by mutableStateOf(initial)
    var error by mutableStateOf<String?>(null)

    fun validate(): Boolean {
        error = validator(value)
        return error == null
    }
}

@Composable
fun <T> rememberFieldState(key: String, initial: T, validator: (T) -> String?): FieldState<T> {
    return remember { FieldState(key, initial, validator) }
}

class FormController {
    private val _fields = mutableStateListOf<FieldState<*>>()
    val fields: List<FieldState<*>> get() = _fields

    fun updateFields(newList: List<FieldState<*>>) {
        Log.d("FormController", "updateFields")
        _fields.clear()
        _fields.addAll(newList)
    }

    fun validateAll(): Boolean {
        Log.d("FormController", "validateAll")
        var ok = true
        _fields.forEach { if (!it.validate()) ok = false }
        return ok
    }

    fun firstInvalid(): FieldState<*>? = _fields.firstOrNull { it.error != null }
}

private val LocalFormCollector = compositionLocalOf<FormCollector> {
    error("No LocalFormCollector found")
}

private class FormCollector {
    val fields = mutableStateListOf<FieldState<*>>()
    private val seen = IdentityHashMap<FieldState<*>, Boolean>()

    fun clear() {
        fields.clear()
        seen.clear()
    }

    fun add(field: FieldState<*>) {
        if (seen.put(field, true) == null) {
            fields.add(field)
        }
    }
}

@Composable
fun rememberFormController(): FormController {
    return remember { FormController() }
}

@Composable
fun Form(
    controller: FormController,
    autoValidate: Boolean = false,
    content: @Composable () -> Unit
) {
    val collector = remember { FormCollector() }
    collector.clear()
    Log.d("Form", "Composing Form")

    CompositionLocalProvider(LocalFormCollector provides collector) {
        content()
    }

    // 同步字段顺序到 controller
    LaunchedEffect(collector) {
        snapshotFlow { collector.fields.toList() }
            .collectLatest { controller.updateFields(it) }
    }

    // 自动校验
    if (autoValidate) {
        LaunchedEffect(collector) {
            snapshotFlow { collector.fields.map { it.value } }
                .collectLatest { collector.fields.forEach { it.validate() } }
        }
    }
}

class FieldRenderScope<T>(
    val error: String?,
    val value: T,
    val onValueChange: (T) -> Unit,
)

@Composable
fun <T> FormField(state: FieldState<T>, content: @Composable FieldRenderScope<T>.() -> Unit) {
    val collector = LocalFormCollector.current
    collector.add(state)
    Log.d("FormField", "Composing FormField：${state.key}")
    FieldRenderScope(
        value = state.value,
        onValueChange = { newValue -> state.value = newValue },
        error = state.error
    ).content()
}