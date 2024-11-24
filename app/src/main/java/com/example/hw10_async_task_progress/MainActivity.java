package com.example.hw10_async_task_progress;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private static final int MILLISECONDS_SLEEP = 300;

    private AtomicBoolean cancelFlag;

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

        cancelFlag = new AtomicBoolean(true);
    }

    public void startProgressOnClick(View view) {
        if(cancelFlag.get()) {
            resetProgressBar();

            cancelFlag.set(false);
            performingHeavyTask();
        }
    }

    public void cancelProgressOnClick(View view) {
        if(!cancelFlag.get()) {
            cancelFlag.set(true);
            updateInfo("0%", R.string.pending);

            resetProgressBar();
        }
    }

    public void performingHeavyTask() {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(MILLISECONDS_SLEEP);

                    if(cancelFlag.get()) {
                        break;
                    }

                    runOnUiThread(this::publishProgress);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            cancelFlag.set(true);
        });

        thread.start();
    }

    public void publishProgress() {
        ProgressBar progressBar = findViewById(R.id.progressBar);

        int progress = progressBar.getProgress();
        progress++;

        progressBar.setProgress(progress);

        if(progress == 100) {
            updateInfo("100%", R.string.finished);
        } else {
            updateInfo(progress + "%", R.string.running);
        }
    }

    public void resetProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
    }

    public void updateInfo(String percent, int status) {
        TextView statusTextView = findViewById(R.id.statusTextView);
        TextView percentageTextView = findViewById(R.id.percentageTextView);

        statusTextView.setText(status);
        percentageTextView.setText(percent);
    }
}