package com.itsvks.layouteditor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.itsaky.androidide.logsender.LogSender;
import com.itsvks.layouteditor.CrashHandler;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogSender.startLogging(this);
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    }
}
