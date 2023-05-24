package com.pjb.survey.data

object QuestionnaireUtil {

    fun getTestQuestionnaire() = Questionnaire(
        title = "标题",
        description = "描述，这是一个这样的问卷",
        questions = listOf(
            Question(
                QuestionType.SingleChoice, "问题1", PossibleAnswer.SingleChoice(
                    listOf("选项1", "选项2", "选项3")
                )
            ),
            Question(
                QuestionType.SingleChoice, "问题2", PossibleAnswer.SingleChoice(
                    listOf("选项1", "选项2", "选项3")
                )
            ),
            Question(
                QuestionType.SingleChoice, "问题3", PossibleAnswer.SingleChoice(
                    listOf("选项1", "选项2", "选项3")
                )
            ),
            Question(
                QuestionType.SingleChoice, "问题4", PossibleAnswer.SingleChoice(
                    listOf("选项1", "选项2", "选项3")
                )
            )
        )
    )
}