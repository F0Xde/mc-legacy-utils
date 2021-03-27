package de.f0x.legacyutils

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

fun Entity.getYaw(tickDelta: Float) =
    if (tickDelta == 1f) yaw else tickDelta.lerp(prevYaw, yaw)

fun Entity.getPitch(tickDelta: Float) =
    if (tickDelta == 1f) pitch else tickDelta.lerp(prevPitch, pitch)

fun Float.lerp(start: Float, end: Float) =
    start + this * (end - start)

fun Double.lerp(start: Double, end: Double) =
    start + this * (end - start)

fun ClientPlayerEntity.send(text: String, prefixed: Boolean = true) {
    send(LiteralText(text), prefixed)
}

fun ClientPlayerEntity.send(text: Text, prefixed: Boolean = true) {
    sendMessage(if (prefixed) LiteralText(CHAT_PREFIX).append(text) else text)
}
