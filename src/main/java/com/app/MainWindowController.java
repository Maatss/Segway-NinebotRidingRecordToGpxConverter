package com.app;

import com.app.components.Component;
import com.app.components.WizardProgressComponent;
import com.app.components.WizardStepComponent;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private final Stage stage;
    private double beginX = 0;
    private double beginY = 0;

    @FXML
    private AnchorPane anchorPane_window;
    @FXML
    private AnchorPane anchorPane_header;
    //    @FXML
//    private Button button_windowSize;
    @FXML
    private Button button_windowMinimize;
    @FXML
    private Button button_windowClose;
    @FXML
    private Button button_fileBrowse;
    @FXML
    private Button button_back;
    @FXML
    private Button button_nextStep;


    @FXML
    private WizardProgressComponent wizardProgressComponent;
    @FXML
    private WizardStepComponent wizardStepComponent;

    private Tooltip buttonBackTooltip;
    private Tooltip buttonNextStepTooltip;

    public MainWindowController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup window
        setupMouseDragSupportForWindow();
        anchorPane_window.setOnMouseClicked(event -> anchorPane_window.requestFocus()); // Clear any focus on click
        button_windowMinimize.setOnMouseClicked(event -> minimizeWindow());
        button_windowClose.setOnMouseClicked(event -> closeWindow());

        // Setup wizard buttons
        button_back.setOnAction(this::backButtonAction);
        button_nextStep.setOnAction(this::nextButtonAction);

        // Setup tooltips
        buttonBackTooltip = new Tooltip();
        buttonNextStepTooltip = new Tooltip();
        button_back.setTooltip(buttonBackTooltip);
        button_nextStep.setTooltip(buttonNextStepTooltip);

        // Listen to step changes
        IntegerProperty wizardStepProperty = wizardStepComponent.getWizardStepProperty();
        wizardStepProperty.addListener((observable, oldValue, newValue) -> updateUI());

        // Sync step across components
        wizardProgressComponent.getWizardStepProperty().bind(wizardStepProperty);

        // Listen to component updates
        wizardStepComponent.addOnComponentUpdateListener(this::onComponentUpdate);

        // Initial ui update
        updateUI();
    }

    private void setupMouseDragSupportForWindow() {
        // Save position upon drag start
        anchorPane_header.setOnMousePressed(event -> {
            beginX = event.getSceneX();
            beginY = event.getSceneY();
        });

        // Update window position during drag
        anchorPane_header.setOnMouseDragged(event -> {
            anchorPane_header.getScene().getWindow().setX(event.getScreenX() - beginX);
            anchorPane_header.getScene().getWindow().setY(event.getScreenY() - beginY);
        });
    }

    private void updateUI() {
        int curStep = wizardStepComponent.getWizardStepProperty().get();
        int lastStep = wizardStepComponent.getWizardNumStepsProperty().get() - 1;

        // Update back button
        if (curStep == lastStep) {
            button_back.setText("Start over");
            buttonBackTooltip.setText("Navigate to the start of the program.");
            button_back.setDisable(!wizardStepComponent.isCurrentWizardStepSatisfied());
        } else {
            button_back.setText("Back");
            buttonBackTooltip.setText("Navigate to the previous page.");
            button_back.setDisable(curStep == 0);
        }

        // Update next button
        if (curStep == lastStep - 1) {
            button_nextStep.setText("Convert");
            buttonNextStepTooltip.setText("Start the conversion process.");
        } else if (curStep == lastStep) {
            button_nextStep.setText(wizardStepComponent.isCurrentWizardStepSatisfied()
                    ? "Close"
                    : "Working..");
            buttonNextStepTooltip.setText("Close the program.");
        } else {
            button_nextStep.setText("Next step");
            buttonNextStepTooltip.setText("Navigate to the next page.");
        }
        button_nextStep.setDisable(!wizardStepComponent.isCurrentWizardStepSatisfied());
    }

    private void onComponentUpdate(Component component) {
        updateUI();
    }

    private void backButtonAction(Event event) {
        IntegerProperty wizardStepProperty = wizardStepComponent.getWizardStepProperty();
        int curStep = wizardStepProperty.get();
        int lastStep = wizardStepComponent.getWizardNumStepsProperty().get() - 1;

        // Determine step change
        int nextStep = curStep == lastStep
                ? 0                             // If on last step, go to first step
                : Math.max(0, curStep - 1);     // else, go to previous step

        wizardStepProperty.setValue(nextStep);
    }

    private void nextButtonAction(Event event) {
        IntegerProperty wizardStepProperty = wizardStepComponent.getWizardStepProperty();
        IntegerProperty wizardNumStepsProperty = wizardStepComponent.getWizardNumStepsProperty();
        int curStep = wizardStepProperty.get();
        int lastStep = wizardStepComponent.getWizardNumStepsProperty().get() - 1;

        // If on last step, close program
        if (curStep == lastStep) {
            closeWindow();
            return;
        }

        // Else, go to next step
        int nextStep = Math.min(wizardNumStepsProperty.get() - 1, curStep + 1);
        wizardStepProperty.setValue(nextStep);
    }

    private void minimizeWindow() {
        stage.setIconified(!stage.isIconified());
    }

//    public void restoreWindow() {
//        stage.setMaximized(false);
//    }

//    public void maximizeWindow() {
//        Rectangle windowRect = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
//        stage.setMaximized(true);
//        stage.setHeight(windowRect.height);
//        stage.setWidth(windowRect.width);
//        System.gc();
//    }

    private void closeWindow() {
        Platform.exit();
        System.exit(0);
    }
}
