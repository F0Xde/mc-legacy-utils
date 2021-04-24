package de.f0x.legacyutils.command

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import de.f0x.legacyutils.Color
import de.f0x.legacyutils.util.colorValue
import net.minecraft.util.Formatting
import java.util.concurrent.CompletableFuture

private val INVALID_HEX = DynamicCommandExceptionType {
    LiteralMessage("Invalid hexadecimal integer $it")
}
private val EXAMPLES: Collection<String> = listOf("123456", "#FFFFFF", "0xABABAB", "green", "rgb 255")

class ColorArgumentType : ArgumentType<Color> {
    override fun parse(reader: StringReader): Color {
        val value = reader.readUnquotedString()
        reader.skipWhitespace()
        val alpha = if (reader.canRead() && reader.peek().isDigit()) {
            reader.readInt()
        } else {
            0xFF
        } and 0xFF

        if (value == "rainbow") {
            return Color(isRainbow = true, alpha = alpha)
        }

        Formatting.values()
            .firstOrNull { it.isColor && it.getName() == value }
            ?.run { return Color(colorValue, alpha) }

        val rgb = try {
            value.stripHexPrefix().toInt(16)
        } catch (e: NumberFormatException) {
            throw INVALID_HEX.createWithContext(reader, value)
        }
        return Color(rgb, alpha)
    }

    private fun String.stripHexPrefix() = substring(
        when {
            startsWith("#") -> 1
            startsWith("0x") -> 2
            else -> 0
        }
    )

    override fun <S> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        for (formatting in Formatting.values()) {
            val name = formatting.getName()
            if (formatting.isColor && name.startsWith(builder.remaining)) {
                builder.suggest(name)
            }
        }
        if ("rainbow".startsWith(builder.remaining)) {
            builder.suggest("rainbow")
        }
        return builder.buildFuture()
    }

    override fun getExamples() = EXAMPLES
}
