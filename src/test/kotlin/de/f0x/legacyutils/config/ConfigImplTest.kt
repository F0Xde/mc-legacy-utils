package de.f0x.legacyutils.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

class ConfigImplTest : FunSpec({
    test("ConfigProperty should generate full path") {
        Outer.Inner.prop.path shouldBe listOf("Outer", "Inner", "prop")
    }

    test("ConfigProperty should have correct class") {
        Outer.Inner.prop.type shouldBe typeOf<Int>()
    }

    test("ConfigDecl should build to ConfigObject") {
        val built = Outer.build()
        built shouldHaveSize 1
        built["Inner"] shouldBe Outer.Inner.build()
        with(Outer.Inner::prop) {
            isAccessible = true
            (built["Inner"] as ConfigObject)["prop"] shouldBe getDelegate()
        }
    }
})

object Outer : ConfigDecl() {
    object Inner : ConfigDecl() {
        val prop by int(42)
    }
}
