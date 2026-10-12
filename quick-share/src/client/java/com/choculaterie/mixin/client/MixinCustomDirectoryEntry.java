package com.choculaterie.mixin.client;

import com.choculaterie.integration.LitematicaIntegration;
import com.choculaterie.ui.ShareButton;
import com.choculaterie.util.QuickShareClickTracker;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import fi.dy.masa.malilib.render.GuiContext;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.io.File;

@Pseudo
@Mixin(targets = "ru.dimaskama.schematicpreview.gui.widget.CustomDirectoryEntry", remap = false)
public abstract class MixinCustomDirectoryEntry extends WidgetBase {
    protected MixinCustomDirectoryEntry(int x, int y, int width, int height) { super(x, y, width, height); }

    @Unique private static final int BUTTON_WIDTH = 80;
    @Unique private static final int TILE_BTN = 21;
    @Unique private int lastMouseX = 0;
    @Unique private int lastMouseY = 0;
    @Unique private int originalWidth = 0;

    @Unique private WidgetFileBrowserBase.DirectoryEntry quickshare$getEntry() {
        return ((WidgetDirectoryEntryAccessor)(Object)this).getEntry();
    }

    @Unique private boolean isTileMode() {
        return this.getHeight() > BUTTON_WIDTH;
    }

    @Unique private boolean isLitematicFile() {
        File file = quickshare$getEntry().getFullPath().toFile();
        return file.isFile() && file.getName().toLowerCase().endsWith(".litematic");
    }

    @Unique private boolean isMouseOverButton(int mouseX, int mouseY) {
        if (!isLitematicFile()) return false;
        if (isTileMode()) {
            int bx = this.getX() + originalWidth - TILE_BTN - 2;
            int by = this.getY() + 2;
            return mouseX >= bx && mouseX < bx + TILE_BTN && mouseY >= by && mouseY < by + TILE_BTN;
        }
        int buttonX = this.getX() + originalWidth - BUTTON_WIDTH;
        return mouseX >= buttonX && mouseX < buttonX + BUTTON_WIDTH &&
               mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void beforeRender(GuiContext context, int mouseX, int mouseY, boolean isActiveGui, CallbackInfo ci) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        originalWidth = this.getWidth();
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
        mouseX = lastMouseX;
        mouseY = lastMouseY;
        WidgetFileBrowserBase.DirectoryEntry entry = quickshare$getEntry();
        File file = entry.getFullPath().toFile();
        if (!file.isFile() || !file.getName().toLowerCase().endsWith(".litematic")) {
            QuickShareClickTracker.clearButtonBounds(entry);
            return;
        }

        LitematicaIntegration.UploadState state = LitematicaIntegration.getUploadState(file);
        if (state.showCopied && System.currentTimeMillis() - state.copiedTimestamp > ShareButton.COPIED_MS) {
            state.showCopied = false;
        }

        if (isTileMode()) {
            int bx = this.getX() + originalWidth - TILE_BTN - 2;
            int by = this.getY() + 2;
            QuickShareClickTracker.updateButtonBounds(entry, bx, by, TILE_BTN, TILE_BTN);

            boolean isHovered = mouseX >= bx && mouseX < bx + TILE_BTN && mouseY >= by && mouseY < by + TILE_BTN;

            ShareButton.render(context, bx, by, TILE_BTN, TILE_BTN, state, isHovered, true);
        } else {
            int buttonX = this.getX() + originalWidth - BUTTON_WIDTH;
            int buttonY = this.getY();
            int buttonHeight = this.getHeight();
            QuickShareClickTracker.updateButtonBounds(entry, buttonX, buttonY, BUTTON_WIDTH, buttonHeight);

            boolean isHovered = mouseX >= buttonX && mouseX < buttonX + BUTTON_WIDTH &&
                               mouseY >= buttonY && mouseY < buttonY + buttonHeight;

            ShareButton.render(context, buttonX, buttonY, BUTTON_WIDTH, buttonHeight, state, isHovered, false);
        }
    }

    @Inject(method = "onMouseClickedImpl", at = @At("HEAD"), cancellable = true)
    private void onQuickShareButtonClick(MouseButtonEvent click, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        WidgetFileBrowserBase.DirectoryEntry entry = quickshare$getEntry();
        File file = entry.getFullPath().toFile();
        if (!file.isFile() || !file.getName().toLowerCase().endsWith(".litematic")) return;

        int clickX = (int) click.x();
        int clickY = (int) click.y();
        boolean isInButton;

        if (isTileMode()) {
            int bx = this.getX() + originalWidth - TILE_BTN - 2;
            int by = this.getY() + 2;
            isInButton = clickX >= bx && clickX < bx + TILE_BTN && clickY >= by && clickY < by + TILE_BTN;
        } else {
            int buttonX = this.getX() + originalWidth - BUTTON_WIDTH;
            int buttonY = this.getY();
            isInButton = clickX >= buttonX && clickX < buttonX + BUTTON_WIDTH &&
                         clickY >= buttonY && clickY < buttonY + this.getHeight();
        }

        if (isInButton) {
            QuickShareClickTracker.markPreventSelection();
            ShareButton.playClick();
            LitematicaIntegration.shareLitematicFile(file);
            cir.setReturnValue(false);
        }
    }
}
