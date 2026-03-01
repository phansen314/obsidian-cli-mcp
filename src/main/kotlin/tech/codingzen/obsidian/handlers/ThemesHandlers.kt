package tech.codingzen.obsidian.handlers

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import tech.codingzen.obsidian.tools.*

object ThemeHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action) ?: "list") {
            "list" -> runObsidianCli(listOf("themes"), params) {
                keyValue(Param.Format)
            }
            "set" -> runObsidianCli(listOf("theme:set"), params) {
                keyValue(Param.Name)
            }
            "snippets" -> runObsidianCli(listOf("snippets"), params) {
                keyValue(Param.Format)
            }
            else -> unknownAction(params, "list, set, snippets")
        }
    }
}
