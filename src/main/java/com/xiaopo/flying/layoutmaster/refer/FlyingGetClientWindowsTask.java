package com.xiaopo.flying.layoutmaster.refer;

import com.android.ddmlib.Client;
import com.android.layoutinspector.model.ClientWindow;
import com.android.tools.idea.editors.layoutInspector.WindowPickerDialog;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * refer : com.android.tools.idea.ddms.actions.LayoutInspectorAction#GetClientWindowsTask
 */
public class FlyingGetClientWindowsTask extends Task.Backgroundable {
  private final Client client;
  private List<ClientWindow> windows;
  private String error;
  private final Project project;

  public FlyingGetClientWindowsTask(final Project project, final Client client) {
    super(project, "Flying Obtaining Windows");
    this.client = client;
    this.project = project;
  }

  @Override public void run(@NotNull ProgressIndicator indicator) {
    indicator.setIndeterminate(true);

    try {
      windows = ClientWindow.getAll(client, 5, TimeUnit.SECONDS);

      if (windows == null) {
        error = "Unable to obtain list of windows used by " +
            client.getClientData().getPackageName() +
            "\nLayout Inspector requires device API version to be 18 or greater.";
      } else if (windows.isEmpty()) {
        error = "No active windows displayed by " + client.getClientData().getPackageName();
      }
    } catch (IOException e) {
      error = "Unable to obtain list of windows used by "
          + client.getClientData().getPackageName()
          + "\nError: "
          + e.getMessage();
    }
  }

  @Override public void onSuccess() {
    if (error != null) {
      Messages.showErrorDialog(error, "Capture View Hierarchy");
      return;
    }

    ClientWindow window;
    if (windows.size() == 1) {
      window = windows.get(0);
    } else { // prompt user if there are more than 1 windows displayed by this application
      WindowPickerDialog pickerDialog = new WindowPickerDialog(project, client, windows);
      if (!pickerDialog.showAndGet()) {
        return;
      }

      window = pickerDialog.getSelectedWindow();
      if (window == null) {
        return;
      }
    }

    FlyingLayoutInspectorCaptureTask
        captureTask = new FlyingLayoutInspectorCaptureTask(project, client, window);
    captureTask.queue();
  }
}
