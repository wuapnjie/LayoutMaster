package com.xiaopo.flying.layoutmaster.action;

import com.android.ddmlib.Client;
import com.android.tools.idea.editors.layoutInspector.actions.AndroidRunLayoutInspectorAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.xiaopo.flying.layoutmaster.GlobalConfig;
import com.xiaopo.flying.layoutmaster.refer.FlyingGetClientWindowsTask;
import org.jetbrains.android.actions.AndroidProcessChooserDialog;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author wupanjie
 */
public class HookLayoutInspectorActionV2 extends AnAction {

    public HookLayoutInspectorActionV2() {
        super("Layout Master (V2, Quick but less info)", "Run Layout Inspector with Layout Master", null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        if (AndroidRunLayoutInspectorAction.isDebuggerPaused(e.getProject())) {
            e.getPresentation().setDescription(AndroidBundle.message("android.ddms.actions.layoutinspector.description.disabled"));
            e.getPresentation().setEnabled(false);
        } else {
            e.getPresentation().setDescription(AndroidBundle.message("android.ddms.actions.layoutinspector.description"));
            e.getPresentation().setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getProject();

        if (project == null) return;

        AndroidProcessChooserDialog dialog = new AndroidProcessChooserDialog(project, false);
        dialog.show();
        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            Client client = dialog.getClient();
            if (client != null) {
                GlobalConfig.INSTANCE.setUseV2(true);
                new FlyingGetClientWindowsTask(project, client).queue();
            } else {
                Logger.getInstance(AndroidRunLayoutInspectorAction.class).warn("Not launching layout inspector - no client selected");
            }
        }
    }
}
