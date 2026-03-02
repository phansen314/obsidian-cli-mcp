package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.PluginHandler
import tech.codingzen.obsidian.tools.Param

class OptionalPluginsIntegrationTest : IntegrationTestBase() {

    @Test
    fun `list plugins does not error`() = runTest {
        val result = PluginHandler.invoke(makeRequest("obsidian_plugin") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "list")
        })
        assertFalse(result.isError == true, "plugins list failed: ${result.text()}")
    }

    @Test
    fun `plugin versions does not error`() = runTest {
        val result = PluginHandler.invoke(makeRequest("obsidian_plugin") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "versions")
        })
        assertFalse(result.isError == true, "plugin versions failed: ${result.text()}")
    }

    @Test
    fun `enable unknown plugin id returns an error`() = runTest {
        // Enabling a plugin ID that does not exist should be reported as an error by the CLI
        val result = PluginHandler.invoke(makeRequest("obsidian_plugin") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "enable")
            put(Param.Id.value, "nonexistent-plugin-id-xyz")
        })
        assertTrue(result.isError == true,
            "Expected isError=true when enabling nonexistent plugin. Got: ${result.text()}")
    }

    @Test
    fun `disable unknown plugin id returns an error`() = runTest {
        val result = PluginHandler.invoke(makeRequest("obsidian_plugin") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "disable")
            put(Param.Id.value, "nonexistent-plugin-id-xyz")
        })
        assertTrue(result.isError == true,
            "Expected isError=true when disabling nonexistent plugin. Got: ${result.text()}")
    }

    @Test
    fun `unknown action returns isError true`() = runTest {
        val result = PluginHandler.invoke(makeRequest("obsidian_plugin") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "badaction")
        })
        assertTrue(result.isError == true, "Expected isError=true for unknown action")
    }
}
