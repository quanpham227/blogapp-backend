package com.pivinadanang.blog.components;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ImageSizeConfig {
    private static final Map<String, int[]> SIZE_CONFIG = Map.of(
            "posts", new int[]{1024, 768},
            "clients", new int[]{400, 173},
            "slides", new int[]{1733, 759},
            "user_profile", new int[]{256, 256}
    );

    public int[] getSizeConfig(String objectType) {
        return SIZE_CONFIG.getOrDefault(objectType, new int[]{500, 500});
    }
}
