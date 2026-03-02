package tech.codingzen.obsidian.tools

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CliArgsTest {

    /**
     * Builds a [ParametersAccessor] from the given pairs.
     * String and Boolean values are included as JSON primitives; null values are omitted (treated as absent).
     */
    private fun accessorWith(vararg pairs: Pair<Param, Any?>): ParametersAccessor =
        ParametersAccessor(buildJsonObject {
            pairs.forEach { (param, value) ->
                when (value) {
                    is String -> put(param.value, JsonPrimitive(value))
                    is Boolean -> put(param.value, JsonPrimitive(value))
                    null -> { /* omit — param is absent */ }
                }
            }
        })

    @Test
    fun `keyValue appends key=value when param is present`() {
        val params = accessorWith(Param.Vault to "my-vault")
        val args = CliArgs(listOf("read"), params)
        args.keyValue(Param.Vault)
        assertTrue(args.build().contains("vault=my-vault"))
    }

    @Test
    fun `keyValue skips when param is absent`() {
        val params = accessorWith()
        val args = CliArgs(listOf("read"), params)
        args.keyValue(Param.Vault)
        assertEquals(listOf("read"), args.build())
    }

    @Test
    fun `keyValue preserves space in value`() {
        val params = accessorWith(Param.Vault to "my vault")
        val args = CliArgs(listOf("read"), params)
        args.keyValue(Param.Vault)
        // ProcessBuilder passes args as-is without shell expansion, so spaces are safe
        assertTrue(args.build().contains("vault=my vault"))
    }

    @Test
    fun `keyValue preserves equals sign in value`() {
        val params = accessorWith(Param.Vault to "key=val")
        val args = CliArgs(listOf("read"), params)
        args.keyValue(Param.Vault)
        assertTrue(args.build().contains("vault=key=val"))
    }

    @Test
    fun `keyValue includes entry for empty string value`() {
        val params = accessorWith(Param.Vault to "")
        val args = CliArgs(listOf("read"), params)
        args.keyValue(Param.Vault)
        assertTrue(args.build().contains("vault="))
    }

    @Test
    fun `flag appends --name when param is true`() {
        val params = accessorWith(Param.Open to true)
        val args = CliArgs(listOf("read"), params)
        args.flag(Param.Open)
        assertTrue(args.build().contains("--open"))
    }

    @Test
    fun `flag skips when param is false`() {
        val params = accessorWith(Param.Open to false)
        val args = CliArgs(listOf("read"), params)
        args.flag(Param.Open)
        assertEquals(listOf("read"), args.build())
    }

    @Test
    fun `flag skips when param is absent`() {
        val params = accessorWith()
        val args = CliArgs(listOf("read"), params)
        args.flag(Param.Open)
        assertEquals(listOf("read"), args.build())
    }

    @Test
    fun `build returns same backing list reference`() {
        val params = accessorWith()
        val args = CliArgs(listOf("read"), params)
        val first = args.build()
        val second = args.build()
        assertSame(first, second)
    }

    @Test
    fun `mutation of build() result corrupts subsequent build() calls`() {
        // build() returns the internal MutableList directly (no defensive copy).
        // Callers who cast and mutate the returned list will corrupt internal state.
        val params = accessorWith()
        val args = CliArgs(listOf("read"), params)
        @Suppress("UNCHECKED_CAST")
        (args.build() as MutableList<String>).add("injected")
        assertTrue(args.build().contains("injected"),
            "Mutating the returned list modifies the backing store — this documents a known hazard")
    }

    @Test
    fun `base args preserved in output`() {
        val params = accessorWith()
        val args = CliArgs(listOf("daily", "read"), params)
        val built = args.build()
        assertEquals(listOf("daily", "read"), built)
    }

    @Test
    fun `multiple keyValue and flag calls produce correct list`() {
        val params = accessorWith(
            Param.File to "my-note",
            Param.Open to true,
            Param.Silent to false,
        )
        val args = CliArgs(listOf("read"), params)
        args.keyValue(Param.File)
        args.keyValue(Param.Vault)  // absent, skipped
        args.flag(Param.Open)
        args.flag(Param.Silent)
        assertEquals(listOf("read", "file=my-note", "--open"), args.build())
    }

    @Test
    fun `flag uses wire name with -- prefix`() {
        val params = accessorWith(Param.ShowText to true)
        val args = CliArgs(listOf("dom"), params)
        args.flag(Param.ShowText)
        assertTrue(args.build().contains("--show-text"))
    }

    @Test
    fun `keyValue uses wire name in key=value pair`() {
        val params = accessorWith(Param.ShowText to "true")
        val args = CliArgs(listOf("dom"), params)
        args.keyValue(Param.ShowText)
        assertTrue(args.build().contains("show-text=true"))
    }
}
