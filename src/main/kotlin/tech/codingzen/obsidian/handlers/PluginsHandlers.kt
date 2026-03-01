package tech.codingzen.obsidian.handlers

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import tech.codingzen.obsidian.tools.*

object PluginHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action)) {
            "list" -> runObsidianCli(listOf("plugins"), params) {
                keyValue(Param.Format)
            }
            "versions" -> runObsidianCli(listOf("plugins", "versions"), params)
            "enable" -> runObsidianCli(listOf("plugin:enable"), params) {
                keyValue(Param.Id)
            }
            "disable" -> runObsidianCli(listOf("plugin:disable"), params) {
                keyValue(Param.Id)
            }
            "reload" -> runObsidianCli(listOf("plugin:reload"), params) {
                keyValue(Param.Id)
            }
            else -> unknownAction(params, "list, versions, enable, disable, reload")
        }
    }
}
