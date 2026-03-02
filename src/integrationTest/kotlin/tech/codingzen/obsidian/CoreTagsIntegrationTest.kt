package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.NoteHandler
import tech.codingzen.obsidian.handlers.TagsHandler
import tech.codingzen.obsidian.tools.Param

class CoreTagsIntegrationTest : IntegrationTestBase() {

    @Test
    fun `list all tags does not error`() = runTest {
        val result = TagsHandler.invoke(makeRequest("obsidian_tags") {
            put(Param.Vault.value, vaultName)
        })
        assertFalse(result.isError == true, "tags list failed: ${result.text()}")
    }

    @Test
    fun `filter by name returns note containing the tag`() = runTest {
        NoteHandler.invoke(makeRequest("obsidian_note") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "create")
            put(Param.Name.value, "test-tags-note")
            put(Param.Content.value, "#mytag content here")
        })
        settle()

        val result = TagsHandler.invoke(makeRequest("obsidian_tags") {
            put(Param.Vault.value, vaultName)
            put(Param.Name.value, "mytag")
        })
        assertFalse(result.isError == true, "tag filter failed: ${result.text()}")
        assertTrue(result.text().contains("test-tags-note"),
            "Expected note 'test-tags-note' in tag filter result. Got: ${result.text()}")
    }

    @Test
    fun `list with counts does not error`() = runTest {
        val result = TagsHandler.invoke(makeRequest("obsidian_tags") {
            put(Param.Vault.value, vaultName)
            put(Param.Counts.value, true)
        })
        assertFalse(result.isError == true, "tags with counts failed: ${result.text()}")
    }
}
