module com.ej.bibletoppt {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;
    requires java.sql;
    requires java.desktop;


    opens com.ej.bibletoppt to javafx.fxml;
    exports com.ej.bibletoppt;
    exports com.ej.bibletoppt.service;
    opens com.ej.bibletoppt.service to javafx.fxml;
    exports com.ej.bibletoppt.infrastructure;
    opens com.ej.bibletoppt.infrastructure to javafx.fxml;
    exports com.ej.bibletoppt.controller;
    opens com.ej.bibletoppt.controller to javafx.fxml;
    exports com.ej.bibletoppt.controller.dto;
    opens com.ej.bibletoppt.controller.dto to javafx.fxml;
}