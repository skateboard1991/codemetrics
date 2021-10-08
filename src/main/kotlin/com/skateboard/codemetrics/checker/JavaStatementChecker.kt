package com.skateboard.codemetrics.checker


import com.intellij.psi.*

class JavaStatementChecker : NodeChecker() {

    override val nodeSet: MutableSet<Class<out PsiElement>>
        get() = mutableSetOf(
            PsiIfStatement::class.java,
            PsiWhileStatement::class.java,
            PsiDoWhileStatement::class.java,
            PsiForStatement::class.java,
            PsiForeachStatement::class.java,
            PsiSwitchLabelStatement::class.java
        )

    override fun check(statement: PsiElement): Int {
        var nodeNum = 0
        if (isNode(statement)) {
            nodeNum++
        }
        statement.children.forEach {
            when (it) {
                is PsiStatement -> {
                    nodeNum += check(it)
                }
                is PsiCodeBlock -> {
                    it.statements.forEach { sta ->
                        nodeNum += check(sta)
                    }
                }
                is PsiCatchSection -> {
                    nodeNum++
                }
                is PsiLocalVariable -> {
                    it.children.forEach { ele ->
                        if (ele is PsiExpression) {
                            nodeNum += checkExpression(ele)
                        }
                    }
                }
                is PsiExpression -> {
                    nodeNum += checkExpression(it)
                }
            }
        }
        return nodeNum
    }

    private fun checkExpression(exp: PsiExpression): Int {
        var nodeNum = 0
        when (exp) {
            //三目运算符
            is PsiConditionalExpression -> {
                nodeNum++
            }
            is PsiExpression -> {
                exp.children.forEach {
                    if (it is PsiExpression) {
                        nodeNum += checkExpression(it)
                    } else if (it is PsiJavaToken) {
                        if (JavaTokenType.ANDAND == it.tokenType || JavaTokenType.OROR == it.tokenType) {
                            nodeNum++
                        }
                    }
                }
            }
        }
        return nodeNum
    }
}