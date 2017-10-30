package com.ah.socketclientdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.et_main)
    EditText etMain;
    @BindView(R.id.b_main_enter)
    Button bMainEnter;
    @BindView(R.id.b_main_up)
    Button bMainUp;
    @BindView(R.id.b_main_left)
    Button bMainLeft;
    @BindView(R.id.b_main_right)
    Button bMainRight;
    @BindView(R.id.b_main_down)
    Button bMainDown;
    @BindView(R.id.tv_main)
    TextView tvMain;

    final static String SERVER_IP = "192.168.43.64";
    final static int SERVER_PORT = 8001;
    Client clientOut, clientIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initListener();

        initClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.b_main_enter:
                clientOut.messageout = etMain.getText().toString()+"\r\n";
                clientOut.bReady = true;
                tvMain.setText(tvMain.getText()+"\r\n"+"I say: "+etMain.getText().toString());
                etMain.setText("");
                break;

            case R.id.b_main_up:
                clientOut.messageout = "上";
                clientOut.bReady = true;
                tvMain.setText(tvMain.getText()+"\r\n"+"I say: 上");
                break;

            case R.id.b_main_down:
                clientOut.messageout = "下";
                clientOut.bReady = true;
                tvMain.setText(tvMain.getText()+"\r\n"+"I say: 下");
                break;

            case R.id.b_main_left:
                clientOut.messageout = "左";
                clientOut.bReady = true;
                tvMain.setText(tvMain.getText()+"\r\n"+"I say: 左");
                break;

            case R.id.b_main_right:
                clientOut.messageout = "右";
                clientOut.bReady = true;
                tvMain.setText(tvMain.getText()+"\r\n"+"I say: 右");
                break;
        }
    }

    private void initClient() {

        clientOut = new Client(SERVER_IP, SERVER_PORT, null);
        clientIn = new Client(SERVER_IP, SERVER_PORT + 1, tvMain);
        clientOut.start();
        clientIn.start();
    }

    private void initListener() {

        bMainEnter.setOnClickListener(this);
        bMainUp.setOnClickListener(this);
        bMainDown.setOnClickListener(this);
        bMainLeft.setOnClickListener(this);
        bMainRight.setOnClickListener(this);
    }

    static class Client extends Thread {
        public InputStream in;
        public OutputStream out;
        private Socket client;
        String host;
        int port;
        TextView tv = null;
        String response, request;
        BufferedReader reader, readerMe;
        PrintWriter writer;
        String messageout = "";
        boolean bReady = false;

        Runnable ru = new Runnable() {
            @Override
            public void run() {
                tv.setText(tv.getText() + "\r\n" + "Server says: " + response);
            }
        };
        Handler hdl = new Handler();

        public Client(String host, int port, TextView tv) {
            this.host = host;
            this.port = port;
            this.tv = tv;
        }

        public void run() {
            if (port == SERVER_PORT) {
                try {
                    client = new Socket(host, port);
                    System.out.println("Client socket: " + client);
                    out = client.getOutputStream();
                } catch (IOException e) {
                    System.err.println("IOExc : " + e);
                }
                writer = new PrintWriter(new OutputStreamWriter(out), true);
                while (true) {
                    try {
                        if (bReady) {
                            bReady = false;
                            byte[] bts = messageout.getBytes();
                            InputStream is = new ByteArrayInputStream(bts);
                            readerMe = new BufferedReader(new InputStreamReader(is));
                            request = readerMe.readLine();
                            writer.println(request);
                        }
                    } catch (IOException e) {
                        System.err.println("Bye Bye~~");
                        System.exit(1);
                    } catch (Exception e) {
                        System.err.println("Bye Bye~~");
                        System.exit(1);
                    }
                }
            } else if (port == SERVER_PORT + 1) {
                try {
                    client = new Socket(host, port);
                    System.out.println("Client socket: " + client);
                    in = client.getInputStream();
                } catch (IOException e) {
                    System.err.println("IOExc : " + e);
                }
                reader = new BufferedReader(new InputStreamReader(in));
                while (true) {
                    try {
                        response = reader.readLine();
                        if (response == null) {
                            System.err.println("Bye Bye~~");
                            System.exit(1);
                        }
                        System.out.println("Server says: " + response);
                        hdl.post(ru);
                    } catch (IOException e) {
                        System.err.println("Bye Bye~~");
                        System.exit(1);
                    } catch (Exception e) {
                        System.err.println("Bye Bye~~");
                        System.exit(1);
                    }
                }
            }
        }
    }
}
