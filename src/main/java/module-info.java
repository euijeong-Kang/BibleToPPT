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
}