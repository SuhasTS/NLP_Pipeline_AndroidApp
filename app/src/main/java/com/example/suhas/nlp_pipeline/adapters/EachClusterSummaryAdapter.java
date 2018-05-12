package com.example.suhas.nlp_pipeline.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.suhas.nlp_pipeline.R;
import com.robertlevonyan.views.chip.Chip;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EachClusterSummaryAdapter extends RecyclerView.Adapter<EachClusterSummaryAdapter.SummaryViewHolder> {

    private List<String> summaryList;
    private TypedArray ta;
    private int[] colors;
    private Random rand;

    public EachClusterSummaryAdapter(List<String> summaryList, Context mContext) {
        this.summaryList = summaryList;
         ta = mContext.getResources().obtainTypedArray(R.array.textColors);
        colors= new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        rand = new Random();
    }

    @Override
    public SummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_summary_layout, parent, false);
        return new SummaryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SummaryViewHolder holder, int position) {
        holder.summaryEachTV.setChipText(summaryList.get(position));
        holder.summaryEachTV.changeBackgroundColor(colors[rand.nextInt(colors.length)]);
    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    public class SummaryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.summaryEachTV)
        Chip summaryEachTV;
        public SummaryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
