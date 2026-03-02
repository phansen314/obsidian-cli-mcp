# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin MCP (Model Context Protocol) server that exposes tools for interacting with Obsidian vaults via the Obsidian CLI and executing shell commands. Runs as an HTTP server on `127.0.0.1:3000` using Ktor CIO with Streamable HTTP transport.

## Build Commands

```bash
./gradlew build          # Build the project
./gradlew shadowJar      # Create fat JAR (build/libs/my-mcp-server.jar)
./gradlew run            # Run the server
./gradlew test           # Run unit tests (JUnit Platform)
./gradlew integrationTest # Run integration tests (requires Obsidian running)
```

## Architecture

**Entry point**: `Main.kt` — validates tool consistency (`validateTools()`), determines enabled tool groups from environment variables (`enabledGroups()`), registers tools with the MCP `Server`, and starts the Ktor HTTP server with `mcpStreamableHttp`.

**Tool system** has three layers, split across per-feature files:

1. **DSL infrastructure** (`tools/ToolDefinition.kt`) — `tool { }` builder creates `ToolDefinition` instances with typed parameter schemas (string, boolean, integer, number). `ParametersAccessor` wraps raw JSON args for typesafe access. `ServerToolDsl` provides `register()` for wiring tools to a server. `ToolName` enum lists all registered tool names. `Param` enum defines all parameter wire names.

2. **Tool definitions** (`tools/*Tools.kt`) — Each feature group has a `*Tools.kt` file (e.g., `CoreTools.kt`, `BookmarksTools.kt`) defining tool definitions using the `tool { }` DSL. `ToolDefinitions.kt` aggregates them into the `toolDefinitions` map and defines `toolGroupings` (mapping `ToolGroup` → sets of `ToolName`) and `visibleTools()` to filter by enabled groups.

3. **Tool handlers** (`handlers/*Handlers.kt`) — Each feature group has a `*Handlers.kt` file implementing handlers. `ToolHandlers.kt` aggregates them into the `toolHandlers` map. Each handler is a suspend function using `getParameters(request)` for parameter access, shelling out via `runObsidianCli` / `ProcessBuilder`.

**Tool groups**: 9 groups gated by environment variables — Core (always on), Bookmarks, Templates, Plugins, Themes, Sync, Publish, Bases, Dev. Optional groups are enabled by setting their env var to `1` (e.g., `OBSIDIAN_BOOKMARKS=1`). The `integrationTest` task enables all groups.

**Adding a new tool**:
1. Add the tool name to the `ToolName` enum in `ToolDefinition.kt`
2. Add the parameter names to the `Param` enum if needed
3. Define the tool in the appropriate `*Tools.kt` file and add it to that file's definitions map
4. Add it to the `toolGroupings` map in `ToolDefinitions.kt`
5. Implement the handler in the appropriate `*Handlers.kt` file and add it to `toolHandlers` in `ToolHandlers.kt`

## Tech Stack

- Kotlin 2.3.10, JVM 21
- MCP Kotlin SDK 0.8.4
- Ktor 3.2.3 (CIO engine, SSE, ContentNegotiation)
- Kotlinx Serialization JSON 1.7.3
- Shadow plugin for fat JAR packaging

## Testing

- **Unit tests**: `src/test/kotlin/` — 4 test classes covering `ToolDefinition`, `ToolDefinitions`, `ParametersAccessor`, and `CliArgs`
- **Integration tests**: `src/integrationTest/kotlin/` — `IntegrationTestBase` uses `assumeTrue` to skip if Obsidian not running. Core tests (`Core*IntegrationTest`) and optional group tests (`Optional*IntegrationTest`).
- `test-vault/.obsidian/app.json` committed as `{}` — open in Obsidian once to register the vault
