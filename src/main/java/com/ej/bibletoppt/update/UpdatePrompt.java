package com.ej.bibletoppt.update;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class UpdatePrompt {

    public static void showUpdateDialog(String currentVersion, String latestVersion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("업데이트 확인");
        alert.setHeaderText("새로운 버전이 있습니다.");
        alert.setContentText("현재 버전: " + currentVersion + "\n최신 버전: " + latestVersion + "\n업데이트하시겠습니까?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: 추후 설치 파일 다운로드 및 실행 처리
            System.out.println("업데이트 실행 준비...");
        }
    }
}