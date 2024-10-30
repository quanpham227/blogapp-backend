package com.pivinadanang.blog.enums;

import lombok.Getter;

@Getter
public enum PostStatus {
        PUBLISHED(0),
        DRAFT(1),
        DELETED(2),
        PENDING(3);

        private final int value;

        PostStatus(int value) {
                this.value = value;
        }

}