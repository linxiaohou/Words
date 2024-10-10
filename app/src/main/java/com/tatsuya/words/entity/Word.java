package com.tatsuya.words.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Word {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "english_word")
    private String english;

    @ColumnInfo(name = "chinese_meaning")
    private String chineseMeaning;

    @ColumnInfo(name = "chinese_invisible")
    private Boolean chineseInvisible = false;

    public Boolean getChineseInvisible() {
        return chineseInvisible;
    }

    public void setChineseInvisible(Boolean chineseInvisible) {
        this.chineseInvisible = chineseInvisible;
    }

    public Word(String english, String chineseMeaning) {
        this.english = english;
        this.chineseMeaning = chineseMeaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        this.chineseMeaning = chineseMeaning;
    }

    public boolean equals(Word word) {
        return word.getEnglish().equals(this.english) &&
                word.getChineseMeaning().equals(this.chineseMeaning) &&
                word.getChineseInvisible().equals(this.chineseInvisible);
    }
}
