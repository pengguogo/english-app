package com.englishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.englishapp.ui.home.HomeScreen
import com.englishapp.ui.lessonlearn.LessonLearnScreen
import com.englishapp.ui.lessonlist.LessonListScreen
import com.englishapp.ui.theme.EnglishAppTheme
import com.englishapp.ui.unitlist.UnitListScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * 应用入口 Activity。
 * 配置导航图,连接首页 → 单元列表 → 课时列表 → 课时学习 四个页面。
 * @author englishapp
 * @since 1.0.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnglishAppTheme {
                AppNavigation()
            }
        }
    }
}

/**
 * 应用导航图。
 * 路由:
 * - home: 首页(主题列表)
 * - unit-list/{themeId}: 单元列表页
 * - lesson-list/{unitId}: 课时列表页
 * - lesson-learn/{lessonId}: 课时学习页
 * @author englishapp
 * @since 1.0.0
 */
@Composable
private fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // 首页:主题列表
        composable("home") {
            HomeScreen(
                onThemeClick = { themeId ->
                    navController.navigate("unit-list/$themeId")
                }
            )
        }

        // 单元列表页
        composable(
            route = "unit-list/{themeId}",
            arguments = listOf(navArgument("themeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val themeId = backStackEntry.arguments?.getInt("themeId") ?: 1
            UnitListScreen(
                themeId = themeId,
                onBack = { navController.popBackStack() },
                onUnitClick = { unitId ->
                    navController.navigate("lesson-list/$unitId")
                }
            )
        }

        // 课时列表页
        composable(
            route = "lesson-list/{unitId}",
            arguments = listOf(navArgument("unitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val unitId = backStackEntry.arguments?.getInt("unitId") ?: 1
            LessonListScreen(
                unitId = unitId,
                onBack = { navController.popBackStack() },
                onLessonClick = { lessonId ->
                    navController.navigate("lesson-learn/$lessonId")
                }
            )
        }

        // 课时学习页(核心)
        composable(
            route = "lesson-learn/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
            LessonLearnScreen(
                lessonId = lessonId,
                onBack = { navController.popBackStack() },
                onComplete = {
                    // 完成后返回课时列表,并刷新
                    navController.popBackStack("lesson-list/{unitId}", inclusive = false)
                }
            )
        }
    }
}
