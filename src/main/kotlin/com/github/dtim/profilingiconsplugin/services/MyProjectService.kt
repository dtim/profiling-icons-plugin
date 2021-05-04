package com.github.dtim.profilingiconsplugin.services

import com.github.dtim.profilingiconsplugin.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
