package com.example.suhas.nlp_pipeline.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.suhas.nlp_pipeline.R;
import com.example.suhas.nlp_pipeline.adapters.EachClusterSummaryAdapter;
import com.example.suhas.nlp_pipeline.data.preference.SharedPreference;
import com.example.suhas.nlp_pipeline.helpers.NLPApplication;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EachFileDetails extends AppCompatActivity {
    @BindView(R.id.fileContents)
    TextView fileContents;
    @BindView(R.id.getTopWords)
    Button getTopWords;
    private String url;
    private SimpleArcDialog mDialog;
    private List<String> topWords;
    private String filePath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_file_details);
        ButterKnife.bind(this);
        url = SharedPreference.get(this,SharedPreference.URL,"")+"/api/file_details/";
        mDialog = new SimpleArcDialog(this);
        mDialog.setConfiguration(new ArcConfiguration(this));
        topWords=new ArrayList<>();
        fileContents.setMovementMethod(new ScrollingMovementMethod());
        filePath=getIntent().getStringExtra("path");
        if(!TextUtils.isEmpty(filePath)) {
            try {
                makeJsonRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getTopWords.setOnClickListener(view1->
        {
            View view= LayoutInflater.from(this).inflate(R.layout.each_file_top_words,null);
            RecyclerView summaryRecylerView=(RecyclerView)view.findViewById(R.id.eachFileRecyclerView);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setAlignItems(AlignItems.STRETCH);
            summaryRecylerView.setLayoutManager(layoutManager);
            EachClusterSummaryAdapter summaryAdapter=new EachClusterSummaryAdapter(topWords,this);
            summaryRecylerView.setAdapter(summaryAdapter);
            new MaterialDialog.Builder(this)
                    .title("Summary")
                    .customView(view, true)
                    .show();
        });
    }

    public void makeJsonRequest() throws JSONException {
        mDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject request=new JSONObject();
        request.put("file",filePath);
        JsonObjectRequest dummyRequest = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        try {
                            String text=response.getString("data");
                            fileContents.setText(text);
                            JSONArray topWordsList=response.getJSONArray("entities");
                            for(int i=0;i<topWordsList.length();i++)
                                topWords.add(topWordsList.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getTopWords.setVisibility(View.VISIBLE);
                        mDialog.dismiss();
                        // jasoDisplayTextView.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                Log.d("response", error.toString());
                //jasoDisplayTextView.setText("That didn't work!" + error.toString());
            }
        });
        dummyRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.MINUTES.toMillis(5),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(dummyRequest);

    }

}
