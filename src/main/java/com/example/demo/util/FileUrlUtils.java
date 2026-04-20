package com.example.demo.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class FileUrlUtils {

    private FileUrlUtils() {
    }

    public static String normalizeStoredPath(String url) {
        if (url == null) {
            return null;
        }

        String value = url.trim();
        if (value.isBlank()) {
            return null;
        }

        int filesIndex = value.indexOf("/files/");
        if (filesIndex >= 0) {
            return value.substring(filesIndex);
        }

        int uploadsIndex = value.indexOf("/uploads/");
        if (uploadsIndex >= 0) {
            return value.substring(uploadsIndex);
        }

        if (value.contains("/files") && !value.contains("/files/")) {
            int brokenFilesIndex = value.indexOf("/files");
            return "/files/" + value.substring(brokenFilesIndex + "/files".length());
        }

        if (value.startsWith("files/") || value.startsWith("uploads/")) {
            return "/" + value;
        }

        if (value.startsWith("/files/") || value.startsWith("/uploads/")) {
            return value;
        }

        if (value.startsWith("http://") || value.startsWith("https://")) {
            return value;
        }

        return "/" + value;
    }

    public static String toPublicUrl(String storedPath) {
        String normalized = normalizeStoredPath(storedPath);
        if (normalized == null) {
            return null;
        }

        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return normalized;
        }

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return normalized;
        }

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(normalized)
                .toUriString();
    }
}
