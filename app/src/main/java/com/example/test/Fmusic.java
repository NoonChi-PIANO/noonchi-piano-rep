package com.example.test;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.request.SimpleMultiPartRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fmusic#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fmusic extends Fragment {

    EditText etName,etMsg;
    ImageView iv;

    //업로드할 이미지의 절대경로(실제 경로)
    String imgPath;

    String folderNAME = "/storage/self/primary/PIANO";
    String saveFileName = "butterFly.txt";



    Button uploadimg;
    public Fmusic() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Fmusic newInstance() {
        Fmusic fragment = new Fmusic();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_fmusic, container, false);
        uploadimg = (Button)rootView.findViewById(R.id.uploadimg);



        uploadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //여기에 코드 갤러리 선택, 서버연동 코드 들어감
                Intent myintent = new Intent(getActivity(),Galary.class);


                getActivity().startActivity(myintent);
            }

        });
        //
        //
        return rootView;
        // return inflater.inflate(R.layout.fragment_fmusic, container, false);
    }




}
