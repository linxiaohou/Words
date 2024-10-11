package com.tatsuya.words.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tatsuya.words.R;
import com.tatsuya.words.adapter.WordsAdapter;
import com.tatsuya.words.entity.Word;
import com.tatsuya.words.viewmodel.WordViewModel;

import java.util.List;


public class WordsFragment extends Fragment {
    private WordViewModel wordViewModel;
    private RecyclerView recyclerView;
    private WordsAdapter wordsAdapter1, wordsAdapter2;
    private LiveData<List<Word>> filteredWords;
    private List<Word> allWords;
    private boolean undoAction = false;
    private DividerItemDecoration dividerItemDecoration;
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
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                        WordsAdapter.WordsViewHolder wordsViewHolder = (WordsAdapter.WordsViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (wordsViewHolder != null) {
                            wordsViewHolder.textView_number.setText(String.valueOf(i + 1));
                        }
                    }
                }
            }
        });
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
        boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW, false);
        dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        ;
        if (viewType) {
            recyclerView.setAdapter(wordsAdapter2);
        } else {
            recyclerView.setAdapter(wordsAdapter1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
        filteredWords = wordViewModel.getAllWordsLive();
        filteredWords.observe(getViewLifecycleOwner(), words -> {
            int temp = wordsAdapter1.getItemCount();
            allWords = words;
            if (temp != words.size()) {
                if (temp < words.size() && !undoAction) {
                    recyclerView.smoothScrollBy(0, -200);
                }
                undoAction = false;
                wordsAdapter1.submitList(words);
                wordsAdapter2.submitList(words);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Word word = allWords.get(viewHolder.getAdapterPosition());
                wordViewModel.deleteWords(word);
                Snackbar.make(requireView(), R.string.message_deleted_a_word, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.message_undo, v -> {
                            wordViewModel.insetWords(word);
                            undoAction = true;
                        }).show();
            }

            //滑动删除的时候绘制灰色背景和显示垃圾桶图标，以增强视觉效果
            Drawable icon = ContextCompat.getDrawable(requireActivity(), R.drawable.baseline_delete_forever_24);
            Drawable background = new ColorDrawable(Color.LTGRAY);

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                int iconLeft, iconRight, iconTop, iconBottom;
                int backTop, backBottom, backLeft, backRight;
                backTop = itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                iconBottom = iconTop + icon.getIntrinsicHeight();
                if (dX > 0) {
                    backLeft = itemView.getLeft();
                    backRight = itemView.getLeft() + (int) dX;
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicHeight();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else if (dX < 0) {
                    backRight = itemView.getRight();
                    backLeft = itemView.getRight() + (int) dX;
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconRight = itemView.getRight() - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicHeight();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else {
                    background.setBounds(0, 0, 0, 0);
                    icon.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                icon.draw(c);
            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
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
            boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW, false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (viewType) {
                recyclerView.setAdapter(wordsAdapter1);
                recyclerView.addItemDecoration(dividerItemDecoration);
                editor.putBoolean(IS_USING_CARD_VIEW, false);
            } else {
                recyclerView.setAdapter(wordsAdapter2);
                recyclerView.removeItemDecoration(dividerItemDecoration);
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
                    filteredWords.observe(getViewLifecycleOwner(), words -> {
                        int temp = wordsAdapter1.getItemCount();
                        allWords = words;
                        if (temp != words.size()) {
                            wordsAdapter1.submitList(words);
                            wordsAdapter2.submitList(words);
                        }
                    });
                    return true;
                }
            });
        }

    }
}