package com.example.ahn.finalproject.MainLogin;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Ahn on 2017-04-01.
 */

public class test extends Fragment{
    private TextView txt;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.capsule_private_main, null);
        //txt = (TextView) view.findViewById(R.id.txt);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
