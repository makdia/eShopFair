package com.photoeditorsdk.android.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ComputeR GallerY on 9/10/2017.
 */

public class ViewProductAdapter extends RecyclerView.Adapter<ViewProductAdapter.MyViewHolder> {

    private ArrayList<ViewProductModel> productLists;
    private Context context;

    public ViewProductAdapter(Context context, List<ViewProductModel> productLists) {
        this.productLists = (ArrayList<ViewProductModel>)productLists;
        this.context = context;
    }


    @Override
    public ViewProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.view_product_card_view,parent,false);
        ViewProductAdapter.MyViewHolder holder=new ViewProductAdapter.MyViewHolder(view,context,productLists);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewProductAdapter.MyViewHolder holder, int position) {

        final ViewProductModel newsModel=productLists.get(position);
        holder.name.setText(Html.fromHtml(newsModel.getName()));
        holder.price.setText(Html.fromHtml("BDT"+newsModel.getCustomerPrice()));
        //holder.discount.setText(Html.fromHtml("Discount "+newsModel.getDiscount()+"%"));
        //holder.quantity.setText(Html.fromHtml("Quantity "+newsModel.getQuantitiy()));
        Picasso.with(context).load(newsModel.getImage1()).placeholder(R.drawable.productpic1).into(holder.imageView1);
        Picasso.with(context).load(newsModel.getImage2()).placeholder(R.drawable.productpic1).into(holder.imageView2);
        Picasso.with(context).load(newsModel.getImage3()).placeholder(R.drawable.productpic1).into(holder.imageView3);

    }


    @Override
    public int getItemCount() {
        return productLists.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        TextView name,price,discount,quantity;
        ImageView imageView1,imageView2,imageView3;
        ArrayList<ViewProductModel> productLists=new ArrayList<>();
        Context context;

        public MyViewHolder(View itemView, Context context, ArrayList<ViewProductModel> productLists) {
            super(itemView);
            this.context=context;
            this.productLists=productLists;
            name=(TextView)itemView.findViewById(R.id.name);
            price=(TextView)itemView.findViewById(R.id.price);
          //  discount=(TextView)itemView.findViewById(R.id.discount);
           // quantity=(TextView)itemView.findViewById(R.id.quantity);
            imageView1=(ImageView)itemView.findViewById(R.id.image1);
            imageView2=(ImageView)itemView.findViewById(R.id.image2);
            imageView3=(ImageView)itemView.findViewById(R.id.image3);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            ViewProductModel productModel=this.productLists.get(position);
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("category",productModel.getCategory());
            intent.putExtra("name",productModel.getName());
            intent.putExtra("details",productModel.getDescription());
            intent.putExtra("customer_price",productModel.getCustomerPrice());
            intent.putExtra("commission",productModel.getCommission());
            intent.putExtra("discount",productModel.getDiscount());
            intent.putExtra("quantity",productModel.getQuantitiy());
            intent.putExtra("image1",productModel.getImage1());
            intent.putExtra("image2",productModel.getImage2());
            intent.putExtra("image3",productModel.getImage3());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}


