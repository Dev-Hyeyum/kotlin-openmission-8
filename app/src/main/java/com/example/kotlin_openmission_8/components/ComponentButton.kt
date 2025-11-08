package com.example.kotlin_openmission_8.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.kotlin_openmission_8.model.Component
import com.example.kotlin_openmission_8.model.ComponentAction
import com.example.kotlin_openmission_8.model.ComponentType
import com.example.kotlin_openmission_8.model.Components
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentButton(
    context: Context,
    viewModel: Components,
    changeComponent: (Component) -> Unit
) {
    var showDialog by remember{ mutableStateOf(false) }

    var expendedAction by remember{ mutableStateOf(false) }
    val actionOptions = ComponentAction.entries.toList()
    var selectedActionOption by remember{ mutableStateOf(actionOptions[0]) }

    var expendedType by remember{ mutableStateOf(false) }
    val typeOptions = ComponentType.entries.toList()
    var selectedTypeOption by remember{ mutableStateOf(typeOptions[0]) }

    var writeText by remember{ mutableStateOf("") }

    val toast = Toast.makeText(context, "컴포넌트를 생성했습니다.", Toast.LENGTH_SHORT)

    Button(
        onClick = {
            showDialog = true
        },
        modifier = Modifier
            .padding(20.dp)
    ) {
        Text(text = "컴포넌트 생성 버튼")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text("컴포넌트 생성할거에요?")
            },
            text = {
                Column() {
                    ExposedDropdownMenuBox(
                        expanded = expendedAction,
                        onExpandedChange = { expendedAction = !expendedAction},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedActionOption.name,
                            onValueChange = {},
                            label = { Text(text = "행동을 선택해주세요") },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expendedAction,
                            onDismissRequest = { expendedAction = false }
                        ) {
                            actionOptions.forEach { actionOption ->
                                DropdownMenuItem(
                                    text = { Text(actionOption.name) },
                                    onClick = {
                                        selectedActionOption = actionOption
                                        expendedAction = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )

                            }
                        }
                    }
                    ExposedDropdownMenuBox(
                        expanded = expendedType,
                        onExpandedChange = { expendedType = !expendedType},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedTypeOption.name,
                            onValueChange = {},
                            label = { Text(text = "타입을 선택해주세요") },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expendedType,
                            onDismissRequest = { expendedType = false }
                        ) {
                            typeOptions.forEach { typeOption ->
                                DropdownMenuItem(
                                    text = { Text(typeOption.name) },
                                    onClick = {
                                        selectedTypeOption = typeOption
                                        expendedType = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )

                            }
                        }
                    }
                    TextField(
                        value = writeText,
                        onValueChange = { newText ->
                            writeText = newText
                        },
                        label = { Text("텍스트를 입력해주세요") },
                        singleLine = true
                    )
                }

            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newComponent = Component(action = selectedActionOption, type = selectedTypeOption, text = writeText)
                        changeComponent(newComponent)
                        viewModel.postComponent(newComponent)
                        toast.show()
                        showDialog = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // 취소 버튼 클릭 시 수행할 작업
                        showDialog = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}
