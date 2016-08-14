package project.etrumper.thomas.ghostbutton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by thoma on 6/14/2016.
 */
public class Logable {

    boolean DEBUG = true;

    Logable(String TAG) {
        this.TAG = TAG;
    }

    String TAG;

    protected void LOGE(String message) {
        // Do not log if object is not set in debug mode
        if (!this.DEBUG) {
            return;
        }
        Log.e(this.TAG, message);
    }

    public static void alertBoxSimple(final String title, final String message, final String dismissButton) {
        // Create initial builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);
        // Add title and message
        builder.setMessage(message)
                .setTitle(title)
                // Make sure user cannot dismiss without pressing button
                .setCancelable(false);
        // Add the dismiss button
        builder.setPositiveButton(dismissButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Pressed button
            }
        });
        // Show the dialogue box
        showDialogueBox(builder);
    }

    volatile static String userString;

    public static String getUserString(final String prompt, final String positive, final String negative) {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);
                // Get the layout inflater
                LayoutInflater inflater = MainActivity.activity.getLayoutInflater();
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                View v = inflater.inflate(R.layout.user_prompt, null);
                // Gather elements
                TextView promptTV = (TextView) v.findViewById(R.id.prompt);
                final EditText text = (EditText) v.findViewById(R.id.text);
                Button positiveB = (Button) v.findViewById(R.id.positive),
                        negativeB = (Button) v.findViewById(R.id.negative);
                // Set elements
                promptTV.setText(prompt);
                positiveB.setText(positive);
                negativeB.setText(negative);
                // Set view
                builder.setView(v);

                userString = null;
                final Dialog dialogue = builder.create();
                // Set handlers
                positiveB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Check text box to see if valid text
                        String gotText = text.getText().toString();
                        if (gotText.length() == 0) {
                            text.setError("Enter text");
                            return;
                        }else if (gotText.equals("-stop")) {
                            text.setError("Invalid name");
                            return;
                        }
                        userString = gotText;
                        dialogue.dismiss();
                    }
                });
                negativeB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userString = "-stop";
                        dialogue.dismiss();
                    }
                });
                // Show dialogue
                dialogue.show();
            }
        });
        // Wait for thread
        while (userString == null) {

        }
        if (userString.equals("-stop")) {
            return null;
        }
        String temp = userString;
        userString = null;
        return temp;
    }


    public static void customDialogue(int layout, String positive) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);
        // Get the layout inflater
        LayoutInflater inflater = MainActivity.activity.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(layout, null))
                // Add action buttons
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                });
        showDialogueBox(builder);
    }

    public static void showDialogueBox(final AlertDialog.Builder builder){
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.create().show();
            }
        });
    }

}
