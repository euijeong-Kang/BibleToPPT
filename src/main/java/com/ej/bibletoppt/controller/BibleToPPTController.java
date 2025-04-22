package com.ej.bibletoppt.controller;

import com.ej.bibletoppt.domain.SlideSizeType;
import com.ej.bibletoppt.controller.dto.PresentationRequest;
import com.ej.bibletoppt.infrastructure.Settings;
import com.ej.bibletoppt.infrastructure.ISettingsManager;
import com.ej.bibletoppt.infrastructure.di.ServiceConfiguration;
import com.ej.bibletoppt.infrastructure.di.ServiceLocator;
import com.ej.bibletoppt.service.IBibleVerseValidator;
import com.ej.bibletoppt.service.IPreviewService;
import com.ej.bibletoppt.service.IVerseManagementService;
import com.ej.bibletoppt.service.command.IPPTGenerator;
import com.ej.bibletoppt.service.query.ISearchBible;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;

import java.net.URI;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
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
    private ComboBox<String> fontSizeComboBox;

    @FXML
    private CheckBox titleSlideCheckBox;


    @FXML
    private VBox verseManagementList;

    @FXML
    private StackPane previewPane;

    @FXML
    private Label previewLabel;

    @FXML
    private WebView adWebView;

    private WebEngine webEngine;

    private final ISettingsManager settingsManager;
    private final IPPTGenerator pptGenerator;
    private final IBibleVerseValidator bibleVerseValidator;
    private final ISearchBible searchBible;
    private final IVerseManagementService verseManagementService;
    private final IPreviewService previewService;

    private String currentVerses = "";
    private final ObservableList<VerseItem> verseItems = FXCollections.observableArrayList();

    public BibleToPPTController() {
        // 서비스 로케이터에서 서비스 가져오기
        ServiceLocator serviceLocator = ServiceConfiguration.getServiceLocator();
        this.settingsManager = serviceLocator.get(ISettingsManager.class);
        this.pptGenerator = serviceLocator.get(IPPTGenerator.class);
        this.bibleVerseValidator = serviceLocator.get(IBibleVerseValidator.class);
        this.searchBible = serviceLocator.get(ISearchBible.class);
        this.verseManagementService = serviceLocator.get(IVerseManagementService.class);
        this.previewService = serviceLocator.get(IPreviewService.class);
    }

    public void initialize() {
        settingsManager.loadAllSettings();

        initializeSettings();

        titleSlideCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateTitleSlideSetting(newValue);
        });

        // 입력 필드에 키 이벤트 리스너 추가
        inputField.setOnKeyPressed(this::handleInputKeyPress);

        // 구절 관리 목록 초기화
        initializeVerseManagementList();

        // 광고 영역 초기화
        initializeWebView();
    }

    /**
     * 구절 관리 목록을 초기화합니다.
     */
    private void initializeVerseManagementList() {
        // 기존 항목 제거
        verseManagementList.getChildren().clear();
    }

    /**
     * 광고 영역의 WebView를 초기화합니다.
     */
    private void initializeWebView() {
        webEngine = adWebView.getEngine();

        // 외부 웹페이지 로드
        loadAdvertisement();

        // 오류 처리
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.FAILED) {
                System.err.println("웹페이지 로드 실패");
                // 오류 발생 시 대체 콘텐츠 표시
                webEngine.loadContent("<html><body><h3>광고를 불러올 수 없습니다.</h3></body></html>");
            }
        });

        // 자바스크립트 활성화
        webEngine.setJavaScriptEnabled(true);

        // 보안 정책 설정
        webEngine.setUserAgent("Mozilla/5.0 JavaFX WebView");

        // 외부 링크를 시스템 브라우저에서 열기
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(getAdUrl())) {
                webEngine.load(getAdUrl()); // 원래 광고 URL로 되돌리기

                // 시스템 브라우저에서 링크 열기
                try {
                    Desktop.getDesktop().browse(new URI(newValue));
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "외부 링크 열기 실패", e);
                }
            }
        });
    }

    /**
     * 광고 URL을 가져옵니다.
     * 먼저 application.properties 파일에서 adUrl 속성을 찾고,
     * 없는 경우 기본 URL을 사용합니다.
     * 
     * @return 광고 URL
     */
    private String getAdUrl() {
        // application.properties에서 광고 URL 가져오기
        String adUrl = settingsManager.getSetting("adUrl");

        // 설정이 없으면 기본 URL 사용
        if (adUrl == null || adUrl.isEmpty()) {
            adUrl = "https://example.com/ads";
        }

        return adUrl;
    }

    /**
     * 광고를 로드합니다.
     * application.properties에서 설정된 adUrl을 사용합니다.
     */
    private void loadAdvertisement() {
        // application.properties에서 설정된 광고 URL 로드
        String adUrl = getAdUrl();

        // 광고 URL 로드
        webEngine.load(adUrl);
    }


    private void handleInputKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            // Enter 키를 누르면 현재 입력된 구절을 추가
            String currentInput = inputField.getText().trim();
            if (!currentInput.isEmpty()) {
                if (bibleVerseValidator.validate(currentInput)) {
                    if (bibleVerseValidator.verseExists(currentInput)) {
                        VerseItem newItem = verseManagementService.addVerse(currentInput);
                        if (newItem != null) {
                            // UI에 구절 항목 추가
                            addVerseItemToUI(newItem);
                            inputField.clear();
                            // 미리보기 업데이트
                            updatePreview();
                        }
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
        verseItemContainer.setPrefWidth(280);
        verseItemContainer.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 8; " +
                                   "-fx-border-color: #E9ECEF; -fx-border-radius: 8; " +
                                   "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        // 구절 텍스트 레이블
        Label verseLabel = new Label(item.getText());
        verseLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #212529; -fx-font-size: 13;");
        verseLabel.setPrefWidth(170);
        verseLabel.setWrapText(true);

        // 버튼 컨테이너
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(5);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

        // 삭제 버튼
        Button removeButton = new Button("✕");
        removeButton.setStyle("-fx-background-color: #F8D7DA; -fx-text-fill: #721C24; -fx-font-weight: bold; " +
                             "-fx-background-radius: 4; -fx-min-width: 28; -fx-min-height: 28; -fx-padding: 0; " +
                             "-fx-cursor: hand;");
        removeButton.setOnAction(e -> removeVerse(item));

        // 마우스 오버 효과
        removeButton.setOnMouseEntered(e -> 
            removeButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; " +
                                 "-fx-background-radius: 4; -fx-min-width: 28; -fx-min-height: 28; -fx-padding: 0; " +
                                 "-fx-cursor: hand;"));
        removeButton.setOnMouseExited(e -> 
            removeButton.setStyle("-fx-background-color: #F8D7DA; -fx-text-fill: #721C24; -fx-font-weight: bold; " +
                                 "-fx-background-radius: 4; -fx-min-width: 28; -fx-min-height: 28; -fx-padding: 0; " +
                                 "-fx-cursor: hand;"));

        // 위로 이동 버튼
        Button upButton = new Button("▲");
        upButton.setStyle("-fx-background-color: #E2E6EA; -fx-text-fill: #495057; -fx-font-weight: bold; " +
                         "-fx-background-radius: 4; -fx-min-width: 28; -fx-min-height: 28; -fx-padding: 0; " +
                         "-fx-cursor: hand;");
        upButton.setOnAction(e -> moveVerseUp(item));

        // 마우스 오버 효과
        upButton.setOnMouseEntered(e -> 
            upButton.setStyle("-fx-background-color: #CED4DA; -fx-text-fill: #212529; -fx-font-weight: bold; " +
                             "-fx-background-radius: 4; -fx-min-width: 28; -fx-min-height: 28; -fx-padding: 0; " +
                             "-fx-cursor: hand;"));
        upButton.setOnMouseExited(e -> 
            upButton.setStyle("-fx-background-color: #E2E6EA; -fx-text-fill: #495057; -fx-font-weight: bold; " +
                             "-fx-background-radius: 4; -fx-min-width: 28; -fx-min-height: 28; -fx-padding: 0; " +
                             "-fx-cursor: hand;"));

        // 아래로 이동 버튼
        Button downButton = new Button("▼");
        downButton.setStyle("-fx-background-color: #E2E6EA; -fx-text-fill: #495057; -fx-font-weight: bold; " +
                           "-fx-background-radius: 4; -fx-min-width: 28; -fx-min-height: 28; -fx-padding: 0; " +
                           "-fx-cursor: hand;");
        downButton.setOnAction(e -> moveVerseDown(item));

        // 마우스 오버 효과
        downButton.setOnMouseEntered(e -> 
            downButton.setStyle("-fx-background-color: #CED4DA; -fx-text-fill: #212529; -fx-font-weight: bold; " +
                               "-fx-background-radius: 4; -fx-min-width: 28; -fx-min-height: 28; -fx-padding: 0; " +
                               "-fx-cursor: hand;"));
        downButton.setOnMouseExited(e -> 
            downButton.setStyle("-fx-background-color: #E2E6EA; -fx-text-fill: #495057; -fx-font-weight: bold; " +
                               "-fx-background-radius: 4; -fx-min-width: 28; -fx-min-height: 28; -fx-padding: 0; " +
                               "-fx-cursor: hand;"));

        // 컨테이너에 컴포넌트 추가
        buttonContainer.getChildren().addAll(removeButton, upButton, downButton);
        verseItemContainer.getChildren().addAll(verseLabel, buttonContainer);

        // 구절 관리 목록에 추가
        verseManagementList.getChildren().add(verseItemContainer);
    }

    /**
     * 구절을 제거합니다.
     * 
     * @param item 제거할 구절 항목
     */
    private void removeVerse(VerseItem item) {
        // 서비스를 통해 구절 제거
        verseManagementService.removeVerse(item);

        // UI 업데이트
        updateVerseManagementList();

        // 미리보기 업데이트
        updatePreview();
    }

    /**
     * 구절을 위로 이동합니다.
     * 
     * @param item 이동할 구절 항목
     */
    private void moveVerseUp(VerseItem item) {
        // 서비스를 통해 구절 위로 이동
        if (verseManagementService.moveVerseUp(item)) {
            // UI 업데이트
            updateVerseManagementList();

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
        // 서비스를 통해 구절 아래로 이동
        if (verseManagementService.moveVerseDown(item)) {
            // UI 업데이트
            updateVerseManagementList();

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

        // 슬라이드 비율 유지 (16:9)
        double aspectRatio = 16.0 / 9.0;
        double width = previewPane.getPrefWidth() - 60;
        double height = width / aspectRatio;

        // 슬라이드가 너무 크면 높이 기준으로 조정
        if (height > previewPane.getPrefHeight() - 60) {
            height = previewPane.getPrefHeight() - 60;
            width = height * aspectRatio;
        }

        // 슬라이드 배경 생성 (검은색 배경)
        javafx.scene.layout.StackPane slideBackground = new javafx.scene.layout.StackPane();
        slideBackground.setPrefSize(width, height);
        slideBackground.setStyle("-fx-background-color: #000000; -fx-background-radius: 8; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        // 슬라이드 내용을 담을 컨테이너
        javafx.scene.layout.VBox slideContent = new javafx.scene.layout.VBox();
        slideContent.setAlignment(Pos.TOP_LEFT);
        slideContent.setSpacing(20);
        slideContent.setPadding(new javafx.geometry.Insets(30, 40, 30, 40));
        slideContent.setPrefWidth(width - 20);
        slideContent.setMaxWidth(width - 20);

        if (currentVerses.isEmpty()) {
            // 구절이 없는 경우 안내 메시지 표시
            javafx.scene.layout.VBox emptyContent = new javafx.scene.layout.VBox();
            emptyContent.setAlignment(Pos.CENTER);
            emptyContent.setPrefSize(width, height);

            javafx.scene.control.Label noVersesLabel = new javafx.scene.control.Label("선택된 구절 없음");
            noVersesLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: white;");

            javafx.scene.control.Label instructionLabel = new javafx.scene.control.Label("왼쪽에서 성경 구절을 입력하세요");
            instructionLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #ADB5BD;");

            emptyContent.getChildren().addAll(noVersesLabel, instructionLabel);
            slideBackground.getChildren().add(emptyContent);
        } else {
            // 구절이 있는 경우 구절 표시
            String[] parts = currentVerses.split(",");
            if (parts.length > 0) {
                // 첫 번째 구절의 참조 부분을 제목으로 표시
                String reference = parts[0].trim();
                javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(reference);
                titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
                titleLabel.setWrapText(true);
                titleLabel.setPrefWidth(width - 80);
                slideContent.getChildren().add(titleLabel);

                // 첫 번째 구절의 실제 내용을 가져오기
                List<String> verseTexts = searchBible.searchVerses(reference);
                String verseContent = "";

                if (!verseTexts.isEmpty()) {
                    // 첫 번째 구절의 내용 추출 (& 이후의 텍스트)
                    String fullVerse = verseTexts.get(0);
                    String[] verseParts = fullVerse.split("&", 2);
                    if (verseParts.length > 1) {
                        verseContent = verseParts[1].trim();
                    }
                }

                // 구절 내용 표시를 위한 스크롤 패널
                javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
                scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(height - 100);

                // 구절 내용을 담을 VBox
                javafx.scene.layout.VBox versesContainer = new javafx.scene.layout.VBox();
                versesContainer.setSpacing(15);

                // 첫 번째 구절의 실제 내용 표시
                if (!verseContent.isEmpty()) {
                    javafx.scene.control.Label contentLabel = new javafx.scene.control.Label(verseContent);
                    contentLabel.setStyle("-fx-font-size: 18; -fx-font-weight: normal; -fx-text-fill: white; -fx-wrap-text: true;");
                    contentLabel.setWrapText(true);
                    contentLabel.setPrefWidth(width - 100);
                    versesContainer.getChildren().add(contentLabel);
                }

                // 프리뷰에는 첫번째 구절만 표시하고 이후 구절은 표시하지 않음
                // 이슈 요구사항: 프리뷰에는 첫번째로 찾고자 하는 구문만 표시

                scrollPane.setContent(versesContainer);
                slideContent.getChildren().add(scrollPane);
            }

            slideBackground.getChildren().add(slideContent);
        }

        // 미리보기 영역에 슬라이드 추가 (중앙 정렬)
        previewPane.setAlignment(Pos.CENTER);
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

        // 사용자가 선택한 크기, 글꼴, 글자 크기 가져오기
        String selectedSize = sizeComboBox.getValue();
        String selectedFont = fontComboBox.getValue();
        String selectedFontSize = fontSizeComboBox.getValue();

        // 글자 크기 기본값 설정
        double bodyFontSize = 65.0; // 기본값
        if (selectedFontSize != null && !selectedFontSize.isEmpty()) {
            try {
                bodyFontSize = Double.parseDouble(selectedFontSize);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "글자 크기 변환 중 오류 발생", e);
            }
        }

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
                , titleSlideCheckBox.isSelected()
                , bodyFontSize);

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

        // Slide Size Option
        initializeSizeComboBox();

        // Font Option
        initializeFontComboBox();

        // Font Size Option
        initializeFontSizeComboBox();
    }

    private void initializeFontSizeComboBox() {
        // 일반적인 글자 크기 옵션 추가
        ObservableList<String> fontSizes = FXCollections.observableArrayList(
            "36", "40", "44", "48", "54", "60", "65", "72", "80", "88", "96"
        );
        fontSizeComboBox.setItems(fontSizes);

        // 기본 글자 크기 설정
        String savedFontSize = settingsManager.getSetting("body_font_size");
        if (savedFontSize != null) {
            fontSizeComboBox.getSelectionModel().select(savedFontSize);
        } else {
            fontSizeComboBox.getSelectionModel().select("65"); // 기본값
        }

        // 글자 크기 변경 시 설정 저장
        fontSizeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                settingsManager.saveSetting(new Settings("body_font_size", newValue));
            }
        });
    }

    private void updateTitleSlideSetting(boolean isSelected) {
        // 새로운 선택 상태를 문자열로 변환하여 설정 값을 업데이트
        settingsManager.saveSetting(new Settings("title_slide", String.valueOf(isSelected)));
    }

    /**
     * 슬라이드 크기 ComboBox를 초기화합니다.
     * SlideSizeType 열거형에 정의된 모든 슬라이드 크기 옵션을 추가하고,
     * 사용자의 이전 선택 값을 로드하거나 기본값을 설정합니다.
     */
    private void initializeSizeComboBox() {
        // 슬라이드 크기 옵션 추가 (SlideSizeType 열거형에 정의된 모든 옵션)
        ObservableList<String> slideSizes = FXCollections.observableArrayList(
            "16:9", "4:3", "16:10", "A4"
        );
        sizeComboBox.setItems(slideSizes);

        // 기본 슬라이드 크기 설정
        String savedSlideSize = settingsManager.getSetting("slide_size");
        if (savedSlideSize != null) {
            sizeComboBox.getSelectionModel().select(savedSlideSize);
        } else {
            sizeComboBox.getSelectionModel().select("16:9"); // 기본값
        }

        // 슬라이드 크기 변경 시 설정 저장
        sizeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                settingsManager.saveSetting(new Settings("slide_size", newValue));
            }
        });
    }

    private void initializeFontComboBox() {
        ObservableList<String> fontFamilies = FXCollections.observableArrayList(Font.getFamilies());
        fontComboBox.setItems(fontFamilies);

        // 기본 폰트 설정 (예: 시스템에 Arial 폰트가 설치되어 있다고 가정)
        fontComboBox.getSelectionModel().select("나눔스퀘어 Bold");
    }
}
