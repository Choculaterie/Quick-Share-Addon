package com.choculaterie.mixin.client;

import com.choculaterie.integration.LitematicaIntegration;
import com.choculaterie.util.QuickShareClickTracker;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetDirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
    private void beforeRender(GuiGraphics graphics, int mouseX, int mouseY, boolean isActiveGui, CallbackInfo ci) {
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

    @Unique
    private void drawColoredRect(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        int argbColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
        graphics.fillGradient(x, y, x + width, y + height, argbColor, argbColor);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void afterRender(GuiGraphics graphics, int mouseX, int mouseY, boolean isActiveGui, CallbackInfo ci) {
        if (isLitematicFile()) {
            this.setWidth(originalWidth);
        }

        int actualMouseX = lastMouseX;
        int actualMouseY = lastMouseY;

        File file = entry.getFullPath().toFile();
        if (!file.isFile() || !file.getName().toLowerCase().endsWith(".litematic")) {
            QuickShareClickTracker.clearButtonBounds(entry);
            return;
        }

        LitematicaIntegration.UploadState state = LitematicaIntegration.getUploadState(file);
        if (state.showCopied && System.currentTimeMillis() - state.copiedTimestamp > 2000) {
            state.showCopied = false;
        }

        int buttonHeight = this.getHeight();
        int buttonX = this.getX() + originalWidth - BUTTON_WIDTH;
        int buttonY = this.getY();
        QuickShareClickTracker.updateButtonBounds(entry, buttonX, buttonY, BUTTON_WIDTH, buttonHeight);

        boolean isHovered = actualMouseX >= buttonX && actualMouseX < buttonX + BUTTON_WIDTH &&
                           actualMouseY >= buttonY && actualMouseY < buttonY + buttonHeight;

        int bgColor, borderColor;
        String text;
        if (state.isUploading) {
            bgColor = 0xFF555555; borderColor = 0xFF555555; text = "Uploading...";
        } else if (state.showCopied) {
            bgColor = 0xFF44AA44; borderColor = 0xFF4477DD; text = "✓ Copied";
        } else if (isHovered) {
            bgColor = 0xFF4488FF; borderColor = 0xFF66AAFF; text = "📤 Share";
        } else {
            bgColor = 0xFF3366CC; borderColor = 0xFF4477DD; text = "📤 Share";
        }

        drawColoredRect(graphics, buttonX, buttonY, BUTTON_WIDTH, buttonHeight, bgColor);
        drawColoredRect(graphics, buttonX, buttonY, BUTTON_WIDTH, 1, borderColor);
        drawColoredRect(graphics, buttonX, buttonY + buttonHeight - 1, BUTTON_WIDTH, 1, borderColor);
        drawColoredRect(graphics, buttonX, buttonY, 1, buttonHeight, borderColor);
        drawColoredRect(graphics, buttonX + BUTTON_WIDTH - 1, buttonY, 1, buttonHeight, borderColor);
        
        int textWidth = this.getStringWidth(text);
        int textX = buttonX + (BUTTON_WIDTH - textWidth) / 2;
        int textY = buttonY + (buttonHeight - 8) / 2;
        this.drawStringWithShadow(graphics, textX, textY, 0xFFFFFFFF, text);
    }

    @Intrinsic(displace = true)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return handleButtonClick(mouseX, mouseY);
    }

    @Intrinsic(displace = true)
    public boolean onMouseClicked(int mouseX, int mouseY, int button) {
        return handleButtonClick(mouseX, mouseY);
    }

    @Unique
    private boolean handleButtonClick(double mouseX, double mouseY) {
        File file = entry.getFullPath().toFile();
        if (file.isFile() && file.getName().toLowerCase().endsWith(".litematic")) {
            int buttonX = this.getX() + originalWidth - BUTTON_WIDTH;
            int buttonY = this.getY();
            int buttonRight = buttonX + BUTTON_WIDTH;
            int buttonBottom = buttonY + this.getHeight();
            boolean isInButton = mouseX >= buttonX && mouseX < buttonRight && mouseY >= buttonY && mouseY < buttonBottom;

            if (isInButton) {
                QuickShareClickTracker.markPreventSelection();
                LitematicaIntegration.shareLitematicFile(file);
                return true;
            }
        }
        return false;
    }
}
