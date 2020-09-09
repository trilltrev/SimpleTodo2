package com.example.simpletodo2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static java.nio.charset.Charset.defaultCharset;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.io.FileUtils.writeLines;

public class MainActivity extends AppCompatActivity {


    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    private ArrayList items;

    {
        setItems(new ArrayList<>(readLines(defaultCharset(),
                getDataFile())));
    }

    private int readLines(Charset defaultCharset, File dataFile) {
        return 0;
    }


    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);



        loadItems();


        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
              //delete the item from the position
              getItems().remove(position);
                //notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }

            @Override
            public void onItemClicked(int adapterPosition) {
                
            }
        };
          ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
              @Override
              public void onItemClicked(int position) {
                 Log.d("MainActivity", "Single click at position " + position);
                 //create the new activity
                  Intent i;
                  i = new Intent(MainActivity.this, EditActivity.class);
                  //pass the data being edited
                  i.putExtra(KEY_ITEM_TEXT, (Bundle) getItems().get(position));
                  i.putExtra(KEY_ITEM_POSITION, position);
                  //display the activity
                  startActivityForResult(i, EDIT_TEXT_CODE);
              }
          };
         itemsAdapter = new ItemsAdapter(getItems(),
                 onLongClickListener,
                 (ItemsAdapter.OnLongClickListener) onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etItem.getText().toString();
                //Add item to model
                getItems().add(todoItem);
                //Notify adapter that an item is inserted
                itemsAdapter.notifyItemInserted(getItems().size() - 1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        });
    }
     //handle result of edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            //retrieve the updated text value

            String itemText = data.getStringExtra(KEY_ITEM_TEXT);

            // extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update the model at the right position with new item text
            getItems().set(position, itemText);

            //notify adapter
            itemsAdapter.notifyItemChanged(position);

            //persist the changes
            saveItems();
        final Toast toast;
        Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
    }




    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }
    // this function will load items by reading every line of the data file
    private void loadItems() {
        setItems(new ArrayList<>(readLines(defaultCharset(), getDataFile())));

    }

    // this function saves items by writing them into the data file
     private void saveItems() {
         try {
             writeLines(getDataFile(), getItems());
         } catch (IOException e) {
             Log.e("MainActivity", "Error writing items", e);
         }
     }

    public ArrayList getItems() {
        return items;
    }

    public void setItems(ArrayList items) {
        this.items = items;
    }
}
