package com.example.app_fast_food.Cart;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_fast_food.Activity.DetailFoodActivity;
import com.example.app_fast_food.Activity.EditFoodActivity;
import com.example.app_fast_food.Adapter.dinhDangTien;
import com.example.app_fast_food.Helper.DatabaseHelper;
import com.example.app_fast_food.Helper.FoodsDatabase;
import com.example.app_fast_food.Model.Foods;
import com.example.app_fast_food.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private CartDatabase cartDB;
    private DatabaseHelper db;
    private FoodsDatabase foodDB;
    private int currentUserId;
    private CartInteractionListener listener; // Interface để giao tiếp lại với Activity

    public interface CartInteractionListener {
        void onCartItemChanged(); // Gọi khi số lượng thay đổi hoặc item bị xóa
        // void onItemRemoved(CartItem item); // Có thể chi tiết hơn nếu cần
    }

    public CartAdapter(Context context, List<CartItem> cartItems, int currentUserId, CartInteractionListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartDB = new CartDatabase(context);
        this.db = new DatabaseHelper(context);
        this.foodDB = new FoodsDatabase(db.getReadableDatabase());
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.viewholder_cart,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.titleTxt.setText(item.getFoodTitle());
        holder.feeEachItem.setText(dinhDangTien.dinhdang(item.getPricePerItem())); // Giá mỗi sản phẩm
        holder.numberItemTxt.setText(String.valueOf(item.getQuantity()));
        holder.totalEachItem.setText(dinhDangTien.dinhdang(item.getToTalPrice())); // Tổng tiền cho loại sản phẩm này

        // Load ảnh sản phẩm
        String imagePath = item.getFoodImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            int imageResourceId = context.getResources().getIdentifier(
                    imagePath, "drawable", context.getPackageName());
            if (imageResourceId != 0) {
                holder.pic.setImageResource(imageResourceId);
            } else {
                holder.pic.setImageResource(R.drawable.jollibear_logo);
            }
        } else {
            holder.pic.setImageResource(R.drawable.jollibear_logo);
        }

        // Xử lý sự kiện nút "+"
        holder.plusCartBtn.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            if (cartDB.updateCartItemQuantity(currentUserId, item.getFoodId(), newQuantity, item.getNote())) {
                item.setQuantity(newQuantity);
                notifyItemChanged(holder.getAdapterPosition()); // Cập nhật item này
                if (listener != null) {
                    listener.onCartItemChanged(); // Thông báo cho Activity cập nhật tổng tiền
                }
            }
        });

        // Xử lý sự kiện nút "-"
        holder.minusCartBtn.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity > 0) {
                if (cartDB.updateCartItemQuantity(currentUserId, item.getFoodId(), newQuantity, item.getNote())) {
                    item.setQuantity(newQuantity);
                    notifyItemChanged(holder.getAdapterPosition());
                    if (listener != null) {
                        listener.onCartItemChanged();
                    }
                }
            } else { // Nếu số lượng giảm xuống 0 hoặc ít hơn, xóa sản phẩm
                removeItem(holder.getAdapterPosition());
            }
        });

        // Xử lý sự kiện nút "Xóa"
        holder.removeCartBtn.setOnClickListener(v -> {
            removeItem(holder.getAdapterPosition());
        });

        Log.d("CartAdapter", "Binding item: " + item.getFoodTitle() + ", quantity: " + item.getQuantity());

        holder.itemView.setOnClickListener(v->{
            Foods fullFood = foodDB.getFoodById(item.getFoodId());

            if (fullFood != null) {
                // Chuyển sang EditFoodActivity và truyền dữ liệu
                Intent intent = new Intent(context, EditFoodActivity.class);
                intent.putExtra("food", fullFood); // Đầy đủ thông tin món ăn
                intent.putExtra("cart", item);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void removeItem(int position){
        if (position >= 0 && position < cartItems.size()) {
            CartItem itemToRemove = cartItems.get(position);
            if (cartDB.removeCartItem(currentUserId, itemToRemove.getFoodId(), itemToRemove.getNote())) {
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size()); // Cập nhật các vị trí sau khi xóa
                if (listener != null) {
                    listener.onCartItemChanged(); // Thông báo cho Activity
                }
            } else {
                Log.e("CartAdapter", "Failed to remove item from DB: " + itemToRemove.getFoodTitle());
                // Có thể hiển thị Toast thông báo lỗi
            }
        }

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateData(List<CartItem> newCartItems) {
        this.cartItems.clear();
        if (newCartItems != null) {
            this.cartItems.addAll(newCartItems);
        }
        notifyDataSetChanged();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt, feeEachItem, totalEachItem, numberItemTxt;
        TextView plusCartBtn, minusCartBtn, removeCartBtn;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.picCart);
            titleTxt = itemView.findViewById(R.id.titleTxtCart);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            numberItemTxt = itemView.findViewById(R.id.numberItemTxt);
            plusCartBtn = itemView.findViewById(R.id.plusCartBtn);
            minusCartBtn = itemView.findViewById(R.id.minusCartBtn);
            removeCartBtn = itemView.findViewById(R.id.removeCartBtn);
        }
    }
}