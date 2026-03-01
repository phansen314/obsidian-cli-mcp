package tech.codingzen.obsidian

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcpStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.*
import tech.codingzen.obsidian.tools.ToolGroup
import tech.codingzen.obsidian.tools.serverTools
import tech.codingzen.obsidian.tools.toolDefinitions
import tech.codingzen.obsidian.tools.toolGroupings
import tech.codingzen.obsidian.tools.toolHandlers
import tech.codingzen.obsidian.tools.visibleTools

private fun validateTools() {
    val ungrouped = toolDefinitions.keys - toolGroupings.values.flatten().toSet()
    check(ungrouped.isEmpty()) { "Tools missing from toolGroupings: $ungrouped" }
    val missingDefinitions = toolHandlers.keys - toolDefinitions.keys
    check(missingDefinitions.isEmpty()) { "Handlers missing definitions: $missingDefinitions" }
    val missingHandlers = toolDefinitions.keys - toolHandlers.keys
    check(missingHandlers.isEmpty()) { "Definitions missing handlers: $missingHandlers" }
}

private fun enabledGroups(): Set<ToolGroup> =
    ToolGroup.entries.filterTo(mutableSetOf()) { group ->
        val envVar = group.envVar ?: return@filterTo true
        val value = System.getenv(envVar) ?: return@filterTo false
        value == "1" || value.equals("true", ignoreCase = true)
    }

fun main() {
    validateTools()
    val server = Server(
        serverInfo = Implementation(
            name = "obsidian-cli-mcp",
            version = "1.0.0"
        ),
        options = ServerOptions(
            capabilities = ServerCapabilities(
                tools = ServerCapabilities.Tools(listChanged = true)
            )
        )
    )
    val enabledGroups = enabledGroups()
    val definitions = visibleTools(enabledGroups)

    serverTools(server, definitions) {
        // All definition keys have handlers — guaranteed by validateTools() above.
        definitions.keys.forEach { name -> register(name, toolHandlers.getValue(name)) }
    }

    embeddedServer(CIO, host = "127.0.0.1", port = 3000) {
        install(ContentNegotiation) {
            json(McpJson)
        }
        mcpStreamableHttp { server }
    }.start(wait = true)
}
