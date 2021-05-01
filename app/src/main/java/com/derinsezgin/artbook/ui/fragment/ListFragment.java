package com.derinsezgin.artbook.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.derinsezgin.artbook.R;
import com.derinsezgin.artbook.data.Books;
import com.derinsezgin.artbook.data.DatabaseHelper;
import com.derinsezgin.artbook.ui.activity.BookDetailActivity;
import com.derinsezgin.artbook.ui.adaper.BooksAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty, tvSellect;
    private FirebaseAuth mAuth;
    private String emptyPath= "deviceID";
    private List<Books> booksList = new ArrayList<>();
    private final DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_list_book, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvSellect = view.findViewById(R.id.tvSellect);
        tvSellect.setOnClickListener(v -> popupMenu());

        checkUser();
        popupMenu();
    }

    private void checkUser(){
        emptyPath = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emptyPath = user.getUid();
        }
    }

    private void popupMenu(){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_questions);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        AppCompatButton btnLocal =  dialog.findViewById(R.id.btnLocal);
        AppCompatButton btnService = dialog.findViewById(R.id.btnService);

        btnLocal.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(getActivity());
            booksList = new ArrayList<>();
            booksList = db.showList();

            if(booksList.isEmpty()){
                tvEmpty.setVisibility(View.VISIBLE);
            }
            else{
                tvEmpty.setVisibility(View.GONE);
            }

            suitUp();
            dialog.dismiss();
        });

        btnService.setOnClickListener(v -> {
            loadData();
            dialog.dismiss();
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void loadData(){

        DatabaseReference vData = mRoot.child(emptyPath+"/Kitaplar");
        vData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                try {
                    booksList = new ArrayList<>();
                    booksList.clear();
                    Books model = dataSnapshot.getValue(Books.class);
                    model.setId(dataSnapshot.getKey());
                    booksList.add(model);

                    if(dataSnapshot.getValue()==null){
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    suitUp();

                }
                catch (Exception e){
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void suitUp(){
        recyclerView.setAdapter(new BooksAdapter(getActivity(), booksList, (books) -> {
            startActivity(new Intent(getActivity(), BookDetailActivity.class)
                    .putExtra("booksID",books.getId())
                    .putExtra("booksTitle",books.getTitle())
                    .putExtra("booksWriter",books.getWriterName())
                    .putExtra("booksYear",books.getYear())
                    .putExtra("booksImage",books.getImage()));
        }));
    }
}