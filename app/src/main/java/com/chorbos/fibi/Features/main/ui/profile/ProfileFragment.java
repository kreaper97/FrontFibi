package com.chorbos.fibi.Features.main.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.chorbos.fibi.Features.assignmentsFeed.UserAssignmentsDialogFragment;
import com.chorbos.fibi.Features.login.LoginActivity;
import com.chorbos.fibi.Features.main.DashBoardActivity;
import com.chorbos.fibi.Features.main.TrainingAdapter;
import com.chorbos.fibi.Features.main.ui.profile.ProfileViewModel;
import com.chorbos.fibi.Models.Assignment;
import com.chorbos.fibi.Models.Training;
import com.chorbos.fibi.Models.User;
import com.chorbos.fibi.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements TrainingAdapter.OnListActionInteractionListener {
    /*@Nullable
    @BindView(R.id.statusTypeIL)
    TextInputLayout statusTypeIL;

    @Nullable
    @BindView(R.id.statusTypeET)
    TextInputEditText statusTypeET;

    @BindView(R.id.emptyView)
    TextView emptyView;

    @BindView(R.id.datalist)
    RecyclerView datalist;

    @BindView(R.id.rootView)
    CoordinatorLayout rootView;*/
    TextView emptyView;
    private Realm realm;
    Button signout;
    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        //profileViewModel =
               // ViewModelProviders.of(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        signout = root.findViewById(R.id.signout);
        emptyView = root.findViewById(R.id.emptyView);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                realm.where(User.class).findFirst().setKoins(100);
                realm.commitTransaction();
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = realm.where(User.class).findFirst();
                if(user != null){
                    realm.beginTransaction();
                    user.deleteFromRealm();
                    realm.commitTransaction();
                }
                startActivity(new Intent(getContext(),LoginActivity.class));
                getActivity().finish();
            }
        });
        return root;
    }

    private void showAssignmentsTypes() {
        DialogFragment dialog = UserAssignmentsDialogFragment.newInstance();

        dialog.show(getFragmentManager(), "UserAssignmentsDialogFragment");
    }

    @Override
    public void onListActionInteraction(String id) {

    }

    @Override
    public void onApplySuccess() {

    }

    @Override
    public void onApplyError(int errorCode, String error) {
        Snackbar.make(root,error,Snackbar.LENGTH_LONG).show();
    }

}