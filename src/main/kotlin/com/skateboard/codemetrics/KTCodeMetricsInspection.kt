package com.skateboard.codemetrics

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.ui.DocumentAdapter
import com.skateboard.codemetrics.checker.KTNodeChecker
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.idea.structuralsearch.visitor.KotlinRecursiveElementWalkingVisitor
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

class KTCodeMetricsInspection : AbstractKotlinInspection() {

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

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {

        return object : KotlinRecursiveElementWalkingVisitor() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                var nodeNum = 0
                val ktExpressionChecker = KTNodeChecker()
                function.bodyBlockExpression?.let {
                    nodeNum = ktExpressionChecker.check(it)
                }
                if (nodeNum + 1 > complexLevel) {
                    holder.registerProblem(
                        function.nameIdentifier!!,
                        "圈复杂度（${nodeNum+1}），兄弟，注意一下", ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                    )
                }
            }


            override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {

                lambdaExpression.bodyExpression?.let {
                    var nodeNum = 0
                    val ktExpressionChecker = KTNodeChecker()
                    nodeNum = ktExpressionChecker.check(it)
                    if (nodeNum + 1 > complexLevel) {
                        holder.registerProblem(
                            lambdaExpression,
                            "圈复杂度（${nodeNum+1}），兄弟，注意一下", ProblemHighlightType.ERROR
                        )
                    }
                }
            }
        }
    }
}