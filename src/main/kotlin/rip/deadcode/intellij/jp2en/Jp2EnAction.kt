package rip.deadcode.intellij.jp2en

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import java.awt.Dimension

class Jp2EnAction : AnAction("Hello") {

    override fun actionPerformed(e: AnActionEvent) {

        val editor = FileEditorManager.getInstance(e.project!!).selectedTextEditor!!
        val cursorPos = editor.visualPositionToXY(editor.caretModel.visualPosition)

        val component = Jp2EnComponent(editor.selectionModel.selectedText)
        val popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(component, component.input)
                .setModalContext(false)
                .setRequestFocus(true)
                .setCancelOnClickOutside(true)
                .setCancelKeyEnabled(false)
                .setResizable(true)
                .setMinSize(Dimension(320, 180))
                .setMovable(true)
                .createPopup()

        component.hideHandler = {
            popup.cancel()
        }

        val rp = RelativePoint(editor.contentComponent, cursorPos)
        popup.show(rp)
    }
}
