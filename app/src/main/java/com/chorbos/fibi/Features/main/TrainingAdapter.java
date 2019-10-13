package com.chorbos.fibi.Features.main;

        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.os.Build;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.chorbos.fibi.Features.login.LoginView;
        import com.chorbos.fibi.Models.Training;
        import com.chorbos.fibi.Models.User;
        import com.chorbos.fibi.R;
        import com.chorbos.fibi.Rest.ApiService;
        import com.chorbos.fibi.Rest.ServiceGenerator;
        import com.google.android.material.snackbar.Snackbar;
        import com.google.gson.JsonObject;
        import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
        import com.squareup.picasso.Picasso;

        import java.io.IOException;
        import java.util.ArrayList;

        import io.realm.OrderedRealmCollection;
        import io.realm.Realm;
        import io.realm.RealmRecyclerViewAdapter;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;


public class TrainingAdapter extends RealmRecyclerViewAdapter<Training, RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Call<Training> callApplyAssignment;
    private OnListActionInteractionListener mListener;
    private static final String TAG = "TrainingAdapter";
    ArrayList<Training> trainingList = new ArrayList<>(5);
    private Context context;

    public interface OnListActionInteractionListener {
        void onListActionInteraction(String id);
        void onApplySuccess();
        void onApplyError(int errorCode, String response);
    }

    // Pass in the contact array into the constructor
    public TrainingAdapter(Context context,
                   OrderedRealmCollection<Training> data,
                   OnListActionInteractionListener onListActionInteractionListener) {

        super(data, true);
        this.mListener = onListActionInteractionListener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listi_item_training_header, parent, false);
            return new ViewHolderHeader(itemView);
        } else if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_trainings, parent, false);
            return new ViewHolderItem(itemView);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderItem) {
            if (getData() != null) {
                ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
                final Training obj = getData().get(position - 1);
                viewHolderItem.TVDate.setText(obj.getCreated_date());
                viewHolderItem.data = obj;
                viewHolderItem.TVProfId.setText(obj.getCreatorId());
                viewHolderItem.TVKoins.setText(obj.getKoins_price() + "");
                viewHolderItem.TVAssignment.setText(obj.getAssignment());
                viewHolderItem.TVClass.setText(obj.getAssignment_class());
                viewHolderItem.BApply.setOnClickListener(view -> {

                    ApiService apiService = ServiceGenerator.createService(ApiService.class);

                    JsonObject req = new JsonObject();
                    Realm realm = Realm.getDefaultInstance();
                    User user = realm.where(User.class).findFirst();
                    if(user.getKoins() >= obj.getKoins_price()) {
                        String studentId = user != null ? user.getUserId() : "0";

                        req.addProperty("trainId", obj.getTrainId());
                        req.addProperty("professorId", obj.getCreatorId());
                        req.addProperty("studentId", studentId);
                        Log.d(TAG, "onBindViewHolder: " + req);
                        callApplyAssignment = apiService.applyAssignment(String.valueOf(req));
                        callApplyAssignment.enqueue(new Callback<Training>() {
                            @Override
                            public void onResponse(@NonNull Call<Training> call, @NonNull final Response<Training> response) {
                                if (response.isSuccessful()) {
                                    final Training training = response.body();
                                    if (training != null) {
                                        Realm realm = null;
                                        try {
                                            realm = Realm.getDefaultInstance();
                                            realm.executeTransactionAsync(realm1 -> realm1.copyToRealmOrUpdate(training), () -> mListener.onApplySuccess());
                                        } catch (Exception e) {
                                            mListener.onApplyError(500, response.message());
                                        } finally {
                                            if (realm != null) {
                                                realm.close();
                                            }
                                        }
                                    } else {
                                        mListener.onApplyError(500, response.message());
                                    }
                                } else {
                                    mListener.onApplyError(response.code(), response.message());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Training> call, @NonNull Throwable t) {
                                mListener.onApplyError(400, null);
                            }
                        });
                    }else{
                        mListener.onApplyError(600,"No Koins avaliable");
                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        if (getData() != null) {
            return getData().size() + 1;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        public Training data;
        View view;
        TextView TVDate;
        TextView TVClass;
        TextView TVProfId;
        TextView TVKoins;
        TextView TVAssignment;
        Button   BApply;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolderItem(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            TVDate = itemView.findViewById(R.id.rowDate); //created date
            TVClass = itemView.findViewById(R.id.aula); //request type
            TVAssignment = itemView.findViewById(R.id.assignment); //comment
            BApply = itemView.findViewById(R.id.apply); // status
            TVKoins = itemView.findViewById(R.id.koins);
            TVProfId = itemView.findViewById(R.id.professorid);
            this.view = itemView;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onListActionInteraction(data.getTrainId());
        }
    }

    static class ViewHolderHeader extends RecyclerView.ViewHolder {
        View view;

        ViewHolderHeader(View view) {
            super(view);
            this.view = view;
        }
    }


}
