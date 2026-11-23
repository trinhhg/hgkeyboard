package app.funput.funput.keyboard.ui.theme

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.funput.funput.keyboard.ui.panel.KeyboardPanelPalette
import app.funput.funput.theme.KeyboardTheme
import app.funput.funput.theme.KeyboardThemeDescriptor
import app.funput.funput.theme.KeyboardThemeId
import app.funput.funput.theme.LocalKeyboardThemeCatalog

/** In-keyboard quick theme & style picker panel opened from the toolbar theme button. */
internal class ThemePanelView(context: Context) : FrameLayout(context) {
    private var palette by mutableStateOf(KeyboardPanelPalette.from(LocalKeyboardThemeCatalog.defaultTheme.theme))
    private var currentTheme by mutableStateOf(LocalKeyboardThemeCatalog.defaultTheme.theme)
    var onThemeSelected: ((KeyboardThemeId) -> Unit)? = null
    var onReturn: (() -> Unit)? = null
    var onOpenSettings: (() -> Unit)? = null

    init {
        addView(
            ComposeView(context).apply {
                setContent {
                    ThemePanelContent(
                        palette = palette,
                        currentTheme = currentTheme,
                        themes = LocalKeyboardThemeCatalog.themes,
                        onSelectTheme = { id -> onThemeSelected?.invoke(id) },
                        onReturn = { onReturn?.invoke() },
                        onOpenSettings = { onOpenSettings?.invoke() },
                    )
                }
            },
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
        )
    }

    fun updateTheme(theme: KeyboardTheme) {
        currentTheme = theme
        palette = KeyboardPanelPalette.from(theme)
        setBackgroundColor(theme.backgroundEndColor)
    }
}

@Composable
private fun ThemePanelContent(
    palette: KeyboardPanelPalette,
    currentTheme: KeyboardTheme,
    themes: List<KeyboardThemeDescriptor>,
    onSelectTheme: (KeyboardThemeId) -> Unit,
    onReturn: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val labelColor = Color(palette.readable(palette.label))
    val secondaryColor = Color(palette.readable(palette.secondaryLabel))
    val accentColor = Color(palette.accent)

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(palette.backgroundStart))
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clickable(onClick = onReturn),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(Modifier.size(16.dp)) {
                    val path = Path().apply {
                        moveTo(size.width * 0.65f, size.height * 0.15f)
                        lineTo(size.width * 0.25f, size.height * 0.5f)
                        lineTo(size.width * 0.65f, size.height * 0.85f)
                    }
                    drawPath(path, labelColor, style = Stroke(width = 2.dp.toPx()))
                }
            }
            BasicText(
                text = "Giao diện bàn phím Hg",
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                style = TextStyle(labelColor, 14.sp, FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            BasicText(
                text = "Cài đặt",
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onOpenSettings)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                style = TextStyle(accentColor, 13.sp, FontWeight.SemiBold),
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(palette.divider).copy(alpha = 0.25f)))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(themes, key = { it.id.value }) { descriptor ->
                val isSelected = descriptor.theme == currentTheme
                ThemeMiniCard(
                    descriptor = descriptor,
                    isSelected = isSelected,
                    palette = palette,
                    onClick = { onSelectTheme(descriptor.id) },
                )
            }
        }
    }
}

@Composable
private fun ThemeMiniCard(
    descriptor: KeyboardThemeDescriptor,
    isSelected: Boolean,
    palette: KeyboardPanelPalette,
    onClick: () -> Unit,
) {
    val t = descriptor.theme
    val borderColor = if (isSelected) Color(palette.accent) else Color(palette.divider).copy(alpha = 0.35f)
    val labelColor = Color(palette.readable(palette.label))

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(palette.searchSurface))
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        // Mini keyboard color preview strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(t.backgroundStartColor))
                .padding(horizontal = 6.dp, vertical = 5.dp),
        ) {
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(3) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(t.keyColor))
                    )
                }
                Box(
                    Modifier
                        .weight(1.2f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(t.accentKeyColor))
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        BasicText(
            text = descriptor.name,
            style = TextStyle(labelColor, 12.sp, if (isSelected) FontWeight.Bold else FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
