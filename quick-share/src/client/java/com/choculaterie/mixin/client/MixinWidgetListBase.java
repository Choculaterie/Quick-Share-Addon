package com.choculaterie.mixin.client;

import com.choculaterie.util.QuickShareClickTracker;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WidgetListBase.class, remap = false)
public abstract class MixinWidgetListBase {
    @Inject(method = "onMouseClicked", at = @At("HEAD"))
    private void preCheckMouseClick(MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (QuickShareClickTracker.preMarkIfClickOnButton((int) event.x(), (int) event.y())) {
            QuickShareClickTracker.markPreventSelection();
        }
    }

    @Inject(method = "setLastSelectedEntry", at = @At("HEAD"), cancellable = true)
    private void preventSelection(Object entry, int index, CallbackInfo ci) {
        if (QuickShareClickTracker.shouldPreventSelection()) {
            QuickShareClickTracker.clearPreventSelection();
            ci.cancel();
        }
    }
}

