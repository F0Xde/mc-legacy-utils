package de.f0x.legacyutils.command

import com.mojang.brigadier.arguments.*
import de.f0x.legacyutils.Color
import de.f0x.legacyutils.config.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

fun ConfigHolder.registerCommands() {
    literal("get").thenConfig(root) { node ->
        executes {
            it.source.send("§7current: §e${node.value} §7default: §e${node.default}")
            1
        }
    }.register()
    literal("set").thenConfig(root) { node ->
        then(argument("value", node.argumentType).executes {
            node.set(it.getArgument("value", (node.type.classifier as KClass<*>).java))
            save()
            it.source.send("§7new value: §e${node.value}")
            1
        })
    }.register()
    literal("reset").thenConfig(root) { node ->
        executes {
            node.reset()
            save()
            it.source.send("§7new value: §e${node.value}")
            1
        }
    }.register()
}

private fun LegacyLiteralArgBuilder.thenConfig(
    node: ConfigNode,
    action: LegacyLiteralArgBuilder.(ConfigValue<*>) -> Unit
): LegacyLiteralArgBuilder =
    when (node) {
        is ConfigObject -> {
            for ((key, value) in node) {
                then(literal(key).thenConfig(value, action))
            }
            this
        }
        is ConfigValue<*> -> {
            action(node)
            this
        }
    }

@Suppress("unchecked_cast")
private val <T> ConfigValue<T>.argumentType: ArgumentType<T>
    get() = when (this) {
        is IntValue -> IntegerArgumentType.integer(min, max)
        is LongValue -> LongArgumentType.longArg(min, max)
        is FloatValue -> FloatArgumentType.floatArg(min, max)
        is DoubleValue -> DoubleArgumentType.doubleArg(min, max)
        else -> {
            when (type) {
                typeOf<Boolean>() -> BoolArgumentType.bool()
                typeOf<String>() -> StringArgumentType.string()
                typeOf<Color>() -> ColorArgumentType()
                // disgusting but I can't help myself
                else -> if (type.isSubtypeOf(typeOf<Enum<*>>())) {
                    EnumArgumentType((type.classifier as KClass<*>).java.enumConstants as Array<out Enum<*>>)
                } else {
                    throw IllegalStateException("No ArgumentType for '$type' exists")
                }
            }
        }
    } as ArgumentType<T>
