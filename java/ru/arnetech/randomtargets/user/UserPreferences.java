package ru.arnetech.randomtargets.user;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class UserPreferences {
    private static final String USER_PREF = "user_pref";

    /**
     * Длительность звука таймера
     *
     * <p>Лимит: от 0.5 до 3.0</p>
     */
    private float soundLength = 3;
    private static final String SOUND_LENGTH = "sound_length";

    /**
     * Минимальная задержка перед началом задачи
     *
     * <p>Лимит: от 1 до 60</p>
     */
    private int minDelay = 20;
    private static final String MIN_DELAY = "min_delay";

    /**
     * Случайная задержка перед началом задачи
     *
     * <p>Лимит: от 1 до 20</p>
     */
    private int randDelay = 10;
    private static final String RAND_DELAY = "rand_delay";

    /**
     * Время на запоминание задачи
     *
     * <p>Лимит: от 1 до 20</p>
     */
    private int memorization = 20;
    private static final String MEMORIZATION = "memorization";

    /**
     * Доп. время на запоминание задачи
     *
     * <p>Лимит: от 1 до 20</p>
     */
    private int extendedTime = 10;
    private static final String EXTENDED_TIME = "extended_time";

    /**
     * Время на выполнение задачи
     *
     * <p>Лимит: от 1 до 60</p>
     */
    private int implementTime = 30;
    private static final String IMPLEMENT_TIME = "implement_time";

    /**
     * Цикличность задач
     *
     * <ul>
     *     <li>true - автоматическое начало выполнения следующей задачи</li>
     *     <li>false - остановка после окончания задачи</li>
     * </ul>
     */
    private boolean isLooping = false;
    private static final String LOOPING = "looping";


    /**
     * Путь к папке с аудио
     */
    private String audioFolder = "";
    private static final String AUDIO_FOLDER = "audio_folder";

    /**
     * Разрешение на использование типа задачи
     */
    private boolean isAudioAllow = false;
    private static final String AUDIO_ALLOW = "audio_allow";

    /**
     * Использование сигнала таймера для задачи
     */
    private boolean isAudioSound = false;
    private static final String AUDIO_SOUND = "audio_sound";

    /**
     * Использование сигнала таймера перед/после задачи
     */
    private boolean isEnableBeepAudio = true;
    private static final String AUDIO_BEEP = "audio_beep";

    /**
     * Путь к папке с картинками
     */
    private String picFolder = "";
    private static final String PIC_FOLDER = "pic_folder";

    /**
     * Разрешение на использование типа задачи
     */
    private boolean isPicAllow = false;
    private static final String PIC_ALLOW = "pic_allow";

    /**
     * Использование сигнала таймера для задачи
     */
    private boolean isPicSound = false;
    private static final String PIC_SOUND = "pic_sound";

    /**
     * Путь к папке с текстом
     */
    private String textFolder = "";
    private static final String TEXT_FOLDER = "text_folder";

    /**
     * Разрешение на использование типа задачи
     */
    private boolean isTextAllow = false;
    private static final String TEXT_ALLOW = "text_allow";

    /**
     * Использование сигнала таймера для задачи
     */
    private boolean isTextSound = false;
    private static final String TEXT_SOUND = "text_sound";

    /**
     * Флаг для первой загрузки
     *
     * Становится false после первого запуска
     */
    private boolean firstLoad = true;
    private static final String FIRST_LOAD = "first_load";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Context context;

    public UserPreferences(Context context) {
        this.context = context;
    }

    /**
     * Длительность таймера
     * @return
     */
    public float getSoundLength() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getFloat(SOUND_LENGTH, soundLength);
    }

    public void setSoundLength(float soundLength) {
        this.soundLength = soundLength;
        editor = preferences.edit();
        editor.putFloat(SOUND_LENGTH, soundLength);
        editor.apply();
    }

    /**
     * Минимальная задержка
     * @return
     */
    public int getMinDelay() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getInt(MIN_DELAY, minDelay);
    }

    public void setMinDelay(int minDelay) {
        this.minDelay = minDelay;
        editor = preferences.edit();
        editor.putInt(MIN_DELAY, minDelay);
        editor.apply();
    }

    /**
     * Случайная задержка
     * @return
     */
    public int getRandDelay(){
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getInt(RAND_DELAY, randDelay);
    }

    public void setRandDelay(int randDelay) {
        this.randDelay = randDelay;
        editor = preferences.edit();
        editor.putInt(RAND_DELAY, randDelay);
        editor.apply();
    }

    /**
     * Время на выполнение задачи
     * @return
     */
    public int getImplementTime(){
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getInt(IMPLEMENT_TIME, implementTime);
    }


    public void setImplementTime(int implementTime) {
        this.implementTime = implementTime;
        editor = preferences.edit();
        editor.putInt(IMPLEMENT_TIME, implementTime);
        editor.apply();
    }

    /**
     * Время на запоминание
     * @return
     */
    public int getMemorization(){
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getInt(MEMORIZATION, memorization);
    }

    public void setMemorization(int memorization) {
        this.memorization = memorization;
        editor = preferences.edit();
        editor.putInt(MEMORIZATION, memorization);
        editor.apply();
    }

    /**
     * Дополнительное время
     * @return
     */
    public int getExtendedTime(){
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getInt(EXTENDED_TIME, extendedTime);
    }

    public void setExtendedTime(int extendedTime) {
        this.extendedTime = extendedTime;
        editor = preferences.edit();
        editor.putInt(EXTENDED_TIME, extendedTime);
        editor.apply();
    }

    /**
     * Повторение задач
     * @return
     */
    public boolean isLooping(){
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getBoolean(LOOPING, isLooping);
    }

    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
        editor = preferences.edit();
        editor.putBoolean(LOOPING, isLooping);
        editor.apply();
    }

    /**
     * Папка с аудио
     * @return
     */
    public String getAudioFolder() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getString(AUDIO_FOLDER, audioFolder);
    }


    public void setAudioFolder(String audioFolder) {
        this.audioFolder = audioFolder;
        editor = preferences.edit();
        editor.putString(AUDIO_FOLDER, audioFolder);
        editor.apply();
    }

    /**
     * Разрешение аудио
     * @return
     */
    public boolean isAudioAllow(){
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getBoolean(AUDIO_ALLOW, isAudioAllow);
    }

    public void setAudioAllow(boolean isAudioAllow) {
        this.isAudioAllow = isAudioAllow;
        editor = preferences.edit();
        editor.putBoolean(AUDIO_ALLOW, isAudioAllow);
        editor.apply();
    }

    /**
     * Папка с картинками
     * @return
     */
    public String getPicFolder() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getString(PIC_FOLDER, picFolder);
    }

    public void setPicFolder(String picFolder) {
        this.picFolder = picFolder;
        editor = preferences.edit();
        editor.putString(PIC_FOLDER, picFolder);
        editor.apply();
    }

    /**
     * Разрешение картинок
     * @return
     */
    public boolean isPicAllow(){
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getBoolean(PIC_ALLOW, isPicAllow);
    }

    public void setPicAllow(boolean isPicAllow) {
        this.isPicAllow = isPicAllow;
        editor = preferences.edit();
        editor.putBoolean(PIC_ALLOW, isPicAllow);
        editor.apply();
    }

    /**
     * Папка с текстом
     * @return
     */
    public String getTextFolder() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getString(TEXT_FOLDER, textFolder);
    }

    public void setTextFolder(String textFolder) {
        this.textFolder = textFolder;
        editor = preferences.edit();
        editor.putString(TEXT_FOLDER, textFolder);
        editor.apply();
    }

    /**
     * Разрешение текста
     * @return
     */
    public boolean isTextAllow(){
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getBoolean(TEXT_ALLOW, isTextAllow);
    }

    public void setTextAllow(boolean isTextAllow) {
        this.isTextAllow = isTextAllow;
        editor = preferences.edit();
        editor.putBoolean(TEXT_ALLOW, isTextAllow);
        editor.apply();
    }

    /**
     * Сигнал таймера
     * @return
     */
    public boolean isEnableBeepAudio() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getBoolean(AUDIO_BEEP, isEnableBeepAudio);
    }

    public void setEnableBeepAudio(boolean isEnableBeepAudio) {
        this.isEnableBeepAudio = isEnableBeepAudio;
        editor = preferences.edit();
        editor.putBoolean(AUDIO_BEEP, isEnableBeepAudio);
        editor.apply();
    }

    /**
     * Звук к аудио
     * @return
     */
    public boolean isAudioSound() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getBoolean(AUDIO_SOUND, isAudioSound);
    }

    public void setAudioSound(boolean audioSound) {
        isAudioSound = audioSound;
        editor = preferences.edit();
        editor.putBoolean(AUDIO_SOUND, audioSound);
        editor.apply();
    }

    /**
     * Звук к картинкам
     * @return
     */
    public boolean isPicSound() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getBoolean(PIC_SOUND, isPicSound);
    }

    public void setPicSound(boolean picSound) {
        isPicSound = picSound;
        editor = preferences.edit();
        editor.putBoolean(PIC_SOUND, picSound);
        editor.apply();
    }

    /**
     * Звук к тексту
     * @return
     */
    public boolean isTextSound() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getBoolean(TEXT_SOUND, isTextSound);
    }

    public void setTextSound(boolean textSound) {
        isTextSound = textSound;
        editor = preferences.edit();
        editor.putBoolean(TEXT_SOUND, textSound);
        editor.apply();
    }

    /**
     * Первый запуск программы
     * @return
     */
    private boolean isFirstLoad() {
        preferences = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        return preferences.getBoolean(FIRST_LOAD, firstLoad);
    }

    private void setFirstLoad(boolean load) {
        firstLoad = load;
        editor = preferences.edit();
        editor.putBoolean(FIRST_LOAD, load);
        editor.apply();
    }

    /**
     * Проверка разрешения на чтение из памяти
     * @return PackageManager.PERMISSION_GRANTED если разрешено
     */
    public int checkReadPerm() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * Создание папок при первом запуске
     *
     * <p><b>Работает до <= 29 API</b></p>
     */
    public void setFolders() {
        if (isFirstLoad()) {
            if (checkReadPerm() == PackageManager.PERMISSION_GRANTED) {
                File mainFolder;

                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    mainFolder = new File(Environment.getDataDirectory(),
                            "RandomTargets");
                } else {
                    mainFolder = new File(Environment.getExternalStorageDirectory(),
                            "RandomTargets");
                }

                if (!mainFolder.exists()) {
                    mainFolder.mkdirs();
                }

                File audio = new File(mainFolder, "Audio");
                audio.mkdirs();
                setAudioFolder(audio.getPath());

                File pic = new File(mainFolder, "Pictures");
                pic.mkdirs();
                setPicFolder(pic.getAbsolutePath());

                File text = new File(mainFolder, "Text");
                text.mkdirs();
                setTextFolder(text.getAbsolutePath());

                setFirstLoad(false);
            }
        }
    }
}