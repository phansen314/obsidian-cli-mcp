// Obsidian CLI core command signatures (boolean = flag, others = key=value)
// For commands not here: obsidian help <command>

/** obsidian create */
interface Create { name: string; path?: string; content?: string; template?: string; overwrite?: boolean; open?: boolean; newtab?: boolean; }
/** obsidian search — query operators: path:, file:, tag:, line:, section: */
interface Search { query: string; path?: string; limit?: number; total?: boolean; case?: boolean; format?: "text" | "json"; }
/** obsidian properties */
interface Properties { file?: string; path?: string; name?: string; total?: boolean; sort?: "count"; counts?: boolean; format?: "yaml" | "json" | "tsv"; }
/** obsidian property:set */
interface PropertySet { file?: string; path?: string; name: string; value: string; type?: "text" | "list" | "number" | "checkbox" | "date" | "datetime"; }
/** obsidian backlinks */
interface Backlinks { file?: string; path?: string; counts?: boolean; total?: boolean; format?: "json" | "tsv" | "csv"; }
/** obsidian unresolved — whole vault, no file/path */
interface Unresolved { total?: boolean; counts?: boolean; verbose?: boolean; format?: "json" | "tsv" | "csv"; }
/** obsidian tags */
interface Tags { file?: string; path?: string; total?: boolean; counts?: boolean; sort?: "count"; format?: "json" | "tsv" | "csv"; }
/** obsidian tasks */
interface Tasks { file?: string; path?: string; total?: boolean; done?: boolean; todo?: boolean; status?: string; verbose?: boolean; daily?: boolean; format?: "json" | "tsv" | "csv" | "text"; }
/** obsidian task — ref is shorthand for file+line, e.g. ref="folder/note.md:12" */
interface Task { file?: string; path?: string; ref?: string; line?: number; toggle?: boolean; done?: boolean; todo?: boolean; daily?: boolean; status?: string; }
/** obsidian vault */
interface Vault { info?: "name" | "path" | "files" | "folders" | "size"; }
