package tech.codingzen.obsidian.tools

val basesDefinitions: Map<ToolName, ToolDefinition> = mapOf(
    ToolName.ObsidianBase to tool(ToolName.ObsidianBase) {
        description = "Bases (database views): list, query."
        vault()
        string(Param.Action, "list|query", required = true)
        fileOrPath()
        format()
    }
)
