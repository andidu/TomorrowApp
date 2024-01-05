package com.adorastudios.tomorrowapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adorastudios.tomorrowapp.presentation.screens.Screens
import com.adorastudios.tomorrowapp.presentation.screens.addEditTodo.AddEditTodoScreen
import com.adorastudios.tomorrowapp.presentation.screens.todoList.TodoListScreen
import com.adorastudios.tomorrowapp.ui.theme.TomorrowAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TomorrowAppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .navigationBarsPadding(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screens.TodoList.route,
                    ) {
                        composable(
                            route = Screens.TodoList.route,
                        ) {
                            TodoListScreen(navController = navController)
                        }
                        composable(
                            route = Screens.AddEditTodo.route + "?todoId={todoId}",
                            arguments = listOf(
                                navArgument(
                                    name = "todoId",
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                            ),
                        ) {
                            AddEditTodoScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
