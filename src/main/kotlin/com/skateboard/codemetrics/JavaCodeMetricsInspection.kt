package com.skateboard.codemetrics

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.ui.DocumentAdapter
import com.skateboard.codemetrics.checker.JavaNodeChecker
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

class JavaCodeMetricsInspection : AbstractBaseJavaLocalInspectionTool() {

    var complexLevel = 10

    override fun createOptionsPanel(): JComponent? {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        val levelTextField = JTextField(complexLevel.toString())
        levelTextField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                complexLevel = levelTextField.text.toInt()
            }
        })
        panel.add(levelTextField)
        return panel
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaRecursiveElementWalkingVisitor() {
            override fun visitMethod(method: PsiMethod?) {
                method?.let { method ->
                    var nodeNum = 0
                    val statementChecker = JavaNodeChecker()
                    method.body?.let {
                        nodeNum+=statementChecker.check(it)
                    }
                    if (nodeNum + 1 > complexLevel) {
                        holder.registerProblem(
                            method.nameIdentifier!!,
                            "圈复杂度（${nodeNum+1}），兄弟，注意一下", ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                        )
                    }
                }
            }
        }
    }


}