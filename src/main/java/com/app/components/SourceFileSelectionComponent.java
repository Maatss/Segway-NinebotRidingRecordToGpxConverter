package com.app.components;

import com.converter.ConversionManager;
import com.converter.ConverterConfiguration;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SourceFileSelectionComponent extends Component implements Initializable {
    private static final String COMPONENT_TITLE = "Select files from app";
    private final ObservableList<String> observableSelectFilesList;
    private final ConverterConfiguration converterConfiguration;

    @FXML
    private Button button_fileBrowse;
    @FXML
    private Button button_clearFiles;
    @FXML
    private ListView<String> listView_fileList;
    @FXML
    private Label label_sourceFileSelectionInfo;
    @FXML
    private Label label_removeFileInfo;
    @FXML
    private Label label_numFilesSelected;

    public SourceFileSelectionComponent() {
        super();

        // Cache converter configuration
        converterConfiguration = ConversionManager.getInstance().getConverterConfiguration();

        // Initialize list for ListView
        observableSelectFilesList = FXCollections.observableArrayList();

        // Setup visibility change listener
        visibleProperty().addListener((observable, oldValue, newValue) -> updateUI());

        // Load fxml and run initialize
        initializeFXML(getClass().getResource("/fxml/SourceFileSelectionComponent.fxml"), false);
    }

    /**
     * Creates an array of Paths from the string array of file paths.
     */
    private static List<Path> getPathsFromStrings(List<String> pathStrings) {
        return pathStrings.stream().map(Paths::get).collect(Collectors.toList());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFileDragAndDropSupport();

        // Setup ListView
        setupCustomCellFactory();
        listView_fileList.setItems(observableSelectFilesList);

        // Setup button event listeners
        button_clearFiles.setOnAction(event -> clearFiles());
        button_fileBrowse.setOnAction(event -> addFilesFromUserPrompt());

        // Attach listener to file list
        observableSelectFilesList.addListener((ListChangeListener<? super String>) c -> updateUI());

        // Set tooltips
        button_clearFiles.setTooltip(new Tooltip("Removes all files from the list."));
        button_fileBrowse.setTooltip(new Tooltip("Select files to add to the list.\nMultiple files can be selected at once."));

        updateUI();
        hideItemInteractiveFeedback();
    }

    private void setupCustomCellFactory() {
        // Setup custom cell factory, used to attach event listeners to individual cells
        Callback<ListView<String>, ListCell<String>> factoryCallback = new Callback<>() {
            // This callback is called any time the factory should create a new cell
            @Override
            public ListCell<String> call(ListView<String> param) {
                // Create a new text only cell,
                // this is the same way that the default factory does things for string items
                ListCell<String> listCell = new ListCell<>() {
                    private final Tooltip toolTip = new Tooltip();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        setGraphic(null);

                        // Setup tooltip, showing the cell's full path
                        if (!isEmpty()) {
                            toolTip.setText(getText());
                            setTooltip(toolTip);
                        } else {
                            setTooltip(null);
                        }
                    }
                };

                // Attach event listeners to newly created cell
                listCell.setOnMouseClicked(event -> {
                    // Ignore event if cell is empty
                    if (!listCell.isEmpty()) {
                        onItemMouseClicked(listCell.getIndex());
                    }
                    event.consume();
                });
                listCell.setOnMouseEntered(event -> {
                    // Ignore event if cell is empty
                    if (!listCell.isEmpty()) {
                        onItemMouseEntered(listCell.getIndex());
                    }
                    event.consume();
                });
                listCell.setOnMouseExited(event -> {
                    // Ignore event if cell is empty
                    if (!listCell.isEmpty()) {
                        onItemMouseExited(listCell.getIndex());
                    }
                    event.consume();
                });

                // Return cell
                return listCell;
            }
        };

        // Set as new cell factory
        listView_fileList.setCellFactory(factoryCallback);
    }

    private void setupFileDragAndDropSupport() {
        // Setup file drag over responsiveness
        listView_fileList.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                final boolean isValidFiles = dragboard.getFiles().stream()
                        .allMatch(file -> file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".json"));

                if (isValidFiles) {
                    event.acceptTransferModes(TransferMode.LINK);
                }
            }
            event.consume();
        });

        // Setup file drop handling
        listView_fileList.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasFiles()) {
                final boolean isValidFiles = dragboard.getFiles().stream()
                        .allMatch(file -> file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".json"));

                if (isValidFiles) {
                    List<File> files = dragboard.getFiles();
                    files.forEach(file -> observableSelectFilesList.add(file.toString()));
                }
            }
            event.consume();
        });
    }

    private void updateUI() {
        // Show info text if list of files is empty
        label_sourceFileSelectionInfo.setVisible(observableSelectFilesList.isEmpty());

        // Enable clear button if list of files is empty
        button_clearFiles.setDisable(observableSelectFilesList.isEmpty());

        // Update label text
        label_numFilesSelected.setText(String.format("Number of selected files: %d", observableSelectFilesList.size()));

        // Update component satisfied status
        setIsSatisfied(!observableSelectFilesList.isEmpty());
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
        List<Path> targetFiles = converterConfiguration.getTargetFiles();
        targetFiles.clear();
        targetFiles.addAll(getPathsFromStrings(observableSelectFilesList));
    }

    private void onItemMouseClicked(int itemIndex) {
        observableSelectFilesList.remove(itemIndex);
        hideItemInteractiveFeedback();
    }

    private void onItemMouseEntered(int itemIndex) {
        showItemInteractiveFeedback();
    }

    private void onItemMouseExited(int itemIndex) {
        hideItemInteractiveFeedback();
    }

    private void clearFiles() {
        observableSelectFilesList.clear();
        hideItemInteractiveFeedback();
    }

    private void showItemInteractiveFeedback() {
        label_removeFileInfo.setVisible(true);
        setCursor(Cursor.HAND);
    }

    private void hideItemInteractiveFeedback() {
        label_removeFileInfo.setVisible(false);
        setCursor(Cursor.DEFAULT);
    }

    private void addFilesFromUserPrompt() {
        List<File> files = promptAddFileBrowser();

        if (files != null) {
            files.forEach(file -> observableSelectFilesList.add(file.toString()));
        }
    }

    private List<File> promptAddFileBrowser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Segway-Ninebot JSON-tracks to add to list");
        fileChooser.setInitialDirectory(Paths.get("").toAbsolutePath().toFile()); // Set current working directory as initial directory
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Segway-Ninebot tracks (*.json)", "*.json"));
        return fileChooser.showOpenMultipleDialog(getScene().getWindow());
    }

    @Override
    public String getComponentTitle() {
        return COMPONENT_TITLE;
    }
}
