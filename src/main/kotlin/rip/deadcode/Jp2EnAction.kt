package rip.deadcode

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import java.awt.Point
import javax.swing.JLabel

class Jp2EnAction : AnAction("Hello") {

    override fun actionPerformed(e: AnActionEvent) {

        val editor = FileEditorManager.getInstance(e.project!!).selectedTextEditor!!
        val cursorPos = editor.visualPositionToXY(editor.caretModel.visualPosition)

        val balloon = JBPopupFactory.getInstance()
                .createBalloonBuilder(JLabel("Boo!"))
                .setShowCallout(false)
                .setHideOnLinkClick(false)
                .createBalloon()

        val cornerAwarePos = Point(cursorPos.x + balloon.preferredSize.width / 2, cursorPos.y + balloon.preferredSize.height / 2)
        val rp = RelativePoint(editor.contentComponent, cornerAwarePos)

        balloon.show(rp, Balloon.Position.below)
    }
}
