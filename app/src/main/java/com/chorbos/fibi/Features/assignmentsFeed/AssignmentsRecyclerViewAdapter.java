package com.chorbos.fibi.Features.assignmentsFeed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chorbos.fibi.Models.Assignment;
import com.chorbos.fibi.R;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;


public class AssignmentsRecyclerViewAdapter extends RealmRecyclerViewAdapter<Assignment, AssignmentsRecyclerViewAdapter.ViewHolder> {


    public interface OnListAssignmentInteractionListener {
        void onListAssignmentInteraction(String ass_name);
    }

    private final OnListAssignmentInteractionListener listener;

    AssignmentsRecyclerViewAdapter(OnListAssignmentInteractionListener listener,
                               RealmResults<Assignment> realmResults,
                               boolean automaticUpdate) {
        super(realmResults, automaticUpdate);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_assignments, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        if (getData() != null) {
            final Assignment training = getData().get(position);
            viewHolder.projectCTV.setText(training.getName());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;
        TextView projectCTV;

        ViewHolder(@NonNull View container) {
            super(container);
            this.view = container;
            this.projectCTV = container.findViewById(R.id.checkboxText);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Assignment assignment;
            if (getData() != null) {
                assignment = getData().get(adapterPosition);
                listener.onListAssignmentInteraction(assignment.getName());
            }

        }
    }

}
