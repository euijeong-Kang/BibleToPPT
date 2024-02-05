package com.ej.bibletoppt.service;

import com.ej.bibletoppt.infrastructure.SQLiteConnector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateBibleDB {

    private SQLiteConnector connector;

    public UpdateBibleDB(SQLiteConnector connector) {
        this.connector = connector;
    }

    public void updateVersesFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder sentenceBuilder = new StringBuilder();
            String previousBookChapterVerse = null;
            Connection connection = connector.getConnection();

            while ((line = br.readLine()) != null) {
                line = line.replaceAll("<[^>]+>", ""); // 모든 태그 제거
                // 책 이름, 장, 절을 포함하는 참조 부분 추출
                Matcher matcher = Pattern.compile("^(\\D+\\d+:\\d+)(?:-\\d+)?\\s").matcher(line);
                if (matcher.find()) {
                    String currentBookChapterVerse = matcher.group(1);
                    if (previousBookChapterVerse != null && !previousBookChapterVerse.equals(currentBookChapterVerse)) {
                        // 이전 구절을 데이터베이스에 업데이트
                        processAndUpdatVerse(connection, previousBookChapterVerse, sentenceBuilder.toString());
                        sentenceBuilder.setLength(0); // StringBuilder 초기화
                    }
                    previousBookChapterVerse = currentBookChapterVerse;
                    line = line.substring(matcher.end()).trim(); // 실제 성경 구절 내용만 추출
                }
                // 성경 구절 내용 추가
                if (sentenceBuilder.length() > 0) sentenceBuilder.append(" ");
                sentenceBuilder.append(line);
            }
            // 마지막 구절 업데이트
            if (previousBookChapterVerse != null) {
                processAndUpdatVerse(connection, previousBookChapterVerse, sentenceBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processAndUpdatVerse(Connection conn, String bookChapterVerse, String sentence) {
        try {
            String[] parts = bookChapterVerse.split(":");
            String book = parts[0].replaceAll("\\d", ""); // 책 이름 추출
            int chapter = Integer.parseInt(parts[0].replaceAll("\\D", "")); // 장 번호 추출
            int verse = Integer.parseInt(parts[1]); // 절 번호 추출

            String query = "UPDATE bible2 SET sentence = ? WHERE short_label = ? AND chapter = ? AND paragraph = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, sentence);
                statement.setString(2, book);
                statement.setInt(3, chapter);
                statement.setInt(4, verse);

                int updatedRows = statement.executeUpdate();
                System.out.println("Updated " + updatedRows + " rows for " + bookChapterVerse);
            }
        } catch (SQLException e) {
            System.err.println("Error updating database for " + bookChapterVerse);
            e.printStackTrace();
        }
    }
}
