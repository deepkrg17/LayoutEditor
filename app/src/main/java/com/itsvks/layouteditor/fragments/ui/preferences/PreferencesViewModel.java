package com.itsvks.layouteditor.fragments.ui.preferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PreferencesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public PreferencesViewModel() {
        this.mText = new MutableLiveData<String>();
        mText.setValue("This is preferences fragment");
    }

    public LiveData<String> getMText() {
        return this.mText;
    }
}
