package txy.galleryhelper;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by tanxinye on 2017/3/31.
 */
public final class GalleryHelper {

    private static final int REQUEST_CODE = 0x1;

    private Activity mActivity;
    private CameraCallback cameraCallback;
    private String imagePath;

    public interface GalleryCallback {
        void complete(List<FolderEntity> medias);
    }

    public interface CameraCallback {
        void complete(String path);
    }

    public GalleryHelper(Activity activity) {
        mActivity = new WeakReference<>(activity).get();
    }

    public void loadImages(final GalleryCallback callback) {
        mActivity.getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new CursorLoader(mActivity,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media.DATA},
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_ADDED + " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                HashMap<String, FolderEntity> folderEntityHashMap = new HashMap<String, FolderEntity>();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        // 路径不存在或者文件不存在就跳过
                        File file = new File(path);
                        if (TextUtils.isEmpty(path) || !file.exists()) {
                            continue;
                        }
                        String folerPath = file.getParent();
                        FolderEntity folderEntity;
                        if (folderEntityHashMap.containsKey(folerPath)) {
                            folderEntity = folderEntityHashMap.get(folerPath);
                        } else {
                            folderEntity = new FolderEntity();
                            folderEntityHashMap.put(file.getParentFile().getName(), folderEntity);
                        }
                        folderEntity.getPaths().add(path);
                    }
                    cursor.close();
                }

                callback.complete(map2List(folderEntityHashMap));
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }

    public void openCamera(CameraCallback callback) {
        cameraCallback = callback;
        File imageStoreDir = new File(Environment.getExternalStorageDirectory(),
                "/DCIM/" + mActivity.getResources().getString(R.string.app_name));
        if (!imageStoreDir.exists()) {
            imageStoreDir.mkdir();
        }
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            String filename = String.format("IMG%s", dateFormat.format(new Date()));
            imagePath = new File(imageStoreDir, filename).getAbsolutePath();
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
            mActivity.startActivityForResult(new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE);
        }

    }

    @NonNull
    private ArrayList<FolderEntity> map2List(HashMap<String, FolderEntity> mediaBeanMap) {
        Iterator<FolderEntity> iterator = mediaBeanMap.values().iterator();
        ArrayList<FolderEntity> list = new ArrayList<FolderEntity>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        Collections.sort(list, new Comparator<FolderEntity>() {
            @Override
            public int compare(FolderEntity lhs, FolderEntity rhs) {
                return lhs.getNum() > rhs.getNum() ? 1 : -1;
            }
        });
        return list;
    }

    /**
     * 回调函数，使用拍照功能必须在Activity接收onActivityResult方法
     *
     * @param requestCode
     * @param resultCode
     */
    public void onActivityResult(int requestCode, int resultCode) {
        if (Activity.RESULT_OK == resultCode && REQUEST_CODE == requestCode) {
            if (cameraCallback != null) {
                cameraCallback.complete(imagePath);
            }
        }
    }
}
