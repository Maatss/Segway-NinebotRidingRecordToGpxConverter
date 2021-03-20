package com.app.components;

import com.converter.ConversionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class SummaryComponent extends Component implements Initializable {
    private static final String COMPONENT_TITLE = "Summary";
    private static final SimpleDateFormat LOG_TIMESTAMP_FORMAT = new SimpleDateFormat("[HH:mm:ss] ");
    private final ConversionManager conversionManager;

    @FXML
    private TextArea textArea_summaryLog;

    public SummaryComponent() {
        super();

        // Cache conversion manager
        conversionManager = ConversionManager.getInstance();

        // Pass converter events to summary log
        conversionManager.addConverterEventListener((eventMessage) -> Platform.runLater(() -> log(eventMessage)));

        // Load fxml and run initialize
        initializeFXML(getClass().getResource("/fxml/SummaryComponent.fxml"), false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableTextAreaCache();
        textArea_summaryLog.clear();
    }

    @Override
    protected void onComponentShow() {
        super.onComponentShow();
        startConversion();
    }

    private void startConversion() {
        setIsSatisfied(false);

        // Toggle isSatisfied upon completion
        conversionManager.startConversion(() -> Platform.runLater(() -> setIsSatisfied(true)));
    }

    private void log(String text) {
        StringBuilder stringBuilder = new StringBuilder();

        // Skip line feed if first entry
        if (!textArea_summaryLog.getText().isEmpty()) {
            stringBuilder.append("\n");
        }

        // Append timestamp
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        stringBuilder.append(LOG_TIMESTAMP_FORMAT.format(date));

        // Append entry to textArea
        stringBuilder.append(text);
        textArea_summaryLog.appendText(stringBuilder.toString());
    }

    /**
     * Fixes blurriness caused by a ScrollPane bug.
     * The bug appears to originate from decimal value constraints for the ScrollPane
     * Based on: https://stackoverflow.com/questions/23728517/blurred-text-in-javafx-textarea
     */
    private void disableTextAreaCache() {
        Platform.runLater(() -> {
            textArea_summaryLog.setCache(false);
            ScrollPane scrollPane = (ScrollPane) textArea_summaryLog.getChildrenUnmodifiable().get(0);
            scrollPane.setCache(false);
            for (Node n : scrollPane.getChildrenUnmodifiable()) {
                n.setCache(false);
            }
        });
    }

    @Override
    public String getComponentTitle() {
        return COMPONENT_TITLE;
    }
}
