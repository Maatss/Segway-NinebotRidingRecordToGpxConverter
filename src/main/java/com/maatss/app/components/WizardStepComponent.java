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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WizardStepComponent extends Component implements Initializable {
    private static final String COMPONENT_TITLE = "";
    private final IntegerProperty wizardStepProperty;
    private final IntegerProperty wizardNumStepsProperty;
    private final List<Component> wizardSteps;

    @FXML
    private Label label_wizardTitle;
    @FXML
    private Label label_wizardStep;

    @FXML
    private SourceFileSelectionComponent sourceFileSelectionComponent;
    @FXML
    private SaveFolderSelectionComponent saveFolderSelectionComponent;
    @FXML
    private ConfigureComponent configureComponent;
    @FXML
    private SummaryComponent summaryComponent;

    public WizardStepComponent() {
        super();
        wizardSteps = new ArrayList<>();

        // Setup properties
        wizardStepProperty = new SimpleIntegerProperty(0);
        wizardNumStepsProperty = new SimpleIntegerProperty(1);
        wizardStepProperty.addListener((observable, oldValue, newValue) -> updateUI());

        // Load fxml and run initialize
        initializeFXML(getClass().getResource("/fxml/WizardStepComponent.fxml"), true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup components
        wizardSteps.add(sourceFileSelectionComponent);
        wizardSteps.add(saveFolderSelectionComponent);
        wizardSteps.add(configureComponent);
        wizardSteps.add(summaryComponent);
        wizardSteps.forEach(component -> component.addOnComponentUpdateListener(listener -> triggerComponentUpdateEvent()));

        // Setup property
        wizardNumStepsProperty.setValue(wizardSteps.size());

        // Initial ui update
        updateUI();
    }

    private void updateUI() {
        int curStep = wizardStepProperty.get();

        // Update label texts
        label_wizardStep.setText(String.format("Step %d/%d", curStep + 1, wizardNumStepsProperty.get()));
        label_wizardTitle.setText(wizardSteps.get(curStep).getComponentTitle());

        // Update component visibility
        for (int i = 0; i < wizardSteps.size(); i++) {
            Node node = wizardSteps.get(i);
            node.setVisible(i == curStep);
        }
    }

    public boolean isCurrentWizardStepSatisfied() {
        return wizardSteps.get(wizardStepProperty.get()).isSatisfied();
    }

    public IntegerProperty getWizardStepProperty() {
        return wizardStepProperty;
    }

    public IntegerProperty getWizardNumStepsProperty() {
        return wizardNumStepsProperty;
    }

    @Override
    public String getComponentTitle() {
        return COMPONENT_TITLE;
    }
}
