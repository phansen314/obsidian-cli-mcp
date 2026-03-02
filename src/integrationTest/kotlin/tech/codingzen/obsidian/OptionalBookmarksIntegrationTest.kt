package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.BookmarksHandler
import tech.codingzen.obsidian.tools.Param

class OptionalBookmarksIntegrationTest : IntegrationTestBase() {

    @Test
    fun `list bookmarks does not error`() = runTest {
        val result = BookmarksHandler.invoke(makeRequest("obsidian_bookmarks") {
            put(Param.Vault.value, vaultName)
        })
        assertFalse(result.isError == true, "bookmarks list failed: ${result.text()}")
    }

    @Test
    fun `list bookmarks with json format does not error`() = runTest {
        val result = BookmarksHandler.invoke(makeRequest("obsidian_bookmarks") {
            put(Param.Vault.value, vaultName)
            put(Param.Format.value, "json")
        })
        assertFalse(result.isError == true, "bookmarks json format failed: ${result.text()}")
        val trimmed = result.text().trimStart()
        assertTrue(trimmed.startsWith("[") || trimmed.startsWith("{"),
            "Expected JSON output for format=json. Got: ${result.text()}")
    }
}
