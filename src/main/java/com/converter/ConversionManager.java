package com.converter;

import java.util.ArrayList;
import java.util.List;

public class ConversionManager {
    private static ConversionManager instance;
    private final List<Thread> converterThreads;
    private final ConverterConfiguration converterConfiguration;
    private final List<ConverterEvent> converterEventListeners;

    private ConversionManager() {
        converterThreads = new ArrayList<>();
        converterConfiguration = new ConverterConfiguration();
        converterEventListeners = new ArrayList<>();
    }

    public static ConversionManager getInstance() {
        if (instance == null) {
            instance = new ConversionManager();
        }

        return instance;
    }

    public void startConversion(Runnable onComplete) {
        // Setup converter
        Converter converter = new Converter();
        converterEventListeners.forEach(converter::addEventListener);

        // Setup thread
        Thread converterThread = new Thread(() -> {
            converter.convert(converterConfiguration);
            onComplete.run();
        });
        converterThreads.add(converterThread);
        converterThread.setDaemon(true);

        converterThread.start();
    }

    public void interruptAllConversions() {
        converterThreads.forEach(Thread::interrupt);
    }

    public ConverterConfiguration getConverterConfiguration() {
        return converterConfiguration;
    }

    public void addConverterEventListener(ConverterEvent listener) {
        converterEventListeners.add(listener);
    }

    public void removeConverterEventListener(ConverterEvent listener) {
        converterEventListeners.remove(listener);
    }
}
