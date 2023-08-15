package com.example.workwithobservabledataobjects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.example.workwithobservabledataobjects.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mActivityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        MainViewModel mainViewModel = new MainViewModel("Observable Tutorial");
        mainViewModel.contentObservableField.set("Content Observable Field");
        mActivityMainBinding.setMainViewModel(mainViewModel);

        mActivityMainBinding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("EdiText", s.toString());
                Log.e("MainViewModel", mainViewModel.contentObservableField.get());
            }
        });

        setContentView(mActivityMainBinding.getRoot());
    }
}