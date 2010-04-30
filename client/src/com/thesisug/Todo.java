package com.thesisug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Todo extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo);
        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.getmusic);
        button.setOnClickListener(mGetMusicListener);
    }

    private OnClickListener mGetMusicListener = new OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent, "Select picture"));
        }
    };
}
