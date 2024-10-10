package com.tatsuya.words.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tatsuya.words.R;
import com.tatsuya.words.adapter.WordsAdapter;
import com.tatsuya.words.entity.Word;
import com.tatsuya.words.viewmodel.WordViewModel;

import java.util.List;


public class WordsFragment extends Fragment {
    private WordViewModel wordViewModel;
    private RecyclerView recyclerView;
    private WordsAdapter wordsAdapter1, wordsAdapter2;
    private FloatingActionButton floatingActionButton;
    private LiveData<List<Word>> filteredWords;
    private static final String VIEW_TYPE_SHP = "view_type_shp";
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";

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
        setHasOptionsMenu(true);
        wordViewModel = new ViewModelProvider(requireActivity()).get(WordViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        wordsAdapter1 = new WordsAdapter(false, wordViewModel);
        wordsAdapter2 = new WordsAdapter(true, wordViewModel);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
        Boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW, false);
        if (viewType) {
            recyclerView.setAdapter(wordsAdapter2);
        } else {
            recyclerView.setAdapter(wordsAdapter1);
        }
        filteredWords = wordViewModel.getAllWordsLive();
        filteredWords.observe(requireActivity(), words -> {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clearData) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle(R.string.message_clearData);
            builder.setPositiveButton(R.string.message_OK, (dialog, which) -> wordViewModel.deleteAllWords());
            builder.setNegativeButton(R.string.message_cancel, (dialog, which) -> {

            });
            builder.create();
            builder.show();
        } else if (item.getItemId() == R.id.swtichViewType) {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
            Boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW, false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (viewType) {
                recyclerView.setAdapter(wordsAdapter1);
                editor.putBoolean(IS_USING_CARD_VIEW, false);
            } else {
                recyclerView.setAdapter(wordsAdapter2);
                editor.putBoolean(IS_USING_CARD_VIEW, true);
            }
            editor.apply();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        if (searchView != null) {
            searchView.setMaxWidth(650);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    String pattern = newText.trim();
                    filteredWords.removeObservers(requireActivity());//重要，清除之前设定的观察以避免发生冲突
                    filteredWords = wordViewModel.getWordsByPattern(pattern);
                    filteredWords.observe(requireActivity(), words -> {
                        int temp = wordsAdapter1.getItemCount();
                        wordsAdapter1.setAllWords(words);
                        wordsAdapter2.setAllWords(words);
                        if (temp != words.size()) {
                            wordsAdapter1.notifyDataSetChanged();
                            wordsAdapter2.notifyDataSetChanged();
                        }
                    });
                    return true;
                }
            });
        }

    }
}