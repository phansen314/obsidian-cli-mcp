# obsidian-cli-plugin

Claude Code plugin for Obsidian vault interaction via the official [Obsidian CLI](https://obsidian.md).

## Install

Add this plugin to Claude Code:

```bash
claude plugin add /path/to/obsidian-cli-plugin
```

Or symlink into your plugins directory:

```bash
ln -s /path/to/obsidian-cli-plugin ~/.claude/plugins/obsidian-cli
```

## Prerequisites

- [Obsidian](https://obsidian.md) desktop app running
- Obsidian CLI available as `obsidian` on PATH

## What It Does

Provides a model-invoked skill that auto-triggers when you mention Obsidian, vaults, notes, tags, tasks, etc. Claude constructs and executes `obsidian` CLI commands directly — no MCP server required.

Covers ~50 commands: notes, daily notes, search, properties, links, tags, tasks, vault info, bookmarks, templates, plugins, themes, sync, publish, bases, and dev tools.

## License

MIT
