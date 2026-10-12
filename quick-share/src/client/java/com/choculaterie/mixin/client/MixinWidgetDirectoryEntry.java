package com.choculaterie.mixin.client;

import com.choculaterie.integration.LitematicaIntegration;
import com.choculaterie.ui.ShareButton;
import com.choculaterie.util.QuickShareClickTracker;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetDirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import fi.dy.masa.malilib.render.GuiContext;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.io.File;

@Mixin(value = WidgetDirectoryEntry.class, remap = false)
public abstract class MixinWidgetDirectoryEntry extends WidgetBase {
    protected MixinWidgetDirectoryEntry(int x, int y, int width, int height) { super(x, y, width, height); }

    @Shadow @Final protected WidgetFileBrowserBase.DirectoryEntry entry;

    @Unique private static final int BUTTON_WIDTH = 80;
    @Unique private int lastMouseX = 0;
    @Unique private int lastMouseY = 0;
    @Unique private int originalWidth = 0;

    @Unique private boolean isLitematicFile() {
        File file = entry.getFullPath().toFile();
        return file.isFile() && file.getName().toLowerCase().endsWith(".litematic");
    }

    @Unique private boolean isMouseOverButton(int mouseX, int mouseY) {
        if (!isLitematicFile()) return false;
        int buttonX = this.getX() + originalWidth - BUTTON_WIDTH;
        return mouseX >= buttonX && mouseX < buttonX + BUTTON_WIDTH &&
               mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void beforeRender(GuiContext context, int mouseX, int mouseY, boolean isActiveGui, CallbackInfo ci) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        originalWidth = this.getWidth();
        if (isLitematicFile()) {
            this.setWidth(originalWidth - BUTTON_WIDTH);
        }
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true, name = "mouseX")
    private int modifyMouseX(int mouseX) {
        return isMouseOverButton(mouseX, lastMouseY) ? -1 : mouseX;
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 1, argsOnly = true, name = "mouseY")
    private int modifyMouseY(int mouseY) {
        return isMouseOverButton(lastMouseX, mouseY) ? -1 : mouseY;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void afterRender(GuiContext context, int mouseX, int mouseY, boolean isActiveGui, CallbackInfo ci) {
        if (isLitematicFile()) {
            this.setWidth(originalWidth);
        }

        mouseX = lastMouseX;
        mouseY = lastMouseY;
        File file = entry.getFullPath().toFile();
        if (!file.isFile() || !file.getName().toLowerCase().endsWith(".litematic")) {
            QuickShareClickTracker.clearButtonBounds(entry);
            return;
        }

        LitematicaIntegration.UploadState state = LitematicaIntegration.getUploadState(file);
        if (state.showCopied && System.currentTimeMillis() - state.copiedTimestamp > ShareButton.COPIED_MS) {
            state.showCopied = false;
        }

        int buttonHeight = this.getHeight();
        int buttonX = this.getX() + originalWidth - BUTTON_WIDTH;
        int buttonY = this.getY();
        QuickShareClickTracker.updateButtonBounds(entry, buttonX, buttonY, BUTTON_WIDTH, buttonHeight);

        boolean isHovered = mouseX >= buttonX && mouseX < buttonX + BUTTON_WIDTH &&
                           mouseY >= buttonY && mouseY < buttonY + buttonHeight;

        ShareButton.render(context, buttonX, buttonY, BUTTON_WIDTH, buttonHeight, state, isHovered, false);
    }

    @Inject(method = "onMouseClickedImpl", at = @At("HEAD"), cancellable = true)
    private void onQuickShareButtonClick(MouseButtonEvent click, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        File file = entry.getFullPath().toFile();
        if (!file.isFile() || !file.getName().toLowerCase().endsWith(".litematic")) return;

        int buttonX = this.getX() + originalWidth - BUTTON_WIDTH;
        int buttonY = this.getY();
        int buttonRight = buttonX + BUTTON_WIDTH;
        int buttonBottom = buttonY + this.getHeight();
        int clickX = (int) click.x();
        int clickY = (int) click.y();
        boolean isInButton = clickX >= buttonX && clickX < buttonRight && clickY >= buttonY && clickY < buttonBottom;

        if (isInButton) {
            QuickShareClickTracker.markPreventSelection();
            ShareButton.playClick();
            LitematicaIntegration.shareLitematicFile(file);
            cir.setReturnValue(false);
        }
    }
}
