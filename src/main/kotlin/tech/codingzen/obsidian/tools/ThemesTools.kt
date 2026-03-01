package tech.codingzen.obsidian.tools

val themesDefinitions: Map<ToolName, ToolDefinition> = mapOf(
    ToolName.ObsidianTheme to tool(ToolName.ObsidianTheme) {
        description = "Themes: list (default), set, snippets."
        string(Param.Action, "list (default)|set|snippets.")
        vault()
        string(Param.Name, "Theme name (set).")
        format()
    }
)
