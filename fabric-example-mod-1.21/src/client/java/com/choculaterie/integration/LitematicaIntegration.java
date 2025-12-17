package com.choculaterie.integration;

import com.choculaterie.QuickShareAddonClient;
import com.choculaterie.network.QuickShareNetwork;
import net.minecraft.client.Minecraft;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LitematicaIntegration {
    private static final Map<String, UploadState> uploadStates = new HashMap<>();

    public static class UploadState {
        public boolean isUploading = false;
        public boolean showCopied = false;
        public long copiedTimestamp = 0;
    }

    public static UploadState getUploadState(File file) {
        return uploadStates.computeIfAbsent(file.getAbsolutePath(), k -> new UploadState());
    }

    public static void shareLitematicFile(File litematicFile) {
        Minecraft mc = Minecraft.getInstance();
        UploadState state = getUploadState(litematicFile);
        if (state.isUploading) return;

        state.isUploading = true;
        state.showCopied = false;

        QuickShareNetwork.uploadLitematic(litematicFile)
            .thenAccept(response -> mc.execute(() -> {
                mc.keyboardHandler.setClipboard(response.getShortUrl());
                state.isUploading = false;
                state.showCopied = true;
                state.copiedTimestamp = System.currentTimeMillis();
            }))
            .exceptionally(error -> {
                mc.execute(() -> {
                    state.isUploading = false;
                    QuickShareAddonClient.LOGGER.error("Quick-Share upload failed", error);
                });
                return null;
            });
    }
}
