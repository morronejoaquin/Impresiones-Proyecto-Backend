package com.example.demo.Model.Enums;

import lombok.Getter;

@Getter
public enum FileTypeEnum {
    PDF("application/pdf"),
    JPG("image/jpeg"),
    PNG("image/png");

    private final String mimeType;

    FileTypeEnum(String mimeType) {
        this.mimeType = mimeType;
    }
}
