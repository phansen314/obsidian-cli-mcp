package tech.codingzen.obsidian.tools

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.*
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject

/** Enum of all tool parameter names. The [value] property carries the wire name used in JSON and CLI args. */
enum class Param(val value: String) {
    Action("action"),
    Vault("vault"),
    File("file"),
    Path("path"),
    Format("format"),
    Total("total"),
    Silent("silent"),
    Open("open"),
    Daily("daily"),
    Name("name"),
    Content("content"),
    Template("template"),
    Folder("folder"),
    To("to"),
    Ext("ext"),
    Query("query"),
    Limit("limit"),
    Sort("sort"),
    Counts("counts"),
    Status("status"),
    Line("line"),
    Text("text"),
    ShowText("show-text"),
    Id("id"),
    Version("version"),
    Code("code"),
    Level("level"),
    Selector("selector"),
    Prop("prop"),
    State("state"),
    Value("value"),
}

/** Enum of all registered MCP tool names. The [value] property carries the wire name passed to the SDK. */
enum class ToolName(val value: String) {
    ObsidianNote("obsidian_note"),
    ObsidianSearch("obsidian_search"),
    ObsidianProperty("obsidian_property"),
    ObsidianLinks("obsidian_links"),
    ObsidianTags("obsidian_tags"),
    ObsidianTasks("obsidian_tasks"),
    ObsidianVault("obsidian_vault"),
    ObsidianBookmarks("obsidian_bookmarks"),
    ObsidianTemplates("obsidian_templates"),
    ObsidianPlugin("obsidian_plugin"),
    ObsidianTheme("obsidian_theme"),
    ObsidianSync("obsidian_sync"),
    ObsidianPublish("obsidian_publish"),
    ObsidianBase("obsidian_base"),
    ObsidianDev("obsidian_dev"),
}

/**
 * JSON Schema metadata for a single tool parameter.
 *
 * @property type The JSON Schema type (e.g. `"string"`, `"boolean"`, `"integer"`, `"number"`).
 * @property description Human-readable description surfaced to the MCP client.
 * @property required Whether the parameter must be provided by the caller.
 */
data class ParameterSchema(val type: String, val description: String, val required: Boolean = false)

/**
 * Provides typesafe access to the raw JSON parameters of a [CallToolRequest].
 *
 * Resolves `JsonNull` to Kotlin `null` so callers can use idiomatic null-checks.
 *
 * ```kotlin
 * val params = request.parameters()
 * val vault: String? = params.str(Param.Vault)
 * ```
 */
class ParametersAccessor(private val parameters: JsonObject?) {
  /** Returns the [JsonPrimitive] for [parameter], or `null` if absent, JSON null, or a non-primitive type. */
  operator fun get(parameter: Param): JsonPrimitive? =
    when (val element = parameters?.get(parameter.value)) {
      null, JsonNull -> null
      is JsonPrimitive -> element
      else -> null
    }

  /** Returns the string content for [param], or `null` if absent or JSON null. */
  fun str(param: Param): String? = this[param]?.content

  /** Returns the boolean value for [param], or `null` if absent or JSON null. */
  fun bool(param: Param): Boolean? = this[param]?.booleanOrNull
}

/** Wraps the raw request arguments in a [ParametersAccessor] for typesafe access. */
fun CallToolRequest.parameters() = ParametersAccessor(arguments)

/**
 * Describes an MCP tool: its name, description, and parameter schema.
 *
 * Construct instances using the [tool] DSL builder rather than calling the constructor directly.
 *
 * @property name The unique MCP tool name (e.g. `"read_note"`).
 * @property description Human-readable description surfaced to the MCP client.
 * @property parameters Ordered map of parameter names to their [ParameterSchema].
 */
data class ToolDefinition(
  val name: ToolName,
  val description: String,
  val parameters: Map<Param, ParameterSchema>,
) {
  /** Converts this definition into the MCP SDK's [ToolSchema], including `required` fields. */
  fun toToolSchema(): ToolSchema = ToolSchema(
    properties = buildJsonObject {
      for ((name, schema) in parameters) {
        put(name.value, buildJsonObject {
          put("type", JsonPrimitive(schema.type))
          put("description", JsonPrimitive(schema.description))
        })
      }
    },
    required = parameters.mapNotNull { (k, v) -> k.value.takeIf { v.required } }.ifEmpty { null },
  )
}

/** Shared parameter description constants to reduce schema token footprint. */
object ParamDesc {
    const val VAULT = "Vault name. Default: last focused."
    const val FILE = "File as wikilink (name only, no path/ext)."
    const val PATH = "Path from vault root, e.g. 'folder/note.md'."
    const val FORMAT = "Output: json|text|md|csv|tsv|yaml|paths|tree."
    const val TOTAL = "Count only."
    const val SILENT = "Suppress opening."
    const val OPEN = "Open in Obsidian."
}

