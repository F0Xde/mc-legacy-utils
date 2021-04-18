package de.f0x.legacyutils.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

typealias ConfigProperty<T> = ReadOnlyProperty<Any?, ConfigKey<T>>

abstract class ConfigDecl {
    companion object {
        fun <T> config(default: T, build: ConfigBuilder<T>.() -> Unit = {}): ConfigProperty<T> =
            ConfigBuilder(default).apply(build).build()

        fun bool(default: Boolean = false, build: ConfigBuilder<Boolean>.() -> Unit = {}): ConfigProperty<Boolean> =
            ConfigBuilder(default).apply(build).build()

        fun int(default: Int, build: IntBuilder.() -> Unit = {}): ConfigProperty<Int> =
            IntBuilder(default).apply(build).build()

        fun long(default: Long, build: LongBuilder.() -> Unit = {}): ConfigProperty<Long> =
            LongBuilder(default).apply(build).build()

        fun float(default: Float, build: FloatBuilder.() -> Unit = {}): ConfigProperty<Float> =
            FloatBuilder(default).apply(build).build()

        fun double(default: Double, build: DoubleBuilder.() -> Unit = {}): ConfigProperty<Double> =
            DoubleBuilder(default).apply(build).build()
    }
}

open class ConfigBuilder<T>(val default: T) {
    var desc: String? = null

    open fun build(): ConfigProperty<T> = ConfigDelegate(default, desc)
}

class IntBuilder(default: Int) : ConfigBuilder<Int>(default) {
    var min = Int.MIN_VALUE
    var max = Int.MAX_VALUE

    override fun build(): ConfigProperty<Int> = IntDelegate(default, desc, min, max)
}

class LongBuilder(default: Long) : ConfigBuilder<Long>(default) {
    var min = Long.MIN_VALUE
    var max = Long.MAX_VALUE

    override fun build(): ConfigProperty<Long> = LongDelegate(default, desc, min, max)
}

class FloatBuilder(default: Float) : ConfigBuilder<Float>(default) {
    var min = Float.NEGATIVE_INFINITY
    var max = Float.POSITIVE_INFINITY

    override fun build(): ConfigProperty<Float> = FloatDelegate(default, desc, min, max)
}

class DoubleBuilder(default: Double) : ConfigBuilder<Double>(default) {
    var min = Double.NEGATIVE_INFINITY
    var max = Double.POSITIVE_INFINITY

    override fun build(): ConfigProperty<Double> = DoubleDelegate(default, desc, min, max)
}

private open class ConfigDelegate<T>(val default: T, val desc: String?) :
    ReadOnlyProperty<Any?, ConfigKey<T>> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        ConfigKey(property.name, default)
}

private class IntDelegate(
    default: Int,
    desc: String?,
    val min: Int,
    val max: Int,
) : ConfigDelegate<Int>(default, desc) {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        IntKey(property.name, default, min, max)
}

private class LongDelegate(
    default: Long,
    desc: String?,
    val min: Long,
    val max: Long,
) : ConfigDelegate<Long>(default, desc) {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        LongKey(property.name, default, min, max)
}

private class FloatDelegate(
    default: Float,
    desc: String?,
    val min: Float,
    val max: Float,
) : ConfigDelegate<Float>(default, desc) {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        FloatKey(property.name, default, min, max)
}

private class DoubleDelegate(
    default: Double,
    desc: String?,
    val min: Double,
    val max: Double,
) : ConfigDelegate<Double>(default, desc) {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        DoubleKey(property.name, default, min, max)
}

open class ConfigKey<T>(name: String, default: T)

private class IntKey(
    name: String,
    default: Int,
    val min: Int,
    val max: Int,
) : ConfigKey<Int>(name, default)

private class LongKey(
    name: String,
    default: Long,
    val min: Long,
    val max: Long,
) : ConfigKey<Long>(name, default)

private class FloatKey(
    name: String,
    default: Float,
    val min: Float,
    val max: Float,
) : ConfigKey<Float>(name, default)

private class DoubleKey(
    name: String,
    default: Double,
    val min: Double,
    val max: Double,
) : ConfigKey<Double>(name, default)

private sealed class ConfigNode

private class ConfigObject(val entries: Map<ConfigKey<*>, ConfigNode>) : ConfigNode()

private class ConfigValue<T>(val value: T) : ConfigNode()

private class ConfigSerializer : KSerializer<ConfigNode> {
    override val descriptor = TODO()

    override fun deserialize(decoder: Decoder): ConfigNode {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: ConfigNode) {
        TODO("Not yet implemented")
    }

}
