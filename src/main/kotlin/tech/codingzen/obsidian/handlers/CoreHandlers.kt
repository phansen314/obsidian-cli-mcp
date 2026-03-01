package tech.codingzen.obsidian.handlers

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import tech.codingzen.obsidian.tools.*

object NoteHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return if (params.bool(Param.Daily) == true) {
            handleDailyNote(params)
        } else {
            handleRegularNote(params)
        }
    }

    private suspend fun handleDailyNote(params: ParametersAccessor): CallToolResult =
        when (params.str(Param.Action)) {
            "open" -> runObsidianCli(listOf("daily"), params) {
                flag(Param.Open)
                flag(Param.Silent)
            }
            "read" -> runObsidianCli(listOf("daily:read"), params)
            "append" -> runObsidianCli(listOf("daily:append"), params) {
                keyValue(Param.Content)
                flag(Param.Silent)
            }
            "prepend" -> runObsidianCli(listOf("daily:prepend"), params) {
                keyValue(Param.Content)
                flag(Param.Silent)
            }
            else -> unknownAction(params, "open, read, append, prepend")
        }

    private suspend fun handleRegularNote(params: ParametersAccessor): CallToolResult =
        when (params.str(Param.Action)) {
            "read" -> runObsidianCli(listOf("read"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
            }
            "create" -> runObsidianCli(listOf("create"), params) {
                keyValue(Param.Name)
                keyValue(Param.Content)
                keyValue(Param.Template)
                keyValue(Param.Folder)
                flag(Param.Open)
                flag(Param.Silent)
            }
            "append" -> runObsidianCli(listOf("append"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Content)
                flag(Param.Silent)
            }
            "prepend" -> runObsidianCli(listOf("prepend"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Content)
                flag(Param.Silent)
            }
            "open" -> runObsidianCli(listOf("open"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
            }
            "move" -> runObsidianCli(listOf("move"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.To)
            }
            "delete" -> runObsidianCli(listOf("delete"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
            }
            "list" -> runObsidianCli(listOf("files"), params) {
                keyValue(Param.Folder)
                keyValue(Param.Ext)
                keyValue(Param.Format)
                flag(Param.Total)
            }
            "outline" -> runObsidianCli(listOf("outline"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Format)
            }
            else -> unknownAction(params, "read, create, append, prepend, open, move, delete, list, outline")
        }
}

object SearchHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return if (params.bool(Param.Open) == true) {
            runObsidianCli(listOf("search:open"), params) {
                keyValue(Param.Query)
            }
        } else {
            runObsidianCli(listOf("search"), params) {
                keyValue(Param.Query)
                keyValue(Param.Limit)
                keyValue(Param.Format)
            }
        }
    }
}

object PropertyHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action) ?: "get") {
            "get" -> runObsidianCli(listOf("properties"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Format)
            }
            "set" -> runObsidianCli(listOf("property:set"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Name)
                keyValue(Param.Value)
            }
            "remove" -> runObsidianCli(listOf("property:remove"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Name)
            }
            else -> unknownAction(params, "get, set, remove")
        }
    }
}

object LinksHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action) ?: "outgoing") {
            "outgoing" -> runObsidianCli(listOf("links"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Format)
                flag(Param.Total)
            }
            "backlinks" -> runObsidianCli(listOf("backlinks"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Format)
                flag(Param.Total)
            }
            "unresolved" -> runObsidianCli(listOf("unresolved"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Format)
                flag(Param.Total)
            }
            "orphans" -> runObsidianCli(listOf("orphans"), params) {
                keyValue(Param.Format)
                flag(Param.Total)
            }
            else -> unknownAction(params, "outgoing, backlinks, unresolved, orphans")
        }
    }
}

// Dispatches on presence of `name` rather than `action` to match CLI behaviour:
// `obsidian tag <name>` queries a single tag; `obsidian tags` lists all.
object TagsHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return if (params.str(Param.Name) != null) {
            runObsidianCli(listOf("tag"), params) {
                keyValue(Param.Name)
                keyValue(Param.Format)
                flag(Param.Total)
            }
        } else {
            runObsidianCli(listOf("tags"), params) {
                keyValue(Param.Sort)
                flag(Param.Counts)
                keyValue(Param.Format)
                flag(Param.Total)
            }
        }
    }
}

object TasksHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action) ?: "list") {
            "list" -> runObsidianCli(listOf("tasks"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Status)
                keyValue(Param.Format)
                flag(Param.Total)
            }
            "toggle" -> runObsidianCli(listOf("task"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Line)
                keyValue(Param.Text)
            }
            else -> unknownAction(params, "list, toggle")
        }
    }
}

object VaultHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action) ?: "info") {
            "info" -> runObsidianCli(listOf("vault"), params)
            "workspace" -> runObsidianCli(listOf("workspace"), params)
            else -> unknownAction(params, "info, workspace")
        }
    }
}
