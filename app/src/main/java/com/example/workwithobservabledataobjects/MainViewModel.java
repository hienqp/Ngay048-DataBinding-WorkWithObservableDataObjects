package com.example.workwithobservabledataobjects;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

public class MainViewModel extends BaseObservable {
    private String message;
    public final ObservableField<String> contentObservableField = new ObservableField<>();

    public MainViewModel(String message) {
        this.message = message;
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        notifyPropertyChanged(BR.message);
    }

    public void changeValueTextView() {
        this.setMessage("New TextValue");
    }

    public void changeContentObservableField() {
        this.contentObservableField.set("Change Content Observable Field");
    }
}
