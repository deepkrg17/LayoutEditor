package com.itsvks.layouteditor.fragments.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        this.mText = new MutableLiveData<String>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getMText() {
        return this.mText;
    }
}
