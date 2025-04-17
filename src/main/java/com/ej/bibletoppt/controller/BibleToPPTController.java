package com.ej.bibletoppt.controller;

import com.ej.bibletoppt.domain.SlideSizeType;
import com.ej.bibletoppt.controller.dto.PresentationRequest;
import com.ej.bibletoppt.infrastructure.Settings;
import com.ej.bibletoppt.infrastructure.ISettingsManager;
import com.ej.bibletoppt.infrastructure.di.DependencyContainer;
import com.ej.bibletoppt.service.IBibleVerseValidator;
import com.ej.bibletoppt.service.command.IPPTGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class BibleToPPTController {
    private static final Logger LOGGER = Logger.getLogger(BibleToPPTController.class.getName());

    @FXML
    private TextField inputField;

    @FXML
    private ComboBox<String> sizeComboBox;

    @FXML
    private ComboBox<String> fontComboBox;

    @FXML
    private CheckBox titleSlideCheckBox;


    @FXML
    private VBox verseManagementList;

    @FXML
    private StackPane previewPane;

    @FXML
    private Label previewLabel;

    @FXML
    private Pane adPlaceholder;

    private final ISettingsManager settingsManager;
    private final IPPTGenerator pptGenerator;
    private final IBibleVerseValidator bibleVerseValidator;

    private String currentVerses = "";
    private ObservableList<VerseItem> verseItems = FXCollections.observableArrayList();

    public BibleToPPTController() {
        // 의존성 주입 컨테이너에서 서비스 가져오기
        DependencyContainer container = DependencyContainer.getInstance();
        this.settingsManager = container.getSettingsManager();
        this.pptGenerator = container.getPPTGenerator();
        this.bibleVerseValidator = container.getBibleVerseValidator();
    }

    public void initialize() {
        settingsManager.loadAllSettings();

        initializeSettings();

        // 초기 선택 설정
        sizeComboBox.setValue("16:9");

        titleSlideCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateTitleSlideSetting(newValue);
        });

        // 입력 필드에 키 이벤트 리스너 추가
        inputField.setOnKeyPressed(this::handleInputKeyPress);

        // 구절 관리 목록 초기화
        initializeVerseManagementList();

        // 광고 영역 초기화 (실제 광고 로직은 여기에 추가)
        // 현재는 플레이스홀더만 표시
    }

    /**
     * 구절 관리 목록을 초기화합니다.
     */
    private void initializeVerseManagementList() {
        // 기존 항목 제거
        verseManagementList.getChildren().clear();
    }


    private void handleInputKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            // Enter 키를 누르면 현재 입력된 구절을 추가
            String currentInput = inputField.getText().trim();
            if (!currentInput.isEmpty()) {
                if (bibleVerseValidator.validate(currentInput)) {
                    if (bibleVerseValidator.verseExists(currentInput)) {
                        addVerseToInput(currentInput);
                        inputField.clear();
                    } else {
                        showAlert("유효하지 않은 구절", "입력한 성경 구절이 존재하지 않습니다.");
                    }
                } else {
                    showAlert("유효하지 않은 형식", "입력한 성경 구절 형식이 올바르지 않습니다.");
                }
            }
        }
    }


    private void addVerseToInput(String verse) {
        // 정규화된 구절로 변환
        String normalizedVerse = bibleVerseValidator.normalize(verse);

        // 중복 검사
        for (VerseItem item : verseItems) {
            if (item.getText().equals(normalizedVerse)) {
                return; // 이미 존재하는 구절이면 추가하지 않음
            }
        }

        // 새 구절 항목 생성 및 추가
        int position = verseItems.size();
        VerseItem newItem = new VerseItem(normalizedVerse, position);
        verseItems.add(newItem);

        // UI에 구절 항목 추가
        addVerseItemToUI(newItem);

        // 현재 구절 문자열 업데이트
        updateCurrentVerses();

        // 입력 필드 초기화
        inputField.clear();

        // 미리보기 영역 업데이트
        updatePreview();
    }

    /**
     * 구절 항목을 UI에 추가합니다.
     * 
     * @param item 추가할 구절 항목
     */
    private void addVerseItemToUI(VerseItem item) {
        // 구절 항목 컨테이너 생성
        HBox verseItemContainer = new HBox();
        verseItemContainer.setSpacing(10);
        verseItemContainer.setAlignment(Pos.CENTER_LEFT);
        verseItemContainer.setPrefWidth(260);
        verseItemContainer.setStyle("-fx-padding: 5; -fx-background-color: #F9F7FD; -fx-background-radius: 4;");

        // 구절 텍스트 레이블
        Label verseLabel = new Label(item.getText());
        verseLabel.setStyle("-fx-font-weight: bold;");
        verseLabel.setPrefWidth(160);

        // 삭제 버튼
        Button removeButton = new Button("X");
        removeButton.setStyle("-fx-background-color: #FFCCCC; -fx-text-fill: #333333; -fx-font-weight: bold; " +
                             "-fx-background-radius: 10; -fx-min-width: 20; -fx-min-height: 20; -fx-padding: 0;");
        removeButton.setOnAction(e -> removeVerse(item));

        // 위로 이동 버튼
        Button upButton = new Button("▲");
        upButton.setStyle("-fx-background-color: #E6E6E6; -fx-text-fill: #333333; -fx-font-weight: bold; " +
                         "-fx-background-radius: 4; -fx-min-width: 20; -fx-min-height: 20; -fx-padding: 0;");
        upButton.setOnAction(e -> moveVerseUp(item));

        // 아래로 이동 버튼
        Button downButton = new Button("▼");
        downButton.setStyle("-fx-background-color: #E6E6E6; -fx-text-fill: #333333; -fx-font-weight: bold; " +
                           "-fx-background-radius: 4; -fx-min-width: 20; -fx-min-height: 20; -fx-padding: 0;");
        downButton.setOnAction(e -> moveVerseDown(item));

        // 컨테이너에 컴포넌트 추가
        verseItemContainer.getChildren().addAll(verseLabel, removeButton, upButton, downButton);

        // 구절 관리 목록에 추가
        verseManagementList.getChildren().add(verseItemContainer);
    }

    /**
     * 구절을 제거합니다.
     * 
     * @param item 제거할 구절 항목
     */
    private void removeVerse(VerseItem item) {
        // 목록에서 항목 제거
        verseItems.remove(item);

        // 위치 정보 업데이트
        for (int i = 0; i < verseItems.size(); i++) {
            verseItems.get(i).setPosition(i);
        }

        // UI 업데이트
        updateVerseManagementList();

        // 현재 구절 문자열 업데이트
        updateCurrentVerses();

        // 미리보기 업데이트
        updatePreview();
    }

    /**
     * 구절을 위로 이동합니다.
     * 
     * @param item 이동할 구절 항목
     */
    private void moveVerseUp(VerseItem item) {
        int position = item.getPosition();
        if (position > 0) {
            // 위치 교환
            VerseItem upperItem = verseItems.get(position - 1);
            item.setPosition(position - 1);
            upperItem.setPosition(position);

            // 목록 재정렬
            FXCollections.sort(verseItems, (a, b) -> Integer.compare(a.getPosition(), b.getPosition()));

            // UI 업데이트
            updateVerseManagementList();

            // 현재 구절 문자열 업데이트
            updateCurrentVerses();

            // 미리보기 업데이트
            updatePreview();
        }
    }

    /**
     * 구절을 아래로 이동합니다.
     * 
     * @param item 이동할 구절 항목
     */
    private void moveVerseDown(VerseItem item) {
        int position = item.getPosition();
        if (position < verseItems.size() - 1) {
            // 위치 교환
            VerseItem lowerItem = verseItems.get(position + 1);
            item.setPosition(position + 1);
            lowerItem.setPosition(position);

            // 목록 재정렬
            FXCollections.sort(verseItems, (a, b) -> Integer.compare(a.getPosition(), b.getPosition()));

            // UI 업데이트
            updateVerseManagementList();

            // 현재 구절 문자열 업데이트
            updateCurrentVerses();

            // 미리보기 업데이트
            updatePreview();
        }
    }

    /**
     * 구절 관리 목록 UI를 업데이트합니다.
     */
    private void updateVerseManagementList() {
        // 기존 항목 제거
        verseManagementList.getChildren().clear();

        // 항목 다시 추가
        for (VerseItem item : verseItems) {
            addVerseItemToUI(item);
        }
    }

    /**
     * 현재 구절 문자열을 업데이트합니다.
     */
    private void updateCurrentVerses() {
        if (verseItems.isEmpty()) {
            currentVerses = "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < verseItems.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(verseItems.get(i).getText());
            }
            currentVerses = sb.toString();
        }
    }

    /**
     * 미리보기 영역을 현재 선택된 구절로 업데이트합니다.
     * 실제 PPT 슬라이드와 유사한 모양으로 표시합니다.
     */
    private void updatePreview() {
        // 기존 내용 제거
        previewPane.getChildren().clear();

        // 슬라이드 배경 생성 (검은색 배경)
        javafx.scene.layout.Pane slideBackground = new javafx.scene.layout.Pane();
        slideBackground.setPrefSize(previewPane.getPrefWidth() - 40, previewPane.getPrefHeight() - 40);
        slideBackground.setStyle("-fx-background-color: #000000; -fx-background-radius: 5;");

        if (currentVerses.isEmpty()) {
            // 구절이 없는 경우 안내 메시지 표시
            javafx.scene.control.Label noVersesLabel = new javafx.scene.control.Label("선택된 구절 없음");
            noVersesLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");
            noVersesLabel.setLayoutX(slideBackground.getPrefWidth() / 2 - 80);
            noVersesLabel.setLayoutY(slideBackground.getPrefHeight() / 2 - 10);
            slideBackground.getChildren().add(noVersesLabel);
        } else {
            // 구절이 있는 경우 구절 표시
            String[] parts = currentVerses.split(",");
            if (parts.length > 0) {
                // 첫 번째 구절의 참조 부분을 제목으로 표시
                String reference = parts[0].trim();
                javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(reference);
                titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white;");
                titleLabel.setLayoutX(20);
                titleLabel.setLayoutY(20);
                slideBackground.getChildren().add(titleLabel);

                // 모든 구절을 내용으로 표시
                javafx.scene.control.Label contentLabel = new javafx.scene.control.Label(currentVerses);
                contentLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white; -fx-wrap-text: true;");
                contentLabel.setPrefWidth(slideBackground.getPrefWidth() - 40);
                contentLabel.setLayoutX(20);
                contentLabel.setLayoutY(60);
                slideBackground.getChildren().add(contentLabel);
            }
        }

        // 미리보기 영역에 슬라이드 추가
        previewPane.getChildren().add(slideBackground);
    }

    /**
     * 현재 선택된 구절을 모두 지웁니다.
     */
    @FXML
    protected void onClearButtonClick() {
        // 구절 목록 초기화
        verseItems.clear();
        verseManagementList.getChildren().clear();

        // 현재 구절 문자열 초기화
        currentVerses = "";

        // 입력 필드 초기화
        inputField.clear();

        // 미리보기 업데이트
        updatePreview();
    }

    private final String DEFAULT_FILE_NAME = "output.pptx";


    @FXML
    protected void onGeneratePPTButtonClick() {
        // 입력 유효성 검사
        String bibleVerseInput = currentVerses;

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
        fileChooser.setTitle("파워포인트 파일 저장");
        fileChooser.setInitialFileName(DEFAULT_FILE_NAME);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("파워포인트 파일", "*.pptx"));
        Stage stage = (Stage) inputField.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);

        // 사용자가 파일 선택을 취소한 경우
        if (file == null) {
            return;
        }

        // 사용자로부터 선택 받은 정보를 PresentationRequest 객체로 묶어서 전달
        String mainTitle = file.getName().replace(".pptx", "");
        PresentationRequest request = new PresentationRequest(mainTitle
                , bibleVerseInput
                , file.toPath()
                , SlideSizeType.fromString(selectedSize)
                , selectedFont
                , titleSlideCheckBox.isSelected());

        // PPT 생성 요청
        try {
            pptGenerator.createPresentation(request);

            // 생성이 완료된 PPT 파일을 열기
            openFile(file.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "PPT 생성 중 오류 발생", e);
            showAlert("PPT 생성 오류", "PPT 파일 생성 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 구절 목록 초기화
        verseItems.clear();
        verseManagementList.getChildren().clear();

        // 현재 구절 문자열 초기화
        currentVerses = "";

        // 입력 필드 초기화
        inputField.clear();

        // 미리보기 영역 업데이트
        updatePreview();
    }

    private boolean checkValid(String bibleVerseInput) {
        if (bibleVerseInput == null || bibleVerseInput.trim().isEmpty()) {
            return false;
        }

        // 형식 검사
        if (!bibleVerseValidator.validate(bibleVerseInput)) {
            return false;
        }

        // 실제 존재하는 구절인지 검사
        String[] parts = bibleVerseInput.split(",\\s*");
        for (String part : parts) {
            if (!bibleVerseValidator.verseExists(part.trim())) {
                showAlert("유효하지 않은 구절", "입력한 성경 구절 중 '" + part.trim() + "'이(가) 존재하지 않습니다.");
                return false;
            }
        }

        return true;
    }

    private void showAlert(String title, String content) {
        if (title == null) {
            title = "오류";
        }
        if (content == null) {
            content = "알 수 없는 오류가 발생했습니다.";
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void openFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return;
        }

        try {
            File file = new File(filePath);

            if (!file.exists()) {
                LOGGER.warning("파일이 존재하지 않습니다: " + filePath);
                return;
            }

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                LOGGER.warning("데스크톱 지원이 되지 않습니다. 수동으로 파일을 열어주세요: " + filePath);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "파일을 열 수 없습니다: " + filePath, e);
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
}
