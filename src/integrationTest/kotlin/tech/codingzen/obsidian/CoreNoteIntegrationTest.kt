package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.NoteHandler
import tech.codingzen.obsidian.tools.Param

class CoreNoteIntegrationTest : IntegrationTestBase() {

    private fun request(vararg pairs: Pair<String, Any?>) = makeRequest("obsidian_note") {
        pairs.forEach { (k, v) ->
            when (v) {
                is String -> put(k, v)
                is Boolean -> put(k, v)
            }
        }
        put(Param.Vault.value, vaultName)
    }

    @Test
    fun `create note then read returns content`() = runTest {
        val content = "Hello from integration test"
        val createResult = NoteHandler.invoke(request(
            Param.Action.value to "create",
            Param.Name.value to "test-create",
            Param.Content.value to content,
        ))
        assertFalse(createResult.isError == true, "create failed: ${createResult.text()}")

        settle()

        val readResult = NoteHandler.invoke(request(
            Param.Action.value to "read",
            Param.File.value to "test-create",
        ))
        assertFalse(readResult.isError == true, "read failed: ${readResult.text()}")
        assertTrue(readResult.text().contains(content), "Expected content not found in read result")
    }

    @Test
    fun `append adds text to file`() = runTest {
        NoteHandler.invoke(request(
            Param.Action.value to "create",
            Param.Name.value to "test-append",
            Param.Content.value to "Original",
        ))
        settle()

        val appendResult = NoteHandler.invoke(request(
            Param.Action.value to "append",
            Param.File.value to "test-append",
            Param.Content.value to "Appended line",
        ))
        assertFalse(appendResult.isError == true, "append failed: ${appendResult.text()}")
        settle()

        val readResult = NoteHandler.invoke(request(
            Param.Action.value to "read",
            Param.File.value to "test-append",
        ))
        assertTrue(readResult.text().contains("Appended line"), "Appended text not found")
    }

    @Test
    fun `prepend adds text to top of file`() = runTest {
        NoteHandler.invoke(request(
            Param.Action.value to "create",
            Param.Name.value to "test-prepend",
            Param.Content.value to "Original line",
        ))
        settle()

        val prependResult = NoteHandler.invoke(request(
            Param.Action.value to "prepend",
            Param.File.value to "test-prepend",
            Param.Content.value to "Prepended line",
        ))
        assertFalse(prependResult.isError == true, "prepend failed: ${prependResult.text()}")
        settle()

        val readResult = NoteHandler.invoke(request(
            Param.Action.value to "read",
            Param.File.value to "test-prepend",
        ))
        val text = readResult.text()
        val prependedIdx = text.indexOf("Prepended line")
        val originalIdx = text.indexOf("Original line")
        assertTrue(prependedIdx < originalIdx, "Prepended text should appear before original")
    }

    @Test
    fun `delete makes file unreadable via CLI`() = runTest {
        NoteHandler.invoke(request(
            Param.Action.value to "create",
            Param.Name.value to "test-delete",
            Param.Content.value to "To be deleted",
        ))
        settle()

        val deleteResult = NoteHandler.invoke(request(
            Param.Action.value to "delete",
            Param.File.value to "test-delete",
        ))
        assertFalse(deleteResult.isError == true, "delete failed: ${deleteResult.text()}")
        settle()

        val readResult = NoteHandler.invoke(request(
            Param.Action.value to "read",
            Param.File.value to "test-delete",
        ))
        assertTrue(readResult.isError == true, "Expected CLI error when reading deleted file")
    }

    @Test
    fun `move makes destination readable and source unreadable via CLI`() = runTest {
        NoteHandler.invoke(request(
            Param.Action.value to "create",
            Param.Name.value to "test-move-src",
            Param.Content.value to "Move me",
        ))
        settle()

        val moveResult = NoteHandler.invoke(request(
            Param.Action.value to "move",
            Param.File.value to "test-move-src",
            Param.To.value to "test-move-dst.md",
        ))
        assertFalse(moveResult.isError == true, "move failed: ${moveResult.text()}")
        settle()

        val dstRead = NoteHandler.invoke(request(
            Param.Action.value to "read",
            Param.File.value to "test-move-dst",
        ))
        assertFalse(dstRead.isError == true, "Expected destination to be readable after move")
        assertTrue(dstRead.text().contains("Move me"), "Expected original content in destination")

        val srcRead = NoteHandler.invoke(request(
            Param.Action.value to "read",
            Param.File.value to "test-move-src",
        ))
        assertTrue(srcRead.isError == true, "Expected CLI error when reading moved source")
    }

    @Test
    fun `list returns note names`() = runTest {
        NoteHandler.invoke(request(
            Param.Action.value to "create",
            Param.Name.value to "test-list-note",
            Param.Content.value to "Listed",
        ))
        settle()

        val listResult = NoteHandler.invoke(request(
            Param.Action.value to "list",
        ))
        assertFalse(listResult.isError == true, "list failed: ${listResult.text()}")
        assertTrue(listResult.text().contains("test-list-note"), "Expected note not in list")
    }

    @Test
    fun `outline returns headings`() = runTest {
        val content = "# Heading One\n\nSome text\n\n## Heading Two\n\nMore text"
        NoteHandler.invoke(request(
            Param.Action.value to "create",
            Param.Name.value to "test-outline",
            Param.Content.value to content,
        ))
        settle()

        val outlineResult = NoteHandler.invoke(request(
            Param.Action.value to "outline",
            Param.File.value to "test-outline",
        ))
        assertFalse(outlineResult.isError == true, "outline failed: ${outlineResult.text()}")
        assertTrue(outlineResult.text().contains("Heading One"), "Expected heading not in outline")
    }

    @Test
    fun `daily read does not error`() = runTest {
        val result = makeRequest("obsidian_note") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "read")
            put(Param.Daily.value, true)
        }.let { NoteHandler.invoke(it) }
        // Daily read may or may not have a note today, but should not produce a CLI error
        assertFalse(result.isError == true, "daily read should not produce a CLI error")
    }

    @Test
    fun `unknown action returns isError true`() = runTest {
        val result = NoteHandler.invoke(request(
            Param.Action.value to "nonexistent_action",
        ))
        assertTrue(result.isError == true, "Expected isError=true for unknown action")
    }
}
