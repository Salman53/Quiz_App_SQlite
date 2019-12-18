package com.example.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.quiz.QuizContract.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class QuizDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyAwesomeQuiz.db";
    private static final int DATABASE_VERSION = 1;

    private static QuizDbHelper instance;

    private SQLiteDatabase db;



    public QuizDbHelper( Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized QuizDbHelper getInstance(Context context){
        if (instance == null)
        {
            instance = new QuizDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
     this.db=db;

     final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
             CategoriesTable.TABLE_NAME + "( " +
             CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
             CategoriesTable.COLUMN_NAME + " TEXT " +
             ")";

       final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +QuestionsTable.TABLE_NAME +
                 "("+ QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
             QuestionsTable.COLUMN_QUESTION + " TEXT,"
               +QuestionsTable.COLUMN_OPTION1 + " TEXT,"
               + QuestionsTable.COLUMN_OPTION2 + " TEXT,"
               +QuestionsTable.COLUMN_OPTION3 + " TEXT,"
               +QuestionsTable.COLUMN_ANSWER_NR + " INTEGER, " +
               QuestionsTable.COLUMN_DIFFICULTY + " TEXT, " +
               QuestionsTable.COLUMN_CATEGORY_ID + " INTEGER," +
               "FOREIGN KEY(" + QuestionsTable.COLUMN_CATEGORY_ID + ") REFERENCES " +
               CategoriesTable.TABLE_NAME + "(" + CategoriesTable._ID + ")" + "ON DELETE CASCADE" +
                ")";

       db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillCategoriesTable();
        fillQuestionsTable();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
       db.execSQL("DROP TABLE IF  EXISTS " + CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db)
    {
        super.onConfigure(db);
        //db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable()
    {
        Category C1 = new Category("Programming");
       insertCategory(C1);
        Category C2 = new Category("Geography");
        insertCategory(C2);
        Category C3 = new Category("Math");
        insertCategory(C3);
    }

    public void addCategory(Category category)
    {
        db = getWritableDatabase();
        insertCategory(category);
    }

    public void addCategories(List<Category> categories)
    {
      db = getWritableDatabase();
      for (Category category : categories)
      {
          insertCategory(category);
      }

    }

    private void insertCategory(Category category)
    {
        ContentValues CV = new ContentValues();
        CV.put(CategoriesTable.COLUMN_NAME,category.getName());
        db.insert(CategoriesTable.TABLE_NAME, null, CV);

    }

    private void fillQuestionsTable()
    {
        Question q1 = new Question("Programming, Easy: A is correct" ,
                "A" , "B" , "C" , 1, Question.DIFFICULTY_EASY,Category.PROGRAMMING);
        insertQuestion(q1);

        Question q2 = new Question("Geography, Easy: A is correct" ,
                "A" , "B" , "C" , 2, Question.DIFFICULTY_MEDIUM,Category.GEOGRAPHY);
        insertQuestion(q2);
        Question q3 = new Question("MATH, Hard: A is correct" ,
                "A" , "B" , "C" , 3, Question.DIFFICULTY_HARD,Category.MATH);
        insertQuestion(q3);
        Question q4 = new Question("MATH, Easy: A is correct" ,
                "A" , "B" , "C" , 1, Question.DIFFICULTY_EASY,Category.MATH);
        insertQuestion(q4);
        Question q5 = new Question("Non_existing, Easy: A is correct" ,
                "A" , "B" , "C" , 1, Question.DIFFICULTY_EASY,4);
        insertQuestion(q5);
        Question q6 = new Question("Non_existing, Medium: A is correct" ,
                "A" , "B" , "C" , 2, Question.DIFFICULTY_MEDIUM,5);
        insertQuestion(q6);
    }

    public void addQuestion(Question question)
    {
        db = getWritableDatabase();
        insertQuestion(question);
    }

    /*public void addQuestions(List<Category> questions)
    {
        db = getWritableDatabase();
        for (Question question : questions)
        {
            insertQuestion(question);

        }

    }*/


    private void insertQuestion(Question question){
         ContentValues cv = new ContentValues();
         cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
         cv.put(QuestionsTable.COLUMN_OPTION1, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        cv.put(QuestionsTable.COLUMN_DIFFICULTY, question.getDifficulty());
        cv.put(QuestionsTable.COLUMN_CATEGORY_ID, question.getCategoryID());
        db.insert(QuestionsTable.TABLE_NAME,null,cv);
    }

    public List<Category> getAllCategory()
    {
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c =db.rawQuery("SELECT * FROM " + CategoriesTable.TABLE_NAME,null);

        if (c.moveToFirst())
        {
            do {
                Category category = new Category();
                category.setId(c.getInt(c.getColumnIndex(CategoriesTable._ID)));
                category.setName(c.getString(c.getColumnIndex(CategoriesTable.COLUMN_NAME)));
                categoryList.add(category);
            }while (c.moveToNext());
        }
        c.close();
        return categoryList;
    }

    public ArrayList<Question> getAllQuestion(){
        ArrayList<Question> questionList = new ArrayList<>();
        db =getReadableDatabase();
        Cursor C = db.rawQuery("SELECT * FROM " +QuestionsTable .TABLE_NAME,null);
        if (C.moveToFirst()){
            do {
Question question = new Question();
                question.setId(C.getColumnIndex(QuestionsTable._ID));
                question.setQuestion(C.getString(C.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(C.getString(C.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));

                question.setOption2(C.getString(C.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(C.getString(C.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));

                question.setAnswerNr(C.getInt(C.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(C.getString(C.getColumnIndex(QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(C.getInt(C.getColumnIndex(QuestionsTable.COLUMN_CATEGORY_ID)));
                 questionList.add(question);
            }while (C.moveToNext());
        }
       C.close();
        return questionList;
            }


    public ArrayList<Question> getQuestions(int categoryID ,String difficulty){
        ArrayList<Question> questionList = new ArrayList<>();
        db =getReadableDatabase();
        Log.d("DIFFICULTY : ","Yeh hai "+QuestionsTable.COLUMN_DIFFICULTY);
        String selection = QuestionsTable.COLUMN_CATEGORY_ID + " = ? " +
                " AND " + QuestionsTable.COLUMN_DIFFICULTY + " = ? ";

        String[] selectionArgs = new String[]{
                String.valueOf(categoryID), difficulty};
        Cursor c = db.query(QuestionsTable.TABLE_NAME,null,selection,selectionArgs,
                null,null,null);

        if (c.moveToFirst()){
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));

                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));

                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            }while (c.moveToNext());
        }
        c.close();
        return questionList;

    }


}