/** DSL marker to prevent scope leaking in nested [ToolDefinitionBuilder] blocks. */
@DslMarker
annotation class ToolDsl

/**
 * Builder for constructing a [ToolDefinition] via the [tool] DSL.
 *
 * ```kotlin
 * val readNote = tool(ToolName.ReadNote) {
 *   description = "Read the contents of a note."
 *   string(Param.Vault, "Target vault name.")
 *   string(Param.File, "Target file resolved like a wikilink.", required = true)
 *   boolean(Param.Copy, "Copy output to clipboard.")
 * }
 * ```
 */
@ToolDsl
class ToolDefinitionBuilder(val name: ToolName) {
  /** Human-readable description surfaced to the MCP client. */
  var description: String = ""

  private val parameters = mutableMapOf<Param, ParameterSchema>()

  /** Adds a `string` parameter. */
  fun string(name: Param, description: String, required: Boolean = false) {
    parameters[name] = ParameterSchema("string", description, required)
  }

  /** Adds a `boolean` parameter. */
  fun boolean(name: Param, description: String, required: Boolean = false) {
    parameters[name] = ParameterSchema("boolean", description, required)
  }

  /** Adds an `integer` parameter. */
  fun integer(name: Param, description: String, required: Boolean = false) {
    parameters[name] = ParameterSchema("integer", description, required)
  }

  /** Adds a `number` parameter. */
  fun number(name: Param, description: String, required: Boolean = false) {
    parameters[name] = ParameterSchema("number", description, required)
  }

  /** Adds the shared `vault` parameter. */
  fun vault() = string(Param.Vault, ParamDesc.VAULT)

  /** Adds the shared `file` and `path` parameters. */
  fun fileOrPath() {
    string(Param.File, ParamDesc.FILE)
    string(Param.Path, ParamDesc.PATH)
  }

  /** Adds the shared `format` parameter. */
  fun format() = string(Param.Format, ParamDesc.FORMAT)

  /** Adds the shared `total` parameter. */
  fun total() = boolean(Param.Total, ParamDesc.TOTAL)

  /** Adds the shared `silent` parameter. */
  fun silent() = boolean(Param.Silent, ParamDesc.SILENT)

  /** Adds the shared `open` parameter. */
  fun open() = boolean(Param.Open, ParamDesc.OPEN)

  /** Builds the [ToolDefinition] from the current builder state. */
  fun build() = ToolDefinition(name, description, parameters.toMap())
}

/**
 * DSL entry point for creating a [ToolDefinition].
 *
 * ```kotlin
 * val myTool = tool(ToolName.MyTool) {
 *   description = "Does something useful."
 *   string(Param.Input, "The input value.", required = true)
 * }
 * ```
 */
fun tool(name: ToolName, block: ToolDefinitionBuilder.() -> Unit): ToolDefinition =
  ToolDefinitionBuilder(name).apply(block).build()

/**
 * Registers a [ToolDefinition] with this MCP [Server].
 *
 * Converts the definition's parameters into a [ToolSchema] and delegates to the SDK's
 * [Server.addTool] method.
 *
 * @param definition The tool to register.
 * @param handler Suspend function invoked when the tool is called by a client.
 */
fun Server.addTool(
  definition: ToolDefinition,
  handler: suspend (CallToolRequest) -> CallToolResult,
) {
  addTool(
    name = definition.name.value,
    description = definition.description,
    inputSchema = definition.toToolSchema(),
    handler = handler,
  )
}

/** Suspend function that handles an MCP tool call. */
interface ToolHandler {
    suspend fun invoke(request: CallToolRequest): CallToolResult
}

/**
 * DSL scope for registering tools on a [Server].
 *
 * Inside a [serverTools] block, use [register] to look up a tool's [ToolDefinition]
 * and wire it to a handler:
 *
 * ```kotlin
 * serverTools(server, definitions) {
 *   register(ToolName.MyTool, MyHandler)
 * }
 * ```
 */
@ToolDsl
class ServerToolDsl(
  private val server: Server,
  private val definitions: Map<ToolName, ToolDefinition>,
) {

  /** Looks up the [ToolDefinition] for [name] and registers it with the given [handler]. */
  fun register(name: ToolName, handler: ToolHandler) {
    val definition = definitions.getValue(name)
    server.addTool(definition) { request -> handler.invoke(request) }
  }
}

/**
 * DSL entry point for registering multiple tools on a [Server].
 *
 * Use the [block] to explicitly register each tool via [ServerToolDsl.register].
 *
 * @param server The MCP server to register tools on.
 * @param definitions Tool definitions map to register (pass a filtered set from [visibleTools]).
 * @param block Configuration block executed in the [ServerToolDsl] scope.
 */
fun serverTools(
  server: Server,
  definitions: Map<ToolName, ToolDefinition>,
  block: ServerToolDsl.() -> Unit,
) {
  ServerToolDsl(server, definitions).apply(block)
}
