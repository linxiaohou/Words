package com.tatsuya.words.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tatsuya.words.R;
import com.tatsuya.words.adapter.WordsAdapter;
import com.tatsuya.words.viewmodel.WordViewModel;


public class WordsFragment extends Fragment {
    private WordViewModel wordViewModel;
    private RecyclerView recyclerView;
    private WordsAdapter wordsAdapter1, wordsAdapter2;
    private FloatingActionButton floatingActionButton;


    public WordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wordViewModel = new ViewModelProvider(requireActivity()).get(WordViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        wordsAdapter1 = new WordsAdapter(false, wordViewModel);
        wordsAdapter2 = new WordsAdapter(true, wordViewModel);
        recyclerView.setAdapter(wordsAdapter1);
        wordViewModel.getAllWordsLive().observe(requireActivity(), words -> {
            int temp = wordsAdapter1.getItemCount();
            wordsAdapter1.setAllWords(words);
            wordsAdapter2.setAllWords(words);
            if (temp != words.size()) {
                wordsAdapter1.notifyDataSetChanged();
                wordsAdapter2.notifyDataSetChanged();
            }
        });
        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            NavController controller = Navigation.findNavController(v);
            controller.navigate(R.id.action_wordsFragment_to_addFragment);
        });
    }
}