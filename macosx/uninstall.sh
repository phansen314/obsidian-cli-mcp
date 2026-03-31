#!/usr/bin/env bash
set -euo pipefail

INSTALL_DIR="${HOME}/.local/lib/obsidian-cli-mcp"
PLIST_DIR="${HOME}/Library/LaunchAgents"
LOG_DIR="${HOME}/Library/Logs/obsidian-cli-mcp"

echo "Stopping MCP server agent..."
launchctl bootout "gui/$(id -u)/com.codingzen.obsidian-cli-mcp" 2>/dev/null || true

echo "Removing MCP plist..."
rm -f "${PLIST_DIR}/com.codingzen.obsidian-cli-mcp.plist"

echo "Removing installed files..."
rm -rf "${INSTALL_DIR}"

echo "Removing log directory..."
rm -rf "${LOG_DIR}"

echo "Uninstall complete. (Obsidian LaunchAgent left intact.)"
