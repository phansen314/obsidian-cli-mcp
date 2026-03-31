#!/usr/bin/env bash
set -euo pipefail

# Migrate from the old Kotlin MCP server to the Claude Code plugin.
# Stops the systemd service, removes installed files, and cleans up
# the MCP server entry from Claude Code settings.

INSTALL_DIR="${HOME}/.local/lib/obsidian-cli-mcp"
SERVICE_NAME="obsidian-cli-mcp"
SERVICE_FILE="${HOME}/.config/systemd/user/${SERVICE_NAME}.service"
CLAUDE_CONFIG="${HOME}/.claude/claude.json"

echo "=== Migrating from MCP server to Claude Code plugin ==="

# 1. Stop and disable systemd service
if systemctl --user is-active "${SERVICE_NAME}" &>/dev/null; then
    echo "Stopping ${SERVICE_NAME} service..."
    systemctl --user stop "${SERVICE_NAME}"
fi

if systemctl --user is-enabled "${SERVICE_NAME}" &>/dev/null; then
    echo "Disabling ${SERVICE_NAME} service..."
    systemctl --user disable "${SERVICE_NAME}"
fi

if [[ -f "${SERVICE_FILE}" ]]; then
    echo "Removing service file..."
    rm -f "${SERVICE_FILE}"
    systemctl --user daemon-reload
fi

# 2. Remove installed JAR
if [[ -d "${INSTALL_DIR}" ]]; then
    echo "Removing installed files from ${INSTALL_DIR}..."
    rm -rf "${INSTALL_DIR}"
fi

# 3. Remove MCP server config from Claude Code config
if [[ -f "${CLAUDE_CONFIG}" ]]; then
    if command -v jq &>/dev/null && jq -e '.mcpServers["obsidian-cli-mcp"]' "${CLAUDE_CONFIG}" &>/dev/null; then
        echo "Removing MCP server entry from ${CLAUDE_CONFIG}..."
        jq 'del(.mcpServers["obsidian-cli-mcp"])' "${CLAUDE_CONFIG}" > "${CLAUDE_CONFIG}.tmp"
        mv "${CLAUDE_CONFIG}.tmp" "${CLAUDE_CONFIG}"
    fi
fi

echo ""
echo "Migration complete. To use the plugin:"
echo "  claude --plugin-dir /path/to/obsidian-cli-plugin"
echo ""
echo "Or install permanently:"
echo "  /plugin marketplace add phansen314/obsidian-cli-mcp"
echo "  /plugin install obsidian-cli@obsidian-cli-mcp"
