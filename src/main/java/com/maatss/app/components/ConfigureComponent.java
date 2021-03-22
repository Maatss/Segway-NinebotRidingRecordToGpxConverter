/*
 * Segway-Ninebot Riding Record To GPX Converter
 * Copyright (C) 2021 Maatss
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.maatss.app.components;

import com.maatss.converter.ConversionManager;
import com.maatss.converter.ConverterConfiguration;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.LongStringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static com.maatss.converter.ConverterConfiguration.*;

public class ConfigureComponent extends Component implements Initializable {
    private static final String COMPONENT_TITLE = "Configure";
    private final ConverterConfiguration converterConfiguration;

    @FXML
    private CheckBox checkbox_fileSizeLimit;
    @FXML
    private Label label_fileLimitInfo;
    @FXML
    private AnchorPane anchorPane_textFieldFileLimit;
    @FXML
    private TextField textField_fileSizeLimit;

    @FXML
    private CheckBox checkbox_excludeDuplicates;
    @FXML
    private Label label_excludeDuplicatesInfo;

    @FXML
    private CheckBox checkbox_AllowSpace;
    @FXML
    private Label label_AllowSpaceInfo;

    @FXML
    private Button button_resetConfiguration;

    public ConfigureComponent() {
        super();

        // Cache converter configuration
        converterConfiguration = ConversionManager.getInstance().getConverterConfiguration();

        // Load fxml and run initialize
        initializeFXML(getClass().getResource("/fxml/ConfigureComponent.fxml"), false);
    }

    private static String getDefaultFileLimitText() {
        return Long.toString(DEFAULT_FILE_SIZE_LIMIT / 1_000L);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Attach listeners for UI responsiveness
        checkbox_fileSizeLimit.setOnAction(event -> updateUI());
        checkbox_excludeDuplicates.setOnAction(event -> updateUI());
        checkbox_AllowSpace.setOnAction(event -> updateUI());
        textField_fileSizeLimit.setOnKeyTyped(event -> updateUI());

        setupResetButton();
        setupTextFieldFormatting();

        updateUI();
    }

    private void setupResetButton() {
        button_resetConfiguration.setOnAction(event -> resetConfiguration());
        button_resetConfiguration.setTooltip(new Tooltip("Reset the configuration to the default values."));

        // Manually increment focus on 'enter'-button action
        textField_fileSizeLimit.setOnKeyReleased(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                checkbox_excludeDuplicates.requestFocus();
            }
        });
    }

    private void setupTextFieldFormatting() {
        // Setup filter
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            // Always allow non content changes
            if (!change.isContentChange()) {
                return change;
            }

            // Setup character limit
            int maxNumCharacters = 10;
            boolean isWithinCharLimit = change.getControlNewText().length() <= maxNumCharacters;

            // Only allow integers
            String text = change.getText();
            return isWithinCharLimit && text.matches("[0-9]*")
                    ? change
                    : null;
        };

        // Setup text formatter
        TextFormatter<Long> textFormatter = new TextFormatter<>(new LongStringConverter(), 5000L, integerFilter);
        textField_fileSizeLimit.setTextFormatter(textFormatter);
    }

    private void updateUI() {
        // Update element disabled statuses
        label_fileLimitInfo.setDisable(!checkbox_fileSizeLimit.isSelected());
        anchorPane_textFieldFileLimit.setDisable(!checkbox_fileSizeLimit.isSelected());
        label_excludeDuplicatesInfo.setDisable(!checkbox_excludeDuplicates.isSelected());
        label_AllowSpaceInfo.setDisable(!checkbox_AllowSpace.isSelected());

        boolean isValidConfig = !checkbox_fileSizeLimit.isSelected() ||
                checkbox_fileSizeLimit.isSelected() && !textField_fileSizeLimit.getText().isEmpty();

        // Update text field style
        ObservableList<String> styleClass = textField_fileSizeLimit.getStyleClass();
        if (isValidConfig) {
            styleClass.remove("defaultTextFieldError");
        } else if (!styleClass.contains("defaultTextFieldError")) {
            styleClass.add("defaultTextFieldError");
        }

        // Update reset button
        boolean isDefaultConfiguration = getDefaultFileLimitText().equals(textField_fileSizeLimit.getText())
                && checkbox_fileSizeLimit.isSelected() == DEFAULT_USE_FILE_SIZE_LIMIT
                && checkbox_excludeDuplicates.isSelected() == DEFAULT_EXCLUDE_DUPLICATES
                && checkbox_AllowSpace.isSelected() == DEFAULT_ALLOW_SPACE;
        button_resetConfiguration.setDisable(isDefaultConfiguration);

        // Update component satisfied status
        setIsSatisfied(isValidConfig);
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
        converterConfiguration.setFileSizeLimit((long) textField_fileSizeLimit.getTextFormatter().getValue() * 1000L);
        converterConfiguration.setUseFileSizeLimit(checkbox_fileSizeLimit.isSelected());
        converterConfiguration.setExcludeDuplicates(checkbox_excludeDuplicates.isSelected());
        converterConfiguration.setAllowSpaces(checkbox_AllowSpace.isSelected());
    }

    private void resetConfiguration() {
        textField_fileSizeLimit.setText(getDefaultFileLimitText());
        checkbox_fileSizeLimit.setSelected(DEFAULT_USE_FILE_SIZE_LIMIT);
        checkbox_excludeDuplicates.setSelected(DEFAULT_EXCLUDE_DUPLICATES);
        checkbox_AllowSpace.setSelected(DEFAULT_ALLOW_SPACE);
        updateUI();
    }

    @Override
    public String getComponentTitle() {
        return COMPONENT_TITLE;
    }
}
