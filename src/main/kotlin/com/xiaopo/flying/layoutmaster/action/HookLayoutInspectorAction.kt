package com.xiaopo.flying.layoutmaster.action

import com.android.tools.idea.editors.layoutInspector.actions.AndroidRunLayoutInspectorAction
import com.android.tools.idea.fd.actions.RestartActivityAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.DialogWrapper
import com.xiaopo.flying.layoutmaster.refer.FlyingGetClientWindowsTask
import org.jetbrains.android.actions.AndroidProcessChooserDialog
import org.jetbrains.android.util.AndroidBundle

/**
 * @author wupanjie on 2018/4/4.
 */
class HookLayoutInspectorAction : AnAction("Layout Master", "Run Layout Inspector with Layout Master", null) {

  override fun update(e: AnActionEvent) {
    super.update(e)
    if (RestartActivityAction.isDebuggerPaused(e.project)) {
      e.presentation.description = AndroidBundle.message("android.ddms.actions.layoutinspector.description.disabled")
      e.presentation.isEnabled = false
    } else {
      e.presentation.description = AndroidBundle.message("android.ddms.actions.layoutinspector.description")
      e.presentation.isEnabled = true
    }
  }

  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project!!

    //if (!AndroidSdkUtils.activateDdmsIfNecessary(project)) {
    //  return;
    //}

    val dialog = AndroidProcessChooserDialog(project, false)
    dialog.show()
    if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {
      val client = dialog.client
      if (client != null) {
        FlyingGetClientWindowsTask(project, client).queue()
      } else {
        Logger.getInstance(AndroidRunLayoutInspectorAction::class.java)
            .warn("Not launching layout inspector - no client selected")
      }
    }
  }
}
