package com.example.suhas.nlp_pipeline.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.suhas.nlp_pipeline.R;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.firstFileSelector)
    Button firstFileSelector;
    @BindView(R.id.secondFileSelector)
    Button secondFileSelector;
    @BindView(R.id.similarityPost)
    Button similarityPost;
    @BindView(R.id.similarityScore)
    TextView similarityScore;
   private SimpleArcDialog mDialog;
    private String url;
    private int FIRST_FILE_CODE = 1;
    private int SECOND_FILE_CODE = 2;
    private String firstFilePath = "";
    private String secondFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        url = "http://192.168.0.104:8000/api/file/";
        mDialog = new SimpleArcDialog(this);
        mDialog.setConfiguration(new ArcConfiguration(this));


    }

    @OnClick({R.id.firstFileSelector, R.id.secondFileSelector, R.id.similarityPost})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.firstFileSelector:
                new MaterialFilePicker()
                        .withActivity(this)
                        .withRequestCode(FIRST_FILE_CODE)
                        .withFilter(Pattern.compile(".*\\.txt$")) // Filtering files and directories by file name using regexp
                        .withFilterDirectories(true) // Set directories filterable (false by default)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();

                break;
            case R.id.secondFileSelector:
                new MaterialFilePicker()
                        .withActivity(this)
                        .withRequestCode(SECOND_FILE_CODE)
                        .withFilter(Pattern.compile(".*\\.txt$")) // Filtering files and directories by file name using regexp
                        .withFilterDirectories(true) // Set directories filterable (false by default)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
                break;
            case R.id.similarityPost:
                getSimilarity();
        }
    }

    private void getSimilarity() {

        if (!TextUtils.isEmpty(firstFilePath) && !TextUtils.isEmpty(secondFilePath)) {
            mDialog.show();
            File firstFile = new File(firstFilePath);
            File secondFile = new File(secondFilePath);
            StringBuilder firstFileContents = new StringBuilder();
            StringBuilder secondFileContents = new StringBuilder();
            try {
                BufferedReader br1 = new BufferedReader(new FileReader(firstFile));
                BufferedReader br2 = new BufferedReader(new FileReader(secondFile));
                String line;

                while ((line = br1.readLine()) != null) {
                    firstFileContents.append(line);
                    firstFileContents.append('\n');
                }
                br1.close();
                while ((line = br2.readLine()) != null) {
                    secondFileContents.append(line);
                    secondFileContents.append('\n');
                }
                br2.close();
                makeJsonRequest(firstFileContents.toString(), secondFileContents.toString());
                Log.d("contents", "First=>" + firstFileContents.toString() + "\nSecond=>" + secondFileContents.toString());
            } catch (IOException e) {
                Log.d("exception", e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FIRST_FILE_CODE && resultCode == RESULT_OK) {
            firstFilePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            firstFileSelector.setText(firstFilePath.substring(firstFilePath.lastIndexOf('/') + 1));
        } else if (requestCode == SECOND_FILE_CODE && resultCode == RESULT_OK) {
            secondFilePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            secondFileSelector.setText(secondFilePath.substring(secondFilePath.lastIndexOf('/') + 1));
        }
    }

    public void makeJsonRequest(String file1Contents, String file2Contents) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("file1", file1Contents);
        jsonBody.put("file2", file2Contents);
        Log.d("contents", jsonBody.toString());
        StringBuilder encodedUrl = new StringBuilder(url).append(" ? filePath=new");
        JsonObjectRequest dummyRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        mDialog.dismiss();
                        // jasoDisplayTextView.setText(response.toString());
                    }
                },new Response.ErrorListener() {
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


//    public void makeStringGetRequest() {
//        progressDialog.show();
//        RequestQueue queue = Volley.newRequestQueue(this);
//        StringBuilder encodedUrl = new StringBuilder(url).append(" ? filePath=new");
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, encodedUrl.toString(),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        progressDialog.dismiss();
//                        jasoDisplayTextView.setText(response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
//                jasoDisplayTextView.setText("That didn't work!" + error.toString());
//            }
//        });
//
//        queue.add(stringRequest);
//    }


//    public void makeStringPostRequest(String file1Contents,String file2Contents) {
//        try {
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("file1",file1Contents);
//            jsonBody.put("file2",file2Contents);
//            final String requestBody = jsonBody.toString();
//
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d("VOLLEY", response);
//                    jasoDisplayTextView.setText(response);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("VOLLEY", error.toString());
//                    jasoDisplayTextView.setText(error.toString());
//                }
//            }) {
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    try {
//                        return requestBody == null ? null : requestBody.getBytes("utf-8");
//                    } catch (UnsupportedEncodingException uee) {
//                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                        return null;
//                    }
//                }
//
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response.statusCode);
//                        Log.d("VOLLEY", "Status=>" + responseString);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
//            };
//
//            requestQueue.add(stringRequest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


}
