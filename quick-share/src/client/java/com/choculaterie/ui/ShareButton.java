package com.choculaterie.ui;

import com.choculaterie.integration.LitematicaIntegration;
import com.choculaterie.vanilib.gui.theme.ButtonStyle;
import com.choculaterie.vanilib.gui.theme.UIDraw;
import com.choculaterie.vanilib.gui.theme.UIIcon;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class ShareButton {
    private ShareButton() {}

    public static final long COPIED_MS = 2000;

    public static void render(GuiGraphicsExtractor ctx, int x, int y, int w, int h,
                              LitematicaIntegration.UploadState state, boolean hovered, boolean iconOnly) {
        boolean uploading = state.isUploading;
        boolean copied = state.showCopied;
        ButtonStyle style = copied ? ButtonStyle.SUCCESS : ButtonStyle.PRIMARY;
        UIIcon icon = uploading ? null : copied ? UIIcon.CHECK : UIIcon.SHARE;
        String label = iconOnly ? (uploading ? "..." : "") : (uploading ? "Uploading..." : copied ? "Copied" : "Share");
        UIDraw.drawButton(ctx, x, y, w, h, style, !uploading, hovered, icon, label);
        if (copied) {
            UIDraw.successFlash(ctx, x, y, x + w, y + h, state.copiedTimestamp);
        }
    }

    public static void playClick() {
        UIDraw.playClick();
    }
}
