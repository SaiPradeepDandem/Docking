package com.sai.javafx.independentwindow.workspace;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for handling all the workspace related activities.
 */
public class WorkspaceUtil {
    private static String FOLDER_PATH = "C:\\Sai\\IndependentWindow_Workspace\\";

    /**
     * Saves the provided properties object to the xml file.
     *
     * @param userName   Current logged in user
     * @param id         Unique identifier of the independent window
     * @param properties Properties object of the independent window
     */
    public static void save(String userName, String id, IndependentWindowWorkspace properties) {
        try {
            final JAXBContext contextObj = JAXBContext.newInstance(IndependentWindowWorkspace.class);
            final Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            final Path pathToFile = getFilePath(userName, id);
            final File directory = new File(FOLDER_PATH);
            if (!directory.exists()) {
                Files.createDirectories(pathToFile.getParent());
            }
            if (!pathToFile.toFile().exists()) {
                Files.createFile(pathToFile);
            }
            marshallerObj.marshal(properties, new FileOutputStream(pathToFile.toFile()));
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
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
        return read(getFilePath(userName, id));
    }

    /**
     * Reads the workspace file and converts to the properties object for the given file path.
     *
     * @param pathToFile Path to the workspace file
     * @return Properties object
     */
    public static IndependentWindowWorkspace read(Path pathToFile) {
        if (pathToFile.toFile().exists()) {
            try {
                final JAXBContext jContext = JAXBContext.newInstance(IndependentWindowWorkspace.class);
                final Unmarshaller unmarshalObj = jContext.createUnmarshaller();
                final IndependentWindowWorkspace properties = (IndependentWindowWorkspace) unmarshalObj.unmarshal(pathToFile.toFile());
                return properties;
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Returns all the independent window workspace files of the given logged in user.
     *
     * @param userName Current logged in user
     * @return List of workspace file paths
     */
    public static List<Path> getAllWorkspaceFiles(String userName) {
        if (Paths.get(FOLDER_PATH).toFile().exists()) {
            try (Stream<Path> walk = Files.walk(Paths.get(FOLDER_PATH))) {
                return walk.map(path -> path.toString())
                        .filter(pathStr -> pathStr.contains(prefix(userName)))
                        .map(Paths::get)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Returns the file path for the provided user's independent window workspace.
     *
     * @param userName Current logged in user
     * @param id       Unique identifier of the independent window
     * @return Path for the workspace file
     */
    public static Path getFilePath(String userName, String id) {
        return Paths.get(FOLDER_PATH + getFileName(userName, id));
    }

    /**
     * Builds the file name based on user name and independent window identifier.
     *
     * @param userName Current logged in user
     * @param id       Unique identifier of the independent window
     * @return XML file name of the independent window workspace
     */
    private static String getFileName(String userName, String id) {
        return prefix(userName) + "_" + id + ".xml";
    }

    /**
     * Builds the prefix for file name based on given user name.
     *
     * @param userName Current logged in user
     * @return Prefix string
     */
    private static String prefix(String userName) {
        return "IW_" + userName.replaceAll(" ", "_");
    }
}
