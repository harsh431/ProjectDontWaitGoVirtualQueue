package com.example.android.dontwait_govirtualqueue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import butterknife.OnClick;

import static com.example.android.dontwait_govirtualqueue.MainActivity.PORT;

public class FinalActivity extends AppCompatActivity {
    String seats="";
    String placeName="";
    TextView seats_text=null;
    TextView queue_text = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        TextView place_name_view = findViewById(R.id.place_name_final);
        seats_text = findViewById(R.id.seats_final);
        TextView seats_edit = findViewById(R.id.seats_edit);
        queue_text=findViewById(R.id.queue_count);
        AppCompatButton confirm_button = findViewById(R.id.book_button);
        Intent intent = getIntent();
        placeName = intent.getStringExtra("placeName");
        Log.v("harshdemo",placeName);
        seats=intent.getStringExtra("seats");
        place_name_view.setText(placeName);
        seats_text.setText(seats);
        seats_edit.setOnClickListener(editListner);
        confirm_button.setOnClickListener(confirmListener);
        queueUpdate q = new queueUpdate();
        q.execute(placeName);
    }
    View.OnClickListener editListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(FinalActivity.this);
            builder.setMessage("How many seats?");
            final EditText input = new EditText(FinalActivity.this);
            input.setHint("Enter no of seats you want to book");
            input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            lp.rightMargin = 10;
            builder.setView(input);
            builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  seats = input.getText().toString();
                  seats_text.setText(seats);
            }});
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.show();
        }
    };
    private class queueUpdate extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            try {
                Socket s = new Socket("0.tcp.ngrok.io",PORT);
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF("UpdateQueue");
                dos.writeInt(0);
                dos.writeUTF(strings[0]);
                result = dis.readUTF();
                Log.v("harshdemo",result);


            } catch (IOException e) {
                Log.v("harshdemo","Error");
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            queue_text.setText(s);
        }
    }
    View.OnClickListener confirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(),BarcodeGeneration.class);
            intent.putExtra("placeName",placeName);
            startActivity(intent);

        }
    };
}
