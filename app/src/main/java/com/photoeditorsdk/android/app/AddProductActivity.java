package com.photoeditorsdk.android.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.activities.ImgLyIntent;
import ly.img.android.ui.utilities.PermissionRequest;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,PermissionRequest.Response {

    private EditText edit_name,edit_description,edit_price,edit_commission,edit_discount,edit_quantity;
    private Button btn_submit,btn_choose1,btn_choose2,btn_choose3;
    private ImageView imageView1,imageView2,imageView3;
    private Uri filePath;
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    private ProgressDialog progressDialog;
    private String str_name,str_description,str_price,str_commission,str_discount,str_quantity,uploadImage1,uploadImage2,uploadImage3;
    private Spinner category;
    ArrayAdapter adapter;
    String str_category;
    String check="0";
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private static final String FOLDER = "eshopfair";
    public static int CAMERA_PREVIEW_RESULT = 1;
    File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccentLight)));


        edit_name=(EditText)findViewById(R.id.edit_name);
        edit_description=(EditText)findViewById(R.id.edit_description);
        edit_price=(EditText)findViewById(R.id.edit_price);
        edit_commission=(EditText)findViewById(R.id.edit_commission);
        edit_discount=(EditText)findViewById(R.id.edit_discount);
        edit_quantity=(EditText)findViewById(R.id.edit_quantity);
        btn_submit=(Button)findViewById(R.id.btn_submit);
        btn_choose1=(Button)findViewById(R.id.btnChoose1);
        btn_choose2=(Button)findViewById(R.id.btnChoose2);
        btn_choose3=(Button)findViewById(R.id.btnChoose3);
        imageView1=(ImageView)findViewById(R.id.product_pic1);
        imageView2=(ImageView)findViewById(R.id.product_pic2);
        imageView3=(ImageView)findViewById(R.id.product_pic3);


        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Your Product Uploading...");



        category=(Spinner)findViewById(R.id.spinner);
        // Spinner click listener
        category.setOnItemSelectedListener(this);
        // Creating adapter for spinner
        adapter=ArrayAdapter.createFromResource(this,R.array.category,android.R.layout.simple_spinner_item);
        // Drop down layout style - list view with radio button
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        category.setAdapter(adapter);



        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadingProduct();
            }
        });

    }




    //spinner selection method for category
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        str_category = parent.getItemAtPosition(position).toString();
        // Showing selected spinner item
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }




    //picking image
    //start
    private void chooseImage() {

        SettingsList settingsList = new SettingsList();


        settingsList
                .getSettingsModel(CameraSettings.class)
                .setExportDir(Directory.DCIM, FOLDER)
                .setExportPrefix("camera_")

                .getSettingsModel(EditorSaveSettings.class)
                .setExportDir(Directory.DCIM, FOLDER)
                .setExportPrefix("result_")
                .setJpegQuality(80, false)
                .setSavePolicy(EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT);

        new CameraPreviewBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, CAMERA_PREVIEW_RESULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //for first picture
        if (check.equals("1")) {
            // handle result of CropImageActivity
            if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
                //now resize image
                //start
                String imagePath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
                Bitmap scaledBitmap = null;

                BitmapFactory.Options options = new BitmapFactory.Options();
                //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
                //you try the use the bitmap here, you will get null.
                options.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
                int actualHeight = options.outHeight;
                int actualWidth = options.outWidth;
                //max Height and width values of the compressed image is taken as 816x612
                float maxHeight = 816.0f;
                float maxWidth = 612.0f;
                float imgRatio = actualWidth / actualHeight;
                float maxRatio = maxWidth / maxHeight;
                //width and height values are set maintaining the aspect ratio of the image
                if (actualHeight > maxHeight || actualWidth > maxWidth) {
                    if (imgRatio < maxRatio) {
                        imgRatio = maxHeight / actualHeight;
                        actualWidth = (int) (imgRatio * actualWidth);
                        actualHeight = (int) maxHeight;
                    } else if (imgRatio > maxRatio) {
                        imgRatio = maxWidth / actualWidth;
                        actualHeight = (int) (imgRatio * actualHeight);
                        actualWidth = (int) maxWidth;
                    } else {
                        actualHeight = (int) maxHeight;
                        actualWidth = (int) maxWidth;
                    }
                }
                //setting inSampleSize value allows to load a scaled down version of the original image
                options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
                //inJustDecodeBounds set to false to load the actual bitmap
                options.inJustDecodeBounds = false;
                //this options allow android to claim the bitmap memory if it runs low on memory
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inTempStorage = new byte[16 * 1024];
                try {
                    //load the bitmap from its path
                    bmp = BitmapFactory.decodeFile(imagePath, options);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }
                try {
                    scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }
                float ratioX = actualWidth / (float) options.outWidth;
                float ratioY = actualHeight / (float) options.outHeight;
                float middleX = actualWidth / 2.0f;
                float middleY = actualHeight / 2.0f;
                Matrix scaleMatrix = new Matrix();
                scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
                //check the rotation of the image and display it properly
                ExifInterface exif;
                try {
                    exif = new ExifInterface(imagePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                        Log.d("EXIF", "Exif: " + orientation);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                        Log.d("EXIF", "Exif: " + orientation);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                        Log.d("EXIF", "Exif: " + orientation);
                    }
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), scaledBitmap, "Title", null);
                //end
                filePath = Uri.parse(path);
                try {
                    bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageView1.setImageBitmap(bitmap1);
                    btn_choose1.setBackgroundResource(R.drawable.captureright);
                    this.getContentResolver().delete(filePath, null, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED && requestCode == CAMERA_PREVIEW_RESULT && data != null) {
                Toast.makeText(this, "Please try again!", Toast.LENGTH_LONG).show();
            }

        }


        //for second picture
        if (check.equals("2")) {
            // handle result of CropImageActivity
            if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
                //now resize image
                //start
                String imagePath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
                // String imagePath = data.getStringExtra(CameraPreviewActivity.RESULT_IMAGE_PATH);
                Bitmap scaledBitmap = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
                //you try the use the bitmap here, you will get null.
                options.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
                int actualHeight = options.outHeight;
                int actualWidth = options.outWidth;
                //max Height and width values of the compressed image is taken as 816x612
                float maxHeight = 816.0f;
                float maxWidth = 612.0f;
                float imgRatio = actualWidth / actualHeight;
                float maxRatio = maxWidth / maxHeight;
                //width and height values are set maintaining the aspect ratio of the image
                if (actualHeight > maxHeight || actualWidth > maxWidth) {
                    if (imgRatio < maxRatio) {
                        imgRatio = maxHeight / actualHeight;
                        actualWidth = (int) (imgRatio * actualWidth);
                        actualHeight = (int) maxHeight;
                    } else if (imgRatio > maxRatio) {
                        imgRatio = maxWidth / actualWidth;
                        actualHeight = (int) (imgRatio * actualHeight);
                        actualWidth = (int) maxWidth;
                    } else {
                        actualHeight = (int) maxHeight;
                        actualWidth = (int) maxWidth;
                    }
                }

                //setting inSampleSize value allows to load a scaled down version of the original image
                options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
                //inJustDecodeBounds set to false to load the actual bitmap
                options.inJustDecodeBounds = false;
                //this options allow android to claim the bitmap memory if it runs low on memory
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inTempStorage = new byte[16 * 1024];
                try {
                    //load the bitmap from its path
                    bmp = BitmapFactory.decodeFile(imagePath, options);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }
                try {
                    scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }
                float ratioX = actualWidth / (float) options.outWidth;
                float ratioY = actualHeight / (float) options.outHeight;
                float middleX = actualWidth / 2.0f;
                float middleY = actualHeight / 2.0f;
                Matrix scaleMatrix = new Matrix();
                scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
                //check the rotation of the image and display it properly
                ExifInterface exif;
                try {
                    exif = new ExifInterface(imagePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                        Log.d("EXIF", "Exif: " + orientation);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                        Log.d("EXIF", "Exif: " + orientation);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                        Log.d("EXIF", "Exif: " + orientation);
                    }
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), scaledBitmap, "Title", null);
                //end
                filePath = Uri.parse(path);
                try {
                    bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageView2.setImageBitmap(bitmap2);
                    btn_choose2.setBackgroundResource(R.drawable.captureright);
                    this.getContentResolver().delete(filePath, null, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED && requestCode == CAMERA_PREVIEW_RESULT && data != null) {
                Toast.makeText(this, "Please try again!", Toast.LENGTH_LONG).show();
            }
        }


        //for third picture
        if (check.equals("3")) {
            // handle result of CropImageActivity
            if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
                //now resize image
                //start
                String imagePath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
                Bitmap scaledBitmap = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
                //you try the use the bitmap here, you will get null.
                options.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
                int actualHeight = options.outHeight;
                int actualWidth = options.outWidth;
                //max Height and width values of the compressed image is taken as 816x612
                float maxHeight = 816.0f;
                float maxWidth = 612.0f;
                float imgRatio = actualWidth / actualHeight;
                float maxRatio = maxWidth / maxHeight;
                //width and height values are set maintaining the aspect ratio of the image
                if (actualHeight > maxHeight || actualWidth > maxWidth) {
                    if (imgRatio < maxRatio) {
                        imgRatio = maxHeight / actualHeight;
                        actualWidth = (int) (imgRatio * actualWidth);
                        actualHeight = (int) maxHeight;
                    } else if (imgRatio > maxRatio) {
                        imgRatio = maxWidth / actualWidth;
                        actualHeight = (int) (imgRatio * actualHeight);
                        actualWidth = (int) maxWidth;
                    } else {
                        actualHeight = (int) maxHeight;
                        actualWidth = (int) maxWidth;
                    }
                }
                //setting inSampleSize value allows to load a scaled down version of the original image
                options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
                //inJustDecodeBounds set to false to load the actual bitmap
                options.inJustDecodeBounds = false;
                //this options allow android to claim the bitmap memory if it runs low on memory
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inTempStorage = new byte[16 * 1024];
                try {
                    //load the bitmap from its path
                    bmp = BitmapFactory.decodeFile(imagePath, options);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }
                try {
                    scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }
                float ratioX = actualWidth / (float) options.outWidth;
                float ratioY = actualHeight / (float) options.outHeight;
                float middleX = actualWidth / 2.0f;
                float middleY = actualHeight / 2.0f;
                Matrix scaleMatrix = new Matrix();
                scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
                //check the rotation of the image and display it properly
                ExifInterface exif;
                try {
                    exif = new ExifInterface(imagePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                        Log.d("EXIF", "Exif: " + orientation);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                        Log.d("EXIF", "Exif: " + orientation);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                        Log.d("EXIF", "Exif: " + orientation);
                    }
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), scaledBitmap, "Title", null);
                //end
                filePath = Uri.parse(path);
                try {
                    bitmap3 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageView3.setImageBitmap(bitmap3);
                    btn_choose3.setBackgroundResource(R.drawable.captureright);
                    this.getContentResolver().delete(filePath, null, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED && requestCode == CAMERA_PREVIEW_RESULT && data != null) {
                Toast.makeText(this, "Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }
    public String getStringImage1(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public String getStringImage2(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public String getStringImage3(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void pickImage1(View view) {
        check="1";
        chooseImage();
    }
    public void pickImage2(View view) {
        check="2";
        chooseImage();
    }
    public void pickImage3(View view) {
        check="3";
        chooseImage();
    }
    //end



    //uploading product
    void uploadingProduct() {
        String url = "https://ityeard.com/Restaurant/addProduct.php";

        str_name = edit_name.getText().toString().trim();
        str_description = edit_description.getText().toString().trim();
        str_price = edit_price.getText().toString().trim();
        str_commission = edit_commission.getText().toString().trim();
        str_discount = edit_discount.getText().toString().trim();
        str_quantity = edit_quantity.getText().toString().trim();

        if (str_category.length() != 0 && str_name.length() != 0 && str_description.length() != 0 && str_price.length() != 0 && str_commission.length() != 0 && str_discount.length() != 0 && str_quantity.length() != 0) {
            if (bitmap1 == null || bitmap2 == null || bitmap3 == null) {
                Toast.makeText(AddProductActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage1 = getStringImage1(bitmap1);
                uploadImage2 = getStringImage2(bitmap2);
                uploadImage3 = getStringImage3(bitmap3);
                progressDialog.show();
                if (str_category.equals("Select category")) {
                    str_category = "Other";
                }
                StringRequest sq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddProductActivity.this, "Your product uploaded successfully!!!", Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                        startActivity(new Intent(AddProductActivity.this, ViewProductActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(UpdateRestaurantProfile.this,"Network connection failed!!!",Toast.LENGTH_LONG).show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(AddProductActivity.this, "No internet connection!!!", Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(AddProductActivity.this, "Network connection failed!!!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AddProductActivity.this, "Please check your internet connection!!!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.cancel();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("category", str_category);
                        params.put("name", str_name);
                        params.put("description", str_description);
                        params.put("price", str_price);
                        params.put("commission", str_commission);
                        params.put("discount", str_discount);
                        params.put("quantity", str_quantity);
                        params.put("image1", uploadImage1);
                        params.put("image2", uploadImage2);
                        params.put("image3", uploadImage3);
                        return params;
                    }
                };
                sq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(sq);
            }
        } else {
            Log.e("ERROR  ::::   ", "      NAME :::" + str_name);
            Toast.makeText(AddProductActivity.this, "Please fill up all requirements", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void permissionGranted() {

    }

    @Override
    public void permissionDenied() {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
