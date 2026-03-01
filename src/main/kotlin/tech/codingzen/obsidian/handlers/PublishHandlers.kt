package tech.codingzen.obsidian.handlers

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import tech.codingzen.obsidian.tools.*

object PublishHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action)) {
            "list" -> runObsidianCli(listOf("publish:list"), params) {
                keyValue(Param.Format)
            }
            "add" -> runObsidianCli(listOf("publish:add"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
            }
            "remove" -> runObsidianCli(listOf("publish:remove"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
            }
            else -> unknownAction(params, "list, add, remove")
        }
    }
}
