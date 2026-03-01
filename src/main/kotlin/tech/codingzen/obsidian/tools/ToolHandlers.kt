package tech.codingzen.obsidian.tools

import tech.codingzen.obsidian.handlers.*

val toolHandlers: Map<ToolName, ToolHandler> = mapOf(
    ToolName.ObsidianNote      to NoteHandler,
    ToolName.ObsidianSearch    to SearchHandler,
    ToolName.ObsidianProperty  to PropertyHandler,
    ToolName.ObsidianLinks     to LinksHandler,
    ToolName.ObsidianTags      to TagsHandler,
    ToolName.ObsidianTasks     to TasksHandler,
    ToolName.ObsidianVault     to VaultHandler,
    ToolName.ObsidianBookmarks to BookmarksHandler,
    ToolName.ObsidianTemplates to TemplatesHandler,
    ToolName.ObsidianPlugin    to PluginHandler,
    ToolName.ObsidianTheme     to ThemeHandler,
    ToolName.ObsidianSync      to SyncHandler,
    ToolName.ObsidianPublish   to PublishHandler,
    ToolName.ObsidianBase      to BaseHandler,
    ToolName.ObsidianDev       to DevHandler,
)
