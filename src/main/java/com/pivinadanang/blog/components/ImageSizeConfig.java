package com.pivinadanang.blog.components;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ImageSizeConfig {
    private static final Map<String, int[]> SIZE_CONFIG = Map.of(
            "posts", new int[]{1440, 900},
            "clients", new int[]{400, 141},
            "slides", new int[]{1733, 759}
    );

    public int[] getSizeConfig(String objectType) {
        return SIZE_CONFIG.getOrDefault(objectType, new int[]{500, 500});
    }
}
