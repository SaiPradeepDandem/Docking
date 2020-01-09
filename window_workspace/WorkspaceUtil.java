package com.sai.javafx.independentwindow.workspace;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling all the workspace related activities.
 */
public class WorkspaceUtil {
    private static String FOLDER_PATH = "C:\\Sai\\IndependentWindow_Workspace\\";

    public static void save(String userName, String id, IndependentWindowWorkspace properties) {
        try {
            final Path pathToFile = getFilePath(userName);
            final File directory = new File(FOLDER_PATH);
            if (!directory.exists()) {
                Files.createDirectories(pathToFile.getParent());
            }
            Map<String,IndependentWindowWorkspace> map = new HashMap<>();
            if (!pathToFile.toFile().exists()) {
                Files.createFile(pathToFile);
            }else{
                map = read(userName);
            }
            map.put(id,properties);
            FileOutputStream file = new FileOutputStream(pathToFile.toFile());
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(map);
            out.close();
            file.close();
        } catch (IOException ex) {
            System.out.println("IOException is caught");
            ex.printStackTrace();
        }
    }

    /**
     * Reads the workspace file and converts to the properties object for the given logged in user and independent window identifier.
     *
     * @param userName Current logged in user
     * @param id       Unique identifier of the independent window
     * @return Properties object
     */
    public static IndependentWindowWorkspace read(String userName, String id) {
        final Map<String,IndependentWindowWorkspace> map = read(userName);
        return map.get(id);
    }

    /**
     * Reads the workspace file and converts to the properties object for the given file path.
     *
     * @param userName Current logged in user
     * @return Properties object
     */
    public static Map<String,IndependentWindowWorkspace> read(String userName) {
        Path pathToFile = getFilePath(userName);
        if (pathToFile.toFile().exists()) {
            try {
                FileInputStream file = new FileInputStream(pathToFile.toFile());
                ObjectInputStream in = new ObjectInputStream(file);
                Map<String,IndependentWindowWorkspace> source = (Map<String,IndependentWindowWorkspace>) in.readObject();
                in.close();
                file.close();
                return source;
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("IOException is caught");
                ex.printStackTrace();
            }
        }
        return Collections.emptyMap();
    }

    /**
     * Returns the file path for the provided user's independent window workspace.
     *
     * @param userName Current logged in user
     * @return Path for the workspace file
     */
    public static Path getFilePath(String userName) {
        return Paths.get(FOLDER_PATH + getFileName(userName));
    }

    /**
     * Builds the file name based on user name and independent window identifier.
     *
     * @param userName Current logged in user
     * @return File name of the independent window workspace
     */
    private static String getFileName(String userName) {
        return "IW_" + userName.replaceAll(" ", "_");
    }
}
