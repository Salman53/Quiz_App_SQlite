package com.example.quiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_CATEGORY_ID = "extraCategoryID";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";
    public static final String EXTRA_DIFFICLTY = "extraDifficulty";


    public static final String SHARED_PREFS = "sharedprefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";
    private Spinner spinnerDifficulty;
    private Spinner spinnerCategory;
    private TextView textViewHighscore;
    private int highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonstartquiz= findViewById(R.id.button_start_quiz);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        spinnerCategory =findViewById(R.id.spinner_category);
        buttonstartquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuiz();
            }
        });
        textViewHighscore = findViewById(R.id.text_view_highscore);
        String[] difficultyLevels = Question.getAllDifficultyLevels();
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);


        loadCategories();
        loadDifficultyLevels();
        loadHighscore();
        startQuiz();
    }

    private void startQuiz(){
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        int categoryID = selectedCategory.getId();
        String categoryName = selectedCategory.getName();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryID);
        intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        startActivityForResult(intent , REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QUIZ)
        {
            if (resultCode==RESULT_OK){
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE,0);
                if(score>highscore)
                {
                    updateHighscore(score);
                }
            }

        }
    }

    private void loadCategories()
    {
        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
        List<Category> categories = dbHelper.getAllCategory();

        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this ,
                android.R.layout.simple_spinner_item , categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategories);
    }
    private void loadDifficultyLevels()
    {
        String[] difficultyLevels = Question.getAllDifficultyLevels();
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);


    }

    private void loadHighscore()
    {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS , MODE_PRIVATE);
        highscore = prefs.getInt(KEY_HIGHSCORE , 0);
        textViewHighscore.setText("Highscore:" + highscore);
    }

    private void updateHighscore(int highscoreNew){
        highscore =highscoreNew;
        textViewHighscore.setText("Highscore:" + highscore);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS , MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE , highscore);
        editor.apply();
    }
}

