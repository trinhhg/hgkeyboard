package app.funput.funput.ime.suggestions

import android.content.Context
import app.funput.funput.ime.editing.AuthoredSuggestionUpdate
import app.funput.funput.ime.editing.EditorInfoPolicy
import app.funput.funput.ime.editing.ImeKeyActionHandler
import app.funput.funput.ime.editing.ImeSuggestionSource
import app.funput.funput.ime.settings.PersonalSuggestionPreferences
import app.funput.funput.ime.suggestions.dictionary.DictionaryEditKind
import app.funput.funput.keyboard.model.ShiftState
import app.funput.funput.keyboard.model.SuggestionSelection
import app.funput.funput.keyboard.ui.KeyboardPanel

internal class PersonalSuggestionService(
    context: Context,
    show: (List<String>) -> Unit,
    shift: () -> ShiftState = { ShiftState.OFF },
    private val acknowledgeReset: (String) -> Unit,
    createWorker: (Context, (PersonalSuggestionRequest, List<String>) -> Unit) -> PersonalSuggestionWorker = ::suggestionWorker,
) {
    private val worker = runCatching { createWorker(context, ::publish) }.getOrNull()
    private var preferences = PersonalSuggestionPreferences.Default
    private var policy = EditorInfoPolicy.Default
    private var panel = KeyboardPanel.LETTERS
    private var session = 0L
    private var generation = 0L
    private var prefix = ""
    /** The tracker's last word: what the next one follows, and the words the bar asks after. */
    private var latest = AuthoredSuggestionUpdate.Empty
    /** Whether the bar is currently answering a context rather than a prefix. */
    private var predicting = false
    private val candidates = PersonalSuggestionCandidates(show, shift)
    private val resets = PersonalSuggestionResetGate(worker) { reset ->
        clear()
        acknowledgeReset(reset)
    }

    fun configure(value: PersonalSuggestionPreferences) {
        preferences = value
        resets.apply(value)
        if (!value.enabled) clear()
    }

    /** Called by [SeedLanguageReportingEngine] whenever the composer's state changes it. */
    fun setSeedLanguage(language: SeedLanguage) {
        worker?.setSeedLanguage(language)
    }

    /** A field gets focus: also the moment to fold in what the settings app posted. */
    fun start(value: EditorInfoPolicy) {
        session += 1
        policy = value
        panel = KeyboardPanel.LETTERS
        clear()
        worker?.applyPendingEdits()
    }

    fun finish() {
        session += 1
        clear()
        worker?.flush()
    }

    fun updatePanel(value: KeyboardPanel) {
        panel = value
        if (value != KeyboardPanel.LETTERS) clear()
    }

    fun consume(update: AuthoredSuggestionUpdate) {
        update.completedToken
            ?.takeIf { eligible() && policy.allowsPersonalizedLearning }
            ?.let { worker?.learn(it, latest.learnContext) }
        // The caret can move to other words with no key typed: then the same empty prefix asks anew.
        val unchanged = update.prefix == prefix && update.queryContext == latest.queryContext
        latest = update
        // A prediction answers a context with no prefix at all. One character is
        // neither a prefix worth completing nor a word boundary, and stays out.
        val predicts = eligible() && update.prefix.isEmpty() && latest.context != null
        if (update.completedToken == null && unchanged && predicts == predicting) {
            // Shift arrives here: same words, new shape, nothing to ask the engine.
            if (eligible()) candidates.draw(prefix)
            return
        }
        prefix = update.prefix
        predicting = predicts
        if (!eligible() || (!predicts && prefix.codePointCount(0, prefix.length) < MinimumPrefix)) {
            return clear()
        }
        requery()
    }

    /** Whether a long press may block or forget [selection]: a word this service drew, from a store that lasts. */
    fun isEditable(selection: SuggestionSelection): Boolean =
        eligible() && worker?.persistentAvailable == true && candidates.isShown(selection.index, selection.text)

    /** Applies a long-press edit to the stored spelling behind [selection], then asks again so the bar drops it. */
    fun edit(selection: SuggestionSelection, kind: DictionaryEditKind) {
        if (!isEditable(selection)) return
        val stored = candidates.stored(selection.index, selection.text) ?: return
        worker?.edit(kind, stored)
        candidates.clear()
        requery()
    }

    fun select(selection: SuggestionSelection, handler: ImeKeyActionHandler): Boolean {
        // A held word answered an older prefix. The tap is dropped; the answer on its way still lands.
        if (candidates.held) return false
        // An empty prefix is the ordinary shape of accepting a prediction: it
        // replaces nothing and inserts a word.
        if (!candidates.isShown(selection.index, selection.text) || !eligible()) return reject()
        if (!handler.acceptSuggestion(selection.text, prefix)) return reject()
        consume(handler.takeSuggestionUpdate())
        return true
    }

    fun flush() = worker?.flush()
    fun close() = worker?.close()

    private fun publish(request: PersonalSuggestionRequest, values: List<String>) {
        if (request.generation != generation || request.session != session || request.prefix != prefix) return
        val enriched = if (request.prefix.isEmpty() && values.size < 3) {
            fillNextWordFallbacks(values, request.context)
        } else {
            values
        }
        candidates.receive(enriched, prefix)
        suggestionCounter("SuggestionQueryToToolbarUs", (System.nanoTime() - request.startedNanos) / 1_000)
    }

    private fun fillNextWordFallbacks(existing: List<String>, context: String?): List<String> {
        val lastWord = context?.trim()?.substringAfterLast(' ')?.lowercase().orEmpty()
        val pool = CommonFollowers[lastWord] ?: DefaultNextWords
        val result = ArrayList<String>(3)
        existing.forEach { if (result.size < 3 && it !in result) result.add(it) }
        pool.forEach { if (result.size < 3 && it !in result) result.add(it) }
        DefaultNextWords.forEach { if (result.size < 3 && it !in result) result.add(it) }
        return result
    }

    /** Asks again for what the bar is answering. What it showed stays up, untappable, until the answer lands. */
    private fun requery() {
        generation += 1
        candidates.hold()
        // No worker means no answer on its way, and nothing to hold the bar for.
        worker?.query(PersonalSuggestionRequest(prefix, generation, session, latest.queryContext)) ?: candidates.clear()
    }

    private fun eligible() = preferences.enabled && policy.allowsPersonalSuggestions &&
        policy.suggestionSource == ImeSuggestionSource.FUNPUT &&
        panel == KeyboardPanel.LETTERS

    private fun reject(): Boolean = false.also { clear() }

    private fun clear() {
        generation += 1
        prefix = ""
        latest = AuthoredSuggestionUpdate.Empty
        predicting = false
        worker?.clearQueries()
        candidates.clear()
    }
}

