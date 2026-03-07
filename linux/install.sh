#!/usr/bin/env bash
set -euo pipefail

INSTALL_DIR="${HOME}/.local/lib/obsidian-cli-mcp"
SERVICE_DIR="${HOME}/.config/systemd/user"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "${SCRIPT_DIR}")"

JAVA_BIN="$(command -v java)"
if [[ -z "${JAVA_BIN}" ]]; then
    echo "Error: java not found on PATH" >&2
    exit 1
fi
echo "Using Java: ${JAVA_BIN}"

echo "Building fat JAR..."
"${PROJECT_DIR}/gradlew" -p "${PROJECT_DIR}" shadowJar

# Derive version from Gradle — single source of truth
VERSION=$("${PROJECT_DIR}/gradlew" -p "${PROJECT_DIR}" -q properties | grep "^version:" | cut -d' ' -f2)
JAR_NAME="obsidian-cli-mcp_${VERSION}.jar"

echo "Installing JAR ${JAR_NAME} to ${INSTALL_DIR}..."
mkdir -p "${INSTALL_DIR}"
cp "${PROJECT_DIR}/build/libs/${JAR_NAME}" "${INSTALL_DIR}/"

# Update stable symlink so the service file never needs editing on version bumps
ln -sf "${INSTALL_DIR}/${JAR_NAME}" "${INSTALL_DIR}/obsidian-cli-mcp-current.jar"

echo "Installing systemd service..."
mkdir -p "${SERVICE_DIR}"
# Substitute @VERSION@ placeholder so the installed file shows the current version
sed -e "s|@VERSION@|${VERSION}|g" \
    -e "s|@JAVA@|${JAVA_BIN}|g" \
    "${SCRIPT_DIR}/obsidian-cli-mcp.service" \
    > "${SERVICE_DIR}/obsidian-cli-mcp.service"

# Make display vars available to the systemd user manager so PassEnvironment can forward them
systemctl --user import-environment WAYLAND_DISPLAY XDG_SESSION_TYPE 2>/dev/null || true

systemctl --user daemon-reload
systemctl --user enable obsidian-cli-mcp
systemctl --user restart obsidian-cli-mcp

echo "Done. Check status with: systemctl --user status obsidian-cli-mcp"
