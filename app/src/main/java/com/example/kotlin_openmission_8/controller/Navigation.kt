package com.example.kotlin_openmission_8.controller

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                // ✅ 4. "캔버스 ID"를 받아서 이동하는 람다로 수정
                onNavigateToCanvas = { boardId ->
                    // 예: "main_canvas/a1b2c3" 경로로 이동
                    navController.navigate("main_canvas/$boardId")
                }
            )
        }

        // 5. "detail" 경로(DetailScreen) 등록
        composable(
            route = "main_canvas/{boardId}",
            arguments = listOf(navArgument("boardId") { type = NavType.StringType })
        ) { backStackEntry ->

            // 6. URL 경로에서 boardId를 추출
            val boardId = backStackEntry.arguments?.getString("boardId")

            if (boardId != null) {
                // 7. CanvasScreen에 boardId와 navController 자체를 전달
                CanvasScreen(
                    context = context,
                    viewModel = viewModel,
                    boardId = boardId,
                    navController = navController // ✅ 8. navController를 여기 전달
                )
            } else {
                // boardId가 없는 경우 예외 처리
                Text("오류: 캔버스 ID가 없습니다.")
            }
        }
    }
}

