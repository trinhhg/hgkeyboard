package app.funput.funput.ime.clipboard.platform

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

internal class AndroidClipboardGateway(context: Context) : ClipboardGateway {
    private val appContext = context.applicationContext
    private val manager = appContext.getSystemService(ClipboardManager::class.java)
    private val fallbackTokens = ClipboardFallbackTokenCache()

    override fun snapshot(): ClipboardSnapshot? = runCatching {
        if (!manager.hasPrimaryClip()) return null
        manager.primaryClipDescription?.clipboardSnapshot()?.let(fallbackTokens::enrich)
    }.getOrNull()

    override fun readText(maxLength: Int): ClipboardReadResult = runCatching {
        val clip = manager.primaryClip ?: return ClipboardReadResult.Empty
        clip.readClipboardText(appContext, maxLength).also { result ->
            if (result is ClipboardReadResult.Success) {
                fallbackTokens.remember(clip.description.clipboardSnapshot(), result.sourceToken)
            }
        }
    }.getOrElse { ClipboardReadResult.Unavailable }

    override fun readImage(): ClipboardImageResult? = runCatching {
        val clip = manager.primaryClip ?: return null
        val description = clip.description ?: return null
        if (description.extras?.getBoolean(SensitiveClipboardKey, false) == true) return null
        val imageUri = findImageUri(clip) ?: return null
        val token = ClipboardSourceToken.fromTimestamp(description.timestamp) ?: "img:${imageUri}"
        val bitmap = decodeSampledBitmap(imageUri, MaxImageEdgePx) ?: return null
        try {
            val dir = File(appContext.filesDir, ImageDirName).apply { mkdirs() }
            val outFile = File(dir, "${UUID.randomUUID()}.jpg")
            FileOutputStream(outFile).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, JpegQuality, stream)) {
                    outFile.delete()
                    return null
                }
            }
            ClipboardImageResult(outFile.absolutePath, token)
        } finally {
            bitmap.recycle()
        }
    }.getOrNull()

    private fun findImageUri(clip: android.content.ClipData): Uri? {
        val descIsImage = clip.description.hasMimeType("image/*")
        for (i in 0 until clip.itemCount) {
            val uri = clip.getItemAt(i).uri ?: continue
            val mime = runCatching { appContext.contentResolver.getType(uri) }.getOrNull()
            if (descIsImage || mime?.startsWith("image/") == true) return uri
        }
        return null
    }

    private fun decodeSampledBitmap(uri: Uri, maxEdge: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        appContext.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sampleSize = 1
        while (bounds.outWidth / sampleSize > maxEdge || bounds.outHeight / sampleSize > maxEdge) {
            sampleSize *= 2
        }
        val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return appContext.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, decodeOpts) }
    }

    override fun observe(onChanged: () -> Unit): ClipboardObservation {
        val closed = AtomicBoolean(false)
        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            fallbackTokens.invalidate()
            if (!closed.get()) onChanged()
        }
        val registered = runCatching { manager.addPrimaryClipChangedListener(listener) }.isSuccess
        return ClipboardObservation {
            if (registered && closed.compareAndSet(false, true)) {
                runCatching { manager.removePrimaryClipChangedListener(listener) }
            }
        }
    }

    private companion object {
        const val ImageDirName = "clipboard_images"
        const val MaxImageEdgePx = 1200
        const val JpegQuality = 88
    }
}
