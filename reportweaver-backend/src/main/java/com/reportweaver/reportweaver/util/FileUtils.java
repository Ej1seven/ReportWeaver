package com.reportweaver.reportweaver.util;

import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {
    private static final SeleniumStatusHandler seleniumStatusHandler = new SeleniumStatusHandler();

    public static Set<String> getFilesInDirectory(String folderPath) {
        seleniumStatusHandler.sendUpdate("Scanning directory: " + folderPath);

        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            seleniumStatusHandler.sendUpdate("Files found in directory: " + files.length);
            return new HashSet<>(Arrays.asList(folder.list()));
        }
        seleniumStatusHandler.sendUpdate("Directory not found or is not a directory: " + folderPath);
        return new HashSet<>();
    }
}
