package tech.codingzen.obsidian.handlers

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import tech.codingzen.obsidian.tools.*

object TemplatesHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult =
        runObsidianCli(listOf("templates"), request.parameters()) {
            keyValue(Param.Format)
        }
}

