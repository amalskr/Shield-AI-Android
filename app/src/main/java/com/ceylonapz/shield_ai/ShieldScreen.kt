package com.ceylonapz.shield_ai

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val EXAMPLES = listOf(
    "OTP request" to "ආයුබෝවන්, මම වැරදීමකින් ඔබේ අංකයට OTP එකක් යැව්වා. කරුණාකර ඒ අංක 6 මට එවන්න, හදිසියි.",
    "Parcel fee" to "SL Post: Your parcel is held at customs. Pay Rs. 350 clearance fee within 24h or it will be returned: http://slpost-delivery.top/pay",
    "Part time job" to "Part time job ekak thiyenawa, YouTube videos like karala dawasakata Rs 5000 hoyanna puluwan. Telegram ekata join wenna: t.me/easyjobs_lk",
    "Family message" to "අම්මේ, මම හවස 6ට විතර ගෙදර එනවා. කෑම මොනවා හරි ගේන්නද?",
)

private val REPLY_LANGUAGES = listOf(
    "auto" to "Same as message",
    "si" to "සිංහල",
    "ta" to "தமிழ்",
    "en" to "English",
)

private val BUBBLE_SHAPE = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomEnd = 22.dp, bottomStart = 6.dp)

@Composable
fun ShieldScreen(vm: ShieldViewModel) {
    val state = vm.state
    val scroll = rememberScrollState()

    BackHandler(enabled = state is UiState.Done) { vm.edit() }
    LaunchedEffect(state) { if (state is UiState.Done) scroll.animateScrollTo(0) }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Header()
            Spacer(Modifier.height(20.dp))
            if (state is UiState.Done) {
                ResultView(state, vm)
            } else {
                InputView(vm, state)
            }
            Spacer(Modifier.height(32.dp))
            Text(
                "Messages are checked and then discarded. Nothing is stored.\n" +
                    "Runs on Gemini in Vertex AI, the Web Risk API and Cloud Run.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Header() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.shield_logo),
            contentDescription = null,
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(9.dp)),
        )
        Spacer(Modifier.width(10.dp))
        Text("Shield-AI", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InputView(vm: ShieldViewModel, state: UiState) {
    val context = LocalContext.current
    val dark = isSystemInDarkTheme()
    val checking = state is UiState.Checking

    Text(
        "Is this message a scam?",
        fontSize = 34.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight.ExtraBold,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        "Share or paste any SMS, WhatsApp or email. Shield-AI checks the links with Google Web Risk, " +
            "reads the message with Gemini, and explains it in Sinhala, Tamil or English.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(20.dp))

    TextField(
        value = vm.input,
        onValueChange = vm::onInput,
        modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
        placeholder = { Text("Paste the message here") },
        shape = BUBBLE_SHAPE,
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 28.sp),
        enabled = !checking,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = if (dark) ShieldColors.BubbleDark else ShieldColors.BubbleLight,
            unfocusedContainerColor = if (dark) ShieldColors.BubbleDark else ShieldColors.BubbleLight,
            disabledContainerColor = if (dark) ShieldColors.BubbleDark else ShieldColors.BubbleLight,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AssistChip(onClick = { readClipboard(context)?.let(vm::onInput) }, label = { Text("Paste") })
        AssistChip(onClick = { vm.onInput("") }, label = { Text("Clear") })
    }

    Spacer(Modifier.height(12.dp))
    Text("Or try an example", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EXAMPLES.forEach { (label, text) ->
            AssistChip(onClick = { vm.onInput(text) }, label = { Text(label) })
        }
    }

    Spacer(Modifier.height(12.dp))
    Text("Explain in", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        REPLY_LANGUAGES.forEach { (code, label) ->
            FilterChip(
                selected = vm.replyLanguage == code,
                onClick = { vm.onReplyLanguage(code) },
                label = { Text(label) },
            )
        }
    }

    Spacer(Modifier.height(20.dp))
    Button(
        onClick = vm::check,
        enabled = !checking,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        if (checking) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(Modifier.width(12.dp))
            Text("Checking links and reading the message")
        } else {
            Text("Check message", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (state is UiState.Failed) {
        Spacer(Modifier.height(12.dp))
        Text(state.message, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ResultView(state: UiState.Done, vm: ShieldViewModel) {
    val context = LocalContext.current
    val dark = isSystemInDarkTheme()
    val a = state.analysis
    val color = verdictColor(a.verdict, dark)

    // The message itself, with the suspicious parts marked. Tap to edit.
    Surface(
        shape = BUBBLE_SHAPE,
        color = if (dark) ShieldColors.BubbleDark else ShieldColors.BubbleLight,
        modifier = Modifier.fillMaxWidth().clickable { vm.edit() },
    ) {
        Text(
            text = highlight(state.text, a.redFlags, if (dark) ShieldColors.MarkDark else ShieldColors.MarkLight),
            modifier = Modifier.padding(18.dp),
            fontSize = 18.sp,
            lineHeight = 28.sp,
        )
    }

    Spacer(Modifier.height(22.dp))
    Row(Modifier.height(IntrinsicSize.Min)) {
        Box(Modifier.width(6.dp).fillMaxHeight().background(color))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(verdictTitle(a.verdict), color = color, fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.ExtraBold)
            localVerdict(a)?.let {
                Text(it, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Risk", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(10.dp))
                LinearProgressIndicator(
                    progress = { a.riskScore / 100f },
                    modifier = Modifier.weight(1f).height(10.dp),
                    color = color,
                    trackColor = MaterialTheme.colorScheme.outline,
                    strokeCap = StrokeCap.Round,
                )
                Spacer(Modifier.width(10.dp))
                Text("${a.riskScore}/100", fontWeight = FontWeight.Bold)
            }
        }
    }

    Section("Why") { Text(a.explanation, fontSize = 17.sp, lineHeight = 27.sp) }
    if (a.redFlags.isNotEmpty()) Section("Warning signs") { Dots(a.redFlags) }
    if (a.urlChecks.isNotEmpty()) Section("Links checked") {
        a.urlChecks.forEach { c ->
            Column(Modifier.padding(vertical = 6.dp)) {
                Text(
                    if (c.malicious) "Flagged by Google Web Risk" else "Not on Google's blocklist",
                    color = if (c.malicious) verdictColor(Verdict.SCAM, dark) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Text(c.url, fontSize = 14.sp)
            }
        }
    }
    if (a.actions.isNotEmpty()) Section("What to do now") { Dots(a.actions) }

    Spacer(Modifier.height(28.dp))
    Button(
        onClick = { shareWarning(context, a) },
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
    ) { Text("Share this warning", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
    Spacer(Modifier.height(10.dp))
    OutlinedButton(
        onClick = vm::reset,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(12.dp),
    ) { Text("Check another message", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Spacer(Modifier.height(24.dp))
    Text(title, fontSize = 19.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(6.dp))
    content()
}

@Composable
private fun Dots(items: List<String>) {
    items.forEach {
        Row(Modifier.padding(vertical = 4.dp)) {
            Text("•", modifier = Modifier.width(18.dp), fontSize = 17.sp)
            Text(it, fontSize = 17.sp, lineHeight = 26.sp)
        }
    }
}

private fun verdictColor(v: Verdict, dark: Boolean): Color = when (v) {
    Verdict.SAFE -> if (dark) ShieldColors.SafeDark else ShieldColors.SafeLight
    Verdict.SUSPICIOUS -> if (dark) ShieldColors.WarnDark else ShieldColors.WarnLight
    Verdict.SCAM -> if (dark) ShieldColors.ScamDark else ShieldColors.ScamLight
}

private fun verdictTitle(v: Verdict) = when (v) {
    Verdict.SAFE -> "Looks safe"
    Verdict.SUSPICIOUS -> "Be careful"
    Verdict.SCAM -> "This is a scam"
}

/** Verdict in the same script as the explanation, so the person reading it sees their language. */
private fun localVerdict(a: Analysis): String? {
    val sinhala = a.explanation.any { it in '\u0D80'..'\u0DFF' }
    val tamil = a.explanation.any { it in '\u0B80'..'\u0BFF' }
    return when {
        sinhala -> when (a.verdict) {
            Verdict.SAFE -> "ආරක්ෂිතයි වගේ"
            Verdict.SUSPICIOUS -> "පරිස්සම් වෙන්න"
            Verdict.SCAM -> "මේක වංචාවක්"
        }
        tamil -> when (a.verdict) {
            Verdict.SAFE -> "பாதுகாப்பானதாகத் தெரிகிறது"
            Verdict.SUSPICIOUS -> "கவனமாக இருங்கள்"
            Verdict.SCAM -> "இது ஒரு மோசடி"
        }
        else -> null
    }
}

private val QUOTE_RE = Regex("[\"“”‘’']([^\"“”‘’']{4,})[\"“”‘’']")
private val TRAILING_NOTE_RE = Regex("\\s*\\([^)]*\\)\\s*$")

/** Marks the parts of the original message that the red flags quote. Same logic as the web app. */
fun highlight(text: String, flags: List<String>, mark: Color): AnnotatedString {
    val candidates = flags.flatMap { f ->
        val quoted = QUOTE_RE.findAll(f).map { it.groupValues[1] }.toList()
        quoted.ifEmpty { listOf(f.replace(TRAILING_NOTE_RE, "")) }
    }.map { it.trim().trimEnd('.', ',', ';', ':', '!', '?') }

    val ranges = candidates
        .filter { it.length >= 4 }
        .mapNotNull { c ->
            val i = text.indexOf(c, ignoreCase = true)
            if (i >= 0) i to i + c.length else null
        }
        .sortedBy { it.first }

    val merged = mutableListOf<Pair<Int, Int>>()
    for (r in ranges) {
        val last = merged.lastOrNull()
        if (last != null && r.first <= last.second) {
            merged[merged.lastIndex] = last.first to maxOf(last.second, r.second)
        } else {
            merged += r
        }
    }

    return buildAnnotatedString {
        append(text)
        merged.forEach { (start, end) ->
            addStyle(SpanStyle(background = mark, textDecoration = TextDecoration.Underline), start, end)
        }
    }
}

private fun readClipboard(context: Context): String? {
    val cm = context.getSystemService(ClipboardManager::class.java) ?: return null
    return cm.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(context)?.toString()
}

private fun shareWarning(context: Context, a: Analysis) {
    val body = buildString {
        append("Shield-AI check: ${verdictTitle(a.verdict)}")
        localVerdict(a)?.let { append(" ($it)") }
        append(", risk ${a.riskScore}/100\n\n")
        append(a.explanation)
        if (a.actions.isNotEmpty()) {
            append("\n\n")
            a.actions.forEach { append("• ").append(it).append('\n') }
        }
        append("\n").append(ShieldApi.BASE_URL)
    }
    val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, body)
    context.startActivity(Intent.createChooser(send, "Share this warning"))
}
