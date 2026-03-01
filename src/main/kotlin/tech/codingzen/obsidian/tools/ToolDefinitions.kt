package tech.codingzen.obsidian.tools

val toolGroupings: Map<ToolGroup, Set<ToolName>> = mapOf(
    ToolGroup.Core      to setOf(ToolName.ObsidianNote, ToolName.ObsidianSearch, ToolName.ObsidianProperty,
                                  ToolName.ObsidianLinks, ToolName.ObsidianTags, ToolName.ObsidianTasks, ToolName.ObsidianVault),
    ToolGroup.Bookmarks to setOf(ToolName.ObsidianBookmarks),
    ToolGroup.Templates to setOf(ToolName.ObsidianTemplates),
    ToolGroup.Plugins   to setOf(ToolName.ObsidianPlugin),
    ToolGroup.Themes    to setOf(ToolName.ObsidianTheme),
    ToolGroup.Sync      to setOf(ToolName.ObsidianSync),
    ToolGroup.Publish   to setOf(ToolName.ObsidianPublish),
    ToolGroup.Bases     to setOf(ToolName.ObsidianBase),
    ToolGroup.Dev       to setOf(ToolName.ObsidianDev),
)

val toolDefinitions: Map<ToolName, ToolDefinition> =
    coreDefinitions + bookmarksDefinitions + templatesDefinitions +
    pluginsDefinitions + themesDefinitions + syncDefinitions +
    publishDefinitions + basesDefinitions + devDefinitions

fun visibleTools(enabledGroups: Set<ToolGroup>): Map<ToolName, ToolDefinition> {
    val visible = enabledGroups.flatMapTo(mutableSetOf()) { toolGroupings[it] ?: emptySet() }
    return toolDefinitions.filterKeys { it in visible }
}
