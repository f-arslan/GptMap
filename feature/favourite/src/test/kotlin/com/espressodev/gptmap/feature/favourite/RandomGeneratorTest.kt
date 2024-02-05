package com.espressodev.gptmap.feature.favourite

import com.espressodev.gptmap.core.common.util.MakeRandomInstanceConfig
import com.espressodev.gptmap.core.common.util.NoUsableConstructor
import com.espressodev.gptmap.core.common.util.makeRandomInstance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

@Suppress("USELESS_IS_CHECK")
class MakeRandomInstanceTest {
    class A {
        override fun toString(): String = "A"
    }

    data class B(val a: A)
    data class C(val b1: B, val c: Char, val b2: B, val str: String, val l: Long)
    class D {
        lateinit var a: A

        constructor() {
            throw Error("Do not use this one")
        }

        constructor(a: A) {
            this.a = a
        }

        override fun toString() = "D(a=$a)"
    }

    data class E(val b: B, val map: Map<Long, String>, val l: Long)
    data class F(val l: List<E>, val e: E)
    data class L(val ints: List<Int>)

    class P private constructor() {
    }

    @Test
    fun `Creates single instance using an empty constructor`() {
        val a: A = makeRandomInstance()
        assertTrue(a is A)
        assertTrue("A" in a.toString())
    }

    @Test
    fun `Throws NoUsableConstructor error if there is no constructor that could be used`() {
        try {
            makeRandomInstance<P>()
            error("makeRandomInstance should throw NoUsableConstructor error")
        } catch (e: NoUsableConstructor) {
            // no-op
        }
    }

    private fun catchError(function: () -> Unit): Throwable? = try {
        function()
        null
    } catch (throwable: Throwable) {
        throwable
    }

    @Test
    fun `Creates using constructor`() {
        val b: B = makeRandomInstance()
        assertTrue(b is B)
        assertEquals("B(a=A)", b.toString())
    }

    @Test
    fun `Skipps constructors that cannot be used`() {
        val d: D = makeRandomInstance()
        assertTrue(d is D)
        assertEquals("D(a=A)", d.toString())
    }

    @Test
    fun `Creates primitives`() {
        assertTrue(makeRandomInstance<Int>() is Int)
        assertTrue(makeRandomInstance<Long>() is Long)
        assertTrue(makeRandomInstance<Double>() is Double)
        assertTrue(makeRandomInstance<Float>() is Float)
        assertTrue(makeRandomInstance<Char>() is Char)
        assertTrue(makeRandomInstance<String>() is String)
    }

    @Test
    fun `Creates an instance using constructor with primitives and standard types`() {
        val b: B = makeRandomInstance()
        assertTrue(b is B)
        assertTrue("B(a=A)" in b.toString())

        val c: C = makeRandomInstance()
        assertTrue(c is C)
        assertTrue(
            c.toString()
                .matches("C\\(b1=B\\(a=A\\), c=[A-z], b2=B\\(a=A\\), str=[A-z]*, l=-?\\d*\\)".toRegex()),
            "It is $c"
        )
    }

    @Test
    fun `Creates collections`() {
        val ints = makeRandomInstance<List<Int>>()
        assertTrue(ints is List<Int>)
        assertTrue(ints.toString().startsWith("["))
        assertTrue(ints.toString().endsWith("]"))

        val map = makeRandomInstance<Map<Long, String>>()
        assertTrue(map is Map<Long, String>)
        assertTrue(map.toString().startsWith("{"))
        assertTrue(map.toString().endsWith("}"))

        assertTrue(makeRandomInstance<Collection<A>>() is Collection<A>)
    }

    @Test
    fun `Creates an instance using constructor with collections, primitives and standard types`() {
        val b: L = makeRandomInstance()
        assertTrue(b is L)
        assertTrue(b.ints is List<Int>)
        assertTrue(b.ints.firstOrNull() is Int?)

        val e: E = makeRandomInstance()
        assertTrue(e is E)
        assertTrue(e.map.all { (k, v) -> k is Long && v is String })
        assertTrue(e.toString().startsWith("E(b=B(a=A), map={"))
    }

    class GT<T> {
        var t: T? = null
    }

    class GA<T>(var t: T)
    class GAA<T1, T2>(val t1: T1, val t2: T2)
    class GTA<T1, T2>(val t2: T2)

