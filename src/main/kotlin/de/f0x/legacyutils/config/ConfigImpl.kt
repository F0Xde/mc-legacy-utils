package de.f0x.legacyutils.config

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

typealias SimpleConfigProperty<T> = ConfigProperty<ConfigKey<T>>
typealias ConfigProperty<T> = ReadOnlyProperty<Any, T>

abstract class ConfigDecl {
    companion object {
        fun <T> config(default: T, build: ConfigBuilder<T>.() -> Unit = {}): SimpleConfigProperty<T> =
            ConfigBuilder(default).apply(build).build()

        fun bool(
            default: Boolean = false,
            build: ConfigBuilder<Boolean>.() -> Unit = {}
        ): SimpleConfigProperty<Boolean> =
            ConfigBuilder(default).apply(build).build()

        fun int(default: Int, build: IntBuilder.() -> Unit = {}): ConfigProperty<IntKey> =
            IntBuilder(default).apply(build).build()

        fun long(default: Long, build: LongBuilder.() -> Unit = {}): ConfigProperty<LongKey> =
            LongBuilder(default).apply(build).build()

        fun float(default: Float, build: FloatBuilder.() -> Unit = {}): ConfigProperty<FloatKey> =
            FloatBuilder(default).apply(build).build()

        fun double(default: Double, build: DoubleBuilder.() -> Unit = {}): ConfigProperty<DoubleKey> =
            DoubleBuilder(default).apply(build).build()
    }
}

open class ConfigBuilder<T>(val default: T) {
    var desc: String? = null

    open fun build(): SimpleConfigProperty<T> = SimpleConfigDelegate(default, desc)
}

class IntBuilder(default: Int) : ConfigBuilder<Int>(default) {
    var min = Int.MIN_VALUE
    var max = Int.MAX_VALUE

    override fun build(): ConfigProperty<IntKey> = IntDelegate(default, desc, min, max)
}

class LongBuilder(default: Long) : ConfigBuilder<Long>(default) {
    var min = Long.MIN_VALUE
    var max = Long.MAX_VALUE

    override fun build(): ConfigProperty<LongKey> = LongDelegate(default, desc, min, max)
}

class FloatBuilder(default: Float) : ConfigBuilder<Float>(default) {
    var min = Float.NEGATIVE_INFINITY
    var max = Float.POSITIVE_INFINITY

    override fun build(): ConfigProperty<FloatKey> = FloatDelegate(default, desc, min, max)
}

class DoubleBuilder(default: Double) : ConfigBuilder<Double>(default) {
    var min = Double.NEGATIVE_INFINITY
    var max = Double.POSITIVE_INFINITY

    override fun build(): ConfigProperty<DoubleKey> = DoubleDelegate(default, desc, min, max)
}

private sealed class ConfigDelegate<T>(val default: T, val desc: String?) {
    protected fun getPath(thisRef: Any, property: KProperty<*>): List<String> {
        val path = mutableListOf(property.name)
        var clazz: Class<*>? = thisRef::class.java
        while (clazz != null) {
            path.add(clazz.simpleName)
            clazz = clazz.enclosingClass
        }
        return path.asReversed()
    }
}

private class SimpleConfigDelegate<T>(default: T, desc: String?) :
    ConfigDelegate<T>(default, desc), SimpleConfigProperty<T> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        ConfigKey(getPath(thisRef, property), default)
}

private class IntDelegate(
    default: Int,
    desc: String?,
    val min: Int,
    val max: Int,
) : ConfigDelegate<Int>(default, desc), ConfigProperty<IntKey> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        IntKey(getPath(thisRef, property), default, min, max)
}

private class LongDelegate(
    default: Long,
    desc: String?,
    val min: Long,
    val max: Long,
) : ConfigDelegate<Long>(default, desc), ConfigProperty<LongKey> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        LongKey(getPath(thisRef, property), default, min, max)
}

private class FloatDelegate(
    default: Float,
    desc: String?,
    val min: Float,
    val max: Float,
) : ConfigDelegate<Float>(default, desc), ConfigProperty<FloatKey> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        FloatKey(getPath(thisRef, property), default, min, max)
}

private class DoubleDelegate(
    default: Double,
    desc: String?,
    val min: Double,
    val max: Double,
) : ConfigDelegate<Double>(default, desc), ConfigProperty<DoubleKey> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        DoubleKey(getPath(thisRef, property), default, min, max)
}

open class ConfigKey<T>(val path: List<String>, val default: T)

class IntKey(
    path: List<String>,
    default: Int,
    val min: Int,
    val max: Int,
) : ConfigKey<Int>(path, default)

class LongKey(
    path: List<String>,
    default: Long,
    val min: Long,
    val max: Long,
) : ConfigKey<Long>(path, default)

class FloatKey(
    path: List<String>,
    default: Float,
    val min: Float,
    val max: Float,
) : ConfigKey<Float>(path, default)

class DoubleKey(
    path: List<String>,
    default: Double,
    val min: Double,
    val max: Double,
) : ConfigKey<Double>(path, default)

private sealed class ConfigNode

private class ConfigObject(val entries: Map<ConfigKey<*>, ConfigNode>) : ConfigNode()

private class ConfigValue<T>(val value: T) : ConfigNode()
