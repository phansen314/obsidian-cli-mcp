package tech.codingzen.obsidian

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequestParams
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import java.nio.file.Path

@Tag("integration")
abstract class IntegrationTestBase {

    companion object {
        // Absolute path to avoid CWD-relative resolution issues when running from different directories
        val vaultPath: Path = Path.of(System.getProperty("user.dir")).resolve("tmp/test-vault")
        val vaultName: String = "test-vault"
        // 2s gives Obsidian time to process file-system changes before we read them back
        const val SETTLEMENT_MS: Long = 2_000

        @BeforeAll
        @JvmStatic
        fun checkPrerequisites() {
            assumeTrue(isObsidianCliAvailable(), "obsidian CLI not on PATH — skipping integration tests")
            assumeTrue(isObsidianRunning(), "Obsidian is not running — skipping integration tests")
            assumeTrue(isVaultRegistered(), "test-vault not registered in Obsidian — skipping integration tests")
        }

        private fun isObsidianCliAvailable(): Boolean = try {
            ProcessBuilder("obsidian", "--version")
                .redirectErrorStream(true)
                .start()
                .waitFor() == 0
        } catch (_: Exception) { false }

        private fun isObsidianRunning(): Boolean = try {
            ProcessBuilder("pgrep", "-xi", "obsidian")
                .start()
                .waitFor() == 0
        } catch (_: Exception) { false }

        private fun isVaultRegistered(): Boolean =
            vaultPath.toFile().exists() && vaultPath.resolve(".obsidian").toFile().isDirectory
    }

    @BeforeEach
    fun cleanVault() {
        vaultPath.toFile().listFiles()?.forEach { f ->
            when {
                f.isFile -> f.delete()
                // Preserve .obsidian — it holds Obsidian's vault config
                f.isDirectory && f.name != ".obsidian" -> f.deleteRecursively()
            }
        }
    }

    suspend fun settle() = kotlinx.coroutines.delay(SETTLEMENT_MS)
}

fun makeRequest(toolName: String, block: JsonObjectBuilder.() -> Unit = {}): CallToolRequest =
    CallToolRequest(CallToolRequestParams(name = toolName, arguments = buildJsonObject(block)))

fun CallToolResult.text(): String =
    content.filterIsInstance<TextContent>().joinToString("") { it.text }
