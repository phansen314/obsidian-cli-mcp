package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.NoteHandler
import tech.codingzen.obsidian.handlers.PropertyHandler
import tech.codingzen.obsidian.tools.Param

class CorePropertyIntegrationTest : IntegrationTestBase() {

    private fun createNote(name: String, content: String = "") = makeRequest("obsidian_note") {
        put(Param.Vault.value, vaultName)
        put(Param.Action.value, "create")
        put(Param.Name.value, name)
        put(Param.Content.value, content)
    }

    @Test
    fun `set property then get returns the value`() = runTest {
        NoteHandler.invoke(createNote("test-prop-set"))
        settle()

        PropertyHandler.invoke(makeRequest("obsidian_property") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "set")
            put(Param.File.value, "test-prop-set")
            put(Param.Name.value, "status")
            put(Param.Value.value, "active")
        })
        settle()

        val result = PropertyHandler.invoke(makeRequest("obsidian_property") {
            put(Param.Vault.value, vaultName)
            put(Param.File.value, "test-prop-set")
        })
        assertFalse(result.isError == true, "get failed: ${result.text()}")
        assertTrue(result.text().contains("status"), "Expected property key in output. Got: ${result.text()}")
        assertTrue(result.text().contains("active"), "Expected property value in output. Got: ${result.text()}")
    }

    @Test
    fun `remove property then absent in get`() = runTest {
        NoteHandler.invoke(createNote("test-prop-remove"))
        settle()

        PropertyHandler.invoke(makeRequest("obsidian_property") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "set")
            put(Param.File.value, "test-prop-remove")
            put(Param.Name.value, "removeme")
            put(Param.Value.value, "yes")
        })
        settle()

        PropertyHandler.invoke(makeRequest("obsidian_property") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "remove")
            put(Param.File.value, "test-prop-remove")
            put(Param.Name.value, "removeme")
        })
        settle()

        val result = PropertyHandler.invoke(makeRequest("obsidian_property") {
            put(Param.Vault.value, vaultName)
            put(Param.File.value, "test-prop-remove")
        })
        assertFalse(result.text().contains("removeme"),
            "Removed property should not appear in get output")
    }

    @Test
    fun `unknown action returns isError true`() = runTest {
        NoteHandler.invoke(createNote("test-prop-badaction"))
        settle()

        val result = PropertyHandler.invoke(makeRequest("obsidian_property") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "badaction")
            put(Param.File.value, "test-prop-badaction")
        })
        assertTrue(result.isError == true, "Expected isError=true for unknown action")
    }
}
