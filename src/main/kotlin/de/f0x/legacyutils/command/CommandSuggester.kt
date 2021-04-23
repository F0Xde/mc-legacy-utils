package de.f0x.legacyutils.command

import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.suggestion.Suggestion
import com.mojang.brigadier.suggestion.Suggestions
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.widget.PagedEntryListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import org.lwjgl.input.Keyboard
import java.util.concurrent.CompletableFuture
import kotlin.math.min

class CommandSuggester(
    private val client: MinecraftClient,
    private val textField: TextFieldWidget,
) : DrawableHelper(), PagedEntryListWidget.Listener {
    private val textRenderer = client.textRenderer

    private val dispatcher = LegacyCommandManager.dispatcher
    private val source = LegacyCommandSource(client.player)

    private var parse: ParseResults<LegacyCommandSource>? = null
    private var pending: CompletableFuture<Suggestions>? = null
    private var window: SuggestionWindow? = null

    init {
        textField.setListener(this)
    }

    fun keyPressed(typedChar: Char, keyCode: Int): Boolean =
        window?.keyPressed(typedChar, keyCode) ?: false

    fun render() {
        window?.render()
    }

    private fun update(cmd: String, cursor: Int) {
        if (parse?.reader?.string != cmd) {
            parse = dispatcher.parse(cmd, source)
        }
        pending?.cancel(true)
        pending = dispatcher.getCompletionSuggestions(parse, cursor).also {
            it.thenAccept {
                synchronized(this) {
                    this.window = if (it.isEmpty) null else SuggestionWindow(
                        it,
                        textField.x +
                                textRenderer.getStringWidth(LegacyCommandManager.PREFIX) +
                                textRenderer.getStringWidth(cmd.substring(0, it.range.start))
                    )
                }
            }
        }
    }

    private val Suggestions.lines get() = min(list.size, 10)

    override fun setBooleanValue(id: Int, value: Boolean) {}
    override fun setFloatValue(id: Int, value: Float) {}

    override fun setStringValue(id: Int, text: String) {
        if (text.startsWith(LegacyCommandManager.PREFIX)) {
            val cursor = textField.cursor - 1
            if (cursor >= 0) {
                update(text.substring(1), cursor)
            }
        }
    }

    private inner class SuggestionWindow(
        private val suggestions: Suggestions,
        private val x: Int
    ) {
        private var selected = 0
        private var completed = -1
        private var completedLen = suggestions.range.length
        private var viewStart = 0

        fun keyPressed(typedChar: Char, keyCode: Int): Boolean = when (keyCode) {
            Keyboard.KEY_TAB -> {
                if (completed == selected) {
                    scroll(if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) -1 else 1)
                }
                val start: Int = suggestions.range.start + LegacyCommandManager.PREFIX.length
                val replacement = suggestions.list[selected].text
                textField.text = StringBuilder(textField.text)
                    .replace(start, start + completedLen, replacement)
                    .toString()
                textField.cursor = start + replacement.length
                completedLen = replacement.length
                completed = selected
                true
            }
            Keyboard.KEY_DOWN -> {
                scroll(1)
                true
            }
            Keyboard.KEY_UP -> {
                scroll(-1)
                true
            }
            else -> false
        }

        fun render() {
            val lines = suggestions.lines
            val sugView: List<Suggestion> = suggestions.list.subList(viewStart, viewStart + lines)
            val width = sugView.maxOf { client.textRenderer.getStringWidth(it.text) }
            val fontHeight: Int = textRenderer.fontHeight
            val height = lines * fontHeight
            val y: Int = textField.y - 4 - height

            // Colors copied from vanilla, maybe i'll convert them to nice unsigned hex values later (also fuck java why are there no unsigned types wtf)
            fill(x - 1, y - 1, x + width + 1, y + height, -805306368)
            sugView.forEachIndexed { i, sug ->
                val color = if (viewStart + i == selected) -256 else -5592406
                drawWithShadow(textRenderer, sug.text, x, y + i * fontHeight, color)
            }
        }

        private fun scroll(by: Int) {
            // floorMod for correct handling of negatives
            selected = Math.floorMod(selected + by, suggestions.list.size)
            val lastLine: Int = suggestions.lines - 1
            if (viewStart + lastLine < selected) {
                viewStart = selected - lastLine
            } else if (viewStart > selected) {
                viewStart = selected
            }
        }
    }
}
