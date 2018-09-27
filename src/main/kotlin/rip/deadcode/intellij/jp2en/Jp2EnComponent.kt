package rip.deadcode.intellij.jp2en

import com.google.common.base.Throwables
import com.intellij.ide.IdeTooltipManager
import com.intellij.ui.HintHint
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import rip.deadcode.intellij.jp2en.Translator.defaultHttpTransport
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

class Jp2EnComponent : JComponent() {

    internal val input = JBTextField()
    internal var hideHandler: () -> Unit = {}
    private val resultPanel = IdeTooltipManager.initPane("<p>No result</p>", HintHint(), null)

    init {
        layout = BorderLayout()

        val topPanel = JPanel(BorderLayout()).also {
            it.add(JBLabel("日本語から英語へ翻訳"), BorderLayout.NORTH)
            it.add(input)
        }
        input.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_ENTER -> {
                        val result = translate(input.text)
                        resultPanel.text = result
                    }
                    KeyEvent.VK_ESCAPE -> {
                        hideHandler()
                    }
                }
            }
        })

        input.also {
            it.border = LineBorder(JBUI.CurrentTheme.SearchEverywhere.searchFieldBorderColor())
            it.background = JBUI.CurrentTheme.SearchEverywhere.searchFieldBackground()
        }

        resultPanel.also {
            it.border = EmptyBorder(0, 0, 0, 0)
            it.background = JBUI.CurrentTheme.SearchEverywhere.dialogBackground()
        }

        this.border = EmptyBorder(0, 0, 0, 0)
        this.background = JBUI.CurrentTheme.SearchEverywhere.dialogBackground()

        this.add(topPanel, BorderLayout.NORTH)
        this.add(JBScrollPane(resultPanel), BorderLayout.CENTER)
    }

    private fun translate(word: String): String {
        return try {
            Translator.translate(defaultHttpTransport, word) ?: "No match found."
        } catch (e: Exception) {
            Throwables.getStackTraceAsString(e)
        }
    }
}
