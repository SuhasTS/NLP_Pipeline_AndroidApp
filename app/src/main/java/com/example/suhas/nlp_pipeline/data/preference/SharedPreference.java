package com.example.suhas.nlp_pipeline.data.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public enum  SharedPreference
{
    URL(String.class);
    private Class dataClass;
    private Object value = null;

    SharedPreference(Class dataClass) {
        this.dataClass = dataClass;
    }

    public static <T> void save(Context context, SharedPreference sharedPreference, T value) {
        SharedPreferences.Editor editor =  context.getSharedPreferences("pref-names", Activity.MODE_PRIVATE).edit();
        if (value == null)
            editor.remove(sharedPreference.name());
        else {
            if (!sharedPreference.dataClass.isInstance(value))
                throw new IllegalArgumentException(sharedPreference.name() + " can only save " + sharedPreference.dataClass + " type object. Found " + value.getClass());
            switch (sharedPreference.dataClass.getSimpleName()) {
                case "Integer":
                    editor.putInt(sharedPreference.name(), (Integer) value);
                    break;
                case "Long":
                    editor.putLong(sharedPreference.name(), (Long) value);
                    break;
                case "Boolean":
                    editor.putBoolean(sharedPreference.name(), (Boolean) value);
                    break;
                case "String":
                    editor.putString(sharedPreference.name(), String.valueOf(value));
            }
            sharedPreference.setValue(value);
        }
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Context context, SharedPreference sharedPreference, T defaultValue) {
        T value = sharedPreference.getValue();
        if(value != null)
            return value;
        switch (sharedPreference.dataClass.getSimpleName()) {
            case "Integer":
                value = (T) Integer.valueOf(context.getSharedPreferences("pref-names", Activity.MODE_PRIVATE).getInt(sharedPreference.name(), (Integer) defaultValue));
                break;
            case "Long":
                value = (T) Long.valueOf(context.getSharedPreferences("pref-names", Activity.MODE_PRIVATE).getLong(sharedPreference.name(), (Long) defaultValue));
                break;
            case "Boolean":
                value = (T) Boolean.valueOf(context.getSharedPreferences("pref-names", Activity.MODE_PRIVATE).getBoolean(sharedPreference.name(), (Boolean) defaultValue));
                break;
            case "String":
                value = (T) String.valueOf(context.getSharedPreferences("pref-names", Activity.MODE_PRIVATE).getString(sharedPreference.name(), String.valueOf(defaultValue)));
        }
        return value == null ? defaultValue : value;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

