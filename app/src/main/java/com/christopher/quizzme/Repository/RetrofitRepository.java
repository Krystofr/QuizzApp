package com.christopher.quizzme.Repository;

import android.text.Html;
import android.util.Log;

import com.christopher.quizzme.Model.Question;
import com.christopher.quizzme.Model.TriviaQuestionObject;
import com.christopher.quizzme.Model.TriviaResponseObject;

import java.util.ArrayList;
import java.util.Random;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RetrofitRepository {

    private static RetrofitRepository retrofitRepository;
    private TriviaApi triviaApi;

    public static RetrofitRepository getInstance() {
        if (retrofitRepository == null) {
            retrofitRepository = new RetrofitRepository();
        }
        return retrofitRepository;
    }

    public RetrofitRepository() {
        triviaApi = RetrofitClientInstance.getRetrofitInstance().create(TriviaApi.class);
    }

    public MutableLiveData<ArrayList<Question>> getQuestions(int length, int category, String level, String type) {
        final MutableLiveData<ArrayList<Question>> questions = new MutableLiveData<>();
        triviaApi.getQuestions(length,category,level,type)
                .subscribeOn(Schedulers.io())
                .doOnError(error -> Log.d("The error message is: " , error.getMessage()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<TriviaResponseObject>() {
                    @Override
                    public void onSuccess(TriviaResponseObject triviaResponseObject) {
                        Log.d("Repository",triviaResponseObject.getResults().get(0).getQuestion());

                        //manipulate the data

                        //List of formatted questions
                        ArrayList<Question> finalQuestions = new ArrayList<>();

                        for (TriviaQuestionObject questionObject : triviaResponseObject.getResults()) {

                            //Transform the questions
                            Question question = new Question();

                            //random number from 0 to 3
                            int answerIndex = new Random().nextInt(4);

                            //set question
                            //decode question object from HTML encoding to normal string
                            String decodedQuestionString = Html.fromHtml((String) questionObject.getQuestion()).toString();
                            question.setQuestion(decodedQuestionString);

                            //set answer
                            question.setAnswer(questionObject.getCorrect_answer());
                            //set title category of question
                            question.setTitle(questionObject.getCategory());

                            //answer choices
                            ArrayList<String> answerChoices = questionObject.getIncorrect_answers();
                            answerChoices.add(answerIndex, questionObject.getCorrect_answer());
                            //set answer options
                            question.setOption_a(Html.fromHtml((String) answerChoices.get(0) ).toString());
                            question.setOption_b(Html.fromHtml((String) answerChoices.get(1) ).toString());
                            question.setOption_c(Html.fromHtml((String) answerChoices.get(2) ).toString());
                            question.setOption_d(Html.fromHtml((String) answerChoices.get(3) ).toString());

                            //add question to list of questions
                            Log.d("Question",question.getTitle());
                            finalQuestions.add(question);


                        }

                        questions.postValue(finalQuestions);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        return questions;
    }
}
