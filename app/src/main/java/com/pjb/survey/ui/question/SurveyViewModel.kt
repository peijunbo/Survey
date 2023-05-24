package com.pjb.survey.ui.question

import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjb.survey.SurveyApplication
import com.pjb.survey.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SurveyViewModel : ViewModel() {
    companion object {
        private const val TAG = "QuestionViewModel"
    }

    private val context by lazy { SurveyApplication.context }
    private val database: SurveyDatabase = SurveyDatabase.getInstance(context)

    private val _surveyQuestionnaireState = MutableStateFlow<SurveyState.QuestionnaireState?>(null)
    val surveyState: StateFlow<SurveyState.QuestionnaireState?>
        get() = _surveyQuestionnaireState

    fun startSurvey(questionnaire: Questionnaire) {
        viewModelScope.launch {
            _surveyQuestionnaireState.value = questionnaire.getSurveyState()
        }
    }

    fun startSurvey(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val questionnaire = database.questionnaireDao().getQuestionnaireById(id = id)
            withContext(Dispatchers.Main) {
                _surveyQuestionnaireState.value = questionnaire.getSurveyState()
            }
        }
    }

    /**
     * 插入一个测试用问卷
     */
    fun insertTestQuestionnaire() {
        viewModelScope.launch(Dispatchers.IO) {
            val questionnaire = Questionnaire(
                title = "标题",
                description = "描述，这是一个这样的问卷",
                questions = listOf(
                    Question(
                        QuestionType.Blank, "这是一个填空题", PossibleAnswer.Blank("填空提示")
                    ),
                    Question(
                        QuestionType.SingleChoice, "这是一个单选题", PossibleAnswer.SingleChoice(
                            listOf("选项1", "选项2", "选项3")
                        )
                    ),
                    Question(
                        QuestionType.SingleChoice, "单选题2", PossibleAnswer.SingleChoice(
                            listOf("选项1", "选项2", "选项3")
                        )
                    ),
                    Question(
                        QuestionType.SingleChoice, "单选题3", PossibleAnswer.SingleChoice(
                            listOf("选项1", "选项2", "选项3")
                        )
                    ),
                    Question(
                        QuestionType.MultipleChoice, "多选问题1", PossibleAnswer.MultipleChoice(
                            listOf("选项1", "选项2", "选项3")
                        )
                    )
                )
            )
            database.questionnaireDao().insert(questionnaire)
        }
    }

    fun processResult() {
        _surveyQuestionnaireState.value?.let {
            val surveyResult = it.getResult()
            viewModelScope.launch(Dispatchers.IO) {
                database.surveyResultDao().insert(surveyResult)
            }
        }
    }

}