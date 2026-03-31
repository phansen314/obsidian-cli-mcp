# obsidian-cli-plugin

Claude Code plugin for Obsidian vault interaction via the official [Obsidian CLI](https://obsidian.md).

## Prerequisites

- [Obsidian](https://obsidian.md) desktop app running
- Obsidian CLI available as `obsidian` on PATH

## Install

### Development / testing

Load the plugin directly from your local clone:

```bash
claude --plugin-dir /path/to/obsidian-cli-mcp
```

### Permanent install

Add this repo as a marketplace, then install the plugin:

```
/plugin marketplace add phansen314/obsidian-cli-mcp
/plugin install obsidian-cli@obsidian-cli-mcp
```

### Migrating from the MCP server

If you previously ran the Kotlin MCP server, run the migration script to clean it up:

```bash
./install/migrate-from-mcp.sh
```

This stops the systemd service, removes the installed JAR, and removes the MCP server config from Claude Code settings.

## What It Does

Provides a model-invoked skill that auto-triggers when you mention Obsidian, vaults, notes, tags, tasks, etc. Claude constructs and executes `obsidian` CLI commands directly — no MCP server required.

Covers ~50 commands: notes, daily notes, search, properties, links, tags, tasks, vault info, bookmarks, templates, plugins, themes, sync, bases, and dev tools.

## License

MIT
