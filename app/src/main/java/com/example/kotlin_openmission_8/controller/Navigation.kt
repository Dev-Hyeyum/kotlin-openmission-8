package com.example.kotlin_openmission_8.controller

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_openmission_8.screens.CanvasScreen
import com.example.kotlin_openmission_8.model.Components
import com.example.kotlin_openmission_8.screens.HomeScreen

@Composable
fun MyAppNavigation(context: Context, viewModel: Components) {

    // 1. 컨트롤러 생성 및 기억
    val navController = rememberNavController()

    // 2. NavHost 컨테이너 설정 (시작 화면은 "home")
    NavHost(navController = navController, startDestination = "home") {

        // 3. "home" 경로(HomeScreen) 등록
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDetail = {
                    navController.navigate("detail")
                }
            )
        }


        // 5. "detail" 경로(DetailScreen) 등록
        composable("detail") {
            CanvasScreen(
                context = context,
                viewModel = viewModel,
                onNavigateBack = {
                    // 6. 뒤로 가기 명령
                    navController.popBackStack()
                }
            )
        }
    }
}

