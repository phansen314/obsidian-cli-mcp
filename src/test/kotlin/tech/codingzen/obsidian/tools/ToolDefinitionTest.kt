package tech.codingzen.obsidian.tools

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ToolDefinitionTest {

    @Test
    fun `tool DSL captures description`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test description"
        }
        assertEquals("Test description", def.description)
    }

    @Test
    fun `tool DSL captures name`() {
        val def = tool(ToolName.ObsidianSearch) {
            description = "Search tool"
        }
        assertEquals(ToolName.ObsidianSearch, def.name)
    }

    @Test
    fun `required params appear in toToolSchema required list`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            string(Param.Action, "The action", required = true)
            string(Param.Vault, "Vault name", required = false)
        }
        val schema = def.toToolSchema()
        assertNotNull(schema.required, "required list should not be null when a param is required")
        assertTrue(schema.required!!.contains(Param.Action.value))
        assertFalse(schema.required!!.contains(Param.Vault.value))
    }

    @Test
    fun `optional params absent from toToolSchema required list`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            string(Param.Vault, "Vault name")
            string(Param.File, "File name")
        }
        val schema = def.toToolSchema()
        assertNull(schema.required)
    }

    @Test
    fun `type strings preserved in JSON schema properties`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            string(Param.Action, "string param")
            boolean(Param.Open, "bool param")
            integer(Param.Limit, "int param")
            number(Param.Level, "number param")
        }
        val schema = def.toToolSchema()
        assertNotNull(schema.properties, "schema.properties should not be null")
        val props = schema.properties!!

        fun typeOf(param: Param): String {
            val propEntry = props[param.value]
            assertNotNull(propEntry, "No schema entry for param '${param.value}'")
            val propObj = assertInstanceOf(JsonObject::class.java, propEntry,
                "Schema entry for '${param.value}' is not a JsonObject")
            val typeElem = propObj["type"]
            assertNotNull(typeElem, "No 'type' field in schema for '${param.value}'")
            val typePrim = assertInstanceOf(JsonPrimitive::class.java, typeElem,
                "'type' for '${param.value}' is not a JsonPrimitive")
            return typePrim.content
        }

        assertEquals("string", typeOf(Param.Action))
        assertEquals("boolean", typeOf(Param.Open))
        assertEquals("integer", typeOf(Param.Limit))
        assertEquals("number", typeOf(Param.Level))
    }

    @Test
    fun `description preserved in JSON schema properties`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            string(Param.Vault, "My vault description")
        }
        val schema = def.toToolSchema()
        assertNotNull(schema.properties, "schema.properties should not be null")
        val propEntry = schema.properties!![Param.Vault.value]
        assertNotNull(propEntry, "No schema entry for vault param")
        val propObj = assertInstanceOf(JsonObject::class.java, propEntry,
            "Vault schema entry should be a JsonObject")
        val descElem = propObj["description"]
        assertNotNull(descElem, "No 'description' field in vault schema")
        val descPrim = assertInstanceOf(JsonPrimitive::class.java, descElem,
            "'description' should be a JsonPrimitive")
        assertEquals("My vault description", descPrim.content)
    }

    @Test
    fun `toToolSchema required is null when no params are required`() {
        val def = tool(ToolName.ObsidianVault) {
            description = "Test"
        }
        assertNull(def.toToolSchema().required)
    }

    @Test
    fun `multiple required params all appear in required list`() {
        val def = tool(ToolName.ObsidianSearch) {
            description = "Test"
            string(Param.Action, "action", required = true)
            string(Param.Query, "query", required = true)
            string(Param.Vault, "vault", required = false)
        }
        assertNotNull(def.toToolSchema().required, "required list should not be null")
        val required = def.toToolSchema().required!!
        assertTrue(Param.Action.value in required)
        assertTrue(Param.Query.value in required)
        assertFalse(Param.Vault.value in required)
    }

    @Test
    fun `vault convenience param is optional string`() {
        val def = tool(ToolName.ObsidianVault) {
            description = "Test"
            vault()
        }
        val schema = def.toToolSchema()
        assertNotNull(schema.properties, "schema.properties should not be null")
        assertTrue(Param.Vault.value in schema.properties!!)
        assertNull(schema.required)
    }

    @Test
    fun `open convenience param is optional boolean`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            open()
        }
        val schema = def.toToolSchema()
        assertNotNull(schema.properties, "schema.properties should not be null")
        val props = schema.properties!!
        assertTrue(Param.Open.value in props, "Expected 'open' param in schema")
        assertNull(schema.required)
        val propObj = assertInstanceOf(JsonObject::class.java, props[Param.Open.value])
        val typeElem = assertInstanceOf(JsonPrimitive::class.java, propObj["type"])
        assertEquals("boolean", typeElem.content)
    }

    @Test
    fun `silent convenience param is optional boolean`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            silent()
        }
        val schema = def.toToolSchema()
        assertNotNull(schema.properties, "schema.properties should not be null")
        val props = schema.properties!!
        assertTrue(Param.Silent.value in props, "Expected 'silent' param in schema")
        assertNull(schema.required)
        val propObj = assertInstanceOf(JsonObject::class.java, props[Param.Silent.value])
        val typeElem = assertInstanceOf(JsonPrimitive::class.java, propObj["type"])
        assertEquals("boolean", typeElem.content)
    }

    @Test
    fun `format convenience param is optional string`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            format()
        }
        val schema = def.toToolSchema()
        assertNotNull(schema.properties, "schema.properties should not be null")
        val props = schema.properties!!
        assertTrue(Param.Format.value in props, "Expected 'format' param in schema")
        assertNull(schema.required)
        val propObj = assertInstanceOf(JsonObject::class.java, props[Param.Format.value])
        val typeElem = assertInstanceOf(JsonPrimitive::class.java, propObj["type"])
        assertEquals("string", typeElem.content)
    }

    @Test
    fun `total convenience param is optional boolean`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            total()
        }
        val schema = def.toToolSchema()
        assertNotNull(schema.properties, "schema.properties should not be null")
        val props = schema.properties!!
        assertTrue(Param.Total.value in props, "Expected 'total' param in schema")
        assertNull(schema.required)
        val propObj = assertInstanceOf(JsonObject::class.java, props[Param.Total.value])
        val typeElem = assertInstanceOf(JsonPrimitive::class.java, propObj["type"])
        assertEquals("boolean", typeElem.content)
    }

    @Test
    fun `fileOrPath convenience adds both file and path params`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            fileOrPath()
        }
        val schema = def.toToolSchema()
        assertNotNull(schema.properties, "schema.properties should not be null")
        val props = schema.properties!!
        assertTrue(Param.File.value in props, "Expected 'file' param in schema")
        assertTrue(Param.Path.value in props, "Expected 'path' param in schema")
        assertNull(schema.required)
    }

    @Test
    fun `fileOrPath file param is string type`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            fileOrPath()
        }
        val props = def.toToolSchema().properties!!
        val fileObj = assertInstanceOf(JsonObject::class.java, props[Param.File.value])
        val typeElem = assertInstanceOf(JsonPrimitive::class.java, fileObj["type"])
        assertEquals("string", typeElem.content)
    }

    @Test
    fun `fileOrPath path param is string type`() {
        val def = tool(ToolName.ObsidianNote) {
            description = "Test"
            fileOrPath()
        }
        val props = def.toToolSchema().properties!!
        val pathObj = assertInstanceOf(JsonObject::class.java, props[Param.Path.value])
        val typeElem = assertInstanceOf(JsonPrimitive::class.java, pathObj["type"])
        assertEquals("string", typeElem.content)
    }
}