    @Test
    fun `Generic classes are supported`() {
        val gt1 = makeRandomInstance<GT<Int>>()
        gt1.t = 1
        assertEquals(1, gt1.t)
        gt1.t = 10

        val gt2 = makeRandomInstance<GT<Long>>()
        gt2.t = 1L
        assertEquals(1L, gt2.t)
        gt2.t = 10L

        val gtRecursive = makeRandomInstance<GT<GT<Int>>>()
        gtRecursive.t = gt1
        assertEquals(gt1, gtRecursive.t)

        val ga1: GA<Int> = makeRandomInstance()
        assertTrue(ga1.t is Int)

        val ga2: GA<String> = makeRandomInstance()
        assertTrue(ga2.t is String)

        val gaa1: GAA<Int, String> = makeRandomInstance()
        assertTrue(gaa1.t1 is Int)
        assertTrue(gaa1.t2 is String)

        val gaa2: GAA<Long, List<Int>> = makeRandomInstance()
        assertTrue(gaa2.t1 is Long)
        assertTrue(gaa2.t2 is List<Int>)

        val gta: GTA<Long, String> = makeRandomInstance()
        gta.t2.length

        val gaaga: GAA<Long, GA<GT<Int>>> = makeRandomInstance()
        assertTrue(gaaga.t1 is Long)
        assertTrue(gaaga.t2 is GA<GT<Int>>)

        val gggg: GA<GA<GA<Int>>> = makeRandomInstance()
        gggg.t.t.t = 10
        assertEquals(10, gggg.t.t.t)
        gggg.t.t = GA(20)
        assertEquals(20, gggg.t.t.t)
    }

    @Test
    fun `When user expects empty collections, both Map and List are empty`() {
        val config = MakeRandomInstanceConfig(possibleCollectionSizes = 0..0)
        repeat(10) {
            assertEquals(emptyList<Int>(), makeRandomInstance<List<Int>>(config = config))
            assertEquals(emptyList<Int>(), makeRandomInstance<List<List<Int>>>(config = config))
            assertEquals(
                emptyMap<String, Int>(),
                makeRandomInstance<Map<Int, String>>(config = config)
            )
        }
    }

    @Test
    fun `When user expects concrete collection size, both Map and List are of this size`() {
        val config = MakeRandomInstanceConfig(possibleCollectionSizes = 5..5)
        repeat(10) {
            assertEquals(5, makeRandomInstance<List<Int>>(config = config).size)
            assertEquals(5, makeRandomInstance<List<List<Int>>>(config = config).size)
            assertEquals(5, makeRandomInstance<Map<Int, String>>(config = config).size)
        }
    }

    @Test
    fun `When user expects concrete String length, all Strings have this length`() {
        val config =
            MakeRandomInstanceConfig(possibleStringSizes = 5..5, possibleCollectionSizes = 2..2)
        repeat(10) {
            assertEquals(5, makeRandomInstance<String>(config = config).length)
            assertEquals(5, makeRandomInstance<List<String>>(config = config)[0].length)
            assertEquals(5, makeRandomInstance<List<List<String>>>(config = config)[1][1].length)
        }
    }

    @Test
    fun `Object set in config as Any, is always returned when we expect Any`() {
        val any = object {}
        val config = MakeRandomInstanceConfig(any = any)
        repeat(10) {
            assertEquals(any, makeRandomInstance<Any>(config = config))
            assertEquals(any, makeRandomInstance<GA<Any>>(config = config).t)
            assertEquals(any, makeRandomInstance<GA<GA<Any>>>(config = config).t.t)
        }
    }

    @Test
    fun `Check expected random values`() {
        val random = Random(12345)
        assertEquals("A", makeRandomInstance<A>(random).toString())
        assertEquals("B(a=A)", makeRandomInstance<B>(random).toString())
        assertEquals(
            "C(b1=B(a=A), c=Y, b2=B(a=A), str=yS, l=-6367288518484839692)",
            makeRandomInstance<C>(random).toString()
        )
        assertEquals("D(a=A)", makeRandomInstance<D>(random).toString())
        assertEquals(
            "E(b=B(a=A), map={-6428220448289816081=voWGUkC\\, 7288696731122253832=kp]U, -4359497035184897174=Nc`tCa, -8481730907691591520=M}, l=1553348274986458979)",
            makeRandomInstance<E>(random).toString()
        )
        assertEquals(
            "F(l=[E(b=B(a=A), map={-6065142614942521822=t, 416890183638600344=p_JLM]iD^y, 4288163720945964501=z`AGmw}, l=5098165797873145605), E(b=B(a=A), map={-1045531387234036165=SYMlUY, 943176485828979=bkcU, 7917721219055033990=xl^\\dB, -3835092262954011188=lOpa^clPX}, l=870933613633965720), E(b=B(a=A), map={3682447410090233778=LfX[Em, -6203180773228722909=KeloXcypXg, -3662781481041013612=]lILXf, 8884333428377293996=e, 827637008512869092=Zwcu}, l=1759343391314632900), E(b=B(a=A), map={3072701447898829435=GYDLUYDSdn}, l=-6214529434004727598), E(b=B(a=A), map={-5540839068437135337=HTxnLiz, 8783042796281279363=vGYAp`, 5347169406213203755=bOpO_P, -4827221593046365475=vkn}, l=1667205364588981005)], e=E(b=B(a=A), map={-826488732485778636=SAv, 398313030478269938=sDFgHE, 7535000932527017313=jboPScOh, 321039882229839452=bg}, l=-2421217787312800394))",
            makeRandomInstance<F>(random).toString()
        )
    }
}
