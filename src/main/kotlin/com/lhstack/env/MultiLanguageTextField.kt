package com.lhstack.data.component

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.ide.highlighter.HighlighterFactory
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.LanguageTextField

class MultiLanguageTextField(
    private var languageFileType: LanguageFileType,
    project: Project,
    value: String,
    private val isLineNumbersShown: Boolean = true,
    val viewer: Boolean = false,
    val editorListener: (EditorEx) -> Unit = {}
) :
    LanguageTextField(languageFileType.language, project, value, false), Disposable {

    private val documentCreator = SimpleDocumentCreator()

    companion object {
        fun dynamic(
            project: Project,
            value: String,
            parent: Disposable,
            isViewer: Boolean = false
        ): MultiLanguageTextField {
            val fileType = FileTypeManager.getInstance().getStdFileType("JSON5") as LanguageFileType
            return MultiLanguageTextField(fileType, project, value, viewer = isViewer).apply {
                Disposer.register(parent, this)
            }
        }
    }

    init {
        border = null
    }

    override fun dispose() {
        editor?.let { EditorFactory.getInstance().releaseEditor(it) }
    }

    fun changeLanguageFieType(languageFileType: LanguageFileType) {
        if (this.languageFileType !== languageFileType) {
            this.setNewDocumentAndFileType(
                languageFileType,
                this.documentCreator.createDocument(this.document.text, languageFileType.language, this.project)
            )
            this.languageFileType = languageFileType
            val editor = this.editor
            if (editor is EditorEx) {
                editor.highlighter = HighlighterFactory.createHighlighter(this.project, this.languageFileType)
            }
        }
    }

    override fun createEditor(): EditorEx {
        val editorEx = EditorFactory.getInstance()
            .createEditor(document, project, languageFileType, this.viewer) as EditorEx
        editorEx.highlighter = HighlighterFactory.createHighlighter(project, languageFileType)
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(
            editorEx.document
        )
        if (psiFile != null) {
            DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, true)
//            if(psiFile is PsiJavaFile){
//                DaemonCodeAnalyzer.getInstance(project).setImportHintsEnabled(psiFile,true)
//            }else if(psiFile is GroovyFile){
//                DaemonCodeAnalyzer.getInstance(project).setImportHintsEnabled(psiFile,true)
//            }
        }
        editorEx.setBorder(null)
        editorListener.invoke(editorEx)
        val settings = editorEx.settings
        //去掉折叠轮廓列,编辑器中
        settings.isFoldingOutlineShown = false
        settings.additionalLinesCount = 0
        settings.additionalColumnsCount = 1
        settings.isLineNumbersShown = isLineNumbersShown
        settings.isUseSoftWraps = false
        settings.lineCursorWidth = 1
        settings.isLineMarkerAreaShown = false
        settings.setRightMargin(-1)
        return editorEx
    }
}