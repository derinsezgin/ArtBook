package com.derinsezgin.artbook.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.derinsezgin.artbook.R;
import com.derinsezgin.artbook.data.Books;
import com.derinsezgin.artbook.data.DatabaseHelper;
import com.derinsezgin.artbook.ui.activity.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class AddBookFragment extends Fragment {

    private ProgressDialog progressDialog;
    private EditText edtTitle, edtWriter, edtYear;
    private RelativeLayout viewImage;
    private LinearLayout container;
    private ImageView addIcon, galleryImage;
    private AppCompatButton btnSave;

    private Uri uriIM = null;
    private String imageUri = "";
    private String emptyPath = "deviceID";
    private Bitmap bitmap = null;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private static final int PICK_PHOTO_REQUEST_CODE = 101;
    private static final int PHOTO_PERMISSIONS_REQUEST_CODE = 103;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        container = view.findViewById(R.id.viewContainer);
        edtTitle = view.findViewById(R.id.edtTitle);
        edtWriter = view.findViewById(R.id.edtWriter);
        edtYear = view.findViewById(R.id.edtYear);

        addIcon = view.findViewById(R.id.imgAddIcon);
        galleryImage = view.findViewById(R.id.imgGallery);

        viewImage = view.findViewById(R.id.viewImage);
        viewImage.setOnClickListener(v -> {
            if (checkPermissions()) {
                openGallery();
            }
        });

        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            if (validation()) {

                progressDialog = new ProgressDialog(requireActivity());
                progressDialog.setTitle(getString(R.string.label_upload_loading));
                progressDialog.show();

                hideKeyboardFrom(btnSave);
                uploadImage(uriIM);
            }
        });

        checkUser();

    }

    private void checkUser() {
        emptyPath = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emptyPath = user.getUid();
        }
    }

    private void saveContent() {

        Books model = new Books(
                edtTitle.getText().toString(),
                edtWriter.getText().toString(),
                edtYear.getText().toString(),
                imageUri);

        mDatabase = FirebaseDatabase.getInstance().getReference(emptyPath + "/Kitaplar");
        mDatabase.push().setValue(model);

        DatabaseHelper db = new DatabaseHelper(getActivity());
        db.addData(model);
        db.close();

        progressDialog.dismiss();
        Snackbar snackbar = Snackbar.make(container, getString(R.string.label_add_book_success), Snackbar.LENGTH_LONG);
        snackbar.show();

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }, 2000);

    }

    private boolean validation() {

        if (edtTitle.getText().toString().isEmpty()) {
            edtTitle.setError(getString(R.string.label_valitation));
            return false;
        }

        if (edtWriter.getText().toString().isEmpty()) {
            edtWriter.setError(getString(R.string.label_valitation));
            return false;
        }

        if (edtYear.getText().toString().isEmpty()) {
            edtYear.setError(getString(R.string.label_valitation));
            return false;
        }

        return true;
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST_CODE);
    }

    public boolean checkPermissions() {
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO_PERMISSIONS_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    private void getSelectedPhoto(Uri uri) {

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            galleryImage.setImageBitmap(bitmap);
            addIcon.setVisibility(View.GONE);
            uriIM = uri;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void uploadImage(Uri uri) {

        if (uri != null) {
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            StorageReference fileReference = storageReference.child("images/" + UUID.randomUUID().toString());

            fileReference.putFile(uri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUri = downloadUri.toString();
                }
                saveContent();

            });
        } else {
            saveContent();
        }
    }

    private void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    getSelectedPhoto(data.getData());
                }
            }
        }
    }
}