package com.choculaterie.mixin.client;

import fi.dy.masa.malilib.gui.widgets.WidgetDirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WidgetDirectoryEntry.class, remap = false)
public interface WidgetDirectoryEntryAccessor {
    @Accessor("entry")
    WidgetFileBrowserBase.DirectoryEntry getEntry();
}
