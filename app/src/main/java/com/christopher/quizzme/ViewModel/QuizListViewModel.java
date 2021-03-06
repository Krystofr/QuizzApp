package com.christopher.quizzme.ViewModel;

import android.util.Log;

import com.christopher.quizzme.Repository.FirebaseRepository;
import com.christopher.quizzme.Repository.RetrofitRepository;
import com.christopher.quizzme.Model.Category;
import com.christopher.quizzme.Model.Question;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class QuizListViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {

    private MutableLiveData<ArrayList<Category>> quizListModelData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Question>> questions = new MutableLiveData<>();

    private RetrofitRepository retrofitRepository;

    //constructor
    public QuizListViewModel() {
        FirebaseRepository firebaseRepository = new FirebaseRepository(this);
        firebaseRepository.getQuizData();
        retrofitRepository = RetrofitRepository.getInstance();
    }

    //get categories
    public LiveData<ArrayList<Category>> getQuizListModelData() {
        return quizListModelData;
    }

    //get questions
    public LiveData<ArrayList<Question>> getQuestions(int length, int category, String level, String type) {
        questions = retrofitRepository.getQuestions(length, category, level, type);
        return questions;
    }


    //firebase callbacks
    @Override
    public void quizListDataAdded(ArrayList<Category> quizListModelsList) {
        quizListModelData.setValue(quizListModelsList);
    }

    @Override
    public void onError(String e) {
        Log.e("QUIZLISTVIEWMODEL-F", e);
    }

}
