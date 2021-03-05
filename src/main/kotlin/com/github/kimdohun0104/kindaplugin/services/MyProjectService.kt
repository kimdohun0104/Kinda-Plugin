package com.github.kimdohun0104.kindaplugin.services

import com.github.kimdohun0104.kindaplugin.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
