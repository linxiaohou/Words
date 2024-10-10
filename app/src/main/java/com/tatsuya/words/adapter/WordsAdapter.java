package com.tatsuya.words.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.tatsuya.words.R;
import com.tatsuya.words.entity.Word;
import com.tatsuya.words.viewmodel.WordViewModel;

public class WordsAdapter extends ListAdapter<Word, WordsAdapter.WordsViewHolder> {
    private final Boolean useCardView;
    private final WordViewModel wordViewModel;

    public WordsAdapter(Boolean useCardView, WordViewModel wordViewModel) {
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.useCardView = useCardView;
        this.wordViewModel = wordViewModel;
    }

    public static class WordsViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_number, textView_english, textView_chinese;
        public MaterialSwitch switch_chinese_invisible;

        public WordsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_number = itemView.findViewById(R.id.textView_number);
            textView_english = itemView.findViewById(R.id.textView_english);
            textView_chinese = itemView.findViewById(R.id.textView_chinese);
            switch_chinese_invisible = itemView.findViewById(R.id.switch_chinese_invisible);
        }
    }

    @NonNull
    @Override
    public WordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (useCardView) {
            itemView = layoutInflater.inflate(R.layout.cell_word_card2, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.cell_word_normal2, parent, false);
        }
        final WordsViewHolder wordsViewHolder = new WordsViewHolder(itemView);
        wordsViewHolder.itemView.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://dict.youdao.com/result?word=" + wordsViewHolder.textView_english.getText() + "&lang=en");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            wordsViewHolder.itemView.getContext().startActivity(intent);
        });
        wordsViewHolder.switch_chinese_invisible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Word word = (Word) wordsViewHolder.itemView.getTag(R.id.word_for_view_holder);
            if (isChecked) {
                wordsViewHolder.textView_chinese.setVisibility(View.GONE);
                word.setChineseInvisible(true);
                wordViewModel.updateWords(word);
            } else {
                wordsViewHolder.textView_chinese.setVisibility(View.VISIBLE);
                word.setChineseInvisible(false);
                wordViewModel.updateWords(word);
            }
        });
        return wordsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WordsViewHolder holder, int position) {
        Word word = getItem(position);
        holder.itemView.setTag(R.id.word_for_view_holder, word);
        holder.textView_number.setText(String.valueOf(position + 1));
        holder.textView_english.setText(word.getEnglish());
        holder.textView_chinese.setText(word.getChineseMeaning());
        if (word.getChineseInvisible()) {
            holder.textView_chinese.setVisibility(View.GONE);
            holder.switch_chinese_invisible.setChecked(true);
        } else {
            holder.textView_chinese.setVisibility(View.VISIBLE);
            holder.switch_chinese_invisible.setChecked(false);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull WordsViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.textView_number.setText(String.valueOf(holder.getAdapterPosition() + 1));
    }
}