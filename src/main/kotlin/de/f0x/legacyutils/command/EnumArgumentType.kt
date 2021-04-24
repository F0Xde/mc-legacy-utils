package de.f0x.legacyutils.command

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

private val EXPECTED_ENUM_CONSTANT = Dynamic2CommandExceptionType { found, values ->
    LiteralMessage("Expected one of $values, found $found")
}

class EnumArgumentType<T : Enum<*>>(private val values: Array<out T>) : ArgumentType<T> {
    override fun parse(reader: StringReader): T {
        val found = reader.readUnquotedString()
        return values.firstOrNull { it.name == found }
            ?: throw EXPECTED_ENUM_CONSTANT.createWithContext(
                reader,
                found,
                values.joinToString(", ", "[", "]")
            )
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        for (value in values) {
            if (value.name.startsWith(builder.remaining)) {
                builder.suggest(value.name)
            }
        }
        return builder.buildFuture()
    }

    override fun getExamples() = values.take(3).map(Enum<*>::name)
}

inline fun <reified T : Enum<T>> enumArg() = EnumArgumentType(enumValues<T>())
