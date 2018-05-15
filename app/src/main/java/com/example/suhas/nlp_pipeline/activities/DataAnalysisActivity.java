package com.example.suhas.nlp_pipeline.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.suhas.nlp_pipeline.R;
import com.example.suhas.nlp_pipeline.adapters.EachClusterAdapter;
import com.example.suhas.nlp_pipeline.data.ClusterInfo;
import com.example.suhas.nlp_pipeline.data.preference.SharedPreference;
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
import de.hdodenhof.circleimageview.CircleImageView;


public class DataAnalysisActivity extends AppCompatActivity {
    @BindView(R.id.clusterRecylerView)
    RecyclerView clusterRecylerView;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    private String url;
    private SimpleArcDialog mDialog;
    private List<ClusterInfo> listOfClusters;
    private RecyclerView.LayoutManager layoutManager;
    private EachClusterAdapter eachClusterAdapter;
    private Context mContext;
    private String statsToBeSet;
    private String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analysis_activity);
        ButterKnife.bind(this);
        type=getIntent().getStringExtra("type");
        url = SharedPreference.get(this, SharedPreference.URL, "") + "/api/demo/";
        mDialog = new SimpleArcDialog(this);
        mDialog.setConfiguration(new ArcConfiguration(this));
        listOfClusters = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        clusterRecylerView.setLayoutManager(layoutManager);
        mContext = this;
        profileImage.setOnClickListener(view->
        {
            new MaterialDialog.Builder(this)
                    .title("Stats")
                    .content(statsToBeSet)
                    .show();
        });
        try {
            makeJsonRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeJsonRequest() throws JSONException {
        mDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("file",type);
        JsonObjectRequest dummyRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dictionary = response.getJSONObject("content").getJSONArray("dict");
                            for (int i = 0; i < dictionary.length(); i++) {
                                ClusterInfo eachCluterData = new ClusterInfo();
                                JSONObject eachCluster = (JSONObject) dictionary.get(i);
                                JSONArray filesList = (JSONArray) eachCluster.get("files");
                                JSONArray wordsList = (JSONArray) eachCluster.get("summary");
                                List<String> eachClusterFilesList = new ArrayList<>();
                                List<String> eachClusterSummaryList = new ArrayList<>();
                                String[] eachClusterFileName = new String[filesList.length()];
                                int[] indexes = new int[filesList.length()];
                                for (int j = 0; j < filesList.length(); j++) {
                                    eachClusterFilesList.add(filesList.getString(j));
                                    eachClusterFileName[j] = filesList.getString(j).substring(filesList.getString(j).lastIndexOf('/') + 1);
                                    indexes[j] = j;
                                    Log.d("words", "j=>" + filesList.getString(j));
                                }
                                for (int k = 0; k < wordsList.length(); k++) {
                                    eachClusterSummaryList.add(wordsList.getString(k));
                                    Log.d("words", "k=>" + wordsList.getString(k));
                                }
                                eachCluterData.setFileNames(eachClusterFileName);
                                eachCluterData.setFilePaths(eachClusterFilesList);
                                eachCluterData.setSummary(eachClusterSummaryList);
                                eachCluterData.setIndexes(indexes);
                                listOfClusters.add(eachCluterData);
                            }
                            JSONObject stats=response.getJSONObject("content").getJSONObject("stats");
                            StringBuilder statsToBeSetBuilder=new StringBuilder();
                            statsToBeSetBuilder.append("Cluster Count : ").append(stats.getString("clusterCount")).append("\nTotal number of files : ")
                                    .append(stats.getString("fileCount")).append("\nRand-Score : ").append(stats.getString("randIndex"))
                            .append("\nPrecision : ") .append(stats.getString("precision")).append("\nRecall : ").append(stats.getString("recall"))
                                    .append("\nF1-Score : ").append(stats.getString("f1"));
                            statsToBeSet=statsToBeSetBuilder.toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        eachClusterAdapter = new EachClusterAdapter(listOfClusters, mContext);
                        clusterRecylerView.setAdapter(eachClusterAdapter);
                        profileImage.setVisibility(View.VISIBLE);
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
