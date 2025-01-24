package ru.arnetech.randomtargets.tasks;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import ru.arnetech.randomtargets.R;
import ru.arnetech.randomtargets.params.ExtendedSettings;
import ru.arnetech.randomtargets.user.UserPreferences;

public class TaskParams {
    private final Context context;

    private final UserPreferences userPreferences;

    private boolean audioPermission;
    private boolean picPermission;
    private boolean textPermission;

    /**
     * List с доступными задачами
     */
    private ArrayList<TaskTypes> permittedTasks;
    private ArrayList<File> audioFiles, picFiles, textFiles;

    public TaskParams(Context context, UserPreferences userPreferences) {
        this.context = context;
        this.userPreferences = userPreferences;
    }

    /**
     * Создание список с файлами для задач
     */
    public void createFiles() {
        permittedTasks = new ArrayList<>();

        //аудио задачи
        audioPermission = userPreferences.isAudioAllow();
        if (audioPermission)
            createAudioFiles();

        //задачи с картинками
        picPermission = userPreferences.isPicAllow();
        if (picPermission)
            createPicFiles();

        //задачи с текстом
        textPermission = userPreferences.isTextAllow();
        if (textPermission)
            createTextFiles();
    }

    /**
     * Проверка задач на доступность
     *
     * @return null в случае доступности задач, текст с ошибкой в случае ошибки
     */
    public String checkTasks() {
        //если отключены все задачи
        if (!audioPermission && !picPermission && !textPermission) {
            return context.getString(R.string.tasksDisabled);
        }

        //если список с задачами пустой
        if (permittedTasks.size() == 0) {
            return context.getString(R.string.noTasks);
        }

        return null;
    }

    /**
     * Получение случайной задачи из доступных
     *
     * @return {@link TaskTypes}
     */
    public TaskTypes getRandomTask() {
        Collections.shuffle(permittedTasks);
        return permittedTasks.get(0);
    }

    /**
     * Заполнение audioFiles список доступных ауидофайлов из пути, указанным в настройках
     */
    private void createAudioFiles() {
        TaskTypes taskType = TaskTypes.AUDIO;

        audioFiles = ExtendedSettings.getListWithExtension(
                userPreferences.getAudioFolder(),
                taskType);

        //Если в списке содержатся файлы, то тип задачи добавляется в список разрешенных задач
        if (audioFiles.size() != 0) {
            permittedTasks.add(taskType);
        }
    }

    /**
     * Заполнение picFiles список доступных ауидофайлов из пути, указанным в настройках
     */
    private void createPicFiles() {
        TaskTypes taskType = TaskTypes.PICTURE;

        picFiles = ExtendedSettings.getListWithExtension(
                userPreferences.getPicFolder(),
                taskType);
        //Если в списке содержатся файлы, то тип задачи добавляется в список разрешенных задач
        if (picFiles.size() != 0) {
            permittedTasks.add(taskType);
        }
    }

    /**
     * Заполнение textFiles список доступных ауидофайлов из пути, указанным в настройках
     */
    private void createTextFiles() {
        TaskTypes taskType = TaskTypes.TEXT;

        textFiles = ExtendedSettings.getListWithExtension(
                userPreferences.getTextFolder(),
                taskType);

        //Если в списке содержатся файлы, то тип задачи добавляется в список разрешенных задач
        if (textFiles.size() != 0) {
            permittedTasks.add(taskType);
        }
    }

    /**
     * Получение аудио файла
     *
     * @return {@link File}
     */
    public File getRandomAudio() {
        return audioFiles.get(ExtendedSettings.genRandNum(audioFiles.size()));
    }

    /**
     * Получение текста из файла формата .txt
     *
     * @return текст из файла
     */
    public String getRandomText() {
        return readFile(textFiles.get(ExtendedSettings.genRandNum(textFiles.size()))
                .getPath());
    }

    /**
     * Получение картинки из файла
     *
     * @return {@link File}
     */
    public File getRandomPic() {
        return picFiles.get(ExtendedSettings.genRandNum(picFiles.size()));
    }

    /**
     * Чтение файла
     *
     * @param path путь к файлу
     * @return текст из файла, null при ошибке
     */
    private String readFile(String path) {
        String result = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (IOException ignored) {
        }

        return result;
    }
}