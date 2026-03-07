#!/usr/bin/env bash
set -euo pipefail

INSTALL_DIR="${HOME}/.local/lib/obsidian-cli-mcp"
PLIST_DIR="${HOME}/Library/LaunchAgents"
LOG_DIR="${HOME}/Library/Logs/obsidian-cli-mcp"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "${SCRIPT_DIR}")"

JAVA_BIN="$(command -v java)"
if [[ -z "${JAVA_BIN}" ]]; then
    echo "Error: java not found on PATH" >&2
    exit 1
fi
echo "Using Java: ${JAVA_BIN}"

OBSIDIAN_BIN="$(command -v obsidian)"
if [[ -z "${OBSIDIAN_BIN}" ]]; then
    echo "Error: obsidian CLI not found on PATH" >&2
    exit 1
fi
OBSIDIAN_BIN_DIR="$(dirname "${OBSIDIAN_BIN}")"
echo "Using obsidian: ${OBSIDIAN_BIN}"

echo "Building fat JAR..."
"${PROJECT_DIR}/gradlew" -p "${PROJECT_DIR}" shadowJar

# Derive version from Gradle — single source of truth
VERSION=$("${PROJECT_DIR}/gradlew" -p "${PROJECT_DIR}" -q properties | grep "^version:" | cut -d' ' -f2)
JAR_NAME="obsidian-cli-mcp_${VERSION}.jar"

echo "Installing JAR ${JAR_NAME} to ${INSTALL_DIR}..."
mkdir -p "${INSTALL_DIR}"
cp "${PROJECT_DIR}/build/libs/${JAR_NAME}" "${INSTALL_DIR}/"

# Update stable symlink so the plist never needs editing on version bumps
ln -sf "${INSTALL_DIR}/${JAR_NAME}" "${INSTALL_DIR}/obsidian-cli-mcp-current.jar"

# Install launcher script with Java path substituted
echo "Installing launcher script..."
sed -e "s|@JAVA@|${JAVA_BIN}|g" \
    "${SCRIPT_DIR}/obsidian-cli-mcp-launcher.sh" \
    > "${INSTALL_DIR}/obsidian-cli-mcp-launcher.sh"
chmod +x "${INSTALL_DIR}/obsidian-cli-mcp-launcher.sh"

# Install LaunchAgent plists
echo "Installing LaunchAgent plists..."
mkdir -p "${PLIST_DIR}"
mkdir -p "${LOG_DIR}"

sed -e "s|@VERSION@|${VERSION}|g" \
    -e "s|@INSTALL_DIR@|${INSTALL_DIR}|g" \
    -e "s|@LOG_DIR@|${LOG_DIR}|g" \
    -e "s|@OBSIDIAN_BIN_DIR@|${OBSIDIAN_BIN_DIR}|g" \
    "${SCRIPT_DIR}/com.codingzen.obsidian-cli-mcp.plist" \
    > "${PLIST_DIR}/com.codingzen.obsidian-cli-mcp.plist"

cp "${SCRIPT_DIR}/com.codingzen.obsidian.plist" \
   "${PLIST_DIR}/com.codingzen.obsidian.plist"

# Unload existing MCP agent if present
GUI_UID="gui/$(id -u)"
launchctl bootout "${GUI_UID}/com.codingzen.obsidian-cli-mcp" 2>/dev/null || true

# Load Obsidian agent (skip if already loaded)
if ! launchctl print "${GUI_UID}/com.codingzen.obsidian" &>/dev/null; then
    launchctl bootstrap "${GUI_UID}" "${PLIST_DIR}/com.codingzen.obsidian.plist"
fi

# Load MCP agent
launchctl bootstrap "${GUI_UID}" "${PLIST_DIR}/com.codingzen.obsidian-cli-mcp.plist"

echo ""
echo "Done. Verify with:"
echo "  launchctl print ${GUI_UID}/com.codingzen.obsidian-cli-mcp"
echo "  launchctl print ${GUI_UID}/com.codingzen.obsidian"
