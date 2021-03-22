package de.f0x.legacyutils

import net.minecraft.entity.Entity

fun Entity.getYaw(tickDelta: Float) =
    if (tickDelta == 1f) yaw else tickDelta.lerp(prevYaw, yaw)

fun Entity.getPitch(tickDelta: Float) =
    if (tickDelta == 1f) pitch else tickDelta.lerp(prevPitch, pitch)

fun Float.lerp(start: Float, end: Float) =
    start + this * (end - start)

fun Double.lerp(start: Double, end: Double) =
    start + this * (end - start)
