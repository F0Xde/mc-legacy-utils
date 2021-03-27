package de.f0x.legacyutils.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.client.network.ClientPlayerEntity

typealias LegacyLiteralArgBuilder = LiteralArgumentBuilder<LegacyCommandSource>

object LegacyCommandManager {
    const val PREFIX = "."

    private val dispatcher = CommandDispatcher<LegacyCommandSource>()

    fun execute(command: String, player: ClientPlayerEntity) {
        execute(command, LegacyCommandSource(player))
    }

    fun execute(command: String, source: LegacyCommandSource) {
        try {
            dispatcher.execute(command, source)
        } catch (e: CommandSyntaxException) {
            source.send("§c" + e.message)
        }
    }

    fun register(builder: LegacyLiteralArgBuilder): LiteralCommandNode<LegacyCommandSource> =
        dispatcher.register(builder)
}

fun literal(name: String): LegacyLiteralArgBuilder =
    LiteralArgumentBuilder.literal(name)

fun <T> argument(name: String, type: ArgumentType<T>): RequiredArgumentBuilder<LegacyCommandSource, T> =
    RequiredArgumentBuilder.argument(name, type)

inline fun <S, reified T> CommandContext<S>.getArgument(name: String): T =
    getArgument(name, T::class.java)

fun LegacyLiteralArgBuilder.register() = LegacyCommandManager.register(this)
