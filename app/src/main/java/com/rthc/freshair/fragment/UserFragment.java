package com.rthc.freshair.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rthc.freshair.R;
import com.rthc.freshair.activity.InstallDeviceActivity;
import com.rthc.freshair.activity.LoginActivity;
import com.rthc.freshair.activity.RepairListActivity;
import com.rthc.freshair.util.ImageTool;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    View view;

    ImageView imageView;

    Button logoutBtn;

    LinearLayout myReportLayout;

    LinearLayout installLayout;

    TextView accountTextView;
    TextView phoneTextView;

    SharedPreferences preferences;

    public UserFragment() {
        // Required empty public constructor
    }

    public void logoutBtnOnClick(View view) {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user, container, false);
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        initView();

        return view;
    }

    void initView() {
        imageView = (ImageView) view.findViewById(R.id.headImageView);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.user_head_img);
        imageView.setImageBitmap(toRoundCorner(b, 100));

        accountTextView = (TextView) view.findViewById(R.id.accountTextView);
        phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
        accountTextView.setText(preferences.getString("phone",""));
        phoneTextView.setText(preferences.getString("phone",""));

        logoutBtn = (Button) view.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);


        myReportLayout = (LinearLayout) view.findViewById(R.id.myReportLayout);
        myReportLayout.setOnClickListener(this);

        installLayout = (LinearLayout) view.findViewById(R.id.installLayout);
        installLayout.setOnClickListener(this);

        view.findViewById(R.id.cameraImageBtn).setOnClickListener(this);
    }


    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);


        final float roundPx = pixels;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rectF, rect.width() / 2, rect.height() / 2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logoutBtn:
                logoutBtnOnClick(this.getView());
                break;
            case R.id.myReportLayout:
                startActivity(new Intent(getActivity(), RepairListActivity.class));
                break;
            case R.id.installLayout:
                startActivity(new Intent(getActivity(), InstallDeviceActivity.class));
                break;
            case R.id.cameraImageBtn:
                dispatchTakePictureIntent();
                break;
        }
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if(imageBitmap != null) {
                Bitmap bitmap = ImageTool.centerSquareScaleBitmap(imageBitmap, imageBitmap.getWidth());
                imageView.setImageBitmap(toRoundCorner(bitmap, 100));
            }
        }
    }
}
