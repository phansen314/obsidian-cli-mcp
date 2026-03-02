package tech.codingzen.obsidian.tools

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequestParams
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParametersAccessorTest {

    private fun makeAccessor(vararg pairs: Pair<String, JsonElement>): ParametersAccessor =
        ParametersAccessor(buildJsonObject { pairs.forEach { (k, v) -> put(k, v) } })

    private fun nullAccessor() = ParametersAccessor(null)

    @Test
    fun `str returns correct string value`() {
        val accessor = makeAccessor(Param.Vault.value to JsonPrimitive("my-vault"))
        assertEquals("my-vault", accessor.str(Param.Vault))
    }

    @Test
    fun `str returns null for absent key`() {
        val accessor = makeAccessor()
        assertNull(accessor.str(Param.Vault))
    }

    @Test
    fun `str returns null for JsonNull value`() {
        val accessor = makeAccessor(Param.Vault.value to JsonNull)
        assertNull(accessor.str(Param.Vault))
    }

    @Test
    fun `str returns null when parameters object is null`() {
        assertNull(nullAccessor().str(Param.Vault))
    }

    @Test
    fun `str returns string representation of numeric primitive`() {
        val accessor = makeAccessor(Param.Vault.value to JsonPrimitive(42))
        assertEquals("42", accessor.str(Param.Vault))
    }

    @Test
    fun `str returns null for JsonArray value`() {
        val accessor = makeAccessor(Param.Vault.value to buildJsonArray { add("item") })
        assertNull(accessor.str(Param.Vault))
    }

    @Test
    fun `bool returns correct boolean value true`() {
        val accessor = makeAccessor(Param.Open.value to JsonPrimitive(true))
        assertEquals(true, accessor.bool(Param.Open))
    }

    @Test
    fun `bool returns correct boolean value false`() {
        val accessor = makeAccessor(Param.Open.value to JsonPrimitive(false))
        assertEquals(false, accessor.bool(Param.Open))
    }

    @Test
    fun `bool returns null for absent key`() {
        val accessor = makeAccessor()
        assertNull(accessor.bool(Param.Open))
    }

    @Test
    fun `bool returns null for JsonNull value`() {
        val accessor = makeAccessor(Param.Open.value to JsonNull)
        assertNull(accessor.bool(Param.Open))
    }

    @Test
    fun `bool returns null when parameters object is null`() {
        assertNull(nullAccessor().bool(Param.Open))
    }

    @Test
    fun `bool returns true for string primitive containing true`() {
        // booleanOrNull parses content regardless of the isString flag, so "true" → true
        val accessor = makeAccessor(Param.Open.value to JsonPrimitive("true"))
        assertEquals(true, accessor.bool(Param.Open))
    }

    @Test
    fun `str returns null for non-primitive JSON object value`() {
        val accessor = makeAccessor(Param.Vault.value to buildJsonObject { put("nested", JsonPrimitive("val")) })
        assertNull(accessor.str(Param.Vault))
    }

    @Test
    fun `get operator returns null for absent key`() {
        val accessor = makeAccessor()
        assertNull(accessor[Param.File])
    }

    @Test
    fun `multiple params accessible independently`() {
        val accessor = makeAccessor(
            Param.Vault.value to JsonPrimitive("vault1"),
            Param.File.value to JsonPrimitive("note"),
            Param.Open.value to JsonPrimitive(true),
        )
        assertEquals("vault1", accessor.str(Param.Vault))
        assertEquals("note", accessor.str(Param.File))
        assertEquals(true, accessor.bool(Param.Open))
        assertNull(accessor.str(Param.Path))
    }

    @Test
    fun `CallToolRequest parameters() extension wraps arguments`() {
        val request = CallToolRequest(
            CallToolRequestParams(
                name = "obsidian_note",
                arguments = buildJsonObject { put(Param.Action.value, JsonPrimitive("read")) }
            )
        )
        val params = request.parameters()
        assertEquals("read", params.str(Param.Action))
    }

    @Test
    fun `CallToolRequest parameters() with null arguments returns accessor that yields nulls`() {
        val request = CallToolRequest(
            CallToolRequestParams(name = "obsidian_note", arguments = null)
        )
        val params = request.parameters()
        assertNull(params.str(Param.Action))
        assertNull(params.bool(Param.Open))
    }
}
