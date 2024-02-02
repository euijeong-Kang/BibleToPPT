package com.ej.bibletoppt;

import java.nio.file.Path;

public record PresentationRequest(String mainTitle, String bibleVerseInput, Path outputPath, SlideSizeType selectedSize, String selectedFont) {
}