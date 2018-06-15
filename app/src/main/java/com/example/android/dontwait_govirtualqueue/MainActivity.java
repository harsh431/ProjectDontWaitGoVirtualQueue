package com.example.android.dontwait_govirtualqueue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    String firstname="";
    String lastname="";
    RelativeLayout relative;
    public static int PORT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relative = findViewById(R.id.relative);
        Button loginButton = findViewById(R.id.login_button);
        TextView signUpButton = findViewById(R.id.signup_button);
        loginButton.setOnClickListener(loginOnClickListener);
        signUpButton.setOnClickListener(signUpOnClickListener);
        getPortFromGitHub extractPort = new getPortFromGitHub();
        extractPort.execute();
        Log.v("harshdemo","The port is "+PORT);



    }

    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*TextView loginsuccess = findViewById(R.id.login_successful_text);
            loginsuccess.setVisibility(View.INVISIBLE);*/

        EditText email = findViewById(R.id.email);
        EditText pass = findViewById(R.id.password);
        String result ="";
        String[] userpass = {email.getText().toString(),pass.getText().toString()};
        LoginValidator loginValidator = new LoginValidator();
            try {
                ConnectivityManager cm =
                        (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(!isConnected)
                {
                    Toast.makeText(getApplicationContext(),"The internet is not available",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!((TextUtils.isEmpty(email.getText().toString()))||(TextUtils.isEmpty(pass.getText().toString())))) {
                    result = loginValidator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userpass).get();
                   // Log.v("harshdemo", result);
                }
                else{
                    Snackbar.make(relative,"Email or Password cannot be empty!",Snackbar.LENGTH_LONG).show();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(!TextUtils.isEmpty(result)) {
                if (result.equals("valid")) {
                    Log.v("harshdemo", "valid");
                    Toast.makeText(getApplicationContext(),"Login Successful!",Toast.LENGTH_LONG).show();
              /*  TextView textView = findViewById(R.id.login_successful_text);
                textView.setText("Login is successful!");
                textView.setVisibility(View.VISIBLE);*/
                    Intent intent = new Intent(getApplicationContext(), Login_Successful.class);
                    intent.putExtra("firstname", firstname);
                    intent.putExtra("lastname", lastname);
                    intent.putExtra("email", email.getText());
                    startActivity(intent);
                } else {
                    Log.v("harshdemo", "invalid");
                /*TextView textView = findViewById(R.id.login_successful_text);
                textView.setText("Sorry! Credentials are not valid!");
                textView.setVisibility(View.VISIBLE);*/
              Snackbar.make(relative,"Sorry! Credential are not valid!",Snackbar.LENGTH_LONG).show();
                }
            }

        }
    };

    public class getPortFromGitHub extends AsyncTask<Void,Void,String>
    {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://raw.githubusercontent.com/harsh431/ProjectDontWaitGoVirtualQueue/master/port.txt");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                PORT = Integer.parseInt(in.readLine());
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return PORT+"";
        }

        @Override
        protected void onPostExecute(String s) {
            PORT = Integer.parseInt(s);
        }
    }

    public class LoginValidator extends AsyncTask<String,Void,String>
    {


        @Override
        protected String doInBackground(String... strings) {
            try {


                Socket s = new Socket("0.tcp.ngrok.io",PORT);
                Log.v("harshdemo","Socket ");
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                DataInputStream dis = new DataInputStream(s.getInputStream());
                dos.writeUTF("validate");
                dos.writeUTF(strings[0]);
                dos.writeUTF(strings[1]);
                String result = dis.readUTF();
                if(result.equals("valid")) {
                    firstname = dis.readUTF();
                    lastname = dis.readUTF();
                }
                dos.close();
                dis.close();
                s.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
    }

    private View.OnClickListener signUpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(getApplicationContext(),SignUp.class);
            startActivity(intent);


        }
    };


}
