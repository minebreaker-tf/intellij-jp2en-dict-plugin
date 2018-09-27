package rip.deadcode.intellij.jp2en

import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import rip.deadcode.intellij.jp2en.Translator.defaultHttpTransport
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.IOException
import javax.swing.JComponent
import javax.swing.JTextArea
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

class Jp2EnComponent(defaultText: String?) : JComponent() {

    internal val input = JBTextField()
    internal var hideHandler: () -> Unit = {}
    private val resultView = JTextArea()

    init {
        layout = BorderLayout()

        input.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_ENTER -> {
                        resultView.text = translate(input.text)
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
            it.font = it.font.deriveFont(18F)
        }

        resultView.also {
            it.isEditable = false
            it.border = EmptyBorder(0, 0, 0, 0)
            it.background = JBUI.CurrentTheme.SearchEverywhere.dialogBackground()
            it.font = UIUtil.getListFont().deriveFont(18F)
        }

        this.border = EmptyBorder(0, 0, 0, 0)
        this.background = JBUI.CurrentTheme.SearchEverywhere.dialogBackground()

        this.add(input, BorderLayout.NORTH)
        this.add(JBScrollPane(resultView), BorderLayout.CENTER)

        if (defaultText != null) {
            resultView.text = translate(input.text)
        }
    }

    private fun translate(word: String): String {
        return try {
            Translator.translate(defaultHttpTransport, word) ?: "No match found."
        } catch (e: IOException) {
            "Failed to fetch results."
        }
    }
}
