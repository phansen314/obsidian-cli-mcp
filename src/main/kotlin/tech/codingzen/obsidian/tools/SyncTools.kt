package tech.codingzen.obsidian.tools

val syncDefinitions: Map<ToolName, ToolDefinition> = mapOf(
    ToolName.ObsidianSync to tool(ToolName.ObsidianSync) {
        description = "Sync: status, history, restore."
        vault()
        string(Param.Action, "status|history|restore", required = true)
        fileOrPath()
        string(Param.Version, "Version ID (restore).")
        format()
    }
)
