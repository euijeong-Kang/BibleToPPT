package com.ej.bibletoppt;

import com.ej.bibletoppt.service.IBibleVerseValidator;
import com.ej.bibletoppt.service.query.ISearchBible;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 성경 구절 형식의 유효성을 검사하고 정규화하는 클래스입니다.
 * 이 클래스는 입력된 성경 구절 문자열이 올바른 형식인지 확인하고,
 * 약어를 전체 이름으로 변환하는 등의 정규화 작업을 수행합니다.
 */
public class BibleVerseValidator implements IBibleVerseValidator {
    private static final Logger LOGGER = Logger.getLogger(BibleVerseValidator.class.getName());

    private ISearchBible searchBible;

    // 성경 책 이름 (한글 전체 이름)
    private static final String[] koreanFullNames = {
        "창세기", "출애굽기", "레위기", "민수기", "신명기", 
        "여호수아", "사사기", "룻기", "사무엘상", "사무엘하", 
        "열왕기상", "열왕기하", "역대상", "역대하", "에스라", 
        "느헤미야", "에스더", "욥기", "시편", "잠언", 
        "전도서", "아가", "이사야", "예레미야", "예레미야애가", 
        "에스겔", "다니엘", "호세아", "요엘", "아모스", 
        "오바댜", "요나", "미가", "나훔", "하박국", 
        "스바냐", "학개", "스가랴", "말라기", 
        "마태복음", "마가복음", "누가복음", "요한복음", 
        "사도행전", "로마서", "고린도전서", "고린도후서", "갈라디아서", 
        "에베소서", "빌립보서", "골로새서", "데살로니가전서", "데살로니가후서", 
        "디모데전서", "디모데후서", "디도서", "빌레몬서", "히브리서", 
        "야고보서", "베드로전서", "베드로후서", "요한일서", "요한이서", 
        "요한삼서", "유다서", "요한계시록"
    };

    // 성경 책 이름 (한글 약어)
    private static final String[] koreanAbbreviations = {
        "창", "출", "레", "민", "신", 
        "수", "삿", "룻", "삼상", "삼하", 
        "왕상", "왕하", "대상", "대하", "스", 
        "느", "에", "욥", "시", "잠", 
        "전", "아", "사", "렘", "애", 
        "겔", "단", "호", "욜", "암", 
        "옵", "욘", "미", "나", "합", 
        "습", "학", "슥", "말", 
        "마", "막", "눅", "요", 
        "행", "롬", "고전", "고후", "갈", 
        "엡", "빌", "골", "살전", "살후", 
        "딤전", "딤후", "딛", "몬", "히", 
        "약", "벧전", "벧후", "요일", "요이", 
        "요삼", "유", "계"
    };

    // 영어 책 이름 (전체 이름)
    private static final String[] englishFullNames = {
        "Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy", 
        "Joshua", "Judges", "Ruth", "1 Samuel", "2 Samuel", 
        "1 Kings", "2 Kings", "1 Chronicles", "2 Chronicles", "Ezra", 
        "Nehemiah", "Esther", "Job", "Psalms", "Proverbs", 
        "Ecclesiastes", "Song of Solomon", "Isaiah", "Jeremiah", "Lamentations", 
        "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", 
        "Obadiah", "Jonah", "Micah", "Nahum", "Habakkuk", 
        "Zephaniah", "Haggai", "Zechariah", "Malachi", 
        "Matthew", "Mark", "Luke", "John", 
        "Acts", "Romans", "1 Corinthians", "2 Corinthians", "Galatians", 
        "Ephesians", "Philippians", "Colossians", "1 Thessalonians", "2 Thessalonians", 
        "1 Timothy", "2 Timothy", "Titus", "Philemon", "Hebrews", 
        "James", "1 Peter", "2 Peter", "1 John", "2 John", 
        "3 John", "Jude", "Revelation"
    };

    // 영어 책 이름 (약어)
    private static final String[] englishAbbreviations = {
        "Gen", "Exod", "Lev", "Num", "Deut", 
        "Josh", "Judg", "Ruth", "1 Sam", "2 Sam", 
        "1 Kgs", "2 Kgs", "1 Chr", "2 Chr", "Ezra", 
        "Neh", "Esth", "Job", "Ps", "Prov", 
        "Eccl", "Song", "Isa", "Jer", "Lam", 
        "Ezek", "Dan", "Hos", "Joel", "Amos", 
        "Obad", "Jonah", "Mic", "Nah", "Hab", 
        "Zeph", "Hag", "Zech", "Mal", 
        "Matt", "Mark", "Luke", "John", 
        "Acts", "Rom", "1 Cor", "2 Cor", "Gal", 
        "Eph", "Phil", "Col", "1 Thess", "2 Thess", 
        "1 Tim", "2 Tim", "Titus", "Phlm", "Heb", 
        "Jas", "1 Pet", "2 Pet", "1 John", "2 John", 
        "3 John", "Jude", "Rev"
    };

