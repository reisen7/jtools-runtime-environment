package com.lhstack.env

import com.intellij.execution.Executor
import com.intellij.execution.JavaRunConfigurationBase
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.runners.JavaProgramPatcher
import com.lhstack.env.service.RuntimeEnvironmentService

class AttachJavaProgramPatcher: JavaProgramPatcher() {

    companion object {
        fun install(){
            EP_NAME.point.registerExtension(AttachJavaProgramPatcher()){}
        }
        fun uninstall(){
            EP_NAME.point.unregisterExtension(AttachJavaProgramPatcher::class.java)
        }
    }
    override fun patchJavaParameters(
        p0: Executor,
        p1: RunProfile,
        p2: JavaParameters
    ) {
        if(p1 is JavaRunConfigurationBase){
            val project = p1.project
            p1.configurationModule.module?.let { module ->
                RuntimeEnvironmentService.getService { service ->
                    if(service.isActive(project, module)){
                        service.getSelectEnvId(project, module)?.also { envId ->
                            service.getById(envId)?.also { runtimeEnv ->
                                val env = runtimeEnv.envValue?:""
                                val args = runtimeEnv.argsValue?:""
                                args.split("\n").forEach { line ->
                                    val array = line.split("=")
                                    p2.programParametersList.add("${array[0].trim()}=${array[1].trim()}")
                                }
                                env.split("\n").forEach { line ->
                                    val array = line.split("=")
                                    p2.addEnv(array[0].trim(), array[1].trim())
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}