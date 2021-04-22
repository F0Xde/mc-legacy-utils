package de.f0x.legacyutils.config

import de.f0x.legacyutils.Log
import de.f0x.legacyutils.PrettyJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

typealias ConfigProperty<T> = ReadOnlyProperty<Any, ConfigKey<T>>
typealias ConfigObserver<T> = (node: ConfigValue<T>, old: T, new: T) -> Unit

abstract class ConfigDecl {
    companion object {
        inline fun <reified T : Any> config(
            default: T,
            build: ConfigBuilder<T>.() -> Unit = {}
        ): ConfigProperty<T> =
            ConfigBuilder(default, typeOf<T>()).apply(build).build()

        inline fun bool(
            default: Boolean = false,
            build: ConfigBuilder<Boolean>.() -> Unit = {}
        ): ConfigProperty<Boolean> =
            ConfigBuilder(default, typeOf<Boolean>()).apply(build).build()

        inline fun int(default: Int, build: IntBuilder.() -> Unit = {}): ConfigProperty<Int> =
            IntBuilder(default).apply(build).build()

        inline fun long(default: Long, build: LongBuilder.() -> Unit = {}): ConfigProperty<Long> =
            LongBuilder(default).apply(build).build()

        inline fun float(default: Float, build: FloatBuilder.() -> Unit = {}): ConfigProperty<Float> =
            FloatBuilder(default).apply(build).build()

        inline fun double(default: Double, build: DoubleBuilder.() -> Unit = {}): ConfigProperty<Double> =
            DoubleBuilder(default).apply(build).build()
    }

    fun build(): ConfigObject {
        val nodes: MutableMap<String, ConfigNode> = HashMap()
        for (it in this::class.nestedClasses) {
            if (it.isSubclassOf(ConfigDecl::class)) {
                nodes[it.simpleName!!] = (it.objectInstance!! as ConfigDecl).build()
            }
        }
        for (it in this::class.memberProperties) {
            it.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val delegate = (it as KProperty1<ConfigDecl, *>).getDelegate(this) ?: continue
            if (delegate is ConfigNode) {
                nodes[it.name] = delegate
            } else {
                Log.warn("ConfigDecl delegated property '${it.name}' is not a ConfigNode, skipping")
            }
        }
        return ConfigObject(nodes)
    }
}

open class ConfigBuilder<T>(val default: T, private val type: KType) {
    var desc: String? = null

    open fun build(): ConfigProperty<T> = ConfigValue(default, desc, type)
}

class IntBuilder(default: Int) : ConfigBuilder<Int>(default, typeOf<Int>()) {
    var min = Int.MIN_VALUE
    var max = Int.MAX_VALUE

    override fun build(): ConfigProperty<Int> = IntValue(default, desc, min, max)
}

class LongBuilder(default: Long) : ConfigBuilder<Long>(default, typeOf<Long>()) {
    var min = Long.MIN_VALUE
    var max = Long.MAX_VALUE

    override fun build(): ConfigProperty<Long> = LongValue(default, desc, min, max)
}

class FloatBuilder(default: Float) : ConfigBuilder<Float>(default, typeOf<Float>()) {
    var min = Float.NEGATIVE_INFINITY
    var max = Float.POSITIVE_INFINITY

    override fun build(): ConfigProperty<Float> = FloatValue(default, desc, min, max)
}

class DoubleBuilder(default: Double) : ConfigBuilder<Double>(default, typeOf<Double>()) {
    var min = Double.NEGATIVE_INFINITY
    var max = Double.POSITIVE_INFINITY

    override fun build(): ConfigProperty<Double> = DoubleValue(default, desc, min, max)
}

// Very fucking weird but I want KType and nice generic parameter
data class ConfigKey<T> internal constructor(val path: List<String>, val type: KType)

class ConfigHolder(decl: ConfigDecl, private val path: Path) {
    private val root = decl.build()

    operator fun <T> get(key: ConfigKey<T>) = ConfigItem(getVal(key))

    fun <T> observe(key: ConfigKey<T>, observer: ConfigObserver<T>) =
        getVal(key).observe(observer)

