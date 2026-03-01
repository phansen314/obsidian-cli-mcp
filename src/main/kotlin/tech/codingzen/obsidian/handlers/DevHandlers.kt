package tech.codingzen.obsidian.handlers

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import tech.codingzen.obsidian.tools.*

object DevHandler : ToolHandler {
    override suspend fun invoke(request: CallToolRequest): CallToolResult {
        val params = request.parameters()
        return when (params.str(Param.Action)) {
            "eval" -> runObsidianCli(listOf("eval"), params) {
                keyValue(Param.Code)
            }
            "screenshot" -> runObsidianCli(listOf("dev:screenshot"), params) {
                keyValue(Param.Path)
            }
            "console" -> runObsidianCli(listOf("dev:console"), params) {
                keyValue(Param.Limit)
                keyValue(Param.Level)
            }
            "errors" -> runObsidianCli(listOf("dev:errors"), params)
            "dom" -> runObsidianCli(listOf("dev:dom"), params) {
                keyValue(Param.Selector)
                flag(Param.ShowText)
                flag(Param.Total)
            }
            "css" -> runObsidianCli(listOf("dev:css"), params) {
                keyValue(Param.Selector)
                keyValue(Param.Prop)
            }
            "debug" -> runObsidianCli(listOf("dev:debug"), params) {
                keyValue(Param.State)
            }
            "mobile" -> runObsidianCli(listOf("dev:mobile"), params) {
                keyValue(Param.State)
            }
            "devtools" -> runObsidianCli(listOf("devtools"), params)
            else -> unknownAction(params, "eval, screenshot, console, errors, dom, css, debug, mobile, devtools")
        }
    }
}
