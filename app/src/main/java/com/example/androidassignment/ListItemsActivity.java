package com.example.androidassignment;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;

public class ListItemsActivity extends AppCompatActivity {

    private static final String TAG = "ListItemsActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private ImageButton cameraButton;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_items);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cameraButton = findViewById(R.id.imageButton);
        cameraButton.setOnClickListener(v -> checkCameraPermission());

        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        // Switch
        Switch mySwitch = findViewById(R.id.mySwitch);
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CharSequence text = isChecked ? "Switch is On" : "Switch is Off";
            int duration = isChecked ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
            print(text.toString(), duration);
        });

        // CheckBox with dialog
        CheckBox myCheckBox = findViewById(R.id.myCheckBox);
        myCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListItemsActivity.this);
                builder.setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title)
                        .setPositiveButton(R.string.ok, (dialog, id) -> {
                            Intent resultIntent = new Intent();
                            String responseMessage = getString(R.string.response_message);
                            resultIntent.putExtra("Response", responseMessage);

                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        })
                        .setNegativeButton(R.string.cancel, (dialog, id) -> {
                            // Do nothing
                        })
                        .show();
            }
        });

        File imageFile = new File(getExternalFilesDir(null), "photo.jpg");
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            try {
                ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = rotateImage(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = rotateImage(bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = rotateImage(bitmap, 270);
                        break;
                }

                final Bitmap finalBitmap = bitmap;
                cameraButton.post(() -> {
                    int buttonWidth = cameraButton.getWidth();
                    int buttonHeight = cameraButton.getHeight();
                    if (buttonWidth > 0 && buttonHeight > 0) {
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(finalBitmap, buttonWidth, buttonHeight, true);
                        cameraButton.setImageBitmap(resizedBitmap);
                    } else {
                        cameraButton.setImageBitmap(finalBitmap);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void print(String message, int duration) {
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        File imageFile = new File(getExternalFilesDir(null), "photo.jpg");
        photoUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                imageFile
        );

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "net.sourceforge.opencamera",
                "net.sourceforge.opencamera.MainActivity"
        ));
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Log.e(TAG, "No camera app available");
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                File imageFile = new File(getExternalFilesDir(null), "photo.jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
                ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = rotateImage(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = rotateImage(bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = rotateImage(bitmap, 270);
                        break;
                }
                final Bitmap finalBitmap = bitmap;
                cameraButton.post(() -> {
                    int w = cameraButton.getWidth();
                    int h = cameraButton.getHeight();
                    Bitmap resized = Bitmap.createScaledBitmap(finalBitmap, w, h, true);
                    cameraButton.setImageBitmap(resized);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
