module SegwayNinebotJsonToGpxConverter.main {
    requires com.gluonhq.charm.glisten;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires gson;
    requires java.sql;

    opens com to javafx.fxml;
    opens com.app to javafx.fxml, javafx.graphics;
    opens com.app.components to javafx.fxml;

    exports com;
}