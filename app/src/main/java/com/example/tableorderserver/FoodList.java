package com.example.tableorderserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tableorderserver.Common.Common;
import com.example.tableorderserver.Interface.ItemClickListener;
import com.example.tableorderserver.Module.Category;
import com.example.tableorderserver.Module.Food;
import com.example.tableorderserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLay;

    FloatingActionButton fab;

    //connection firebase
    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageRef;

    String categoryId=";";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    MaterialEditText edtName, edtDes, edtPrice, edtDis;
    Button btnSelected, btnUpload;

    Food newFood;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //firebase con
        db= FirebaseDatabase.getInstance();
        foodList=db.getReference("Food");
        storage= FirebaseStorage.getInstance();
        storageRef= storage.getReference();

        //int
        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLay = findViewById(R.id.rootLay);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFoodDialog();
            }
        });
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
            if (!categoryId.isEmpty())
                loadFoodList(categoryId);


    }

    private void loadFoodList(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)
        ) {
                        @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food modal, int i) {

                            foodViewHolder.food_name.setText(modal.getName());
                            Picasso.with((getBaseContext())).load(modal.getImage())
                                    .into(foodViewHolder.food_image);
                            foodViewHolder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {
                                    showFoodDialog();
                                }
                            });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    private void showFoodDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add Food");
        alertDialog.setMessage("Insert full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_food_layout, null);

        edtName=add_menu_layout.findViewById(R.id.edtName);
        edtDis=add_menu_layout.findViewById(R.id.edtdis);
        edtPrice=add_menu_layout.findViewById(R.id.edtprice);
        edtDes=add_menu_layout.findViewById(R.id.edtDes);
        btnSelected = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //Event for the button selected
        btnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(); //user select image from the galerry
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (newFood != null){
                    foodList.push().setValue(newFood);
                    Toast.makeText(FoodList.this, "New Food"+newFood.getName()+"added", Toast.LENGTH_SHORT).show();

                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Processing...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageRef.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Success!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newFood = new Food();
                            newFood.setName(edtName.getText().toString());
                            newFood.setDescription(edtDes.getText().toString());
                            newFood.setPrice(edtPrice.getText().toString());
                            newFood.setDiscount(edtDis.getText().toString());
                            newFood.setMenuId(categoryId);
                            newFood.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double  progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded "+progress+"%");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode ==RESULT_OK && data  != null
                && data.getData() !=null){
            saveUri = data.getData();
            btnSelected.setText("Image Selected");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);

    }

    private void showUpdateDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Update Food");
        alertDialog.setMessage("Insert full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_food_layout, null);

        edtName=add_menu_layout.findViewById(R.id.edtName);
        edtDis=add_menu_layout.findViewById(R.id.edtdis);
        edtPrice=add_menu_layout.findViewById(R.id.edtprice);
        edtDes=add_menu_layout.findViewById(R.id.edtDes);

        edtName.setText(item.getName());
        edtDes.setText(item.getDescription());
        edtPrice.setText(item.getPrice());
        edtDis.setText(item.getDiscount());

        btnSelected = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //Event for the button selected
        btnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(); //user select image from the galerry
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                    item.setName(edtName.getText().toString());
                    item.setPrice(edtPrice.getText().toString());
                    item.setDiscount(edtDis.getText().toString());
                    item.setDescription(edtDes.getText().toString());


                    foodList.child(key).setValue(item);
                    Toast.makeText(FoodList.this, "New Food name: "+item.getName()+"edited", Toast.LENGTH_SHORT).show();


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    private void changeImage(final Food item) {
        if (saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Processing...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageRef.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "Success!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double  progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded "+progress+"%");
                }
            });
        }
    }
}
