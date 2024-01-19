package com.ej.bibletoppt;

import java.nio.file.Path;

public record PresentationRequest(String bibleVerseInput, Path outputPath, SlideSizeType selectedSize, String selectedFont) {
}