package de.f0x.legacyutils.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ConfigImplTest : FunSpec({
    test("ConfigProperty should generate full path") {
        Outer.Inner.prop.path shouldBe listOf("Outer", "Inner", "prop")
    }

    test("ConfigProperty should have correct default") {
        Outer.Inner.prop.default shouldBe 42
    }
})

object Outer : ConfigDecl() {
    object Inner : ConfigDecl() {
        val prop by int(42)
    }
}
