package com.xiaopo.flying.layoutmaster.refer;

import com.android.ddmlib.Client;
import com.android.layoutinspector.LayoutInspectorCaptureOptions;
import com.android.layoutinspector.LayoutInspectorResult;
import com.android.layoutinspector.ProtocolVersion;
import com.android.layoutinspector.model.ClientWindow;
import com.android.tools.analytics.UsageTracker;
import com.android.tools.idea.editors.layoutInspector.LayoutInspectorCaptureType;
import com.android.tools.idea.editors.layoutInspector.LayoutInspectorEditor;
import com.android.tools.idea.profiling.capture.Capture;
import com.android.tools.idea.profiling.capture.CaptureService;
import com.android.tools.idea.stats.AndroidStudioUsageTracker;
import com.google.wireless.android.sdk.stats.AndroidStudioEvent;
import com.google.wireless.android.sdk.stats.LayoutInspectorEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.xiaopo.flying.layoutmaster.GlobalConfig;
import com.xiaopo.flying.layoutmaster.LayoutInspectorBridge2;
import com.intellij.util.ui.UIUtil;
import com.xiaopo.flying.layoutmaster.LayoutInspectorHook;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * refer : com.android.tools.idea.editors.layoutInspector.LayoutInspectorCaptureTask
 */
public class FlyingLayoutInspectorCaptureTask extends Task.Backgroundable {
  private static final String TITLE = "Capture View Hierarchy";

  @NotNull private final Client myClient;
  @NotNull private final ClientWindow myWindow;
  private final Project project;

  private String myError;
  private byte[] myData;

  public FlyingLayoutInspectorCaptureTask(@NotNull Project project, @NotNull Client client, @NotNull
      ClientWindow window) {
    super(project, "Capturing View Hierarchy");
    myClient = client;
    myWindow = window;
    this.project = project;
  }

  @Override public void run(@NotNull ProgressIndicator indicator) {
    LayoutInspectorCaptureOptions options = new LayoutInspectorCaptureOptions();
    options.setTitle(myWindow.getDisplayName());
    // 开启V2加快速度，但不支持Custom Properties
    if (GlobalConfig.INSTANCE.getUseV2()) {
      options.setVersion(ProtocolVersion.Version2);
    }

    // Capture view hierarchy
    indicator.setText("Capturing View Hierarchy");
    indicator.setIndeterminate(false);

    long startTimeMs = System.currentTimeMillis();
    LayoutInspectorResult result = LayoutInspectorBridge2.captureView(myWindow, options);
    if (!result.getError().isEmpty()) {
      myError = result.getError();
      return;
    }

    long captureDurationMs = System.currentTimeMillis() - startTimeMs;
    UsageTracker.log(AndroidStudioEvent.newBuilder()
            .setKind(AndroidStudioEvent.EventKind.LAYOUT_INSPECTOR_EVENT)
            .setDeviceInfo(AndroidStudioUsageTracker.deviceToDeviceInfo(myClient.getDevice()))
            .setLayoutInspectorEvent(LayoutInspectorEvent.newBuilder()
                .setType(LayoutInspectorEvent.LayoutInspectorEventType.CAPTURE)
                .setDurationInMs(captureDurationMs)));

    myData = result.getData();
  }

  @Override public void onSuccess() {
    if (myError != null) {
      Messages.showErrorDialog("Error obtaining view hierarchy: " + StringUtil.notNullize(myError),
          TITLE);
      return;
    }

    CaptureService service = CaptureService.getInstance(project);
    try {
      Capture capture = service.createCapture(LayoutInspectorCaptureType.class, myData,
          service.getSuggestedName(myClient));
      final VirtualFile file = capture.getFile();
      file.refresh(true, false, () -> UIUtil.invokeLaterIfNeeded(() -> {
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
        List<FileEditor> editors =
            FileEditorManager.getInstance(project).openEditor(descriptor, true);
        editors
            .stream()
            .filter(e -> e instanceof LayoutInspectorEditor)
            .findFirst()
            .ifPresent(fileEditor -> {
              new LayoutInspectorHook(
                  project,
                  (LayoutInspectorEditor) fileEditor,
                  myClient,
                  myWindow
              ).hook();
            });
      }));
    } catch (IOException e) {
      Messages.showErrorDialog("Error creating hierarchy view capture: " + e, TITLE);
    }
  }
}
