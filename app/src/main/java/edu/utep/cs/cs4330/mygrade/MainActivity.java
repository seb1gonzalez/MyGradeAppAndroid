package edu.utep.cs.cs4330.mygrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText userEdit;
    private EditText pinEdit;
    private Button submitButton;
    private WebClient webClient;
    private String u;
    private String p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        submitButton = findViewById(R.id.submitButton);

    }
    public void submitClicked(View view){

        u = userEdit.getText().toString();
        p = pinEdit.getText().toString();
        Intent i = new Intent(this,GradeActivity.class);
        i.putExtra("user",u);
        i.putExtra("pin",p);
        startActivity(i);
        webClient.query(u,p);

    }
}
