package com.pjb.survey.ui

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pjb.survey.ui.design.DesignScreen
import com.pjb.survey.ui.design.DesignViewModel
import com.pjb.survey.ui.overview.OverviewScreen
import com.pjb.survey.ui.question.SurveyScreen
import com.pjb.survey.ui.question.SurveyViewModel
import com.pjb.survey.ui.result.ResultScreen

enum class Screen { OVERVIEW, SURVEY, DESIGN, RESULT }

@Composable
fun NavigationScreen(
    surveyViewModel: SurveyViewModel = viewModel(),
    designViewModel: DesignViewModel = viewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Screen.OVERVIEW.name,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.OVERVIEW.name) {
            OverviewScreen(
                onAdd = {
                    navController.navigate(Screen.DESIGN.name)
                    designViewModel.insertAndDesign()
                },
                onSurvey = {
                    if (it.questions.isEmpty()) {
                        Toast.makeText(context, "该问卷问题数为0，无法作答", Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate(Screen.SURVEY.name)
                        surveyViewModel.startSurvey(it)
                    }
                },
                onEdit = {
                    navController.navigate(Screen.DESIGN.name)
                    designViewModel.startDesign(it)
                },
                onShowResult = {
                    navController.navigate(Screen.RESULT.name + "/" + it)
                }
            )
        }
        composable(Screen.SURVEY.name) {
            surveyViewModel.surveyState.collectAsState().value?.let {
                SurveyScreen(
                    questionnaireState = it,
                    onDonePressed = {
                        surveyViewModel.processResult()
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(Screen.DESIGN.name) {
            designViewModel.uiState.collectAsState().value?.let {
                DesignScreen(
                    designState = it,
                    onDone = {
                        designViewModel.finishDesign()
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(
            Screen.RESULT.name + "/{id}",
            arguments = listOf(navArgument("id") {type = NavType.IntType})
        ) {
            val id = it.arguments?.getInt("id") ?: -1
            ResultScreen(
                id
            )
        }
    }
}

