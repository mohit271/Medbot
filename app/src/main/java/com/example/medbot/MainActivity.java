package com.example.medbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.medbot.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;

    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private ImageView button1;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";

    // creating a variable for
    // our volley request queue.
    private RequestQueue mRequestQueue;

    // creating a variable for array list and adapter class.
    private ArrayList<MessageModal> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
//        binding= ActivityMainBinding.inflate(getLayoutInflater());
//        View view = binding.getRoot();
        setContentView(R.layout.activity_main);

        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);

        mAuth=FirebaseAuth.getInstance();
        String s ="a";
        if(mAuth.getCurrentUser()!=null) {
            s = mAuth.getCurrentUser().getEmail();
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            Log.d("USER", s);
        }
//        binding.userNameChat.setText(s);

        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.getCache().clear();

        // creating a new array list
        messageModalArrayList = new ArrayList<>();


        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking if the message entered
                // by user is empty or not.
                if (userMsgEdt.getText().toString().isEmpty()) {
                    // if the edit text is empty display a toast message.
                    Toast.makeText(MainActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // calling a method to send message
                // to our bot to get response.
                send(userMsgEdt.getText().toString());

                // below line we are setting text in our edit text as empty
                userMsgEdt.setText("");
            }
        });

        // on below line we are initialing our adapter class and passing our array list to it.
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);

        // below line we are creating a variable for our linear layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);

        // below line is to set layout
        // manager to our recycler view.
        chatsRV.setLayoutManager(linearLayoutManager);

        // below line we are setting
        // adapter to our recycler view.
        chatsRV.setAdapter(messageRVAdapter);

        button1 = (ImageView) findViewById(R.id.dots);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, button1);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mAuth.signOut();
                        Intent intent = new Intent(MainActivity.this,LogInActivity.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "Log Out Successful", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });



    }

    private void sendMessage2(String userMsg){
        messageModalArrayList.add(new MessageModal(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();

        String BASE_URl="https://0.0.0.0:5000/chat/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HealthAPI newsAPI =retrofit.create(HealthAPI.class);
        Call<String> call;

        call=newsAPI.getAnswer2(userMsg);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                // in on response method we are extracting data
                // from json response and adding this response to our array list.
                String botResponse = response.toString();
                Log.i("RESPONSE: -",botResponse);
                messageModalArrayList.add(new MessageModal(botResponse, BOT_KEY));

                // notifying our adapter as data changed.
                messageRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                messageModalArrayList.add(new MessageModal("Sorry no response found", BOT_KEY));
                Toast.makeText(MainActivity.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });


    }

    private void sendMessage(String userMsg) {
        // below line is to pass message to our
        // array list which is entered by the user.
        messageModalArrayList.add(new MessageModal(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();

        // url for our brain
        // make sure to add mshape for uid.
        // make sure to add your url.
        String url = "Enter you API URL here" + userMsg;

        // creating a variable for our request queue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // on below line we are making a json object request for a get request and passing our url .
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // in on response method we are extracting data
                    // from json response and adding this response to our array list.
                    String botResponse = response.getString("cnt");
                    messageModalArrayList.add(new MessageModal(botResponse, BOT_KEY));

                    // notifying our adapter as data changed.
                    messageRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                    // handling error response from bot.
                    messageModalArrayList.add(new MessageModal("No response", BOT_KEY));
                    messageRVAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling.
                messageModalArrayList.add(new MessageModal("Sorry no response found", BOT_KEY));
                Toast.makeText(MainActivity.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
            }
        });

        // at last adding json object
        // request to our queue.
        queue.add(jsonObjectRequest);
    }

    private void send(String userMsg){
        messageModalArrayList.add(new MessageModal(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://0.0.0.0:5000/chat/";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Question", userMsg);

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String botResponse = response.toString();
                    messageModalArrayList.add(new MessageModal(botResponse, BOT_KEY));

                    // notifying our adapter as data changed.
                    messageRVAdapter.notifyDataSetChanged();
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    messageModalArrayList.add(new MessageModal("No response", BOT_KEY));
                    messageRVAdapter.notifyDataSetChanged();
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = String.valueOf(response.statusCode);

                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}