    public BibleVerseValidator() {
        // Default constructor for backward compatibility
    }

    public BibleVerseValidator(ISearchBible searchBible) {
        this.searchBible = searchBible;
    }

    /**
     * 입력된 성경 구절 문자열이 올바른 형식인지 검사합니다.
     * 
     * @param input 검사할 성경 구절 문자열 (예: "창세기 1:1", "창 1:1-20", "창 1:1-20, 마 28:19-20")
     * @return 입력된 문자열이 올바른 형식이면 true, 그렇지 않으면 false
     */
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
        if (book == null || book.trim().isEmpty()) {
            return false;
        }

        // 입력된 책 이름이 유효한지 확인
        for (String name : koreanFullNames) {
            if (name.equalsIgnoreCase(book)) {
                return true;
            }
        }

        for (String abbr : koreanAbbreviations) {
            if (abbr.equalsIgnoreCase(book)) {
                return true;
            }
        }

        for (String name : englishFullNames) {
            if (name.equalsIgnoreCase(book)) {
                return true;
            }
        }

        for (String abbr : englishAbbreviations) {
            if (abbr.equalsIgnoreCase(book)) {
                return true;
            }
        }

        return false;
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

    /**
     * 입력된 성경 구절 문자열을 정규화합니다.
     * 이 메서드는 약어를 전체 이름으로 변환하고, 형식을 일관되게 만듭니다.
     * 
     * @param input 정규화할 성경 구절 문자열 (예: "창 1:1", "마 28:19-20")
     * @return 정규화된 성경 구절 문자열 (예: "창세기 1:1", "마태복음 28:19-20")
     */
    public String normalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        String[] parts = input.split(",");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (!part.isEmpty()) {
                if (i > 0) {
                    result.append(", ");
                }
                result.append(normalizeSinglePart(part));
            }
        }

        return result.toString();
    }

    private String normalizeSinglePart(String part) {
        String[] tokens = part.split("\\s+");
        if (tokens.length < 2) {
            return part; // 형식이 맞지 않으면 그대로 반환
        }

        String book = tokens[0];
        String normalizedBook = normalizeBookName(book);

        StringBuilder result = new StringBuilder(normalizedBook);
        for (int i = 1; i < tokens.length; i++) {
            result.append(" ").append(tokens[i]);
        }

        return result.toString();
    }

    private String normalizeBookName(String book) {
        // 한글 약어를 전체 이름으로 변환
        String[][] koreanMapping = {
            {"창", "창세기"}, {"출", "출애굽기"}, {"레", "레위기"}, {"민", "민수기"}, {"신", "신명기"},
            {"수", "여호수아"}, {"삿", "사사기"}, {"룻", "룻기"}, {"삼상", "사무엘상"}, {"삼하", "사무엘하"},
            {"왕상", "열왕기상"}, {"왕하", "열왕기하"}, {"대상", "역대상"}, {"대하", "역대하"}, {"스", "에스라"},
            {"느", "느헤미야"}, {"에", "에스더"}, {"욥", "욥기"}, {"시", "시편"}, {"잠", "잠언"},
            {"전", "전도서"}, {"아", "아가"}, {"사", "이사야"}, {"렘", "예레미야"}, {"애", "예레미야애가"},
            {"겔", "에스겔"}, {"단", "다니엘"}, {"호", "호세아"}, {"욜", "요엘"}, {"암", "아모스"},
            {"옵", "오바댜"}, {"욘", "요나"}, {"미", "미가"}, {"나", "나훔"}, {"합", "하박국"},
            {"습", "스바냐"}, {"학", "학개"}, {"슥", "스가랴"}, {"말", "말라기"},
            {"마", "마태복음"}, {"막", "마가복음"}, {"눅", "누가복음"}, {"요", "요한복음"},
            {"행", "사도행전"}, {"롬", "로마서"}, {"고전", "고린도전서"}, {"고후", "고린도후서"}, {"갈", "갈라디아서"},
            {"엡", "에베소서"}, {"빌", "빌립보서"}, {"골", "골로새서"}, {"살전", "데살로니가전서"}, {"살후", "데살로니가후서"},
            {"딤전", "디모데전서"}, {"딤후", "디모데후서"}, {"딛", "디도서"}, {"몬", "빌레몬서"}, {"히", "히브리서"},
            {"약", "야고보서"}, {"벧전", "베드로전서"}, {"벧후", "베드로후서"}, {"요일", "요한일서"}, {"요이", "요한이서"},
            {"요삼", "요한삼서"}, {"유", "유다서"}, {"계", "요한계시록"}
        };

        // 영어 약어를 전체 이름으로 변환
        String[][] englishMapping = {
            {"Gen", "Genesis"}, {"Exod", "Exodus"}, {"Lev", "Leviticus"}, {"Num", "Numbers"}, {"Deut", "Deuteronomy"},
            {"Josh", "Joshua"}, {"Judg", "Judges"}, {"Ruth", "Ruth"}, {"1 Sam", "1 Samuel"}, {"2 Sam", "2 Samuel"},
            {"1 Kgs", "1 Kings"}, {"2 Kgs", "2 Kings"}, {"1 Chr", "1 Chronicles"}, {"2 Chr", "2 Chronicles"}, {"Ezra", "Ezra"},
            {"Neh", "Nehemiah"}, {"Esth", "Esther"}, {"Job", "Job"}, {"Ps", "Psalms"}, {"Prov", "Proverbs"},
            {"Eccl", "Ecclesiastes"}, {"Song", "Song of Solomon"}, {"Isa", "Isaiah"}, {"Jer", "Jeremiah"}, {"Lam", "Lamentations"},
            {"Ezek", "Ezekiel"}, {"Dan", "Daniel"}, {"Hos", "Hosea"}, {"Joel", "Joel"}, {"Amos", "Amos"},
            {"Obad", "Obadiah"}, {"Jonah", "Jonah"}, {"Mic", "Micah"}, {"Nah", "Nahum"}, {"Hab", "Habakkuk"},
            {"Zeph", "Zephaniah"}, {"Hag", "Haggai"}, {"Zech", "Zechariah"}, {"Mal", "Malachi"},
            {"Matt", "Matthew"}, {"Mark", "Mark"}, {"Luke", "Luke"}, {"John", "John"},
            {"Acts", "Acts"}, {"Rom", "Romans"}, {"1 Cor", "1 Corinthians"}, {"2 Cor", "2 Corinthians"}, {"Gal", "Galatians"},
            {"Eph", "Ephesians"}, {"Phil", "Philippians"}, {"Col", "Colossians"}, {"1 Thess", "1 Thessalonians"}, {"2 Thess", "2 Thessalonians"},
            {"1 Tim", "1 Timothy"}, {"2 Tim", "2 Timothy"}, {"Titus", "Titus"}, {"Phlm", "Philemon"}, {"Heb", "Hebrews"},
            {"Jas", "James"}, {"1 Pet", "1 Peter"}, {"2 Pet", "2 Peter"}, {"1 John", "1 John"}, {"2 John", "2 John"},
            {"3 John", "3 John"}, {"Jude", "Jude"}, {"Rev", "Revelation"}
        };

        // 한글 약어 확인
        for (String[] mapping : koreanMapping) {
            if (mapping[0].equalsIgnoreCase(book)) {
                return mapping[1]; // 약어를 전체 이름으로 변환
            }
        }

        // 영어 약어 확인
        for (String[] mapping : englishMapping) {
            if (mapping[0].equalsIgnoreCase(book)) {
                return mapping[1]; // 약어를 전체 이름으로 변환
            }
        }

        // 약어가 아니거나 매핑이 없는 경우 원래 값 반환
        return book;
    }

    /**
     * 입력된 부분적인 성경 구절 문자열을 자동완성합니다.
     * 
     * @param partialInput 자동완성할 부분적인 성경 구절 문자열 (예: "창 1", "마 28")
     * @return 자동완성된 성경 구절 문자열 목록 (예: ["창세기 1:1", "창세기 1:2", ...])
     */
    @Override
    public List<String> autocomplete(String partialInput) {
        List<String> suggestions = new ArrayList<>();

        if (partialInput == null || partialInput.trim().isEmpty()) {
            return suggestions;
        }

        try {
            // 입력값 분석
            String[] parts = partialInput.trim().split("\\s+");

            // 책 이름만 입력된 경우 (예: "창")
            if (parts.length == 1) {
                String bookPart = parts[0];
                suggestions.addAll(autocompleteBookName(bookPart));
            }
            // 책 이름과 장 번호가 입력된 경우 (예: "창 1")
            else if (parts.length == 2) {
                String bookPart = parts[0];
                String chapterPart = parts[1];

                // 장 번호에 콜론이 있는지 확인 (예: "창 1:")
                if (chapterPart.contains(":")) {
                    String[] chapterVerseParts = chapterPart.split(":");

                    // 콜론 앞에 숫자가 없는 경우 (예: "창 :") 또는 빈 문자열인 경우
                    // 수정: chapterVerseParts.length == 0 조건 제거 (split은 항상 최소 1개 이상의 요소 반환)
                    if (chapterVerseParts.length > 0 && chapterVerseParts[0].isEmpty()) {
                        suggestions.addAll(autocompleteChapter(bookPart, "1"));
                    } else {
                        String chapter = chapterVerseParts[0];

                        // 절 번호가 없는 경우 (예: "창 1:")
                        if (chapterVerseParts.length == 1 || chapterVerseParts[1].isEmpty()) {
                            suggestions.addAll(autocompleteVerses(bookPart, chapter));
                        }
                        // 절 번호가 일부 입력된 경우 (예: "창 1:2")
                        else {
                            String verse = chapterVerseParts[1];
                            suggestions.addAll(autocompleteSpecificVerse(bookPart, chapter, verse));
                        }
                    }
                }
                // 장 번호만 입력된 경우 (예: "창 1")
                else {
                    suggestions.addAll(autocompleteChapter(bookPart, chapterPart));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "자동완성 중 오류 발생", e);
        }

        return suggestions;
    }

    /**
     * 책 이름을 자동완성합니다.
     */
    private List<String> autocompleteBookName(String bookPart) {
        List<String> suggestions = new ArrayList<>();

        // 한글 전체 이름
        for (String name : koreanFullNames) {
            if (name.startsWith(bookPart)) {
                suggestions.add(name);
            }
        }

        // 한글 약어
        for (String abbr : koreanAbbreviations) {
            if (abbr.startsWith(bookPart)) {
                String fullName = normalizeBookName(abbr);
                if (!suggestions.contains(fullName)) {
                    suggestions.add(fullName);
                }
            }
        }

        // 영어 전체 이름
        for (String name : englishFullNames) {
            if (name.toLowerCase().startsWith(bookPart.toLowerCase())) {
                suggestions.add(name);
            }
        }

        // 영어 약어
        for (String abbr : englishAbbreviations) {
            if (abbr.toLowerCase().startsWith(bookPart.toLowerCase())) {
                String fullName = normalizeBookName(abbr);
                if (!suggestions.contains(fullName)) {
                    suggestions.add(fullName);
                }
            }
        }

        return suggestions;
    }

    /**
     * 장 번호를 자동완성합니다.
     */
    private List<String> autocompleteChapter(String bookPart, String chapterPart) {
        List<String> suggestions = new ArrayList<>();
        String normalizedBook = normalizeBookName(bookPart);

        // 장 번호가 유효한 숫자인지 확인
        if (isValidNumber(chapterPart)) {
            suggestions.add(normalizedBook + " " + chapterPart + ":1");
        }

        return suggestions;
    }

    /**
     * 절 번호를 자동완성합니다.
     */
    private List<String> autocompleteVerses(String bookPart, String chapter) {
        List<String> suggestions = new ArrayList<>();
        String normalizedBook = normalizeBookName(bookPart);

        // 장 번호가 유효한 숫자인지 확인
        if (isValidNumber(chapter)) {
            suggestions.add(normalizedBook + " " + chapter + ":1");
        }

        return suggestions;
    }

    /**
     * 특정 절을 자동완성합니다.
     */
    private List<String> autocompleteSpecificVerse(String bookPart, String chapter, String verse) {
        List<String> suggestions = new ArrayList<>();
        String normalizedBook = normalizeBookName(bookPart);

        // 장과 절 번호가 유효한 숫자인지 확인
        if (isValidNumber(chapter) && isValidNumber(verse)) {
            suggestions.add(normalizedBook + " " + chapter + ":" + verse);
        }

        return suggestions;
    }

    /**
     * 입력된 성경 구절이 실제로 존재하는지 확인합니다.
     * 
     * @param input 확인할 성경 구절 문자열 (예: "창세기 1:1", "창 1:1")
     * @return 성경 구절이 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean verseExists(String input) {
        if (input == null || input.trim().isEmpty() || searchBible == null) {
            return false;
        }

        try {
            // 정규화된 입력값으로 검색
            String normalizedInput = normalize(input);
            List<String> verses = searchBible.searchVerses(normalizedInput);

            // 검색 결과가 있으면 해당 구절이 존재함
            return !verses.isEmpty();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "성경 구절 존재 여부 확인 중 오류 발생", e);
            return false;
        }
    }

    /**
     * 입력된 성경 구절을 기존 입력에 추가합니다.
     * 
     * @param currentInput 현재 입력된 성경 구절 문자열 (예: "창세기 1:1")
     * @param newVerse 추가할 성경 구절 문자열 (예: "마태복음 28:19")
     * @return 두 구절이 합쳐진 문자열 (예: "창세기 1:1, 마태복음 28:19")
     */
    @Override
    public String appendVerse(String currentInput, String newVerse) {
        if (currentInput == null || currentInput.trim().isEmpty()) {
            return newVerse;
        }

        if (newVerse == null || newVerse.trim().isEmpty()) {
            return currentInput;
        }

        // 현재 입력값의 끝에 쉼표가 있는지 확인
        if (currentInput.trim().endsWith(",")) {
            return currentInput.trim() + " " + newVerse.trim();
        } else {
            return currentInput.trim() + ", " + newVerse.trim();
        }
    }
}
