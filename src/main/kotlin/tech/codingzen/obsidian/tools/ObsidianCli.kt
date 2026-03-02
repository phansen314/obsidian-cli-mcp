package tech.codingzen.obsidian.tools

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ObsidianCli")

class CliArgs(baseArgs: List<String>, private val params: ParametersAccessor) {
    private val args = baseArgs.toMutableList()

    fun keyValue(name: Param) {
        params.str(name)?.let { args.add("${name.value}=$it") }
    }

    fun flag(name: Param) {
        if (params.bool(name) == true) args.add("--${name.value}")
    }

    fun build(): List<String> = args
}

fun errorResult(msg: String): CallToolResult =
    CallToolResult(content = listOf(TextContent(text = msg)), isError = true)

fun unknownAction(params: ParametersAccessor, expected: String): CallToolResult =
    errorResult("Unknown action '${params.str(Param.Action) ?: "(none)"}'. Expected: $expected.")

suspend fun runObsidianCli(
    command: List<String>,
    params: ParametersAccessor,
    block: CliArgs.() -> Unit = {},
): CallToolResult {
    // The CLI expects: obsidian [vault=<name>] <command> [args...]
    // Vault must be the first positional argument before the command.
    val vaultArgs = params.str(Param.Vault)?.let { listOf("vault=$it") } ?: emptyList()
    val commandArgs = CliArgs(command, params).apply(block).build()
    val fullArgs = vaultArgs + commandArgs
    return withContext(Dispatchers.IO) {
        try {
            val process = ProcessBuilder(listOf("obsidian") + fullArgs)
                .redirectErrorStream(true)
                .start()
            val outputDeferred = async { process.inputStream.bufferedReader().use { it.readText() } }
            val exited = process.waitFor(30, TimeUnit.SECONDS)
            if (!exited) {
                // destroyForcibly() closes the process's stdout pipe, which unblocks
                // the readText() call in outputDeferred — no explicit cancel needed.
                process.destroyForcibly()
                logger.error("Command timed out after 30 seconds: {}", fullArgs)
                errorResult("Command timed out after 30 seconds.")
            } else {
                val output = outputDeferred.await()
                val exitCode = process.exitValue()
                if (exitCode != 0) {
                    logger.error("Command failed (exit {}): {} — {}", exitCode, fullArgs, output)
                }
                CallToolResult(content = listOf(TextContent(text = output)), isError = exitCode != 0)
            }
        } catch (e: IOException) {
            logger.error("Failed to start command {}: {}", fullArgs, e.message, e)
            errorResult("Error: ${e.message ?: e.toString()}")
        }
    }
}
