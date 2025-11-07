@file:Suppress("SpellCheckingInspection")

package com.aitsuki.compose.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

typealias FieldValidator<T> = (T?) -> String?

class FieldState<T>(
    initialValue: T? = null,
    val validator: FieldValidator<T>? = null,
    val dependencies: Set<String> = emptySet(),
) {
    // 拆分为独立 State，避免 copy 导致全字段重组
    val value = mutableStateOf(initialValue)
    val error = mutableStateOf<String?>(null)
    val isDirty = mutableStateOf(false)
}


class FormController(
    private val initialValues: Map<String, Any?> = emptyMap(),
) {
    var autoValidate by mutableStateOf(false)
    private val fields = mutableMapOf<String, FieldState<*>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> registerField(
        name: String,
        dependencies: Set<String> = emptySet(),
        validator: FieldValidator<T>? = null
    ): FieldState<T> {
        return fields.getOrPut(name) {
            val init = initialValues[name] as? T
            FieldState(init, validator, dependencies)
        } as FieldState<T>
    }

    fun unregisterField(name: String) {
        fields.remove(name)
        if (autoValidate) {
            validateDependentFields(name)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getField(name: String): FieldState<T>? =
        fields[name] as? FieldState<T>


    fun <T> updateField(name: String, newValue: T) {
        val field = getField<T>(name) ?: return
        field.value.value = newValue
        field.isDirty.value = true

        if (autoValidate) {
            validateField(name)
            validateDependentFields(name)
        }
    }

    private fun validateDependentFields(changedFieldName: String) {
        for ((name, field) in fields) {
            if (changedFieldName in field.dependencies) {
                validateField(name)
            }
        }
    }

    private fun validateField(name: String): Boolean {
        val field = getField<Any>(name) ?: return true
        val error = field.validator?.invoke(field.value.value)
        field.error.value = error
        return error == null
    }

    fun validate(): Boolean {
        var allValid = true
        for (name in fields.keys) {
            if (!validateField(name)) allValid = false
        }
        return allValid
    }

    fun getFieldValues(): Map<String, Any?> = fields.mapValues { (_, f) -> f.value.value }
}

class FieldRenderScope<T>(
    val error: String?,
    val value: T?,
    val onValueChange: (T) -> Unit,
)

@Composable
fun rememberFormController(initialValues: Map<String, Any?> = emptyMap()): FormController {
    return remember { FormController(initialValues) }
}

@Composable
fun <T> FormField(
    controller: FormController,
    name: String,
    dependencies: Set<String> = emptySet(),
    visible: Boolean = true,
    validator: FieldValidator<T>? = null,
    content: @Composable FieldRenderScope<T>.() -> Unit,
) {
    val field = remember(name, visible) {
        if (visible) controller.registerField(name, dependencies, validator) else null
    }

    LaunchedEffect(visible) {
        if (!visible) controller.unregisterField(name)
    }

    LaunchedEffect(visible) {
        if (!visible) {
            controller.unregisterField(name)
        }
    }

    if (field != null && visible) {
        val value by field.value
        val error by field.error

        FieldRenderScope(
            value = value,
            error = error,
            onValueChange = { controller.updateField(name, it) }
        ).content()
    }
}