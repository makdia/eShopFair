package com.photoeditorsdk.android.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    public static List<ViewProductModel> productLists = new ArrayList<>();
    RecyclerView recyclerView;
    ViewProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccentLight)));


        productAdapter = new ViewProductAdapter(getApplicationContext(), productLists);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(productAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading...");

        gettingAllProduct();
    }


    public void gettingAllProduct() {
        recyclerView.removeAllViews();
        productLists.clear();
        progressDialog.show();
        String myURL = "https://ityeard.com/Restaurant/viewProduct.php";
        JsonArrayRequest mainArrayReq = new JsonArrayRequest(myURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.isNull(0)) {
                    progressDialog.cancel();
                    Toast.makeText(ViewProductActivity.this, "You have no product!!!", Toast.LENGTH_LONG).show();
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) response.get(i);

                        ViewProductModel productModel = new ViewProductModel();
                        productModel.setId(jsonObject.getString("id"));
                        productModel.setCategory(jsonObject.getString("category"));
                        productModel.setName(jsonObject.getString("name"));
                        productModel.setDescription(jsonObject.getString("description"));
                        productModel.setCustomerPrice(jsonObject.getString("customer_price"));
                        productModel.setCommission(jsonObject.getString("commission"));
                        productModel.setDiscount(jsonObject.getString("discount"));
                        productModel.setQuantitiy(jsonObject.getString("quantity"));
                        productModel.setImage1(jsonObject.getString("image1"));
                        productModel.setImage2(jsonObject.getString("image2"));
                        productModel.setImage3(jsonObject.getString("image3"));
                        productModel.setDate(jsonObject.getString("adding_date"));
                        productLists.add(productModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        productAdapter.notifyItemChanged(i);
                        progressDialog.cancel();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ViewProductActivity.this, "No internet connection!!!", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ViewProductActivity.this, "Network connection failed!!!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ViewProductActivity.this, "Please check your internet connection!!!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.cancel();
            }
        });
        mainArrayReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(mainArrayReq);
    }


    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        finish();
    }

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