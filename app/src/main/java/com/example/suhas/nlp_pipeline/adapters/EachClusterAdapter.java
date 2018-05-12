package com.example.suhas.nlp_pipeline.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.suhas.nlp_pipeline.R;
import com.example.suhas.nlp_pipeline.activities.EachFileDetails;
import com.example.suhas.nlp_pipeline.data.ClusterInfo;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EachClusterAdapter extends RecyclerView.Adapter<EachClusterAdapter.ClusterHolder> {

    private List<ClusterInfo> clusterList;
    private Context mContext;


    public EachClusterAdapter(List<ClusterInfo> clusterList, Context context) {
        this.clusterList = clusterList;
        mContext = context;

    }

    @Override
    public ClusterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_cluster_layout, parent, false);
        return new ClusterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClusterHolder holder, int position)
    {
        if(holder.summaryRecylerView.getAdapter()==null) {
            EachClusterSummaryAdapter summaryAdapter = new EachClusterSummaryAdapter(clusterList.get(position).getSummary(),mContext);
            holder.summaryRecylerView.setAdapter(summaryAdapter);
        }

    }

    @Override
    public int getItemCount() {
        return clusterList.size();
    }

    public class ClusterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.summaryRecylerView)
        RecyclerView summaryRecylerView;
        @BindView(R.id.viewFileList)
        Button viewFileList;
        public ClusterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(mContext);
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setAlignItems(AlignItems.STRETCH);
            summaryRecylerView.setLayoutManager(layoutManager);
            viewFileList.setOnClickListener(view1->
            {
                int position=getAdapterPosition();
                new MaterialDialog.Builder(mContext)
                        .title("Choose a file")
                        .items(clusterList.get(position).getFileNames())
                        .itemsIds(clusterList.get(position).getIndexes())
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                            {
                                Log.d("dialog","name=>"+text.toString()+"path=>"+clusterList.get(position).getFilePaths().get(which));
                                Intent eachFileActivity=new Intent(mContext, EachFileDetails.class);
                                eachFileActivity.putExtra("path",clusterList.get(position).getFilePaths().get(which));
                                mContext.startActivity(eachFileActivity);
                            }
                        })
                        .show();
            });


        }
    }
}
