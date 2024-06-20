package com.example.pm0120242p;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ActivityPhoto extends AppCompatActivity {
    static final int peticion_acceso_camara = 101;
    static final int peticion_captura_imagen = 102;
    ImageView ObjetoImagen;
    Button btncaptura;
    String PathImagen;
    String currentPhotoPath, image64;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ObjetoImagen = (ImageView) findViewById(R.id.imageView);
        btncaptura = (Button) findViewById(R.id.btntakefoto);

        btncaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permisos();
            }
        });
    }

    private void Permisos() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},peticion_acceso_camara);
        }
        else{
           //TomarFoto();
           dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode== peticion_acceso_camara){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //TomarFoto();
                dispatchTakePictureIntent();
            }
            else {
                Toast.makeText(getApplicationContext(),"Acceso Denegado", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void TomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,peticion_captura_imagen);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==peticion_captura_imagen && resultCode == RESULT_OK){
        //    if(data != null) {
        //        Bundle extras = data.getExtras();
        //        if(extras != null) {
        //            Bitmap imagen = (Bitmap) extras.get("data");
        //            ObjetoImagen.setImageBitmap(imagen);
        //            image64 = ConvertImageBase64(imagen); //Para guardar en blob en base de datos

        //            Log.i("Image",image64);
        //        }
        //    }
            setPic();
            galleryAddPic();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        PathImagen = currentPhotoPath; // Set the PathImagen variable
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.pmo120232p.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //currentPhotoPath = photoFile.getAbsolutePath(); // Set the currentPhotoPath variable
                startActivityForResult(takePictureIntent, peticion_captura_imagen);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = ObjetoImagen.getWidth();
        int targetH = ObjetoImagen.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        ObjetoImagen.setImageBitmap(bitmap);
    }

    private String ConvertImageBase64(Bitmap bitmap){
        ByteArrayOutputStream byteImage = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteImage);

        byte[] byteArray = byteImage.toByteArray();

        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }

}