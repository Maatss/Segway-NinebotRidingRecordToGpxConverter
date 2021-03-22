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

package com.maatss.converter;

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
