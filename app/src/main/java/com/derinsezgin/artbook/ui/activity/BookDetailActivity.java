package com.derinsezgin.artbook.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.derinsezgin.artbook.data.DatabaseHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Handler;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.ImageView;
import com.derinsezgin.artbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {

    private CoordinatorLayout container;
    private CollapsingToolbarLayout toolBarLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private AppCompatButton shareBtn;
    private EditText edtTitle, edtWriter, edtYear;
    private ImageView imageViewBanner;
    private String title, writer, year, image, id, emptyPath="deviceID";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        initView();
    }

    private void initView(){

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        checkUser();

        id = getIntent().getExtras().getString("booksID");
        title = getIntent().getExtras().getString("booksTitle");
        writer = getIntent().getExtras().getString("booksWriter");
        year = getIntent().getExtras().getString("booksYear");
        image = getIntent().getExtras().getString("booksImage");

        edtTitle = findViewById(R.id.edtTitle);
        edtTitle.setText(title);

        edtWriter = findViewById(R.id.edtWriter);
        edtWriter.setText(writer);

        edtYear = findViewById(R.id.edtYear);
        edtYear.setText(year);

        container = findViewById(R.id.viewContainer);
        toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(title);

        imageViewBanner = findViewById(R.id.imageHeader);
        imageViewBanner.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ImageDetailActivity.class);
            i.putExtra("sendImage",image);
            startActivity(i);
        });

        if(image.isEmpty()){
           imageViewBanner.setBackgroundResource(R.drawable.place_holder);
        }
        else{
            Picasso.get()
                    .load(image)
                    .resize(600,600)
                    .into(imageViewBanner);
        }

        fab =  findViewById(R.id.fab);
        fab.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(BookDetailActivity.this);
            builder.setTitle(title);
            builder.setMessage(getString(R.string.label_dialog_questions));
            builder.setNegativeButton(getString(R.string.label_dialog_no), null);
            builder.setPositiveButton((getString(R.string.label_dialog_yes)), (dialogInterface, i) -> {

                mDatabase = FirebaseDatabase.getInstance().getReference(emptyPath+"/Kitaplar/"+id);
                mDatabase.removeValue();

                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                db.deleteData(title);

                Snackbar snackbar = Snackbar.make(container, getString(R.string.label_delete_book_success), Snackbar.LENGTH_LONG);
                snackbar.show();

                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }, 2000);

            });
            builder.show();

        });

        shareBtn = findViewById(R.id.btnShare);
        shareBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + "\n" + title + "\n" + writer + "\n" + year);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });
    }

    private void checkUser(){
        emptyPath = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emptyPath = user.getUid();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}