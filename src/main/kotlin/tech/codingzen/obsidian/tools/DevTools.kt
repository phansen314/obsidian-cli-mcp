package tech.codingzen.obsidian.tools

val devDefinitions: Map<ToolName, ToolDefinition> = mapOf(
    ToolName.ObsidianDev to tool(ToolName.ObsidianDev) {
        description = "Dev tools: eval, screenshot, console, errors, dom, css, debug, mobile, devtools."
        string(Param.Action, "eval|screenshot|console|errors|dom|css|debug|mobile|devtools", required = true)
        vault()
        string(Param.Code, "JS code (eval).")
        string(Param.Path, "File path (screenshot).")
        integer(Param.Limit, "Max messages (console).")
        string(Param.Level, "Filter: log, warn, error (console).")
        string(Param.Selector, "CSS selector (dom, css).")
        boolean(Param.ShowText, "Text content (dom).")
        total()
        string(Param.Prop, "CSS property (css).")
        string(Param.State, "on|off (debug, mobile).")
    }
)