/** The shortest prefix worth completing from the 1st typed character (Laban Key responsiveness). */
private const val MinimumPrefix = 1

private val DefaultNextWords = listOf("cho", "mà", "rồi", "và", "là", "có", "không", "để")

private val CommonFollowers: Map<String, List<String>> = mapOf(
    "của" to listOf("mình", "em", "anh"),
    "và" to listOf("các", "nhiều", "một"),
    "là" to listOf("một", "gì", "người"),
    "có" to listOf("thể", "gì", "không"),
    "không" to listOf("có", "biết", "phải"),
    "được" to listOf("không", "rồi", "nhiều"),
    "cho" to listOf("mình", "em", "anh"),
    "với" to listOf("nhau", "mình", "ai"),
    "các" to listOf("bạn", "mục", "loại"),
    "những" to listOf("người", "điều", "cái"),
    "này" to listOf("nha", "rồi", "luôn"),
    "đã" to listOf("có", "làm", "xong"),
    "khi" to listOf("nào", "đó", "mà"),
    "thì" to listOf("sao", "có", "làm"),
    "mà" to listOf("không", "vẫn", "còn"),
    "nhưng" to listOf("mà", "không", "vẫn"),
    "vì" to listOf("vậy", "sao", "mình"),
    "nên" to listOf("không", "cần", "làm"),
    "thật" to listOf("sự", "lòng", "tốt"),
    "rất" to listOf("nhiều", "tốt", "vui"),
    "một" to listOf("chút", "ít", "ngày"),
    "người" to listOf("ta", "khác", "dùng"),
    "đi" to listOf("chơi", "làm", "ăn"),
    "đang" to listOf("làm", "đi", "ở"),
    "mình" to listOf("đang", "cũng", "sẽ"),
    "bạn" to listOf("có", "đang", "ơi"),
    "anh" to listOf("ơi", "đang", "có"),
    "em" to listOf("ơi", "đang", "có"),
    "cũng" to listOf("được", "có", "không"),
    "vẫn" to listOf("còn", "chưa", "đang"),
    "tôi" to listOf("đang", "cũng", "muốn"),
    "chúng" to listOf("ta", "mình", "tôi"),
    "để" to listOf("mình", "xem", "làm"),
    "như" to listOf("vậy", "thế", "nào"),
    "ra" to listOf("ngoài", "về", "xem"),
    "vào" to listOf("trong", "nhà", "đây"),
    "đến" to listOf("nơi", "đây", "khi"),
    "về" to listOf("nhà", "rồi", "sau"),
    "sẽ" to listOf("có", "làm", "đi"),
    "phải" to listOf("không", "làm", "có"),
    "quá" to listOf("rồi", "trời", "lắm"),
    "lắm" to listOf("luôn", "rồi", "nha"),
    "hơn" to listOf("nữa", "nhiều", "trước"),
    "nhất" to listOf("là", "định", "rồi"),
    "rồi" to listOf("nhé", "nha", "sao"),
    "chưa" to listOf("xong", "có", "biết"),
    "đừng" to listOf("có", "lo", "làm"),
    "hãy" to listOf("thử", "cùng", "xem"),
    "lại" to listOf("đây", "xem", "nhé"),
    "luôn" to listOf("nhé", "rồi", "luôn"),
    "đều" to listOf("có", "được", "rất"),
    "cứ" to listOf("thế", "làm", "để"),
    "bị" to listOf("lỗi", "mất", "đau"),
    "tại" to listOf("sao", "vì", "đây"),
    "cùng" to listOf("nhau", "đi", "làm"),
    "đây" to listOf("rồi", "nè", "nhé"),
    "đó" to listOf("nhé", "rồi", "là"),
    "nếu" to listOf("mà", "có", "không"),
    "thấy" to listOf("rồi", "sao", "có"),
    "biết" to listOf("không", "rồi", "đâu"),
    "nghĩ" to listOf("là", "lại", "xem"),
    "nói" to listOf("chuyện", "gì", "vậy"),
    "làm" to listOf("gì", "sao", "việc"),
    "muốn" to listOf("đi", "làm", "hỏi"),
    "cần" to listOf("gì", "phải", "làm"),
    "gửi" to listOf("cho", "lại", "tin"),
    "nhắn" to listOf("tin", "cho", "lại"),
    "gọi" to listOf("điện", "cho", "lại"),
    "xem" to listOf("lại", "thử", "sao"),
    "đọc" to listOf("sách", "tin", "kỹ"),
    "nghe" to listOf("nói", "lại", "thử"),
    "mua" to listOf("đồ", "hàng", "mới"),
    "ăn" to listOf("cơm", "uống", "gì"),
    "uống" to listOf("nước", "trà", "cà"),
    "ngủ" to listOf("ngon", "sớm", "rồi"),
    "chơi" to listOf("vui", "không", "nhé"),
    "học" to listOf("tập", "bài", "hành"),
    "dùng" to listOf("thử", "cái", "này"),
    "nhớ" to listOf("nhé", "lại", "nhà"),
    "quên" to listOf("mất", "rồi", "đi"),
    "nhiều" to listOf("quá", "lắm", "người"),
    "tốt" to listOf("quá", "rồi", "nhất"),
    "đẹp" to listOf("quá", "trời", "lắm"),
    "hay" to listOf("là", "quá", "lắm"),
    "mới" to listOf("xong", "về", "làm"),
    "trong" to listOf("nhà", "lúc", "khi"),
    "ngoài" to listOf("ra", "trời", "đường"),
    "trước" to listOf("đã", "nhé", "khi"),
    "sau" to listOf("này", "đó", "nhé"),
)
