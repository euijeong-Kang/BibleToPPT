package com.ej.bibletoppt.controller.dto;

import com.ej.bibletoppt.domain.SlideSizeType;

import java.nio.file.Path;

public record PresentationRequest(
        String mainTitle,
        String bibleVerseInput,
        Path outputPath,
        SlideSizeType selectedSize,
        String selectedFont,
        boolean titleSlideOption) {
}