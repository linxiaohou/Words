package com.tatsuya.words;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.tatsuya.words.adapter.WordsAdapter;
import com.tatsuya.words.entity.Word;
import com.tatsuya.words.viewmodel.WordViewModel;

public class MainActivity extends AppCompatActivity {
    Button button_insert, button_clear;
    MaterialSwitch switch1;
    WordViewModel wordViewModel;
    RecyclerView recyclerView;
    WordsAdapter wordsAdapter1, wordsAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerView);
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordsAdapter1 = new WordsAdapter(false, wordViewModel);
        wordsAdapter2 = new WordsAdapter(true, wordViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(wordsAdapter1);

        switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                recyclerView.setAdapter(wordsAdapter2);
            } else {
                recyclerView.setAdapter(wordsAdapter1);
            }
        });

        button_insert = findViewById(R.id.button_insert);
        button_clear = findViewById(R.id.button_clear);

        wordViewModel.getAllWordsLive().observe(this, words -> {
            int temp = wordsAdapter1.getItemCount();
            wordsAdapter1.setAllWords(words);
            wordsAdapter2.setAllWords(words);
            if (temp != words.size()) {
                wordsAdapter1.notifyDataSetChanged();
                wordsAdapter2.notifyDataSetChanged();
            }
        });
        button_insert.setOnClickListener(v -> {
            Word word1 = new Word("Hello", "你好");
            Word word2 = new Word("World", "世界");
            wordViewModel.insetWords(word1, word2);
        });
        button_clear.setOnClickListener(v -> wordViewModel.deleteAllWords());

    }
}