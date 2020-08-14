package com.xiaopo.flying.layoutmaster

import com.android.layoutinspector.LayoutInspectorCaptureOptions
import com.android.layoutinspector.LayoutInspectorResult
import com.android.layoutinspector.model.ClientWindow
import com.android.layoutinspector.model.ViewNode
import com.android.layoutinspector.parser.ViewNodeParser
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.util.concurrent.TimeUnit

/**
 * refer: com.android.layoutinspector.LayoutInspectorBridge
 */
object LayoutInspectorBridge2 {
    @JvmStatic
    val V2_MIN_API = 23

    @JvmStatic
    fun captureView(
        window: ClientWindow, options: LayoutInspectorCaptureOptions
    ): LayoutInspectorResult {
        val hierarchy =
            window.loadWindowData(options, 120, TimeUnit.SECONDS) ?: return LayoutInspectorResult(
                null,
                "There was a timeout error capturing the layout data from the device.\n" +
                "The device may be too slow, the captured view may be too complex, or the view may contain animations.\n\n" +
                "Please retry with a simplified view and ensure the device is responsive."
            )

        var root: ViewNode?
        try {
            root = ViewNodeParser.parse(hierarchy, options.version)
        } catch (e: StringIndexOutOfBoundsException) {
            return LayoutInspectorResult(null, "Unexpected error: $e")
        } catch (e: IOException) {
            return LayoutInspectorResult(null, "Unexpected error: $e")
        }

        if (root == null) {
            return LayoutInspectorResult(
                null,
                "Unable to parse view hierarchy"
            )
        }

        //  Get the preview of the root node
        val preview = window.loadViewImage(
            root,
            10,
            TimeUnit.SECONDS
        ) ?: return LayoutInspectorResult(
            null,
            "Unable to obtain preview image"
        )

        val bytes = ByteArrayOutputStream(4096)
        var output: ObjectOutputStream? = null

        try {
            output = ObjectOutputStream(bytes)
            output.writeUTF(options.toString())

            output.writeInt(hierarchy.size)
            output.write(hierarchy)

            output.writeInt(preview.size)
            output.write(preview)
        } catch (e: IOException) {
            return LayoutInspectorResult(
                null,
                "Unexpected error while saving hierarchy snapshot: $e"
            )
        } finally {
            try {
                if (output != null) {
                    output.close()
                }
            } catch (e: IOException) {
                return LayoutInspectorResult(
                    null,
                    "Unexpected error while closing hierarchy snapshot: $e"
                )
            }

        }

        return LayoutInspectorResult(bytes.toByteArray(), "")
    }
}