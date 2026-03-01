package tech.codingzen.obsidian.tools

val pluginsDefinitions: Map<ToolName, ToolDefinition> = mapOf(
    ToolName.ObsidianPlugin to tool(ToolName.ObsidianPlugin) {
        description = "Plugins: list, versions, enable, disable, reload."
        vault()
        string(Param.Action, "list|versions|enable|disable|reload", required = true)
        string(Param.Id, "Plugin ID.")
        format()
    }
)
