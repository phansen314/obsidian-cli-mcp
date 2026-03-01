package tech.codingzen.obsidian.handlers

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import tech.codingzen.obsidian.tools.*

object BookmarksHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult =
        runObsidianCli(listOf("bookmarks"), request.parameters()) {
            keyValue(Param.Format)
        }
}

