package tech.codingzen.obsidian.tools

val coreDefinitions: Map<ToolName, ToolDefinition> = listOf(
    tool(ToolName.ObsidianNote) {
        description = "Notes: read, create, append, prepend, open, move, delete, list, outline. daily=true targets today's note."
        string(Param.Action, "read|create|append|prepend|open|move|delete|list|outline", required = true)
        vault()
        fileOrPath()
        boolean(Param.Daily, "Target today's daily note.")
        string(Param.Name, "Note name (create).")
        string(Param.Content, "Body (create/append/prepend).")
        string(Param.Template, "Template (create).")
        string(Param.Folder, "Folder (create, list).")
        string(Param.To, "Destination path (move).")
        string(Param.Ext, "Extension filter (list).")
        open()
        silent()
        format()
        total()
    },
    tool(ToolName.ObsidianSearch) {
        description = "Search vault notes. Operators: path:, file:, tag:, line:, section:."
        vault()
        string(Param.Query, "Search query.", required = true)
        integer(Param.Limit, "Max results.")
        format()
        boolean(Param.Open, "Open match instead of listing.")
    },
    tool(ToolName.ObsidianProperty) {
        description = "Frontmatter properties: get (default), set, remove."
        vault()
        fileOrPath()
        string(Param.Action, "get (default)|set|remove.")
        string(Param.Name, "Name (set/remove).")
        string(Param.Value, "Value (set).")
        format()
    },
    tool(ToolName.ObsidianLinks) {
        description = "Note links: outgoing (default), backlinks, unresolved, orphans."
        vault()
        fileOrPath()
        string(Param.Action, "outgoing (default)|backlinks|unresolved|orphans.")
        format()
        total()
    },
    tool(ToolName.ObsidianTags) {
        description = "Tags. List all or filter by name."
        vault()
        string(Param.Name, "Tag name (with or without #).")
        string(Param.Sort, "Sort: name|count.")
        boolean(Param.Counts, "Show counts.")
        format()
        total()
    },
    tool(ToolName.ObsidianTasks) {
        description = "Tasks: list (default), toggle."
        vault()
        fileOrPath()
        string(Param.Action, "list (default)|toggle.")
        string(Param.Status, "Filter: todo|done|daily.")
        integer(Param.Line, "Line number (toggle).")
        string(Param.Text, "Task text (toggle).")
        format()
        total()
    },
    tool(ToolName.ObsidianVault) {
        description = "Vault: info (default) returns name/path/stats. workspace lists workspaces."
        vault()
        string(Param.Action, "info (default)|workspace.")
    },
).associateBy { it.name }
