package com.example.suhas.nlp_pipeline.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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


public class DataAnalysisActivity extends AppCompatActivity {
    @BindView(R.id.clusterRecylerView)
    RecyclerView clusterRecylerView;
    private String url;
    private SimpleArcDialog mDialog;
    private List<ClusterInfo> listOfClusters;
    private RecyclerView.LayoutManager layoutManager;
    private EachClusterAdapter eachClusterAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analysis_activity);
        ButterKnife.bind(this);
        url = "http://192.168.0.104:8000/api/demo/";
        mDialog = new SimpleArcDialog(this);
        mDialog.setConfiguration(new ArcConfiguration(this));
        listOfClusters = new ArrayList<>();
        layoutManager=new LinearLayoutManager(this);
        clusterRecylerView.setLayoutManager(layoutManager);
        mContext=this;
        makeJsonRequest();
    }

    public void makeJsonRequest() {
        mDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringBuilder encodedUrl = new StringBuilder(url).append(" ? filePath=new");
        JsonObjectRequest dummyRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dictionary = response.getJSONObject("content").getJSONArray("dict");
                            for (int i = 0; i < dictionary.length(); i++)
                            {
                                ClusterInfo eachCluterData = new ClusterInfo();
                                JSONObject eachCluster = (JSONObject) dictionary.get(i);
                                JSONArray filesList = (JSONArray) eachCluster.get("files");
                                JSONArray wordsList = (JSONArray) eachCluster.get("summary");
                                List<String> eachClusterFilesList = new ArrayList<>();
                                List<String> eachClusterSummaryList = new ArrayList<>();
                                String[] eachClusterFileName = new String[filesList.length()];
                                int[] indexes =new int[filesList.length()];
                                for (int j = 0; j < filesList.length(); j++) {
                                    eachClusterFilesList.add(filesList.getString(j));
                                    eachClusterFileName[j] = filesList.getString(j).substring(filesList.getString(j).lastIndexOf('/')+1);
                                    indexes[j]=j;
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        eachClusterAdapter=new EachClusterAdapter(listOfClusters,mContext);
                        clusterRecylerView.setAdapter(eachClusterAdapter);
                        Log.d("response", response.toString());
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
