package ru.arnetech.randomtargets;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import ru.arnetech.randomtargets.tasks.TaskParams;
import ru.arnetech.randomtargets.tasks.TaskTypes;
import ru.arnetech.randomtargets.params.TimerStage;
import ru.arnetech.randomtargets.user.UserPreferences;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 0;
    private static final int colorGray = android.R.color.darker_gray;
    private static final int colorPrimary = R.color.colorPrimary;

    static UserPreferences userPref; //настройки пользователя
    static TaskParams params;

    Timer mTimer; //таймер

    MediaPlayer mPlayer, mPlayerTime;

    Button startBtn, nextBtn; //кнопка старт и далее
    TextView taskText; //текстовая задача
    ImageView taskPic; //картинка с заданием

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //запрет на блокировку экрана
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        userPref = new UserPreferences(this);
        params = new TaskParams(this, userPref);

        checkPermission();

        //заполение листов
        params.createFiles();

        startBtn = findViewById(R.id.startButton); //кнопка старт
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startBtn.getText().equals(getString(R.string.start))) {
                    String toastMessage = params.checkTasks();

                    if (toastMessage == null) {
                        //запуск задачи
                        startTask();
                    } else {
                        //вывод Toast с ошибкой
                        Toast toast = Toast.makeText(getApplicationContext(),
                                toastMessage, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } else {
                    //если стоп
                    stopTask();
                }
            }
        });

        nextBtn = findViewById(R.id.nextButton); //кнопка далее
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTask();
            }
        });
        setButtonEnable(false, colorGray);

        taskText = findViewById(R.id.taskText); //текстовая задача
        taskPic = findViewById(R.id.taskPic); //картинка с заданием
    }

    /**
     * Проверка разрешения на запись на внутреннюю память
     */
    private void checkPermission() {
        if (userPref.checkReadPerm() != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            userPref.setFolders();
        }
    }

    /**
     * Провека запроса разрешения на запись в память
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                userPref.setFolders();
            }
        }
    }

    /**
     * Запуск следующей задачи, при условии что есть условие "повторение задач"
     */
    private void nextTask() {
        if (userPref.isLooping()) {
            //запуск новой задачи
            mTimer.cancel();
            startTask();
            setButtonEnable(false, colorGray);
        } else {
            //остановка задач
            stopTask();
        }
    }

    /**
     * Запуск задачи
     */
    private void startTask() {
        startBtn.setText(getString(R.string.stop));
        startBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        //запуск минимальной задержки
        mTimer = new Timer(userPref.getMinDelay(),
                1000,
                TimerStage.MIN_DELAY,
                TaskTypes.NONE);
        mTimer.start();
    }

    /**
     * Остановка задачи
     */
    private void stopTask() {
        startBtn.setText(getString(R.string.start));
        startBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        setButtonEnable(false, colorGray);
        try {
            mTimer.cancel();
        } catch (NullPointerException ignored) {
        }
        try {
            mPlayer.release();
        } catch (NullPointerException ignored) {
        }
        taskPic.setVisibility(View.INVISIBLE);
        taskText.setText("");
    }

    /**
     * При скрытии активности, текущая задача останавливается
     */
    @Override
    protected void onPause() {
        super.onPause();
        //остановка задачи
        stopTask();
    }

    /**
     * Обновление активности после обратного появления
     */
    @Override
    protected void onResume() {
        super.onResume();
        //заполение листа после возможного обновления настроек
        params.createFiles();

        if (startBtn.getVisibility() == View.INVISIBLE) {
            startBtn.setVisibility(View.VISIBLE);
        }

        if (nextBtn.getVisibility() == View.INVISIBLE) {
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Остановка задачи при закрытии
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTask();
    }

    /**
     * Создание меню
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Обработка кнопок в меню
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingsMenu) {
            //Настройки приложения
            stopTask();
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Звучание таймера в зависимости от установленных настроек
     * @param taskTypes {@link TaskTypes}
     * @param b
     */
    private void isSetBeep(TaskTypes taskTypes, boolean b) {
        switch (taskTypes) {
            case AUDIO:
                if (userPref.isAudioSound() == b)
                    setBeepSound();
                break;
            case PICTURE:
                if (userPref.isPicSound() == b)
                    setBeepSound();
                break;
            case TEXT:
                if (userPref.isTextSound() == b)
                    setBeepSound();
                break;
        }
    }

    /**
     * Звучание таймера
     */
    private void setBeepSound() {
        //Создание MediaPlayer со звуком таймера
        mPlayerTime = MediaPlayer.create(this, R.raw.timer);

        mPlayerTime.seekTo(0);
        mPlayerTime.setLooping(false);

        //Установка таймера с длинной звука таймера, указанной в настройках
        PoolTimer timer = new PoolTimer((long) (userPref.getSoundLength() * 1000),
                1000);
        mPlayerTime.start();
        timer.start();

        mPlayerTime.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerTime.release();
            }
        });
    }

    /**
     * Установка текущей задачи
     */
    private void setTask() {
        setButtonEnable(false, colorGray);

        switch (params.getRandomTask()) {
            case AUDIO:
                createAudioTask();
                break;
            case PICTURE:
                createPicTask();
                break;
            case TEXT:
                createTextTask();
                break;
        }
    }

    /**
     * Создание аудио задачи
     */
    private void createAudioTask() {
        final TaskTypes type = TaskTypes.AUDIO;

        File audio = params.getRandomAudio();

        mPlayer = MediaPlayer.create(this, Uri.fromFile(audio));
        mPlayer.seekTo(0);
        mPlayer.setLooping(false);

        //звук таймера, если указан в настройках
        if (userPref.isEnableBeepAudio()) {
            isSetBeep(type, true);
        }

        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.release();
                mTimer = new Timer(
                        userPref.getImplementTime(),
                        1000,
                        TimerStage.IMPLEMENTATION,
                        type);

                if (userPref.isEnableBeepAudio()) {
                    isSetBeep(type, false);
                }

                setButtonEnable(true, colorPrimary);
                mTimer.start();
            }
        });
    }

    /**
     * Создание текстовой задачи
     */
    private void createTextTask() {
        TaskTypes type = TaskTypes.TEXT;

        String result = params.getRandomText();
        if (result != null) {
            taskText.setText(result);
        } else {
            setTask();
        }

        startBtn.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);

        isSetBeep(type, true);
        mTimer = new Timer(userPref.getMemorization(),
                1000,
                TimerStage.MEMORIZATION,
                type);
        mTimer.start();
    }

    /**
     * Создание задачи с картинкой
     */
    private void createPicTask() {
        TaskTypes type = TaskTypes.PICTURE;

        File pic = params.getRandomPic();
        taskPic.setImageDrawable(Drawable.createFromPath(pic.getPath()));
        taskPic.setVisibility(View.VISIBLE);

        //запуск таймера на запоминание
        isSetBeep(type, true);
        startBtn.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        mTimer = new Timer(userPref.getMemorization(),
                1000,
                TimerStage.MEMORIZATION,
                type);

        mTimer.start();
    }


    /**
     * Установка состояния для кнопки Далее
     * @param b
     * @param color
     */
    private void setButtonEnable(boolean b, int color) {
        nextBtn.setEnabled(b);
        nextBtn.setBackgroundColor(getResources().getColor(color));
    }

    /**
     * Таймер для обработки состояний
     */
    class Timer extends CountDownTimer {
        private final TimerStage stage;
        private final TaskTypes type; //код текущей задачи

        public Timer(long millisInFuture, long countDownInterval, TimerStage stage, TaskTypes type) {
            super((millisInFuture * 1000), countDownInterval);
            this.stage = stage;
            this.type = type;
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            switch (stage) {
                case MIN_DELAY:
                    //минимальная задержка
                    mTimer = new Timer((long) (Math.random() * userPref.getRandDelay()),
                            1000,
                            TimerStage.RAND_DELAY,
                            type);
                    mTimer.start();
                    break;
                case RAND_DELAY:
                    //случайная задержка
                    isSetBeep(type, true);
                    setTask();
                    break;
                case MEMORIZATION:
                    //время на запоминание
                    mTimer = new Timer((long) (Math.random() * userPref.getExtendedTime()),
                            1000,
                            TimerStage.EXTENDED,
                            type);
                    mTimer.start();
                    break;
                case EXTENDED:
                    //дополнительное время
                    taskPic.setVisibility(View.INVISIBLE);
                    taskText.setText("");

                    setButtonEnable(true, colorPrimary);
                    isSetBeep(type, false);

                    startBtn.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);

                    mTimer = new Timer(userPref.getImplementTime(),
                            1000,
                            TimerStage.IMPLEMENTATION,
                            type);
                    mTimer.start();
                    break;
                case IMPLEMENTATION:
                    //время выполнения
                    nextTask();
                    break;
            }
        }
    }

    /**
     * Таймер для обработки звука таймера на n-ое кол-во секунд
     */
    class PoolTimer extends CountDownTimer {
        public PoolTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            mPlayerTime.release();
        }
    }

}