package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FileUrlUtilsTest {

    @Test
    void normalizeStoredPathCorrigeUrlQuebradaDoFiles() {
        String result = FileUrlUtils.normalizeStoredPath("http://localhost:8080/filesabc.png");
        assertEquals("/files/abc.png", result);
    }

    @Test
    void normalizeStoredPathMantemPathRelativoPadrao() {
        String result = FileUrlUtils.normalizeStoredPath("/files/avatar.png");
        assertEquals("/files/avatar.png", result);
    }

    @Test
    void normalizeStoredPathConverteUrlAbsolutaEmPathRelativo() {
        String result = FileUrlUtils.normalizeStoredPath("http://localhost:8080/uploads/foto.png");
        assertEquals("/uploads/foto.png", result);
    }

    @Test
    void normalizeStoredPathRetornaNullQuandoVazio() {
        assertNull(FileUrlUtils.normalizeStoredPath("   "));
    }
}
