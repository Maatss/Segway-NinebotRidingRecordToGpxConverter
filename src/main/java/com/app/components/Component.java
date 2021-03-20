package com.app.components;

import com.app.ComponentUpdateListener;
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
