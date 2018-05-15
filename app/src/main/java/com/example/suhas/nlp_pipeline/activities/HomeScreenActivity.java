package com.example.suhas.nlp_pipeline.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.suhas.nlp_pipeline.R;
import com.example.suhas.nlp_pipeline.data.preference.SharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeScreenActivity extends AppCompatActivity {

    @BindView(R.id.fileSimilarity)
    Button fileSimilarity;
    @BindView(R.id.nlpPieline)
    Button nlpPieline;
    @BindView(R.id.urlTextView)
    EditText urlTextView;
    @BindView(R.id.setUrl)
    Button setUrl;
    //hard-coded
    String[] typeOfDataSet=new String[]{"Movie plots","Random documents"};
    String[] postNameDataset=new String[]{"movie_plots","source2"};
    int[] indexes=new int[]{0,1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);
        String urlFromPref=SharedPreference.get(this,SharedPreference.URL,"");
        if(!TextUtils.isEmpty(urlFromPref))
            urlTextView.setText(urlFromPref);

    }

    @OnClick({R.id.fileSimilarity, R.id.nlpPieline,R.id.setUrl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fileSimilarity:
                if (!TextUtils.isEmpty(urlTextView.getText())) {
                    Intent fileSimilarityIntent = new Intent(this, FileSimilarity.class);
                    startActivity(fileSimilarityIntent);
                } else
                    Toast.makeText(this, "Enter the server base url", Toast.LENGTH_LONG).show();
                break;


            case R.id.nlpPieline:
                if (!TextUtils.isEmpty(urlTextView.getText())) {
                    Intent dataAnalysisIntent = new Intent(this, DataAnalysisActivity.class);
                    new MaterialDialog.Builder(this)
                            .title("Choose input documents")
                            .items(typeOfDataSet)
                            .itemsIds(indexes)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                                {
                                   dataAnalysisIntent.putExtra("type",postNameDataset[which]);
                                    startActivity(dataAnalysisIntent);
                                }
                            })
                            .show();

                } else
                    Toast.makeText(this, "Enter the server base url", Toast.LENGTH_LONG).show();
                break;
            case R.id.setUrl:
                if(!TextUtils.isEmpty(urlTextView.getText())) {
                    SharedPreference.save(this, SharedPreference.URL, urlTextView.getText().toString());
                    Toast.makeText(this, "URL Set", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this, "Enter the server base url", Toast.LENGTH_LONG).show();
                break;



        }
    }
}
