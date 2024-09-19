package com.tatsuya.words.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tatsuya.words.entity.Word;
import com.tatsuya.words.repository.WordRepository;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private final WordRepository wordRepository;

    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }

    public LiveData<List<Word>> getAllWordsLive() {
        return wordRepository.getAllWordsLive();
    }

    public void insetWords(Word... words) {
        wordRepository.insetWords(words);
    }

    public void updateWords(Word... words) {
        wordRepository.updateWords(words);
    }

    public void deleteWords(Word... words) {
        wordRepository.deleteWords(words);
    }

    public void deleteAllWords() {
        wordRepository.deleteAllWords();
    }

}
