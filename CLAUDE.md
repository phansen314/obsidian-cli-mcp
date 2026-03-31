# CLAUDE.md

## Project

Claude Code plugin that provides Obsidian vault interaction via the official Obsidian CLI. Replaces the previous Kotlin MCP server with a lightweight skill-based approach.

## Structure

```
.claude-plugin/plugin.json          # Plugin manifest
skills/obsidian/SKILL.md            # Model-invoked skill (auto-triggers on Obsidian mentions)
skills/obsidian/references/
  obsidian-cli.d.ts                 # TypeScript CLI type definitions (~40 commands)
```

## How It Works

The skill teaches Claude the Obsidian CLI invocation pattern:
```
obsidian [vault=<name>] <command> [key=value ...] [--flags]
```

Claude constructs and executes CLI commands directly via Bash. The `.d.ts` reference file provides typed parameter signatures loaded on-demand.

## Adding a Command

1. Add an interface to `obsidian-cli.d.ts` with a JSDoc comment showing the command name
2. Add a row to the command table in `SKILL.md`

## Token Budget

| Layer | When loaded | ~Tokens |
|-------|-------------|---------|
| Skill metadata | Always | 50 |
| SKILL.md body | On Obsidian trigger | 400 |
| obsidian-cli.d.ts | On parameter lookup | 150 |

## Prerequisites

- Obsidian desktop app running
- Obsidian CLI available as `obsidian` on PATH

## Legacy

The original Kotlin MCP server is preserved on the `archive/mcp-server` branch.
