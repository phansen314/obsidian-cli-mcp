package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.TemplatesHandler
import tech.codingzen.obsidian.tools.Param

class OptionalTemplatesIntegrationTest : IntegrationTestBase() {

    @Test
    fun `list templates does not error`() = runTest {
        val result = TemplatesHandler.invoke(makeRequest("obsidian_templates") {
            put(Param.Vault.value, vaultName)
        })
        assertFalse(result.isError == true, "templates list failed: ${result.text()}")
    }
}
