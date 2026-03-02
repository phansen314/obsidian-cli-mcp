package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.NoteHandler
import tech.codingzen.obsidian.handlers.SearchHandler
import tech.codingzen.obsidian.tools.Param

class CoreSearchIntegrationTest : IntegrationTestBase() {

    @Test
    fun `search for content in created note returns note name`() = runTest {
        val uniqueToken = "searchtoken_xq9z"
        NoteHandler.invoke(makeRequest("obsidian_note") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "create")
            put(Param.Name.value, "test-search-note")
            put(Param.Content.value, "Contains $uniqueToken here")
        })
        settle()

        val result = SearchHandler.invoke(makeRequest("obsidian_search") {
            put(Param.Vault.value, vaultName)
            put(Param.Query.value, uniqueToken)
        })
        assertFalse(result.isError == true, "search failed: ${result.text()}")
        assertTrue(result.text().contains("test-search-note"),
            "Expected note name 'test-search-note' in search results. Got: ${result.text()}")
    }

    @Test
    fun `limit param is accepted without error`() = runTest {
        // Smoke test: verifies the limit parameter is passed without causing a CLI error.
        // Enforcing the actual limit would require knowing how many results exist in the vault.
        val result = SearchHandler.invoke(makeRequest("obsidian_search") {
            put(Param.Vault.value, vaultName)
            put(Param.Query.value, "the")
            put(Param.Limit.value, 5)
        })
        assertFalse(result.isError == true, "search with limit failed: ${result.text()}")
    }

    @Test
    fun `open=true dispatches without error`() = runTest {
        val result = SearchHandler.invoke(makeRequest("obsidian_search") {
            put(Param.Vault.value, vaultName)
            put(Param.Query.value, "test")
            put(Param.Open.value, true)
        })
        // open dispatches a UI action; we just verify no exception and not an IO error
        assertFalse(result.isError == true, "open=true should dispatch without a CLI error")
    }
}
