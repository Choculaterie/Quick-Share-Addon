package com.choculaterie.mixin.client;

import com.choculaterie.util.QuickShareClickTracker;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WidgetListBase.class, remap = false)
public abstract class MixinWidgetListBase {

    @Inject(method = "setLastSelectedEntry", at = @At("HEAD"), cancellable = true)
    private void preventSelection(Object entry, int index, CallbackInfo ci) {
        if (QuickShareClickTracker.shouldPreventSelection()) {
            QuickShareClickTracker.clearPreventSelection();
            ci.cancel();
        }
    }
}

