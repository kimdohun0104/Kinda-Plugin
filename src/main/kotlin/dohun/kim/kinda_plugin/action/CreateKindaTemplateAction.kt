package dohun.kim.kinda_plugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class CreateKindaTemplateAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val currentProject = e.project
        val selectedDirectory = e.getData(CommonDataKeys.VIRTUAL_FILE)

        if (!isEnableDirectory(selectedDirectory!!.path)) {
            showErrorDialog(
                currentProject!!,
                "src/main/(java|kotlin)에 해당하지 않습니다",
                "유효하지 않은 디렉토리입니다"
            )
        }

        CreateKindaTemplateDialog(currentProject!!, selectedDirectory).show()
    }

    override fun update(e: AnActionEvent) {
        val path = e.getData(CommonDataKeys.VIRTUAL_FILE)?.path
        e.presentation.isEnabledAndVisible = isEnableDirectory(path)
    }

    private fun isEnableDirectory(path: String?): Boolean {
        return path?.contains("src/main/(java|kotlin)".toRegex()) ?: false
    }

    private fun showErrorDialog(
        project: Project,
        message: String,
        title: String
    ) {
        Messages.showMessageDialog(
            project,
            message,
            title,
            null
        )
    }
}