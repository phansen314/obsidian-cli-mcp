package tech.codingzen.obsidian.tools

enum class ToolGroup(val envVar: String?) {
  Core(null),
  Bookmarks("OBSIDIAN_BOOKMARKS"),
  Templates("OBSIDIAN_TEMPLATES"),
  Plugins("OBSIDIAN_PLUGINS"),
  Themes("OBSIDIAN_THEMES"),
  Sync("OBSIDIAN_SYNC"),
  Publish("OBSIDIAN_PUBLISH"),
  Bases("OBSIDIAN_BASES"),
  Dev("OBSIDIAN_DEV_TOOLS");
}
