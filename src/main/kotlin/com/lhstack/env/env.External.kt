package com.lhstack.env

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager

fun Module.isMainModule(): Boolean {
    val rootManager = ModuleRootManager.getInstance(this)
    val sourceRoots = rootManager.sourceRoots
    return sourceRoots.isNotEmpty()
}