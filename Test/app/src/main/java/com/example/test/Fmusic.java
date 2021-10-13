package com.example.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.ParseError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fmusic#newInstance} factory method to
 * create an instance of getActivity() fragment.
 */
public class Fmusic extends Fragment {
    EditText etName,etMsg;
    ImageView iv;

    Button uploadB,sendB,downB,startB;

    private midiRecord md2;
    private midiRecord.RecordAudio recordTask;

    //업로드할 이미지의 절대경로(실제 경로)
    String imgPath;

    String folderNAME = "/storage/self/primary/PIANO";
    //String saveFileName = "butterFly.txt";
    String name;
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
        // Inflate the layout for getActivity() fragment
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_fmusic, container, false);
        iv=rootView.findViewById(R.id.iv); //imgae view
        uploadB=(Button)rootView.findViewById(R.id.upload);
        sendB=(Button)rootView.findViewById(R.id.send);
        downB=(Button)rootView.findViewById(R.id.download);
        startB=(Button)rootView.findViewById(R.id.gameStart);

        //동적퍼미션 작업
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int permissionResult= checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionResult== PackageManager.PERMISSION_DENIED){
                String[] permissions= new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,10);
            }
        }else{
            //cv.setVisibility(View.VISIBLE);
        }
        // testButton = (LinearLayout)rootView.findViewById(R.id.testbutton);

        uploadB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });

        sendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //안드로이드에서 보낼 데이터를 받을 php 서버 주소
                //Toast.makeText(getActivity(), imgPath, Toast.LENGTH_SHORT).show();
                String serverUrl="http://27.96.131.137/noonchi/OpenCV_PJT/insertDB.php";
                Toast.makeText(getActivity(), "악보 전송중...", Toast.LENGTH_SHORT).show();

                SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // new androidx.appcompat.app.AlertDialog.Builder(getActivity()).setMessage("응답:"+response).create().show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getActivity(), "sendBERROR", Toast.LENGTH_SHORT).show();
                    }
                });

                //요청 객체에 보낼 데이터를 추가
                smpr.addStringParam("name", name);
                //  smpr.addStringParam("msg", msg);
                //이미지 파일 추가
                smpr.addFile("img", imgPath);

                //요청객체를 서버로 보낼 우체통 같은 객체 생성
                RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
                requestQueue.add(smpr);
            }
        });

        downB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String serverUrl="http://27.96.131.137/noonchi/OpenCV_PJT/download.php";

                SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //new AlertDialog.Builder(Galary.getActivity()).setMessage("file download success!").create().show();
                                // new AlertDialog.Builder(getActivity()).setMessage("응답:\n"+response).create().show();
                                new AlertDialog.Builder(getActivity()).setMessage("다운로드 성공").create().show();

                                WriteTextFile(folderNAME,"bair.txt",response.replace("\uFEFF", ""));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), "downBERROR", Toast.LENGTH_SHORT).show();
                            }
                        }){
                    @Override //response를 UTF8로 변경해주는 소스코드
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        try {
                            String utf8String = new String(response.data, "UTF-8");
                            return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            // log error
                            return Response.error(new ParseError(e));
                        } catch (Exception e) {
                            // log error
                            return Response.error(new ParseError(e));
                        }
                    }
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return super.getParams();
                    }
                };

                RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
                requestQueue.add(smpr);
            }
        });

        startB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getActivity(),GameActivity.class);

                myintent.putExtra("select",1);

                startActivity(myintent);
            }
        });

        //
        //
        return rootView;
        // return inflater.inflate(R.layout.fragment_fmusic, container, false);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 10 :
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) //사용자가 허가 했다면
                {
                    Toast.makeText(getActivity(), "외부 메모리 읽기/쓰기 사용 가능", Toast.LENGTH_SHORT).show();

                }else{//거부했다면
                    Toast.makeText(getActivity(), "외부 메모리 읽기/쓰기 제한", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){
                    //선택한 사진의 경로(Uri)객체 얻어오기
                    Uri uri= data.getData();
                    if(uri!=null){
                        iv.setImageURI(uri);

                        //갤러리앱에서 관리하는 DB정보가 있는데, 그것이 나온다 [실제 파일 경로가 아님!!]
                        //얻어온 Uri는 Gallery앱의 DB번호임. (content://-----/2854)
                        //업로드를 하려면 이미지의 절대경로(실제 경로: file:// -------/aaa.png 이런식)가 필요함
                        //Uri -->절대경로(String)로 변환
                        imgPath= getRealPathFromUri(uri);   //임의로 만든 메소드 (절대경로를 가져오는 메소드)

                        //이미지 경로 uri 확인해보기
                        new androidx.appcompat.app.AlertDialog.Builder(getActivity()).setMessage(uri.toString()+"\n"+imgPath).create().show();
                    }

                }else
                {
                    Toast.makeText(getActivity(), "이미지 선택을 하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }//onActivityResult() ..

    //Uri -- > 절대경로로 바꿔서 리턴시켜주는 메소드
    String getRealPathFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(getActivity(), uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
    }


    public void WriteTextFile(String foldername, String filename, String contents){
        try{
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, false);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}