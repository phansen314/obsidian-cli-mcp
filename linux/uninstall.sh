#!/usr/bin/env bash
set -euo pipefail

INSTALL_DIR="${HOME}/.local/lib/obsidian-cli-mcp"
SERVICE_DIR="${HOME}/.config/systemd/user"
SERVICE_FILE="${SERVICE_DIR}/obsidian-cli-mcp.service"

echo "Stopping and disabling service..."
systemctl --user stop obsidian-cli-mcp    || true
systemctl --user disable obsidian-cli-mcp || true

echo "Removing service file..."
rm -f "${SERVICE_FILE}"
systemctl --user daemon-reload

echo "Removing installed files..."
rm -rf "${INSTALL_DIR}"

echo "Uninstall complete."
