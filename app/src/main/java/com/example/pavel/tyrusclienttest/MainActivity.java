package com.example.pavel.tyrusclienttest;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.myButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("TYRUS-TEST", "### 0 Button.onClick");

                final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            final ClientManager client = ClientManager.createClient();
                            client.getProperties().put(ClientProperties.RECONNECT_HANDLER, new ClientManager.ReconnectHandler() {
                                @Override
                                public boolean onDisconnect(CloseReason closeReason) {
                                    Log.i("TYRUS-TEST", "### 5a Tyrus Client onDisconnect: " + closeReason);
                                    return true;
                                }

                                @Override
                                public boolean onConnectFailure(Exception exception) {
                                    Log.i("TYRUS-TEST", "### 5b Tyrus Client onConnectFailure: " + exception);
                                    return true;
                                }
                            });

                            Log.i("TYRUS-TEST", "### 1 AsyncTask.doInBackground");
                            client.connectToServer(new Endpoint() {
                                @Override
                                public void onOpen(Session session, EndpointConfig EndpointConfig) {

                                    try {
                                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                                            @Override
                                            public void onMessage(String message) {
                                                Log.i("TYRUS-TEST", "### 3 Tyrus Client onMessage: " + message);
                                            }
                                        });

                                        Log.i("TYRUS-TEST", "### 2 Tyrus Client onOpen");
                                        session.getBasicRemote().sendText("Do or do not, there is no try.");
                                    } catch (IOException e) {
                                        // do nothing
                                    }
                                }

                                @Override
                                public void onClose(Session session, CloseReason closeReason) {
                                    Log.i("TYRUS-TEST", "### 4 Tyrus Client onClose: " + closeReason);
                                }
                            }, ClientEndpointConfig.Builder.create().build(), URI.create("ws://10.164.164.133:8025/sample-echo/echo"));

                        } catch (DeploymentException | IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };

                asyncTask.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
