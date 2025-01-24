package ru.arnetech.randomtargets;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import ru.arnetech.randomtargets.params.ExtendedSettings;
import ru.arnetech.randomtargets.tasks.TaskTypes;
import ru.arnetech.randomtargets.user.UserPreferences;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;

public class SettingsActivity extends AppCompatActivity {
    private static final int EX_FILE_PICKER_RESULT = 1;
    static UserPreferences userPref;

    private static TaskTypes key = TaskTypes.NONE;

    TextView soundLengthTV, //продолжительность звука таймера
            minDelayTV,     //минимальная задержка
            randDelayTV,    //случайная задержка
            timeMemoryTV,   //время на запоминание задачи
            timeExtendTV,   //доп. время на запоминание задачи
            timeExecTV;     //время выполнения задачи

    Switch looping;  //циклиность задач

    TextView audioFolder,   //путь к папке с аудио
            picFolder,      //путь к папке с картинками
            textFolder;     //путь к папке с текстом

    CheckBox enableBeepAudio; //сигнал таймера для аудио задачи

    Switch audioPerm,       //разрешение аудио задач
            picPerm,        //разрешение картинок
            textPerm;       //разрешение текстовых задач

    Switch audioSound,      //звук к аудио
            picSound,       //звук к картинкам
            textSound;      //звук к текстам

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.timeSettings);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        userPref = new UserPreferences(this);

        //длительность таймера
        soundLengthTV = findViewById(R.id.soundLength);
        //минимальная задержка
        minDelayTV = findViewById(R.id.minDelay);
        //случайная задержка
        randDelayTV = findViewById(R.id.randDelay);
        //время запоминания
        timeMemoryTV = findViewById(R.id.timeMemorization);
        //дополнительное время
        timeExtendTV = findViewById(R.id.timeExtended);
        //время выполнения задачи
        timeExecTV = findViewById(R.id.timeExec);
        //заполнение текста
        updateText();

        //цикличность
        looping = findViewById(R.id.looping);
        looping.setChecked(userPref.isLooping());
        //изменение настройки цикличности
        looping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPref.setLooping(isChecked);
            }
        });

        //аудио
        audioFolder = findViewById(R.id.audioFolder);
        audioPerm = findViewById(R.id.audioPerm);
        audioPerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPref.setAudioAllow(isChecked);
            }
        });

        enableBeepAudio = findViewById(R.id.enableBeepAudio); //сигнал таймера для аудио
        enableBeepAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPref.setEnableBeepAudio(isChecked);
                audioSound.setEnabled(isChecked);
            }
        });

        audioSound = findViewById(R.id.audioSound);
        audioSound.setText(userPref.isAudioAllow() ? getText(R.string.soundBeforeTask) :
                getText(R.string.soundAfterTask));
        audioSound.setOnCheckedChangeListener(changeListener);

        //картинки
        picFolder = findViewById(R.id.picFolder);
        picPerm = findViewById(R.id.picPerm);
        picPerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPref.setPicAllow(isChecked);
            }
        });

        picSound = findViewById(R.id.picSound);
        picSound.setText(userPref.isPicAllow() ? getText(R.string.soundBeforeTask) :
                getText(R.string.soundAfterTask));
        picSound.setOnCheckedChangeListener(changeListener);

        //текст
        textFolder = findViewById(R.id.textFolder);
        textPerm = findViewById(R.id.textPerm);
        textPerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userPref.setTextAllow(isChecked);
            }
        });

        textSound = findViewById(R.id.textSound);
        textSound.setText(userPref.isTextAllow() ? getText(R.string.soundBeforeTask) :
                getText(R.string.soundAfterTask));
        textSound.setOnCheckedChangeListener(changeListener);

        //заполнить пути папок
        setFolderText();

        //заполнить разрешения задач
        setPermChecked();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.helpMenu:
                helpDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            buttonView.setText(isChecked ? getText(R.string.soundBeforeTask) :
                    getText(R.string.soundAfterTask));
            switch (buttonView.getId()) {
                case R.id.audioSound:
                    userPref.setAudioSound(isChecked);
                    break;
                case R.id.picSound:
                    userPref.setPicSound(isChecked);
                    break;
                case R.id.textSound:
                    userPref.setTextSound(isChecked);
                    break;
            }
        }
    };

    /**
     * Обработка нажатия на редактирование переменных
     * @param view
     */
    public void onPrefClick(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (view.getId() == R.id.soundLength_pref) {
            builder = selectNumberDialog(builder);
        } else {
            builder = inputNumDialog(view, builder);
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Создание диалога с выбором продолжительности звука таймера
     * @param builder {@link AlertDialog.Builder}
     * @return {@link AlertDialog.Builder}
     */
    private AlertDialog.Builder selectNumberDialog(AlertDialog.Builder builder) {
        LinearLayout layout;
        layout = (LinearLayout) getLayoutInflater().inflate(R.layout.time_length_dialog, null);
        builder.setView(layout);

        Spinner spinner = layout.findViewById(R.id.timeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ExtendedSettings.getTimes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final float[] num = new float[1];

        spinner.setSelection(adapter.getPosition(String.valueOf(userPref.getSoundLength())));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);

                num[0] = Float.parseFloat(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userPref.setSoundLength(num[0]);
                        updateText();
                    }
                })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        return builder;
    }

    /**
     * Создание диалога для ввода продолжительности определенного параметра
     * @param view
     * @param builder {@link AlertDialog.Builder}
     * @return {@link AlertDialog.Builder}
     */
    private AlertDialog.Builder inputNumDialog(final View view, AlertDialog.Builder builder) {
        LinearLayout layout;
        layout = (LinearLayout) getLayoutInflater().inflate(R.layout.input_dialog, null);
        builder.setView(layout);

        final EditText time = layout.findViewById(R.id.timePrefInput);
        TextView limit = layout.findViewById(R.id.limit);

        //значения начала и конца
        int start = 0;
        int end = 0;

        //Id выбранного парамера
        final int selectParamId = view.getId();

        //установка необходимого текста, время начала и конца
        switch (selectParamId) {
            case R.id.minDelay_pref:
            case R.id.timeExec_pref:
                start = 1;
                end = 60;

                limit.setText(getString(R.string.sixteenSec_Limit));
                break;
            case R.id.randDelay_pref:
            case R.id.memorization_pref:
            case R.id.extended_task:
                start = 1;
                end = 20;

                limit.setText(getString(R.string.twentySec_Limit));
                break;
        }


        final int finalStart = start;
        final int finalEnd = end;
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean success = true;

                        int sec;
                        try {
                            sec = Integer.parseInt(time.getText().toString());

                            //проверка на корректность ввода
                            if (ExtendedSettings.isPermissible(finalStart, finalEnd, sec)) {
                                switch (selectParamId) {
                                    case R.id.minDelay_pref:
                                        //минимальная задержка
                                        userPref.setMinDelay(sec);
                                        break;
                                    case R.id.randDelay_pref:
                                        //случайная задержка
                                        userPref.setRandDelay(sec);
                                        break;
                                    case R.id.timeExec_pref:
                                        //время выполнения задачи
                                        userPref.setImplementTime(sec);
                                        break;
                                    case R.id.memorization_pref:
                                        //запоминание
                                        userPref.setMemorization(sec);
                                        break;
                                    case R.id.extended_task:
                                        //дополнительное время
                                        userPref.setExtendedTime(sec);
                                        break;
                                }

                                //обновление текста
                                updateText();
                            } else {
                                success = false;
                            }
                        } catch (NumberFormatException e) {
                            //если введено не число
                            success = false;
                        }

                        if (!success) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.inCorrectEntered),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        return builder;
    }

    /**
     * Обработка диалога с FAQ
     */
    public void helpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ConstraintLayout layout;
        layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.help_dialog, null);
        builder.setView(layout)
                .setNegativeButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });;

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //обработчик нажатия на ConstraintLayout в выборе папок
    public void onFolderClick(View view) {
        ExFilePicker exFilePicker = new ExFilePicker();
        exFilePicker.setChoiceType(ExFilePicker.ChoiceType.DIRECTORIES);
        exFilePicker.setCanChooseOnlyOneItem(true);

        switch (view.getId()) {
            case R.id.audioFolder_pref:
                key = TaskTypes.AUDIO;
                break;
            case R.id.picFolder_pref:
                key = TaskTypes.PICTURE;
                break;
            case R.id.textFolder_pref:
                key = TaskTypes.TEXT;
                break;
        }

        exFilePicker.start(this, EX_FILE_PICKER_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == EX_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < result.getCount(); i++) {
                    stringBuilder.append(result.getNames().get(i));
                    if (i < result.getCount() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                String pathStr = result.getPath() + stringBuilder.toString();

                switch (key) {
                    case AUDIO:
                        userPref.setAudioFolder(pathStr);
                        break;
                    case PICTURE:
                        userPref.setPicFolder(pathStr);
                        break;
                    case TEXT:
                        userPref.setTextFolder(pathStr);
                        break;
                }

                setFolderText();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Установка значений параметров из настроек
     */
    private void updateText() {
        //длительность таймера
        soundLengthTV.setText(createLengthText(
                Float.toString(userPref.getSoundLength())));

        //минимальная задержка
        minDelayTV.setText(createLengthText(
                String.valueOf(userPref.getMinDelay())));

        //случайная задержка
        randDelayTV.setText(createLengthText(
                String.valueOf(userPref.getRandDelay())));

        //время выполнения задачи
        timeExecTV.setText(createLengthText(
                String.valueOf(userPref.getImplementTime())));

        //время запоминания
        timeMemoryTV.setText(createLengthText(
                String.valueOf(userPref.getMemorization())));

        //дополнительное время
        timeExtendTV.setText(createLengthText(
                String.valueOf(userPref.getExtendedTime())));
    }

    /**
     * Создание текста с секундами
     * @param time время
     * @return строка вида time + сек
     */
    private String createLengthText(String time) {
        return String.format("%s %s", time, getString(R.string.seconds));
    }

    /**
     * Получение путей к папкам из настроек
     */
    private void setFolderText() {
        audioFolder.setText(userPref.getAudioFolder());
        audioPerm.setEnabled(!audioFolder.getText().toString().isEmpty());

        picFolder.setText(userPref.getPicFolder());
        picPerm.setEnabled(!picFolder.getText().toString().isEmpty());

        textFolder.setText(userPref.getTextFolder());
        textPerm.setEnabled(!textFolder.getText().toString().isEmpty());
    }

    /**
     * Установка значений на checkbox из настроек
     */
    private void setPermChecked() {
        audioPerm.setChecked(userPref.isAudioAllow());
        enableBeepAudio.setChecked(userPref.isEnableBeepAudio());
        audioSound.setEnabled(enableBeepAudio.isChecked());
        audioSound.setChecked(userPref.isAudioSound());

        picPerm.setChecked(userPref.isPicAllow());
        picSound.setChecked(userPref.isPicSound());

        textPerm.setChecked(userPref.isTextAllow());
        textSound.setChecked(userPref.isTextSound());
    }

}