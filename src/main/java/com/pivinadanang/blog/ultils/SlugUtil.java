package com.pivinadanang.blog.ultils;

public class SlugUtil {
    public static String toSlug(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase().trim().replaceAll("\\s+", "-");
    }
}
