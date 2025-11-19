package com.example.kotlin_openmission_8.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumberDataField(data: Float, label: String, changeData: (Float) -> Unit) {

    var textInput by remember { mutableStateOf(data.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(data) {
        // --- 0f일 경우 비우기 로직 (기존과 동일) ---
        if (data == 0f) {
            if (textInput != "") textInput = "0"
            return@LaunchedEffect
        }
        // ... (소수점 꼬리 제거 로직은 그대로) ...
        val formattedString = if (data % 1.0f == 0.0f) {
            data.toLong().toString()
        } else {
            data.toString()
        }

        if (formattedString != textInput) {
            textInput = formattedString
        }
    }

    OutlinedTextField(
        value = textInput,
        modifier = Modifier.fillMaxWidth(0.9f),
        onValueChange = { newText ->
            textInput = newText
            val newFloat = newText.toFloatOrNull()

            if (newText.isEmpty()) {
                errorMessage = null
                changeData(0f)
            } else if (newFloat != null) {
                errorMessage = null
                changeData(newFloat)
            } else {
                errorMessage = "유효한 숫자만 입력해 주세요."
            }
        },
        label = { Text(label) }, // ✨ [사용] 파라미터로 받은 label 사용
        isError = errorMessage != null,
        supportingText = {
            if (errorMessage != null) {
                Text(errorMessage!!)
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}