package tech.codingzen.obsidian.handlers

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import tech.codingzen.obsidian.tools.*

object BaseHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action)) {
            "list" -> runObsidianCli(listOf("bases"), params) {
                keyValue(Param.Format)
            }
            "query" -> runObsidianCli(listOf("base:query"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Format)
            }
            else -> unknownAction(params, "list, query")
        }
    }
}
