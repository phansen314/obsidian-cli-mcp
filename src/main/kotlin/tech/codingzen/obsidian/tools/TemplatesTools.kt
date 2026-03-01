package tech.codingzen.obsidian.tools

val templatesDefinitions: Map<ToolName, ToolDefinition> = mapOf(
    ToolName.ObsidianTemplates to tool(ToolName.ObsidianTemplates) {
        description = "List templates."
        vault()
        format()
    }
)
