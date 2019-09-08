package migrator.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Utility functions about File IO.
 */
public class FileUtil {

    /**
     * Read all lines from a file.
     *
     * @param filepath path to the file
     * @return a list of string where each string is a line in the file
     */
    public static List<String> readLinesFromFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File reading error");
        }
    }

    /**
     * Read a file as a string.
     *
     * @param filepath path to the file
     * @return file content as a string
     */
    public static String readFromFile(String filepath) {
        List<String> lines = readLinesFromFile(filepath);
        return lines.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Write a list of strings to a file.
     *
     * @param filepath path to the file
     * @param lines    a list of strings
     */
    public static void writeLinesToFile(String filepath, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File writing error");
        }
    }

    /**
     * Write a string to a file.
     *
     * @param filepath path to he file
     * @param content  the string
     */
    public static void writeStringToFile(String filepath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File writing error");
        }
    }

    /**
     * Read an object from a Json file.
     *
     * @param <T>      type for the object
     * @param filepath path to the Json file
     * @param classOfT class of the object
     * @return the object
     */
    public static <T> T readFromJsonFile(String filepath, Class<T> classOfT) {
        Gson json = new GsonBuilder().create();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            return json.fromJson(reader, classOfT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File reading error");
        }
    }
}
