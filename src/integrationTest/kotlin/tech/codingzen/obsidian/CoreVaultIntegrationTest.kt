package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.VaultHandler
import tech.codingzen.obsidian.tools.Param

class CoreVaultIntegrationTest : IntegrationTestBase() {

    @Test
    fun `info contains vault name`() = runTest {
        val result = VaultHandler.invoke(makeRequest("obsidian_vault") {
            put(Param.Vault.value, vaultName)
        })
        assertFalse(result.isError == true, "vault info failed: ${result.text()}")
        assertTrue(result.text().contains(vaultName), "Vault name not found in info output")
    }

    @Test
    fun `workspace list does not error`() = runTest {
        val result = VaultHandler.invoke(makeRequest("obsidian_vault") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "workspace")
        })
        assertFalse(result.isError == true, "workspace list failed: ${result.text()}")
    }

    @Test
    fun `unknown action returns isError true`() = runTest {
        val result = VaultHandler.invoke(makeRequest("obsidian_vault") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "badaction")
        })
        assertTrue(result.isError == true, "Expected isError=true for unknown action")
    }
}
