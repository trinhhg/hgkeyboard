package app.funput.funput.keyboard.surface.geometry

import android.view.View
import app.funput.funput.keyboard.utility.KeyboardMicrophoneState
import app.funput.funput.keyboard.layout.KeyboardSizingProfile
import app.funput.funput.keyboard.layout.ResolvedKeyboard
import app.funput.funput.keyboard.layout.geometry.resolveGeometry
import app.funput.funput.keyboard.layout.toolbar.ToolbarArrangement
import app.funput.funput.keyboard.model.KeyboardLayout

/** Owns resolved geometry and utility visibility without transcript or session knowledge. */
internal class KeyboardSurfaceGeometry(
    private val host: View,
    private val layout: () -> KeyboardLayout,
    private val profile: () -> KeyboardSizingProfile,
    private val utilitiesVisible: () -> Boolean,
    private val hasSuggestions: () -> Boolean = { false },
    private val isDualBar: () -> Boolean = { false },
    private val microphoneAllowed: () -> Boolean,
    private val changed: () -> Unit,
) {
    var keyboard: ResolvedKeyboard? = null
        private set
    var microphone = KeyboardMicrophoneState()
        set(value) {
            if (field == value) return
            field = value
            resolve()
            host.invalidate()
        }
    var clipboardKeyVisible = false
        set(value) {
            if (field == value) return
            field = value
            resolve()
        }
    var placementKeyVisible = true
        set(value) {
            if (field == value) return
            field = value
            resolve()
            host.invalidate()
        }
    var arrangement = ToolbarArrangement.Default
        set(value) {
            if (field == value) return
            field = value
            resolve()
            host.invalidate()
        }

    fun resolve() {
        val showingUtilities = utilitiesVisible()
        keyboard = layout().resolveGeometry(
            width = host.width,
            height = host.height,
            density = host.resources.displayMetrics.density,
            profile = profile(),
            showClipboard = clipboardKeyVisible && showingUtilities,
            showPlacement = placementKeyVisible && showingUtilities,
            microphone = microphone.copy(visible = microphone.visible && microphoneAllowed()),
            arrangement = arrangement,
            showUtilities = showingUtilities,
            hasSuggestions = hasSuggestions(),
            isDualBar = isDualBar(),
        )
        changed()
    }
}
