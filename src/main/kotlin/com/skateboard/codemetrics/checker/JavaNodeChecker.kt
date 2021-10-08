package com.skateboard.codemetrics.checker


import com.intellij.psi.*

class JavaNodeChecker : NodeChecker() {

    override val nodeSet: MutableSet<Class<out PsiElement>>
        get() = mutableSetOf(
            PsiIfStatement::class.java,
            PsiWhileStatement::class.java,
            PsiDoWhileStatement::class.java,
            PsiForStatement::class.java,
            PsiForeachStatement::class.java,
            PsiSwitchLabelStatement::class.java,
            PsiCatchSection::class.java,
            PsiConditionalExpression::class.java,
        )

    override fun check(statement: PsiElement): Int {
        var nodeNum = 0
        if (isNode(statement)) {
            nodeNum++
        }
        statement.children.forEach {
            if (it is PsiJavaToken && (JavaTokenType.ANDAND == it.tokenType || JavaTokenType.OROR == it.tokenType)) {
                nodeNum++
            }
            nodeNum += check(it)
        }
        return nodeNum
    }
}