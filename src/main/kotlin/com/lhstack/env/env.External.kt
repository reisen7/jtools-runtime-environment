package com.lhstack.env

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager

fun Module.isMainModule(): Boolean {
    val rootManager = ModuleRootManager.getInstance(this)
    val sourceRoots = rootManager.sourceRoots
    return sourceRoots.isNotEmpty()
}

fun Any.setArgs(project: Project, module: Module, env: String, value: String) {
    PropertiesComponent.getInstance().setValue("RUNTIME_ENVIRONMENT_args_${env}_${project.locationHash}_$module", value)
}

fun Any.setEnv(project: Project, module: Module, env: String, value: String) {
    PropertiesComponent.getInstance().setValue("RUNTIME_ENVIRONMENT_env_${env}_${project.locationHash}_$module", value)
}

fun Any.getActiveEnv(project: Project, module: Module) =
    PropertiesComponent.getInstance().getValue("RUNTIME_ENVIRONMENT_activeEnv_${project.locationHash}_$module") ?: "dev"

fun Any.setActiveEnv(project: Project, module: Module, value: String) {
    PropertiesComponent.getInstance().setValue("RUNTIME_ENVIRONMENT_activeEnv_${project.locationHash}_$module",value)
}

fun Any.getArgs(project: Project, module: Module,env:String) =
    PropertiesComponent.getInstance().getValue("RUNTIME_ENVIRONMENT_args_${env}_${project.locationHash}_$module") ?: ""

fun Any.getEnv(project: Project, module: Module,env: String) =
    PropertiesComponent.getInstance().getValue("RUNTIME_ENVIRONMENT_env_${env}_${project.locationHash}_$module") ?: ""

fun Any.getActive(project: Project,module: Module) = PropertiesComponent.getInstance().getBoolean("RUNTIME_ENVIRONMENT_active_${project.locationHash}_$module",false)
fun Any.setActive(project: Project,module: Module,active: Boolean) = PropertiesComponent.getInstance().setValue("RUNTIME_ENVIRONMENT_active_${project.locationHash}_$module","$active")