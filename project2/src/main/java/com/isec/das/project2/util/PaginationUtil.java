package com.isec.das.project2.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PaginationUtil {

    public static int getPageFromToken(String pageToken) {
        if (pageToken == null || pageToken.isEmpty()) {
            return 0;
        }
        try {
            String decoded = new String(Base64.getDecoder().decode(pageToken), StandardCharsets.UTF_8);
            return Integer.parseInt(decoded);
        } catch (Exception e) {
            return 0; // Default to first page on error
        }
    }

    public static String getNextPageToken(int currentPage, int currentSize, int maxResults) {
        if (currentSize < maxResults) {
            return null; // No more pages
        }
        int nextPage = currentPage + 1;
        return Base64.getEncoder().encodeToString(String.valueOf(nextPage).getBytes(StandardCharsets.UTF_8));
    }
}
