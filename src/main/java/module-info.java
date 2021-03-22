module SegwayNinebotRidingRecordToGpxConverter.main {
    requires com.gluonhq.charm.glisten;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires gson;
    requires java.sql;

    opens com.maatss to javafx.fxml;
    opens com.maatss.app to javafx.fxml, javafx.graphics;
    opens com.maatss.app.components to javafx.fxml;

    exports com.maatss;
}
