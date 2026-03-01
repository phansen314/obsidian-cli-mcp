# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin MCP (Model Context Protocol) server that exposes tools for interacting with Obsidian vaults via the Obsidian CLI and executing shell commands. Runs as an HTTP server on `127.0.0.1:3000` using Ktor CIO with Streamable HTTP transport.

## Build Commands

```bash
./gradlew build          # Build the project
./gradlew shadowJar      # Create fat JAR (build/libs/my-mcp-server.jar)
./gradlew run            # Run the server
./gradlew test           # Run tests (JUnit Platform)
```

## Architecture

**Entry point**: `Main.kt` — creates the MCP `Server`, registers tools, and starts the Ktor HTTP server with `mcpStreamableHttp`.

**Tool system** (`tools/` package) has three layers:

1. **ToolDefinition.kt** — DSL infrastructure for defining tools. `tool { }` builder creates `ToolDefinition` instances with typed parameter schemas (string, boolean, integer, number). `ParametersAccessor` wraps raw JSON args for typesafe access. `ServerToolDsl` provides `register()` and `handledBy` infix for wiring tools to a server.

2. **ToolDefinitions.kt** — Registry (`toolDefinitions` map) of all tool definitions using the `tool { }` DSL. Add new tool definitions here.

3. **ToolHandlers.kt** — Registry (`toolHandlers` map) of handler implementations. Each handler is a `ToolHandler` (suspend function with `ToolDefinition` as receiver), uses `getParameters(request)` for parameter access, and shells out via `ProcessBuilder`.

**Adding a new tool**: Define it in `ToolDefinitions.kt`, implement the handler in `ToolHandlers.kt` with a matching key. Both maps are iterated in `Main.kt` via `serverTools(server) { toolHandlers.forEach { register(name, handler) } }`.

## Tech Stack

- Kotlin 2.3.10, JVM 21
- MCP Kotlin SDK 0.8.4
- Ktor 3.2.3 (CIO engine, SSE, ContentNegotiation)
- Kotlinx Serialization JSON 1.7.3
- Shadow plugin for fat JAR packaging
