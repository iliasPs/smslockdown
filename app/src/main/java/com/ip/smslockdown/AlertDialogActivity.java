package com.ip.smslockdown;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.libraries.places.api.model.Place;
import com.ip.smslockdown.databinding.ActivityAlertDialogBinding;
import com.ip.smslockdown.models.User;
import com.ip.smslockdown.viewmodel.UserViewModel;

import java.util.Objects;

public class AlertDialogActivity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener {

    private static final String TAG = "AlertDialogActivity";
    private ActivityAlertDialogBinding binding;
    private EditText userNameEdit;
    private EditText userAddressEdit;
    private Button saveUserButton;
    private Button cancelButton;
    private RecyclerView recyclerViewPlaces;
    private PlacesAutoCompleteAdapter adapter;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlertDialogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        this.setFinishOnTouchOutside(false);
        initViews(binding);

        userViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(UserViewModel.class);
        userAddressEdit.addTextChangedListener(filterTextWatcher);
        adapter = new PlacesAutoCompleteAdapter(getApplicationContext());
        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        RecyclerView.ItemAnimator animator = recyclerViewPlaces.getItemAnimator();
        if(animator instanceof SimpleItemAnimator){
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        adapter.setClickListener(AlertDialogActivity.this);
        recyclerViewPlaces.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // save user to db and fix ui
        final User[] currentUser = new User[1];
        saveUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userNameEdit.getText().toString().isEmpty() || userAddressEdit.getText().toString().isEmpty()) {
                    Toast.makeText(AlertDialogActivity.this, R.string.error_saving_user, Toast.LENGTH_SHORT).show();
                } else {
                    currentUser[0] = User.builder()
                            .fullName(userNameEdit.getText().toString())
                            .address(userAddressEdit.getText().toString())
                            .build();

                    Log.d(TAG, "onCreate: Adding user to db: " + currentUser[0].getFullName() + " " + currentUser[0].getAddress() + " " + currentUser[0].getUid());
                    userViewModel.insert(currentUser[0].withLastUsed(true));
                    finish();
                }
            }
        });

        // just return
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initViews(ActivityAlertDialogBinding binding) {
        userNameEdit = binding.userNameEt;
        userAddressEdit = binding.userAddressEt;
        saveUserButton = binding.saveButton;
        cancelButton = binding.cancelButton;
        recyclerViewPlaces = binding.placesRecyclerView;
    }

    @Override
    public void click(Place place) {
        //TODO here i need to set the geofence
        String address = Objects.requireNonNull(place.getAddress()).substring(0, place.getAddress().indexOf(","));
        userAddressEdit.setText(address);
        recyclerViewPlaces.setVisibility(View.GONE);

    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                adapter.getFilter().filter(s.toString());
                if (recyclerViewPlaces.getVisibility() == View.GONE) {
                    recyclerViewPlaces.setVisibility(View.VISIBLE);
                }
            } else {
                if (recyclerViewPlaces.getVisibility() == View.VISIBLE) {
                    recyclerViewPlaces.setVisibility(View.GONE);
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

}