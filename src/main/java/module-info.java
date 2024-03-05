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


    exports com.ej.bibletoppt.infrastructure;
    opens com.ej.bibletoppt.infrastructure to javafx.fxml;
    exports com.ej.bibletoppt.controller;
    opens com.ej.bibletoppt.controller to javafx.fxml;
    exports com.ej.bibletoppt.controller.dto;
    opens com.ej.bibletoppt.controller.dto to javafx.fxml;
    exports com.ej.bibletoppt.infrastructure.database;
    opens com.ej.bibletoppt.infrastructure.database to javafx.fxml;
    exports com.ej.bibletoppt.service.command;
    opens com.ej.bibletoppt.service.command to javafx.fxml;
    exports com.ej.bibletoppt.infrastructure.database.command;
    opens com.ej.bibletoppt.infrastructure.database.command to javafx.fxml;
    exports com.ej.bibletoppt.service.query;
    opens com.ej.bibletoppt.service.query to javafx.fxml;
    exports com.ej.bibletoppt.domain;
    opens com.ej.bibletoppt.domain to javafx.fxml;
}