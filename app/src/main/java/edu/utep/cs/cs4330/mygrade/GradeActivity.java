package edu.utep.cs.cs4330.mygrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GradeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        Intent j = getIntent();
        String user = j.getStringExtra("user");
        String pin = j.getStringExtra("pin");
    }
}
