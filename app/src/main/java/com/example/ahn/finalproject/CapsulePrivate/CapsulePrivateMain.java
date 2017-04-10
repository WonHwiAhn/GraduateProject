package com.example.ahn.finalproject.CapsulePrivate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.ahn.finalproject.MainLogin.R;

/**
 * Created by Ahn on 2017-04-01.
 */

public class CapsulePrivateMain extends Fragment {
    //private OnGetFromUserClickListener mListener;
    static EditText editText;
    public CapsulePrivateMain(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.capsule_private_main, container, false);
        editText = (EditText) rootView.findViewById(R.id.capsulePrivateLetter);
        return rootView;
    }

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGetFromUserClickListener ) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnGetFromUserClickListener");
        }
    }

    public void getFromUser(View v) {
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("@@@", "들어왔어!!!!!!!!!!!!");
            }
        });
        if (mListener != null) {
            EditText edit = (EditText) v.findViewById(R.id.capsulePrivateLetter);
            Log.d("@@@", edit.getText().toString());
        }
    }

    public interface OnGetFromUserClickListener {
        void getFromUser(String message);
    }*/
}
