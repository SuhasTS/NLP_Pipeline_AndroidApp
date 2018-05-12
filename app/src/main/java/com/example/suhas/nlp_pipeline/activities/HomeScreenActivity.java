package com.example.suhas.nlp_pipeline.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.suhas.nlp_pipeline.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeScreenActivity extends AppCompatActivity {

    @BindView(R.id.fileSimilarity)
    Button fileSimilarity;
    @BindView(R.id.nlpPieline)
    Button nlpPieline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.fileSimilarity, R.id.nlpPieline})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fileSimilarity:
                Intent fileSimilarityIntent=new Intent(this,MainActivity.class);
                startActivity(fileSimilarityIntent);
                break;
            case R.id.nlpPieline:
                Intent dataAnalysisIntent=new Intent(this,DataAnalysisActivity.class);
                startActivity(dataAnalysisIntent);
                break;
        }
    }
}
