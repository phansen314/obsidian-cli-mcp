package tech.codingzen.obsidian.tools

val publishDefinitions: Map<ToolName, ToolDefinition> = mapOf(
    ToolName.ObsidianPublish to tool(ToolName.ObsidianPublish) {
        description = "Publish: list, add, remove."
        vault()
        string(Param.Action, "list|add|remove", required = true)
        fileOrPath()
        format()
    }
)
