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
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.kotlin_openmission_8.model.Components


@Composable
fun SideBar(
    context: Context,
    viewModel: Components,
    modifier: Modifier,
    isLandscape: Boolean,
    isShowSideBar: Boolean
) {
    val isSideBarMenu by viewModel.isSideBarMenu.collectAsState()

    // 가로일 경우
    if (isLandscape) {
        if (isShowSideBar) {
            Column(
                modifier = modifier
                    .fillMaxHeight()
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MenuBar(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, viewModel = viewModel, isShowFunction = { viewModel.notShowSideBar() })
                }
                if (isSideBarMenu) {
                    CreateButtonList(
                        context = context,
                        viewModel = viewModel,
                        modifier = Modifier.weight(0.25f)
                    )
                } else {
                    CreateMenu(
                        viewModel = viewModel
                    )
                }
            }
        } else {
            Column(
                modifier = modifier.fillMaxHeight()
            ) {
                MenuBar(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, viewModel = viewModel, isShowFunction = { viewModel.showSideBar() })
            }
        }
        // 세로일 경우
    } else {
        if (isShowSideBar) {
            Column(
                modifier = modifier
                    .fillMaxHeight()
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MenuBar(imageVector = Icons.Default.KeyboardArrowDown, viewModel = viewModel, isShowFunction = { viewModel.notShowSideBar() })
                }
                if (isSideBarMenu) {
                    CreateButtonList(
                        context = context,
                        viewModel = viewModel,
                        modifier = Modifier.weight(0.25f)
                    )
                } else {
                    CreateMenu(
                        viewModel = viewModel
                    )
                }
            }
        } else {
            Row(
                modifier = modifier.fillMaxWidth()
            ) {
                MenuBar(imageVector = Icons.Default.KeyboardArrowUp, viewModel = viewModel, isShowFunction = { viewModel.showSideBar() })
            }
        }
    }

}