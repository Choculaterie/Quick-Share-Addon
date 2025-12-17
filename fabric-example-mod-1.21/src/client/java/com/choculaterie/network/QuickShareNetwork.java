package com.choculaterie.network;

import com.choculaterie.models.QuickShareResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

public class QuickShareNetwork {
    private static final String BASE_URL = "https://choculaterie.com/api/LitematicDownloaderModAPI";
    private static final Gson GSON = new Gson();
    private static final int TIMEOUT = 30000;
    private static final String BOUNDARY = "----WebKitFormBoundary" + System.currentTimeMillis();

    public static CompletableFuture<QuickShareResponse> uploadLitematic(File file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (file == null || !file.exists()) throw new IllegalArgumentException("File does not exist");
                if (!file.getName().toLowerCase().endsWith(".litematic")) throw new IllegalArgumentException("Only .litematic files allowed");
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                String json = uploadMultipartFile(file.getName(), fileBytes);
                JsonObject root = GSON.fromJson(json, JsonObject.class);
                if (!root.has("shortUrl")) throw new RuntimeException("Response missing shortUrl");
                return new QuickShareResponse(root.get("shortUrl").getAsString());
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload litematic file", e);
            }
        });
    }

    private static String uploadMultipartFile(String fileName, byte[] fileBytes) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL + "/upload").openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
            conn.setRequestProperty("User-Agent", "Quick-Share-Addon/1.0");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                out.writeBytes("--" + BOUNDARY + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n");
                out.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
                out.write(fileBytes);
                out.writeBytes("\r\n--" + BOUNDARY + "--\r\n");
            }

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
            StringBuilder sb = new StringBuilder();
            if (is != null) {
                try (BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line; while ((line = r.readLine()) != null) sb.append(line);
                }
            }
            if (code != HttpURLConnection.HTTP_OK) throw new IOException("HTTP " + code + ": " + sb);
            return sb.toString();
        } finally { conn.disconnect(); }
    }
}
