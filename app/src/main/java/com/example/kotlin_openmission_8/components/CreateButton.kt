package com.example.kotlin_openmission_8.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateButton(context: Context, type: String) {
    val toast = Toast.makeText(context, type, Toast.LENGTH_SHORT)

    Surface {
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {toast.show()}
        ) {
            Text(text = "$type 생성 버튼")
        }
    }
}