    private fun <T> getVal(key: ConfigKey<T>): ConfigValue<T> {
        val node = key.path.fold(root) { node: ConfigNode, segment ->
            (node as ConfigObject)[segment] ?: throw MissingNode(segment)
        } as ConfigValue<*>
        if (node.type != key.type) {
            throw IllegalArgumentException("ConfigKey '$key' is not valid for this config")
        }
        @Suppress("UNCHECKED_CAST")
        return node as ConfigValue<T>
    }


    fun load() {
        if (path.exists()) {
            try {
                root.fromJson(Json.parseToJsonElement(path.readText()))
            } catch (e: Exception) {
                Log.error("Error loading config, replacing with default", e)
                save()
            }
        } else {
            Log.info("Config does not exist, writing default")
            save()
        }
    }

    fun save() {
        try {
            path.parent?.createDirectories()
            path.writeText(PrettyJson.encodeToString(root.toJson()))
        } catch (e: IOException) {
            Log.error("Error saving config", e)
        }
    }
}

class ConfigItem<T>(private val node: ConfigValue<T>) : ReadOnlyProperty<Any?, T> {
    fun get() = node.value
    override fun getValue(thisRef: Any?, property: KProperty<*>) = get()
}

sealed class ConfigNode {
    abstract fun toJson(): JsonElement
    abstract fun fromJson(json: JsonElement)
}

class ConfigObject(private val content: Map<String, ConfigNode>) :
    ConfigNode(), Map<String, ConfigNode> by content {
    inline operator fun <reified T> get(key: ConfigKey<T>): T {
        val node = key.path.fold(this) { node: ConfigNode, name ->
            (node as ConfigObject)[name] ?: throw MissingNode(name)
        } as ConfigValue<*>
        return node.value as T
    }

    override fun toJson() = JsonObject(mapValues { it.value.toJson() })

    override fun fromJson(json: JsonElement) {
        for ((key, value) in json.jsonObject) {
            content[key]?.fromJson(value)
        }
    }

    override fun equals(other: Any?) = content == other
    override fun hashCode() = content.hashCode()
    override fun toString() = "ConfigObject($content)"
}

open class ConfigValue<T>(
    val default: T,
    val desc: String?,
    val type: KType
) : ConfigNode(), ConfigProperty<T> {
    private val observers = mutableListOf<ConfigObserver<T>>()

    var value = default
        set(value) {
            if (!value.isValid) throw InvalidValue(value)
            observers.forEach { it(this, field, value) }
            field = value
        }

    override fun getValue(thisRef: Any, property: KProperty<*>) =
        ConfigKey<T>(getPath(thisRef, property), type)

    fun reset() {
        value = default
    }

    fun observe(observer: ConfigObserver<T>) {
        observers.add(observer)
    }

    override fun toJson() = Json.encodeToJsonElement(serializer(type), value)

    override fun fromJson(json: JsonElement) {
        @Suppress("UNCHECKED_CAST")
        value = Json.decodeFromJsonElement(serializer(type), json) as T
    }

    protected open val T.isValid: Boolean get() = true

    private fun getPath(thisRef: Any, property: KProperty<*>): List<String> {
        val path = mutableListOf(property.name)
        var clazz: Class<*>? = thisRef::class.java
        while (clazz != null) {
            path.add(clazz.simpleName)
            clazz = clazz.enclosingClass
        }
        path.removeLast()
        return path.asReversed()
    }
}

private class IntValue(
    default: Int,
    desc: String?,
    val min: Int,
    val max: Int,
) : ConfigValue<Int>(default, desc, typeOf<Int>()) {
    override val Int.isValid: Boolean get() = this in min..max
}

private class LongValue(
    default: Long,
    desc: String?,
    val min: Long,
    val max: Long,
) : ConfigValue<Long>(default, desc, typeOf<Long>()) {
    override val Long.isValid: Boolean get() = this in min..max
}

private class FloatValue(
    default: Float,
    desc: String?,
    val min: Float,
    val max: Float,
) : ConfigValue<Float>(default, desc, typeOf<Float>()) {
    override val Float.isValid: Boolean get() = this in min..max
}

private class DoubleValue(
    default: Double,
    desc: String?,
    val min: Double,
    val max: Double,
) : ConfigValue<Double>(default, desc, typeOf<Double>()) {
    override val Double.isValid: Boolean get() = this in min..max
}

class MissingNode(val path: String) : Exception("Node '$path' does not exist")

class InvalidValue(val value: Any?) : Exception("Config value '$value' is not valid for this node")
