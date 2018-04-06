package com.xiaopo.flying.layoutmaster;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import java.util.Optional;

/**
 * @author wupanjie on 2018/4/4.
 */
public class ActionKit {
  private ActionKit() {
    //no instance
  }

  public static PsiFile getCurrentEditFile(AnActionEvent anActionEvent) {
    PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
    if (psiFile != null) {
      return psiFile;
    }

    Project project = anActionEvent.getProject();
    Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);

    if (editor == null || project == null) {
      return null;
    }

    return PsiUtilBase.getPsiFileInEditor(editor, project);
  }

  public static Optional<FileEditor> getCurrentFileEditor(final AnActionEvent anActionEvent) {
    return Optional.ofNullable(anActionEvent.getProject())
        .map(project -> ActionKit.getCurrentEditFile(anActionEvent))
        .map(file ->
            FileEditorManager
                .getInstance(anActionEvent.getProject())
                .getSelectedEditor(file.getVirtualFile())
        );
  }
}
