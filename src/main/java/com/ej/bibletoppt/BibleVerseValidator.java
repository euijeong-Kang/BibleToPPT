package com.ej.bibletoppt;

public class BibleVerseValidator {

    public boolean validate(String input) {
        String[] parts = input.split(",");
        for (String part : parts) {
            if (!validateSinglePart(part.trim())) {
                return false;
            }
        }
        return true;
    }

    private boolean validateSinglePart(String part) {
        String[] tokens = part.split("\\s+");
        if (tokens.length < 2) {
            return false;
        }

        String book = tokens[0];
        String chapterVerse = tokens[1];

        String[] chapterVerseTokens = chapterVerse.split(":");
        if (chapterVerseTokens.length != 2) {
            return false;
        }

        String chapter = chapterVerseTokens[0];
        String[] verseTokens = chapterVerseTokens[1].split("-");

        if (!isValidBook(book) || !isValidChapterVerse(chapter, verseTokens)) {
            return false;
        }

        return true;
    }

    private boolean isValidBook(String book) {
        // 여기에 적절한 책 이름인지 확인하는 로직 추가
        return true;
    }

    private boolean isValidChapterVerse(String chapter, String[] verses) {
        if (!isValidNumber(chapter)) {
            return false;
        }

        for (String verse : verses) {
            if (!isValidNumber(verse)) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidNumber(String number) {
        try {
            int value = Integer.parseInt(number);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String normalize(String input) {
        // 여기에 정규화 로직 추가 (예: 약어를 풀네임으로 변환 등)
        // 현재는 입력값이 올바르다고 가정하고 그대로 반환
        return input;
    }
}