package tech.codingzen.obsidian

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tech.codingzen.obsidian.handlers.NoteHandler
import tech.codingzen.obsidian.handlers.TasksHandler
import tech.codingzen.obsidian.tools.Param

class CoreTasksIntegrationTest : IntegrationTestBase() {

    private val taskText = "Buy groceries"

    private suspend fun createNoteWithTask() {
        NoteHandler.invoke(makeRequest("obsidian_note") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "create")
            put(Param.Name.value, "test-tasks-note")
            put(Param.Content.value, "- [ ] $taskText\n- [x] Already done\n")
        })
        settle()
    }

    @Test
    fun `list tasks from note contains expected task text`() = runTest {
        createNoteWithTask()

        val result = TasksHandler.invoke(makeRequest("obsidian_tasks") {
            put(Param.Vault.value, vaultName)
            put(Param.File.value, "test-tasks-note")
        })
        assertFalse(result.isError == true, "tasks list failed: ${result.text()}")
        assertTrue(result.text().contains(taskText), "Expected task not found in result")
    }

    @Test
    fun `filter by status=todo returns only unchecked tasks`() = runTest {
        createNoteWithTask()

        val result = TasksHandler.invoke(makeRequest("obsidian_tasks") {
            put(Param.Vault.value, vaultName)
            put(Param.File.value, "test-tasks-note")
            put(Param.Status.value, "todo")
        })
        assertFalse(result.isError == true, "tasks filter failed: ${result.text()}")
        assertTrue(result.text().contains(taskText),
            "Expected todo task '$taskText' in filtered result. Got: ${result.text()}")
    }

    @Test
    fun `toggle task by text moves it to done status via CLI`() = runTest {
        createNoteWithTask()

        val toggleResult = TasksHandler.invoke(makeRequest("obsidian_tasks") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "toggle")
            put(Param.File.value, "test-tasks-note")
            put(Param.Text.value, taskText)
        })
        assertFalse(toggleResult.isError == true, "toggle failed: ${toggleResult.text()}")
        settle()

        val doneResult = TasksHandler.invoke(makeRequest("obsidian_tasks") {
            put(Param.Vault.value, vaultName)
            put(Param.File.value, "test-tasks-note")
            put(Param.Status.value, "done")
        })
        assertFalse(doneResult.isError == true, "tasks status=done query failed: ${doneResult.text()}")
        assertTrue(doneResult.text().contains(taskText),
            "Expected toggled task '$taskText' in done-status results. Got: ${doneResult.text()}")
    }

    @Test
    fun `unknown action returns isError true`() = runTest {
        val result = TasksHandler.invoke(makeRequest("obsidian_tasks") {
            put(Param.Vault.value, vaultName)
            put(Param.Action.value, "badaction")
        })
        assertTrue(result.isError == true, "Expected isError=true for unknown action")
    }
}
