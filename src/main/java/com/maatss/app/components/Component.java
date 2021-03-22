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

import com.maatss.app.ComponentUpdateListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Component extends AnchorPane {
    protected final List<ComponentUpdateListener> componentUpdateListeners;
    protected boolean isSatisfied = false;

    public Component() {
        componentUpdateListeners = new ArrayList<>();

        // Setup visibility change listener
        visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                onComponentShow();
            } else {
                onComponentHide();
            }
        });
    }

    protected void initializeFXML(URL fxmlResourceUrl, boolean nestedCustomComponentsSupport) {
        // Setup fxml
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlResourceUrl);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        if (nestedCustomComponentsSupport) {
            // Pass this outer classLoader to the nested control classes
            // Required for SceneBuilder to work with nested custom components
            fxmlLoader.setClassLoader(getClass().getClassLoader());
        }

        // Finish fxml setup and run initialize
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Component - initializeFXML: loading failed\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public void addOnComponentUpdateListener(ComponentUpdateListener componentUpdateListener) {
        componentUpdateListeners.add(componentUpdateListener);
    }

    protected void triggerComponentUpdateEvent() {
        componentUpdateListeners.forEach(listener -> listener.onUpdate(this));
    }

    /**
     * Sets isSatisfied, upon a change of value 'triggerComponentUpdateEvent()' will also be called.
     */
    protected void setIsSatisfied(boolean isSatisfied) {
        boolean prevIsSatisfied = this.isSatisfied;
        this.isSatisfied = isSatisfied;

        if (this.isSatisfied != prevIsSatisfied) {
            triggerComponentUpdateEvent();
        }
    }

    public boolean isSatisfied() {
        return isSatisfied;
    }

    /**
     * Gets called everytime the component becomes visible
     */
    protected void onComponentShow() {

    }

    /**
     * Gets called everytime the component becomes hidden
     */
    protected void onComponentHide() {

    }

    public abstract String getComponentTitle();
}
