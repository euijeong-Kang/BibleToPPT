package com.ej.bibletoppt.controller;

import com.ej.bibletoppt.BibleVerseValidator;
import com.ej.bibletoppt.domain.SlideSizeType;
import com.ej.bibletoppt.controller.dto.PresentationRequest;
import com.ej.bibletoppt.infrastructure.database.SQLiteConnector;
import com.ej.bibletoppt.infrastructure.Settings;
import com.ej.bibletoppt.infrastructure.SettingsManager;
import com.ej.bibletoppt.service.command.PPTGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BibleToPPTController {

    @FXML
    private TextField inputField;
//    @FXML
//    private Button updateDBButton;

    @FXML
    private ComboBox<String> sizeComboBox;

    @FXML
    private ComboBox<String> fontComboBox;

    @FXML
    private CheckBox titleSlideCheckBox;

//    private UpdateBibleDB updateBibleDB;


    private SettingsManager settingsManager;

    public void initialize() {
        this.settingsManager = new SettingsManager(new SQLiteConnector());

        settingsManager.loadAllSettings();

        initializeSettings();

        // 초기 선택 설정
        sizeComboBox.setValue("16:9");

        titleSlideCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateTitleSlideSetting(newValue);
        });
    }

    private final PPTGenerator pptGenerator = new PPTGenerator();

    private final String DEFAULT_FILE_NAME = "output.pptx";


    @FXML
    protected void onGeneratePPTButtonClick() {
        // 입력 유효성 검사
        String bibleVerseInput = inputField.getText();

        if (!checkValid(bibleVerseInput)) {
            showAlert("입력 오류", "입력된 성경 구절 형식이 올바르지 않습니다. 입력값을 확인해주세요. 아래와 같은 형식 중 하나를 사용하세요:\n\n"
                    + "1. 풀네임 사용: 창세기 1:1\n"
                    + "2. 약칭 사용: 창 1:1\n"
                    + "3. 여러 범위 사용: 창 1:1-20\n"
                    + "4. 여러 내용 사용: 창 1:1-20, 마: 28:19-20, 시 1:1-5\n"
                    + "5. 올바른 형태: <성경챕터> <장>:<절>(-<절> *생략가능)");
            return;
        }

        // 사용자가 선택한 크기 및 글꼴 가져오기
        String selectedSize = sizeComboBox.getValue();
        String selectedFont = fontComboBox.getValue();

        // 사용자로부터 저장 경로 및 이름을 선택받음
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PowerPoint File");
        fileChooser.setInitialFileName(DEFAULT_FILE_NAME);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PowerPoint Files", "*.pptx"));
        Stage stage = (Stage) inputField.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);

        // 사용자로부터 선택 받은 정보를 PresentationRequest 객체로 묶어서 전달
        String mainTitle = file.getName().replace(".pptx", "");
        PresentationRequest request = new PresentationRequest(mainTitle
                , bibleVerseInput
                , file.toPath()
                , SlideSizeType.fromString(selectedSize)
                , selectedFont
                , titleSlideCheckBox.isSelected());

        // PPT 생성 요청
        pptGenerator.createPresentation(request);

        // 생성이 완료된 PPT 파일을 열기
        openFile(file.getAbsolutePath());
    }

    private boolean checkValid(String bibleVerseInput) {
        BibleVerseValidator verseValidator = new BibleVerseValidator();
        return verseValidator.validate(bibleVerseInput);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void openFile(String filePath) {
        try {
            File file = new File(filePath);

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("Desktop not supported. Open the file manually: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeSettings() {
        // Title Slide Option
        String titleSlideIncluded = settingsManager.getSetting("title_slide");
        if (titleSlideIncluded != null) {
            // 설정 값이 "true"이면 CheckBox를 선택된 상태로, 그렇지 않으면 선택 해제된 상태로 설정
            titleSlideCheckBox.setSelected(Boolean.parseBoolean(titleSlideIncluded));
        }

        // Font Option
        initializeFontComboBox();
    }

    private void updateTitleSlideSetting(boolean isSelected) {
        // 새로운 선택 상태를 문자열로 변환하여 설정 값을 업데이트
        settingsManager.saveSetting(new Settings("title_slide", String.valueOf(isSelected)));
    }

    private void initializeFontComboBox() {
        ObservableList<String> fontFamilies = FXCollections.observableArrayList(Font.getFamilies());
        fontComboBox.setItems(fontFamilies);

        // 기본 폰트 설정 (예: 시스템에 Arial 폰트가 설치되어 있다고 가정)
        fontComboBox.getSelectionModel().select("나눔스퀘어 Bold");
    }


    // DBUpdate를 위한
//    @FXML
//    protected void onUpdateDBButtonClick() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Open Bible Text File");
//        Stage stage = (Stage) updateDBButton.getScene().getWindow();
//        File file = fileChooser.showOpenDialog(stage);
//
//        if (file != null) {
//            updateBibleDB.updateVersesFromFile(file.getAbsolutePath()); // 파일 경로를 updateVersesFromFile 메소드에 전달
//            showAlert("업데이트 완료", "데이터베이스 업데이트가 성공적으로 완료되었습니다.");
//        }
//    }
}