package txy.galleryhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GalleryHelper galleryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        galleryHelper = new GalleryHelper(this);
        galleryHelper.loadImages(new GalleryHelper.GalleryCallback() {
            @Override
            public void complete(List<FolderEntity> list) {
                //加载本地图片返回结果
            }
        });

        findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryHelper.openCamera(new GalleryHelper.CameraCallback() {
                    @Override
                    public void complete(String path) {
                        //拍照返回结果
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //使用拍照，必须接收Activity的onActivityResult方法
        galleryHelper.onActivityResult(requestCode, resultCode);
    }
}
