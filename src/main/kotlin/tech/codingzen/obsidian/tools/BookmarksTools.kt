package tech.codingzen.obsidian.tools

val bookmarksDefinitions: Map<ToolName, ToolDefinition> = mapOf(
    ToolName.ObsidianBookmarks to tool(ToolName.ObsidianBookmarks) {
        description = "List bookmarks."
        vault()
        format()
    }
)
