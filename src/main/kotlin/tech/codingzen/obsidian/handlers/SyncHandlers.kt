package tech.codingzen.obsidian.handlers

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import tech.codingzen.obsidian.tools.*

object SyncHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action)) {
            "status" -> runObsidianCli(listOf("sync:status"), params) {
                keyValue(Param.Format)
            }
            "history" -> runObsidianCli(listOf("sync:history"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Format)
            }
            "restore" -> runObsidianCli(listOf("sync:restore"), params) {
                keyValue(Param.File)
                keyValue(Param.Path)
                keyValue(Param.Version)
            }
            else -> unknownAction(params, "status, history, restore")
        }
    }
}
