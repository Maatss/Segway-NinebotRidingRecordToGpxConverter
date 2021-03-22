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
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class WizardProgressComponent extends Component implements Initializable {
    private static final String COMPONENT_TITLE = "";
    private final IntegerProperty wizardStepProperty;
    private final List<List<Node>> wizardSteps;

    @FXML
    public ImageView imageView_progressBar_1;
    @FXML
    public ImageView imageView_progressBar_2;
    @FXML
    public ImageView imageView_progressBar_3;
    @FXML
    public ImageView imageView_progressBar_4;
    @FXML
    public Label label_progressBar_1;
    @FXML
    public Label label_progressBar_2;
    @FXML
    public Label label_progressBar_3;
    @FXML
    public Label label_progressBar_4;
    @FXML
    public Circle circle_progressBar_1;
    @FXML
    public Circle circle_progressBar_2;
    @FXML
    public Circle circle_progressBar_3;
    @FXML
    public Circle circle_progressBar_4;
    @FXML
    public Line line_progressBar_1;
    @FXML
    public Line line_progressBar_2;
    @FXML
    public Line line_progressBar_3;

    public WizardProgressComponent() {
        super();
        wizardSteps = new ArrayList<>();

        // Setup property
        wizardStepProperty = new SimpleIntegerProperty(0);
        wizardStepProperty.addListener((observable, oldValue, newValue) -> update());

        // Load fxml and run initialize
        initializeFXML(getClass().getResource("/fxml/WizardProgressComponent.fxml"), false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup wizard steps
        addWizardStep(imageView_progressBar_1, circle_progressBar_1, label_progressBar_1);
        addWizardStep(imageView_progressBar_2, circle_progressBar_2, label_progressBar_2, line_progressBar_1);
        addWizardStep(imageView_progressBar_3, circle_progressBar_3, label_progressBar_3, line_progressBar_2);
        addWizardStep(imageView_progressBar_4, circle_progressBar_4, label_progressBar_4, line_progressBar_3);

        // Initial update
        update();
    }

    private void addWizardStep(Node... nodes) {
        List<Node> wizardStep = new ArrayList<>(Arrays.asList(nodes));
        wizardSteps.add(wizardStep);
    }

    private void update() {
        int curStep = wizardStepProperty.get();

        if (curStep >= numWizardSteps()) {
            curStep = numWizardSteps() - 1;
            System.out.println("WizardProgressComponent - update: attempt to set wizard step too large. Setting it to within bounds");
        }

        for (int i = 0; i < wizardSteps.size(); i++) {
            List<Node> wizardStep = wizardSteps.get(i);

            if (i > curStep) {
                wizardStep.forEach(node -> node.setOpacity(0.25));
            } else {
                wizardStep.forEach(node -> node.setOpacity(1));
            }
        }
    }

    private int numWizardSteps() {
        return wizardSteps.size();
    }

    public IntegerProperty getWizardStepProperty() {
        return wizardStepProperty;
    }

    @Override
    public String getComponentTitle() {
        return COMPONENT_TITLE;
    }
}
