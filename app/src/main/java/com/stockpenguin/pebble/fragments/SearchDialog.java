package com.stockpenguin.pebble.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stockpenguin.pebble.R;
import com.stockpenguin.pebble.adapters.SearchUserAdapter;
import com.stockpenguin.pebble.interfaces.PebbleListListener;
import com.stockpenguin.pebble.utils.SearchDialogDataHolder;

import java.util.Map;

public class SearchDialog extends BottomSheetDialogFragment {

    private ConstraintLayout constraintLayout;
    private ConstraintLayout bottomSheetLayout;
    private EditText searchBar;
    private RecyclerView recyclerView;
    private SearchUserAdapter adapter;

    private DatabaseReference usersRef;

    private Map<String, Map<String, String>> users;

    private SearchDialogDataHolder dataHolder;

    @Override
    public void onStart() {
        super.onStart();
        usersRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) { return; }
            DataSnapshot snapshot = task.getResult();
            users = (Map<String, Map<String, String>>) snapshot.getValue();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* inflate layout */
        constraintLayout = (ConstraintLayout)
                inflater.inflate(R.layout.layout_search_bottom_sheet, container, false);
        searchBar = constraintLayout.findViewById(R.id.searchBottomSheetSearchBar);
        recyclerView = constraintLayout.findViewById(R.id.searchBottomSheetRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        dataHolder = new SearchDialogDataHolder();
        adapter = new SearchUserAdapter(dataHolder);

        System.out.println(dataHolder.getPhotoUrls());
        dataHolder.getPhotoUrls().addOnListChangedListener(new PebbleListListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onListChanged() {
                adapter.notifyDataSetChanged();
            }
        });

        dataHolder.getUsernames().addOnListChangedListener(new PebbleListListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onListChanged() {
                adapter.notifyDataSetChanged();
            }
        });

        dataHolder.getUids().addOnListChangedListener(new PebbleListListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onListChanged() {
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(adapter);

        /* init the users database reference */
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        /* query the data from the users database */
        usersRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) { return; }
            DataSnapshot snapshot = task.getResult();
            users = (Map<String, Map<String, String>>) snapshot.getValue();
        });

        /*
        whenever a new letter is typed or removed, we want to update the list
         */
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataHolder.clearLists();

                if (users == null
                || charSequence.length() == 0) return;

                users.forEach((uid, userMap) -> {
                    String displayName = userMap.get("displayName");
                    String photoUrl = userMap.get("photoUrl");
                    System.out.println(userMap);

                    if (displayName == null || photoUrl == null) return;
                    if (displayName.contains(charSequence)) {
                        dataHolder.addPhotoUrl(photoUrl);
                        dataHolder.addUsername(displayName);
                        dataHolder.addUid(uid);
                        System.out.println(dataHolder.getPhotoUrls().toString());
                    } else {
//                        dataHolder.clearLists();
//                        dataHolder.addPhotoUrl("https://firebasestorage.googleapis.com/v0/b/pebble-c46e0.appspot.com/o/pfp%2Fdefault_pfp.png?alt=media&token=451537a2-99a4-4ea0-921f-fe9ab5747394");
//                        dataHolder.addUsername("User does not exist");
//                        System.out.println("user does not exist");
//                        Toast.makeText(getActivity().getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        /* set the height to 75% of the screen height */
        constraintLayout.setMinHeight(
                (int) (getResources().getDisplayMetrics().heightPixels)
        );

        return constraintLayout;
    }
}
