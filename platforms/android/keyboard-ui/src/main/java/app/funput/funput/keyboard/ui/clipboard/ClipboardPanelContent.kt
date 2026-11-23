package app.funput.funput.keyboard.ui.clipboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.funput.funput.keyboard.ui.R
import app.funput.funput.keyboard.ui.clipboard.row.ClipboardSwipeRow
import app.funput.funput.keyboard.ui.panel.KeyboardPanelPalette
import java.time.Instant

@Composable
internal fun ClipboardPanelContent(
    entries: List<KeyboardClipboardEntry>, shortcuts: List<KeyboardShortcutEntry>,
    loading: Boolean, now: Instant,
    palette: KeyboardPanelPalette, reset: Int,
    initialTab: Int = 1,
    tabSelectGeneration: Int = 0,
    onSelect: (KeyboardClipboardEntry) -> Unit, onSelectShortcut: (KeyboardShortcutEntry) -> Unit,
    onPin: (KeyboardClipboardEntry) -> Unit,
    onRemove: (KeyboardClipboardEntry) -> Unit, onClear: () -> Unit,
    onDelete: () -> Unit, onReturn: () -> Unit,
    onOpenSettings: () -> Unit = {},
) {
    var preview by remember { mutableStateOf<KeyboardClipboardEntry?>(null) }
    var revealedId by remember { mutableStateOf<java.util.UUID?>(null) }
    var currentTab by remember { mutableIntStateOf(initialTab) } // 0: GHI CHÚ, 1: BẢNG NHỚ TẠM (mặc định), 2: GÕ TẮT
    val listState = rememberLazyListState()

    LaunchedEffect(reset) { listState.scrollToItem(0); revealedId = null }
    LaunchedEffect(tabSelectGeneration) { currentTab = initialTab }
    LaunchedEffect(entries) {
        if (entries.none { it.id == revealedId }) revealedId = null
    }

    Column(Modifier.fillMaxSize()) {
        // Laban Key Style Top Tab Header (< GHI CHÚ | BẢNG NHỚ TẠM | GÕ TẮT)
        LabanClipboardTabBar(
            selectedTab = currentTab,
            onSelectTab = { currentTab = it },
            onReturn = onReturn,
            palette = palette,
        )

        Box(Modifier.weight(1f)) {
            when (currentTab) {
                0 -> {
                    val pinnedEntries = entries.filter { it.isPinned }
                    if (pinnedEntries.isEmpty()) {
                        Column(
                            Modifier.fillMaxSize().padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            BasicText(
                                "Chưa có ghi chú đã ghim",
                                Modifier.fillMaxWidth(),
                                TextStyle(Color(palette.readable(palette.label)), 15.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center),
                            )
                            BasicText(
                                "Nhấn biểu tượng Ghim trên thẻ ở Bảng nhớ tạm để lưu vào Ghi chú.",
                                Modifier.fillMaxWidth().padding(top = 6.dp),
                                TextStyle(Color(palette.readable(palette.secondaryLabel)), 13.sp, textAlign = TextAlign.Center),
                            )
                            Spacer(Modifier.height(12.dp))
                            LabanActionChip("Mở Bảng nhớ tạm", onClick = { currentTab = 1 })
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 4.dp),
                        ) {
                            items(pinnedEntries.size, key = { pinnedEntries[it].id }) { index ->
                                val entry = pinnedEntries[index]
                                ClipboardSwipeRow(
                                    entry, now, palette, revealedId == entry.id,
                                    onReveal = { reveal ->
                                        revealedId = if (reveal) entry.id else revealedId.takeUnless { it == entry.id }
                                    },
                                    onSelect = { if (revealedId == null) onSelect(entry) else revealedId = null },
                                    onPin = { onPin(entry) }, onRemove = { onRemove(entry) },
                                    onPreview = {
                                        if (revealedId == null) preview = entry else revealedId = null
                                    },
                                )
                            }
                        }
                    }
                }
                2 -> {
                    if (shortcuts.isEmpty()) {
                        Column(
                            Modifier.fillMaxSize().padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            BasicText(
                                "Chưa có từ gõ tắt",
                                Modifier.fillMaxWidth(),
                                TextStyle(Color(palette.readable(palette.label)), 15.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center),
                            )
                            BasicText(
                                "Thêm từ viết tắt (ví dụ: k -> không) để nhập nhanh.",
                                Modifier.fillMaxWidth().padding(top = 6.dp),
                                TextStyle(Color(palette.readable(palette.secondaryLabel)), 13.sp, textAlign = TextAlign.Center),
                            )
                            Spacer(Modifier.height(12.dp))
                            LabanActionChip("+ Quản lý Gõ tắt", onClick = onOpenSettings)
                        }
                    } else {
                        Box(Modifier.fillMaxSize()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                items(shortcuts.size, key = { shortcuts[it].trigger }) { index ->
                                    val item = shortcuts[index]
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(palette.searchSurface))
                                            .clickable { onSelectShortcut(item) }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        BasicText(
                                            text = item.trigger,
                                            modifier = Modifier
                                                .widthIn(min = 48.dp, max = 110.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(Color(palette.accent).copy(alpha = 0.14f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = TextStyle(
                                                color = Color(palette.accent),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        BasicText(
                                            text = item.expansion,
                                            modifier = Modifier.weight(1f),
                                            style = TextStyle(
                                                color = Color(palette.readable(palette.label)),
                                                fontSize = 14.sp,
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                            LabanActionChip(
                                "+ Thêm / Sửa",
                                onClick = onOpenSettings,
                                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                            )
                        }
                    }
                }
                else -> {
                    when {
                        loading -> Empty(stringResource(R.string.clipboard_loading), null, palette)
                        entries.isEmpty() -> Empty(
                            stringResource(R.string.clipboard_empty_title),
                            stringResource(R.string.clipboard_empty_detail), palette,
                        )
                        else -> LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 4.dp),
                        ) {
                            clipboardGroups(entries).forEach { group ->
                                stickyHeader(group.pinned) { Header(group.pinned, palette) }
                                items(group.entries.size, key = { group.entries[it].id }) { index ->
                                    val entry = group.entries[index]
                                    ClipboardSwipeRow(
                                        entry, now, palette, revealedId == entry.id,
                                        onReveal = { reveal ->
                                            revealedId = if (reveal) entry.id else revealedId.takeUnless { it == entry.id }
                                        },
                                        onSelect = { if (revealedId == null) onSelect(entry) else revealedId = null },
                                        onPin = { onPin(entry) }, onRemove = { onRemove(entry) },
                                        onPreview = {
                                            if (revealedId == null) preview = entry else revealedId = null
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            preview?.let { entry ->
                ClipboardPreviewOverlay(
                    entry, palette, { preview = null },
                    { preview = null; onPin(entry) }, { preview = null; onRemove(entry) },
                )
            }
        }

        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(palette.divider).copy(alpha = 0.2f)))
        Box(Modifier.fillMaxWidth().height(46.dp)) {
            ClipboardBottomBar(palette, entries.isNotEmpty(), onReturn, onClear, onDelete)
        }
    }
}

@Composable
private fun LabanClipboardTabBar(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onReturn: () -> Unit,
    palette: KeyboardPanelPalette,
) {
    val tabs = listOf("GHI CHÚ", "BẢNG NHỚ TẠM", "GÕ TẮT")
    val activeColor = Color(0xFF2563EB) // Blue accent characteristic of Laban Key
    val inactiveColor = Color(palette.readable(palette.secondaryLabel))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(palette.backgroundStart))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Nút Back (<)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable(onClick = onReturn),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(16.dp)) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width * 0.65f, size.height * 0.15f)
                        lineTo(size.width * 0.25f, size.height * 0.5f)
                        lineTo(size.width * 0.65f, size.height * 0.85f)
                    }
                    drawPath(
                        path = path,
                        color = Color(palette.readable(palette.label)),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
                    )
                }
            }

            // 3 Tabs: GHI CHÚ | BẢNG NHỚ TẠM | GÕ TẮT
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelectTab(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Spacer(modifier = Modifier.height(6.dp))
                        BasicText(
                            text = title,
                            style = TextStyle(
                                color = if (isSelected) activeColor else inactiveColor,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            ),
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(2.5.dp)
                                    .background(activeColor, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)),
                            )
                        } else {
                            Spacer(modifier = Modifier.height(2.5.dp))
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(palette.divider).copy(alpha = 0.25f)),
        )
    }
}

@Composable
private fun Header(pinned: Boolean, palette: KeyboardPanelPalette) {
    BasicText(
        stringResource(if (pinned) R.string.clipboard_pinned else R.string.clipboard_recent),
        Modifier.fillMaxWidth().background(Color(palette.backgroundStart)).padding(12.dp, 5.dp),
        TextStyle(Color(palette.readable(palette.secondaryLabel)), 12.sp, fontWeight = FontWeight.Medium),
    )
}

@Composable
private fun Empty(title: String, detail: String?, palette: KeyboardPanelPalette) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BasicText(
            title, Modifier.fillMaxWidth(),
            TextStyle(
                Color(palette.readable(palette.label)), 15.sp,
                fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
            ),
        )
        detail?.let {
            BasicText(
                it, Modifier.fillMaxWidth().padding(top = 6.dp),
                TextStyle(
                    Color(palette.readable(palette.secondaryLabel)), 13.sp,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Composable
private fun LabanActionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF2563EB))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            style = TextStyle(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium),
        )
    }
}
