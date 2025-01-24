package ru.arnetech.randomtargets.params;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

import ru.arnetech.randomtargets.tasks.TaskTypes;

public class ExtendedSettings {
    private static final String[] times = {"0.5", "1.0", "1.5", "2.0", "2.5", "3.0"};

    public static String[] getTimes() {
        return times;
    }

    /**
     * Проверка вхождения числа в допустимый диапозон
     * @param min минимальное значение
     * @param max максимальное значение
     * @param i проверяемое число
     * @return true - входит в диапозон, false - нет
     */
    public static boolean isPermissible(int min, int max, int i) {
        return min <= i && i <= max;
    }

    /**
     * Генерация случайного числа от 0 до n
     * @param n предел
     * @return число
     */
    public static int genRandNum(int n) {
        return (int) (Math.random() * n);
    }

    /**
     * Проверка корректности разрешения файла
     * @param file файл для проверки
     * @param extensions список допустимых разрешений
     * @return true - разрешение подходит, false - нет
     */
    private static boolean isCorrectExtension(File file, String[] extensions) {
        String fileExtension = FilenameUtils.getExtension(file.getName()).trim();

        for (String extension : extensions) {
            if (fileExtension.equals(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Создание листа с файлами для определенных задач
     * @param folder папка с файлами
     * @param taskTypes {@link TaskTypes}
     * @return {@link ArrayList<File>}
     */
    public static ArrayList<File> getListWithExtension(String folder, TaskTypes taskTypes) {
        String[] extensions = new String[0];

        switch (taskTypes) {
            case AUDIO:
                extensions = new String[]{"mp3", "wav", "ogg", "m4a"};
                break;
            case PICTURE:
                extensions = new String[]{"jpg", "png", "jpeg", "gif"};
                break;
            case TEXT:
                extensions = new String[]{"txt"};
                break;
        }

        ArrayList<File> taskFiles = new ArrayList<>();

        File fold = new File(folder);
        try {
            File[] list = fold.listFiles();

            for (File file : list) {
                if (isCorrectExtension(file, extensions)) {
                    taskFiles.add(file);
                }
            }
        } catch (Exception ignored) { }

        return taskFiles;
    }

}