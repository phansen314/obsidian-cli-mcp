package tech.codingzen.obsidian.tools

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ToolDefinitionsTest {

    @Test
    fun `toolDefinitions contains an entry for every ToolName`() {
        assertEquals(ToolName.entries.size, toolDefinitions.size)
        for (name in ToolName.entries) {
            assertTrue(toolDefinitions.containsKey(name), "Missing definition for $name")
        }
    }

    @Test
    fun `every ToolName is covered by exactly one toolGrouping`() {
        val groupedNames = toolGroupings.values.flatten().toSet()
        for (name in ToolName.entries) {
            assertTrue(name in groupedNames, "$name is not in any toolGrouping")
        }
        // No duplicates across groups
        val allWithDuplicates = toolGroupings.values.flatten()
        val duplicates = allWithDuplicates.groupBy { it }.filter { it.value.size > 1 }.keys
        assertTrue(duplicates.isEmpty(), "Duplicate tools in groupings: $duplicates")
    }

    @Test
    fun `toolHandlers has a handler for every toolDefinition`() {
        for (name in toolDefinitions.keys) {
            assertTrue(toolHandlers.containsKey(name), "Missing handler for $name")
        }
    }

    @Test
    fun `toolDefinitions has a definition for every toolHandler`() {
        for (name in toolHandlers.keys) {
            assertTrue(toolDefinitions.containsKey(name), "Handler exists but no definition for $name")
        }
    }

    @Test
    fun `visibleTools with Core only returns all core tools`() {
        val visible = visibleTools(setOf(ToolGroup.Core))
        assertEquals(toolGroupings[ToolGroup.Core]!!.size, visible.size)
        assertTrue(ToolName.ObsidianNote in visible)
        assertTrue(ToolName.ObsidianSearch in visible)
        assertTrue(ToolName.ObsidianProperty in visible)
        assertTrue(ToolName.ObsidianLinks in visible)
        assertTrue(ToolName.ObsidianTags in visible)
        assertTrue(ToolName.ObsidianTasks in visible)
        assertTrue(ToolName.ObsidianVault in visible)
    }

    @Test
    fun `visibleTools with all groups returns all tools`() {
        val visible = visibleTools(ToolGroup.entries.toSet())
        assertEquals(toolDefinitions.size, visible.size)
    }

    @Test
    fun `visibleTools with empty set returns empty map`() {
        val visible = visibleTools(emptySet())
        assertTrue(visible.isEmpty())
    }

    @Test
    fun `visibleTools with optional group includes that group's tools`() {
        val visible = visibleTools(setOf(ToolGroup.Bookmarks))
        assertTrue(ToolName.ObsidianBookmarks in visible)
        assertFalse(ToolName.ObsidianNote in visible)
    }

    @Test
    fun `toolGroupings covers all ToolGroup entries`() {
        for (group in ToolGroup.entries) {
            assertTrue(toolGroupings.containsKey(group), "toolGroupings missing group $group")
        }
    }
}
