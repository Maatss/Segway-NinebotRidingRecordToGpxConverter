package com.app.components;

import com.converter.ConversionManager;
import com.converter.ConverterConfiguration;
import com.gluonhq.charm.glisten.control.AutoCompleteTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static com.converter.ConverterConfiguration.DEFAULT_SAVE_DIRECTORY_PATH;

public class SaveFolderSelectionComponent extends Component implements Initializable {
    private static final String COMPONENT_TITLE = "Specify save folder";
    private final ConverterConfiguration converterConfiguration;

    @FXML
    private AutoCompleteTextField<String> autoField_path;
    @FXML
    private Button button_fileBrowse;
    @FXML
    private Button button_resetPath;
    @FXML
    private Label label_saveFolderPathRequirementsInfo;

    private Tooltip textField_tooltip;

    public SaveFolderSelectionComponent() {
        super();

        // Cache converter configuration
        converterConfiguration = ConversionManager.getInstance().getConverterConfiguration();

        // Load fxml and run initialize
        initializeFXML(getClass().getResource("/fxml/SaveFolderSelectionComponent.fxml"), false);
    }

    private static void createDefaultFolderIfNotExisting() {
        if (!Files.exists(DEFAULT_SAVE_DIRECTORY_PATH)) {
            try {
                Files.createDirectories(DEFAULT_SAVE_DIRECTORY_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isValidDirectoryPath(String pathStr) {
        if (pathStr.isEmpty()) return false;

        try {
            File directory = Paths.get(pathStr).toFile();
            if (!directory.exists()) throw new InvalidPathException(pathStr, "Path does not exist");
            if (!directory.isDirectory()) throw new InvalidPathException(pathStr, "Path does not lead to a folder");
        } catch (InvalidPathException ignored) {
            return false;
        }

        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup text field
        autoField_path.setOnKeyReleased(event -> updateUI());
        textField_tooltip = new Tooltip(autoField_path.getText());
        autoField_path.setTooltip(textField_tooltip); // Todo fix, it is not showing up at all

        // Setup buttons
        button_fileBrowse.setOnAction(event -> setSaveDirectoryFromUserPrompt());
        button_resetPath.setOnAction(event -> resetPath());
        button_fileBrowse.setTooltip(new Tooltip("Select a folder to use as a save directory."));
        button_resetPath.setTooltip(new Tooltip("Set the default save directory as the selected folder."));

        createDefaultFolderIfNotExisting();
        resetPath();

        // Initial ui update
        updateUI();
    }

    private void updateUI() {
        boolean hasValidPath = isValidDirectoryPath(autoField_path.getText());

        // Update element styles
        ObservableList<String> styleClassField = autoField_path.getStyleClass();
        ObservableList<String> styleClassLabel = label_saveFolderPathRequirementsInfo.getStyleClass();
        if (hasValidPath) {
            styleClassLabel.remove("defaultErrorText");
            styleClassField.remove("fileBrowseTextFieldError");
        } else {
            if (!styleClassLabel.contains("defaultErrorText")) {
                styleClassLabel.add("defaultErrorText");
            }
            if (!styleClassField.contains("fileBrowseTextFieldError")) {
                styleClassField.add("fileBrowseTextFieldError");
            }
        }

        // Update reset button
        button_resetPath.setDisable(DEFAULT_SAVE_DIRECTORY_PATH.toString().equals(autoField_path.getText()));

        // Update tooltip
        textField_tooltip.setText(autoField_path.getText());

        // Update component satisfied status
        setIsSatisfied(hasValidPath);
    }

    @Override
    protected void onComponentShow() {
        super.onComponentShow();
        updateUI();
    }

    @Override
    protected void onComponentHide() {
        super.onComponentHide();

        // Update converter configuration
        converterConfiguration.setSaveDirectoryPath(Paths.get(autoField_path.getText()));
    }

    private void resetPath() {
        autoField_path.setText(DEFAULT_SAVE_DIRECTORY_PATH.toString());
        updateUI();
    }

    private void setSaveDirectoryFromUserPrompt() {
        File directoryFile = promptSaveDirectorySelection();

        // Watch out if no file was selected
        if (directoryFile != null) {
            autoField_path.setText(directoryFile.getAbsolutePath());
        }

        updateUI();
    }

    private File promptSaveDirectorySelection() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select save folder for GPX files");
        directoryChooser.setInitialDirectory(getInitialBrowseDirectory());
        return directoryChooser.showDialog(getScene().getWindow());
    }

    private File getInitialBrowseDirectory() {
        String curPathStr = autoField_path.getText();

        // Try to use the path specified in textField
        // If invalid, use default directory
        return isValidDirectoryPath(curPathStr)
                ? Paths.get(curPathStr).toFile()
                : DEFAULT_SAVE_DIRECTORY_PATH.toFile();
    }

    @Override
    public String getComponentTitle() {
        return COMPONENT_TITLE;
    }
}
