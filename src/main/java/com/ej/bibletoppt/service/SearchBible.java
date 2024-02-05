package com.ej.bibletoppt.service;

import com.ej.bibletoppt.infrastructure.SQLiteConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchBible {
    private SQLiteConnector connector;

    public SearchBible(SQLiteConnector connector) {
        this.connector = connector;
    }

    public List<String> searchVerses(String input) {
        List<String> verses = new ArrayList<>();

        try (Connection connection = connector.getConnection()) {
            // 각 권의 성경 구절을 분리
            String[] verseRequests = input.split(",\\s*");

            // 각 구절에 대한 쿼리 수행
            for (String verseRequest : verseRequests) {
                // 입력값에서 책 이름과 장/절 범위 추출
                String[] parts = extractParts(verseRequest);
                String book = parts[0];
                String chapterVerseRange = parts[1];

                // 해당 정보를 활용하여 쿼리문 생성
                // paragraph와 sentence 값 조회
                String query = "SELECT long_label, paragraph, sentence FROM bible2 WHERE (long_label = ? OR short_label = ?) AND chapter = ? AND paragraph BETWEEN ? AND ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    // long_label, chapter, startVerse, endVerse 값으로 PreparedStatement 설정
                    statement.setString(1, book);
                    statement.setString(2, book);  // Assuming short_label and long_label have the same type
                    setChapterAndParagraph(statement, chapterVerseRange);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            String chapter = resultSet.getString("long_label");
                            int paragraph = resultSet.getInt("paragraph");
                            String sentence = resultSet.getString("sentence");

                            // '<'로 시작해서 '>'로 끝나는 내용 제거
                            sentence = sentence.replaceAll("<[^>]+>", "");

                            // 절과 문장 정보를 조합하여 리스트에 저장
                            verses.add(chapter+" "+chapterVerseRange + "&" + paragraph + ". " + sentence);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connector.closeConnection();
        return verses;
    }

    // 입력값에서 책 이름과 장/절 범위 추출하는 메서드
    private String[] extractParts(String input) {
        String[] parts = new String[2];

        // 입력값에서 공백을 기준으로 책 이름과 장/절 범위 추출
        String[] inputParts = input.split(" ");
        parts[0] = inputParts[0].trim(); // 책 이름
        parts[1] = inputParts[1].trim(); // 장/절 범위

        return parts;
    }

    // chapter와 paragraph 값 설정하는 메서드
    private void setChapterAndParagraph(PreparedStatement statement, String chapterVerseRange) throws SQLException {
        String[] rangeParts = chapterVerseRange.split(":");
        int chapter = Integer.parseInt(rangeParts[0].replaceAll("[^0-9]", ""));

        String[] verseRange = rangeParts[1].split("-");
        int startVerse = Integer.parseInt(verseRange[0]);
        int endVerse = (verseRange.length == 2) ? Integer.parseInt(verseRange[1]) : startVerse;

        statement.setInt(3, chapter);
        statement.setInt(4, startVerse);
        statement.setInt(5, endVerse);
    }
}
