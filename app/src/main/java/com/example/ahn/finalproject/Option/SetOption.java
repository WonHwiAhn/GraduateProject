package com.example.ahn.finalproject.Option;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ahn.finalproject.MainLogin.R;

/**
 * Created by Ahn on 2017-04-18.
 */

public class SetOption extends Fragment {
    View view;
    Button logout;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_set_option, container, false);
        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(listener);
        return view;
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences auto = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = auto.edit();
            //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
            editor.clear();
            editor.commit();
        }
    };
}
