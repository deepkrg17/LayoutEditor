package com.itsvks.layouteditor.fragments.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public AboutViewModel() {
        this.mText = new MutableLiveData<String>();
        mText.setValue("This is about me.\nThis is my BiO.");
    }

    public LiveData<String> getMText() {
        return this.mText;
    }
}
