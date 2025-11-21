package com.example.kotlin_openmission_8.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.kotlin_openmission_8.model.Components
import com.example.kotlin_openmission_8.model.Menu


@Composable
fun SideBar(
    context: Context,
    viewModel: Components,
    modifier: Modifier,
    isLandscape: Boolean,
    isShowSideBar: Boolean,
    onNavigateBack: () -> Unit
) {
    val isSideBarMenu by viewModel.isSideBarMenu.collectAsState()

    Column(
        modifier = modifier
            .fillMaxHeight()
    ) {
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            MenuBar(
                imageVector = if (isLandscape) {
                    if (isShowSideBar) {
                        Icons.AutoMirrored.Filled.KeyboardArrowRight
                    } else {
                        Icons.Default.KeyboardArrowLeft
                    }
                } else {
                    if (isShowSideBar) {
                        Icons.Default.KeyboardArrowDown
                    } else {
                        Icons.Default.KeyboardArrowUp
                    }
                },
                viewModel = viewModel,
                isShowFunction = if (isShowSideBar) {
                    { viewModel.notShowSideBar() }
                } else {
                    { viewModel.showSideBar() }
                },
                onNavigateBack = onNavigateBack
            )
        }
        if (isShowSideBar) {
            when(isSideBarMenu) {
                Menu.CREATE -> {
                    CreateMenu(
                        context = context,
                        viewModel = viewModel
                    )
                }
                Menu.EDIT -> {
                    EditMenu(
                        viewModel = viewModel
                    )
                }
                else -> {
                    ComponentListMenu(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}