package app.funput.funput.keyboard.ui.clipboard.row

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.funput.funput.keyboard.ui.R
import app.funput.funput.keyboard.ui.clipboard.ClipboardRowText
import app.funput.funput.keyboard.ui.clipboard.ClipboardTimeStrings
import app.funput.funput.keyboard.ui.clipboard.KeyboardClipboardEntry
import app.funput.funput.keyboard.ui.panel.KeyboardPanelPalette
import java.time.Instant

internal object ClipboardRowTextContent {
    fun preview(text: String) = ClipboardRowText.preview(text)

    @Composable
    operator fun invoke(
        entry: KeyboardClipboardEntry, preview: String, now: Instant,
        palette: KeyboardPanelPalette, modifier: Modifier,
    ) {
        val resources = LocalContext.current.resources
        ClipboardTimeStrings(
            stringResource(R.string.clipboard_just_now),
            { resources.getString(R.string.clipboard_minutes_ago, it) },
            { resources.getString(R.string.clipboard_hours_ago, it) },
            { resources.getString(R.string.clipboard_days_ago, it) },
        )
        val imageBitmap = remember(entry.imagePath) {
            entry.imagePath?.let { path ->
                runCatching { BitmapFactory.decodeFile(path)?.asImageBitmap() }.getOrNull()
            }
        }
        Column(modifier.padding(start = 12.dp, top = 7.dp, bottom = 7.dp, end = 6.dp)) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = entry.text,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp, max = 104.dp)
                        .clip(RoundedCornerShape(6.dp)),
                )
            } else {
                BasicText(
                    preview, style = TextStyle(Color(palette.readable(palette.label)), 14.sp),
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
            }
            BasicText(
                ClipboardRowText.formatTimestamp(entry.capturedAt),
                style = TextStyle(Color(palette.readable(palette.secondaryLabel)), 11.sp),
                modifier = Modifier.padding(top = 3.dp),
            )
        }
    }
}
