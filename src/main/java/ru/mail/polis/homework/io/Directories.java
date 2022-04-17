package ru.mail.polis.homework.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Directories {


    /**
     * Реализовать рекурсивное удаление всех файлов и директорий из директории по заданному пути.
     * Метод должен возвращать количество удаленных файлов и директорий.
     * Если директории по существующему пути нет, то возвращаем 0.
     * Написать двумя способами. С использованием File
     * 2 тугрика
     */
    public static int removeWithFile(String path) {
        File file = new File(path);
        int counter = 0;
        if (!file.exists()) {
            return counter;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                counter += removeWithFile(f.toString());
                if (f.delete()) {
                    counter++;
                }
            }
        }
        if (file.delete()) {
            counter++;
        }
        return counter;
    }

    /**
     * С использованием Path
     * 2 тугрика
     */
    public static int removeWithPath(String path) throws IOException {
        Path currentPath = Paths.get(path);
        int counter = 0;
        if (!Files.exists(currentPath)) {
            return counter;
        }
        if (Files.isDirectory(currentPath)) {
            try (DirectoryStream<Path> files = Files.newDirectoryStream(currentPath)) {
                for (Path nextPath : files) {
                    counter += removeWithPath(nextPath.toString());
                }
            }
        }
        if (Files.deleteIfExists(currentPath)) {
            counter++;
        }
        return counter;
    }
}
