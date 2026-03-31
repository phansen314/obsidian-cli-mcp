---
name: obsidian
description: >-
  Use when the user mentions Obsidian, vault, notes, daily note, search vault,
  tags, tasks, backlinks, properties, frontmatter, templates, plugins, themes,
  sync, bases, or any Obsidian CLI interaction.
allowed-tools: [Bash, Read, Grep, Glob]
---

## Syntax

```
obsidian [vault=<name>] <command> [key=value ...] [flags]
```

## Rules

- `vault=` is accepted on all commands; omit for last-focused vault
- `file=` resolves by name (like wikilinks); `path=` is exact from vault root. Always specify one — never rely on "active file" defaulting.
- Key-value params: `key=value` — quote values with spaces: `name="My Note"`
- Flags are bare words (no `--` prefix): `total`, `counts`, `open`
- Use `\n` for newlines in content values
- Prefer `format=json` when a command supports it
- Non-zero exit code = error; check Obsidian is running if commands fail
- For parameter details on core commands, read `references/obsidian-cli.d.ts`
- For other commands, run `obsidian help <command>`

## Examples

```bash
# Read a note
obsidian read file="Project Plan"

# Search with json output
obsidian vault=Work search query="meeting" format=json limit=5

# Create from template
obsidian create name="2026-03-30" template="Daily" open

# Append to today's daily note
obsidian daily:append content="- Met with team re: deployment"

# List incomplete tasks as json
obsidian tasks todo format=json

# Toggle a task by line number
obsidian task file="Todo" line=3 toggle

# Set a frontmatter property
obsidian property:set file="Project Plan" name="status" value="active" type=text

# Find orphan notes and unresolved links
obsidian orphans format=json
obsidian unresolved counts format=json

# Tag query
obsidian tag name="project" verbose

# List files in a folder
obsidian files folder="Projects" ext=md
```

## Commands

Notes: `read`, `create`(name), `append`(content), `prepend`(content), `open`, `move`(to), `delete`, `files`, `outline`
Daily: `daily`, `daily:read`, `daily:append`(content), `daily:prepend`(content)
Search: `search`(query), `search:open`
Properties: `properties`, `property:set`(name,value), `property:remove`(name)
Links: `links`, `backlinks`, `unresolved`, `orphans`
Tags: `tags`, `tag`(name)
Tasks: `tasks`, `task`(ref|file+line)
Vault: `vault`, `workspace`
Bookmarks: `bookmarks`
Templates: `templates`
Plugins: `plugins`, `plugin:enable`(id), `plugin:disable`(id), `plugin:reload`(id)
Themes: `themes`, `theme:set`(name), `snippets`
Sync: `sync:status`, `sync:history`, `sync:restore`(version)
Bases: `bases`, `base:query`
Dev: `eval`(code), `dev:screenshot`, `dev:console`, `dev:errors`, `dev:dom`(selector), `dev:css`(selector), `dev:debug`, `dev:mobile`, `devtools`

Parenthesized params are required.
