package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.LinksHandler
import tech.codingzen.obsidian.handlers.NoteHandler
import tech.codingzen.obsidian.tools.Param

class CoreLinksIntegrationTest : IntegrationTestBase() {

    @Test
    fun `outgoing wikilinks returns target name`() = runTest {
        NoteHandler.invoke(makeRequest("obsidian_note") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "create")
            put(Param.Name.value, "test-links-outgoing")
            put(Param.Content.value, "See [[other-note]] for details.")
        })
        settle()

        val result = LinksHandler.invoke(makeRequest("obsidian_links") {
            put(Param.Vault.value, vaultName)
            put(Param.File.value, "test-links-outgoing")
        })
        assertFalse(result.isError == true, "outgoing links failed: ${result.text()}")
        assertTrue(result.text().contains("other-note"),
            "Expected 'other-note' in outgoing links output. Got: ${result.text()}")
    }

    @Test
    fun `backlinks to target contains source note name`() = runTest {
        NoteHandler.invoke(makeRequest("obsidian_note") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "create")
            put(Param.Name.value, "test-links-target")
            put(Param.Content.value, "I am the target.")
        })
        NoteHandler.invoke(makeRequest("obsidian_note") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "create")
            put(Param.Name.value, "test-links-source")
            put(Param.Content.value, "Link to [[test-links-target]].")
        })
        settle()

        val result = LinksHandler.invoke(makeRequest("obsidian_links") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "backlinks")
            put(Param.File.value, "test-links-target")
        })
        assertFalse(result.isError == true, "backlinks failed: ${result.text()}")
        assertTrue(result.text().contains("test-links-source"),
            "Expected backlink source 'test-links-source' in result. Got: ${result.text()}")
    }

    @Test
    fun `orphans list contains unlinked note`() = runTest {
        NoteHandler.invoke(makeRequest("obsidian_note") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "create")
            put(Param.Name.value, "test-links-orphan")
            put(Param.Content.value, "I have no links pointing to me.")
        })
        settle()

        val result = LinksHandler.invoke(makeRequest("obsidian_links") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "orphans")
        })
        assertFalse(result.isError == true, "orphans failed: ${result.text()}")
        assertTrue(result.text().contains("test-links-orphan"),
            "Expected orphan note 'test-links-orphan' in result. Got: ${result.text()}")
    }

    @Test
    fun `unresolved dangling links contains target name`() = runTest {
        NoteHandler.invoke(makeRequest("obsidian_note") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "create")
            put(Param.Name.value, "test-links-dangling")
            put(Param.Content.value, "Link to [[nonexistent-note-xyz]].")
        })
        settle()

        val result = LinksHandler.invoke(makeRequest("obsidian_links") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "unresolved")
            put(Param.File.value, "test-links-dangling")
        })
        assertFalse(result.isError == true, "unresolved failed: ${result.text()}")
        assertTrue(result.text().contains("nonexistent-note-xyz"),
            "Expected dangling link target 'nonexistent-note-xyz' in result. Got: ${result.text()}")
    }

    @Test
    fun `unknown action returns isError true`() = runTest {
        val result = LinksHandler.invoke(makeRequest("obsidian_links") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "badaction")
        })
        assertTrue(result.isError == true, "Expected isError=true for unknown action")
    }
}
