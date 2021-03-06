package dohun.kim.kinda_plugin.action

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.components.JBTextField
import com.intellij.util.ResourceUtil
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class CreateKindaTemplateDialog(
    private val project: Project,
    private val selectedDirectory: VirtualFile
) : DialogWrapper(true) {

    private val nameTextField = JBTextField()
    private val panel = FormBuilder.createFormBuilder()
        .addLabeledComponent("이름: ", nameTextField)
        .panel

    init {
        init()
        title = "Create Kinda Template"
    }

    override fun createCenterPanel(): JComponent? {
        return panel
    }

    override fun doOKAction() {
        val name = nameTextField.text

        if (isFileExist("${name}State") || isFileExist("${name}ViewModel")) {
            showErrorDialog(project, "$name 으로 생성된 State 혹은 ViewModel이 존재합니다", "이미 존재하는 파일")
            return
        }


        val packageName = getPackageNameByPath(selectedDirectory.path)
        createFileWithTemplate(
            type = "State",
            name = name,
            packageName = packageName
        )
        createFileWithTemplate(
            type = "ViewModel",
            name = name,
            packageName = packageName
        )

        super.doOKAction()
    }

    private fun isFileExist(fileName: String): Boolean {
        val file = VirtualFileManager.getInstance().findFileByUrl("file://${selectedDirectory.path}/${fileName}")
        return file != null
    }

    private fun getPackageNameByPath(path: String): String {
        return try {
            val splited =
                if (path.contains("java/")) path.split("java/")
                else path.split("kotlin/")
            return splited[1].replace("/", ".")
        } catch (e: IndexOutOfBoundsException) {
            ""
        }
    }

    private fun createFileWithTemplate(
        type: String,
        packageName: String,
        name: String
    ) {
        val file = selectedDirectory.createChildData(this, "${name}${type}.kt")
        val templateContent = getTemplateContentByName("${type}Template")
        val replaced = templateContent.replace("\$NAME$", name)
            .replace("\$PACKAGE$", packageName)
        VfsUtil.saveText(file, replaced)
    }

    private fun getTemplateContentByName(templateName: String): String {
        val templateFileInputStream = ResourceUtil.getResourceAsStream(
            javaClass.classLoader,
            "templates",
            templateName
        )
        return templateFileInputStream.bufferedReader()
            .readLines()
            .reduce { acc, s -> "${acc}\n${s}" }
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