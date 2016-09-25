package com.example.ramitix.locations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;


import com.maksim88.passwordedittext.PasswordEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import Modules.ExpandableViewAdapter;

/**
 * Created by ramitix on 8/14/16.
 */
public class MainActivity extends Activity implements ExpandableListView.OnChildClickListener {

    private View view;
    private PasswordEditText passwordEditText;
    private ExpandableListView expandableList;
    private ArrayList<String> parentItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();
    private ArrayList<Object> checkedItems = new ArrayList<>();


    private ProgressBar progressBar;
    private boolean validateEmail =false;
    private boolean validatePassword =false;
    private ExpandableViewAdapter adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view =  findViewById(R.id.scroll);
        passwordEditText = (PasswordEditText)findViewById(R.id.etPassword);
        progressBar = (ProgressBar) findViewById( R.id.signinBar );
        expandableList = (ExpandableListView) findViewById(R.id.expandableListView);
        adapter = new ExpandableViewAdapter(parentItems, childItems,checkedItems);
        adapter.setInflater((LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE), this);
        expandableList.setAdapter(adapter);
        expandableList.setOnChildClickListener(this);
        setupUsernameLabelError();
        setupPasswordLabelError();
        setGroupParents();
        setChildData();

    }


    public void setGroupParents() {
        parentItems.add("Get Location Every");

    }

    public void setChildData() {

        // Android
        ArrayList<String> child = new ArrayList<String>();
        child.add("30 Seconds");
        child.add("60 Seconds");
        child.add("5 Minutes");
        child.add("10 Minutes");
        childItems.add(child);

        ArrayList<String> checked = new ArrayList<String>();
        checked.add("true");
        checked.add("false");
        checked.add("false");
        checked.add("false");
        checkedItems.add(checked);



    }



    public void signInProcess(){



        EditText usernameLabel = (EditText) findViewById( R.id.etUsername );
        EditText passwordLabel = (EditText) findViewById( R.id.etPassword );
        try {
            String usernameValue   = URLEncoder.encode(usernameLabel.getText().toString(), "UTF-8");
            String passwordValue    = URLEncoder.encode(passwordLabel.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.INVISIBLE);
            final Snackbar snackbar = Snackbar.make(view, "Error", Snackbar.LENGTH_LONG);
            snackbar.setAction("DISMISS", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    snackbar.dismiss();
                }
            });

            snackbar.show();
        }

    }


    public void signInClicked(View view){

        checkCredentials();

    }


    private void setupUsernameLabelError() {
        final TextInputLayout usernameLayout = (TextInputLayout) findViewById(R.id.username_text);

        usernameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if ( text.toString().contains("@")&& text.toString().contains(".com")) {
                    usernameLayout.setErrorEnabled(false);
                    validateEmail = true;
                } else if (text.length() > 1) {

                    usernameLayout.setError(getString(R.string.username_required));
                    usernameLayout.setErrorEnabled(true);
                    validateEmail = false;
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
 private void setupPasswordLabelError() {
        final TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.password_text);

        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() >= 8) {
                    passwordLayout.setErrorEnabled(false);
                    validatePassword = true;
                } else if (text.length() > 1 && text.length() < 8) {

                    passwordLayout.setError(getString(R.string.Password_required));
                    passwordLayout.setErrorEnabled(true);
                    validatePassword = false;
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkCredentials() {

        progressBar.setVisibility(View.VISIBLE);
        if (!validateEmail) {
            final Snackbar snackbar1 = Snackbar.make(view, "Please enter iCloud Email", Snackbar.LENGTH_INDEFINITE);
            snackbar1.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    snackbar1.dismiss();
                }
            });

            snackbar1.show();
            progressBar.setVisibility(View.INVISIBLE);


            return;
        }

        if (!validatePassword) {
            final Snackbar snackbar2 = Snackbar.make(view, "Please enter Password", Snackbar.LENGTH_INDEFINITE);
            snackbar2.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    snackbar2.dismiss();
                }
            });

            snackbar2.show();
            progressBar.setVisibility(View.INVISIBLE);

            return;
        }
        signInProcess();
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("time",adapter.getTimeValue());
        startActivity(intent);
        progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        return false;
    }
}


