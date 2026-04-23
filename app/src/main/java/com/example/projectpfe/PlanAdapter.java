package com.example.projectpfe;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projectpfe.model.PlanResponse;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    List<PlanResponse> plans;

    public PlanAdapter(List<PlanResponse> plans) {
        this.plans = plans;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, step1, step2, step3, progressText;
        ImageView p1, p2, p3;
        CircularProgressView progress;

        public ViewHolder(View v) {
            super(v);

            title = v.findViewById(R.id.title);

            step1 = v.findViewById(R.id.step1);
            step2 = v.findViewById(R.id.step2);
            step3 = v.findViewById(R.id.step3);

            p1 = v.findViewById(R.id.point1);
            p2 = v.findViewById(R.id.point2);
            p3 = v.findViewById(R.id.point3);

            progress = v.findViewById(R.id.progress);
            progressText = v.findViewById(R.id.progressText);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {

        PlanResponse plan = plans.get(i);

        h.title.setText(plan.title);

        // steps
        h.step1.setText(plan.steps.get(0));
        h.step2.setText(plan.steps.get(1));
        h.step3.setText(plan.steps.get(2));

        // status
        applyStep(h.step1, h.p1, plan.doneSteps.get(0));
        applyStep(h.step2, h.p2, plan.doneSteps.get(1));
        applyStep(h.step3, h.p3, plan.doneSteps.get(2));

        // progress
        h.progress.setProgress(plan.progress);
        h.progressText.setText(plan.progress + "%");
    }

    private void applyStep(TextView text, ImageView dot, boolean done) {

        if (done) {
            text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            text.setTextColor(Color.parseColor("#444455"));
            dot.setImageResource(R.drawable.circle_selected);
        } else {
            text.setPaintFlags(0);
            text.setTextColor(Color.parseColor("#AAAACC"));
            dot.setImageResource(R.drawable.ic_target);
        }
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }
}