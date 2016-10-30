package com.example.josefcutler.foodnotifier;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import java.io.IOException;
import android.widget.ListView;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.util.ArrayList;

import android.os.AsyncTask;

import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView savedItems;

    private ArrayList<String> today;
    private ArrayAdapter<String> todayAdapter;
    private ListView todayItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todayItems = (ListView) findViewById(R.id.todayItems);

        today = new ArrayList<String>();
        todayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, today);
        todayItems.setAdapter(todayAdapter);
        todayAdapter.add("Meatball Pizza");



        savedItems = (ListView) findViewById(R.id.savedItems);
        readItems();
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        savedItems.setAdapter(itemsAdapter);
        itemsAdapter.add("Chipotle Chicken Bowl");
        itemsAdapter.add("Spiralini Alfredo");
        itemsAdapter.add("Shrimp Tortellini");

        // Setup remove listener method call
        setupListViewListener();

    }

    private void setupListViewListener() {
        savedItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position
                        items.remove(pos);
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }

                });
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);


        etNewItem.setText("");
        writeItems();

        MenuScanner mScanner = new MenuScanner(itemText);
        final MenuScanner.MyTask myTask = mScanner.new MyTask();
        myTask.execute();

//        if (mScanner.itemIsServed) {
//            todayAdapter.add(itemText);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*private void searchFood(ArrayList <String> ){

    }*/

    public class MenuScanner {

        String item;
        boolean itemIsServed = false;

        public MenuScanner(String dish) {
            item = dish;
        }

        public void processItem(String menuItems) {
//            if (menuItems.toLowerCase().contains(item.toLowerCase())) {
//                todayAdapter.add(item);
//            }
            if(menuItems == "yes"){
                todayAdapter.add(item);
            }
        }

        public class MyTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... params) {
                ArrayList menuItems;
                Document doc;
                try {
                    doc = Jsoup.connect("http://menu.ha.ucla.edu/foodpro").get(); // this is the problem line
                    System.out.println(doc);
                    menuItems = doc.getElementsContainingText(item);
                    if(menuItems.size() > 0) {
                        return "yes";
                    }
                    // check full menus
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return "no";
            }

            @Override
            protected void onPostExecute(String result) {
                processItem(result);
            }
        }
    }
}

