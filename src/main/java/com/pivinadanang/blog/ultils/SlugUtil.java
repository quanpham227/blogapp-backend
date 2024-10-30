package com.pivinadanang.blog.ultils;

import com.github.slugify.Slugify;

import java.util.Locale;

public class SlugUtil {
    public static String generateSlug(String title) {
        Slugify slugify = Slugify.builder()
                .transliterator(true)          // use transliteration
                .locale(Locale.ENGLISH)
                .build();
        return slugify.slugify(title);
    }


}
