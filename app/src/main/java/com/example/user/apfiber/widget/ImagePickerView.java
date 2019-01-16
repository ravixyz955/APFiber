package com.example.user.apfiber.widget;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.example.user.apfiber.R;
import com.example.user.apfiber.adapter.ImagePickerAdapter;
import com.example.user.apfiber.utils.AWSUtils;
import com.example.user.apfiber.utils.ItemOffsetDecoration;
import com.example.user.apfiber.utils.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static com.example.user.apfiber.utils.Utils.isDownloadsDocument;
import static com.example.user.apfiber.utils.Utils.isExternalStorageDocument;
import static com.example.user.apfiber.utils.Utils.isMediaDocument;

public class ImagePickerView extends LinearLayout {

    private Context mContext;

    private TextView titleView;

    private RecyclerView recyclerView;

    private ImagePickerAdapter imageAdapter;

    public Uri fileUri;

    private ArrayList<Uri> images;

    private TransferUtility transferUtility;

    private int fileCount;

    private ImagePickerListener imagePickerListener;

    private ImageTransferListener transferListener;

    private ProgressDialog progress;

    public ImagePickerView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ImagePickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ImagePickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_image_picker, this);
        setPadding(12, 12, 12, 12);

        titleView = findViewById(R.id.image_picker_title);
        recyclerView = findViewById(R.id.image_picker_rv);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(mContext, R.dimen.list_margin);
        imageAdapter = new ImagePickerAdapter(mContext, new ArrayList<>());
        recyclerView.setAdapter(imageAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        progress = new ProgressDialog(mContext);
        progress.setMessage("Uploding ...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setCancelable(false);
        progress.setIndeterminate(false);
        progress.setMax(100);
        progress.setProgress(0);

        initializeTransferUtility();

        titleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerListener.onClick();
            }
        });
    }

    public void setTransferListener(ImageTransferListener transferListener) {
        this.transferListener = transferListener;
    }

    public void setImagePickerListener(ImagePickerListener imagePickerListener) {
        this.imagePickerListener = imagePickerListener;
    }

    private void initializeTransferUtility() {
        transferUtility = AWSUtils.getTransferUtility(mContext);
    }

    public void addImages(ArrayList<Uri> images) {
        imageAdapter.addAll(images);
        this.images = images;
    }

    public void clear() {
        imageAdapter.clear();
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public ArrayList<Uri> getImages() {
        return images;
    }

    public void uploadImages(String commentfolderName) {
        progress.show();
        try {
            if (images != null) {
                if (images.size() > 0) {
                    for (Uri uri : images) {
                        if (uri != null)
                            Log.d("DONE", "uploadImages: " + "Test Success!");
                        beginUpload(commentfolderName, getFilePath(uri));
                    }
                }
            } else {
                String path = getFilePath(fileUri);
                if (path != null)
                    Log.d("DONE", "uploadImages: " + "Test Success!");
                beginUpload(commentfolderName, path);
            }
        } catch (URISyntaxException e) {
            Toast.makeText(mContext,
                    "Unable to get the file from the given URI. See error log for details",
                    Toast.LENGTH_LONG).show();
            Log.e("FileError", "Unable to upload file from the given uri", e);
            progress.dismiss();
        }
    }

    private void beginUpload(String commentfolderName, String filePath) {
        if (filePath == null) {
            Utils.showToast(mContext, "Could not find the filepath of the selected file");
            return;
        }

        File file = new File(filePath);
        TransferObserver uploadObserver = null;
        Log.d("filename", "beginUpload: " + file.getName());
        if (filePath.contains(".pdf")) {
            uploadObserver = transferUtility.upload("apfiber-deployments-mobilehub-1066384050", commentfolderName + "/pdf/" + file.getName(), file);
        } else {
            uploadObserver = transferUtility.upload("apfiber-deployments-mobilehub-1066384050", commentfolderName + "/images/" + file.getName(), file);
        }

        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    fileCount += 1;
                    if (images != null) {
                        if (images.size() == fileCount) {
                            transferListener.onTransferComplete();
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                }
                            });
                        } else {
//                            transferListener.onProgressUpdate(fileCount * 100 / images.size());
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.setProgress(fileCount * 100 / images.size());
                                    Log.d("MSPSPSPSP", String.valueOf(fileCount * 100 / images.size()));
                                }
                            });
                        }
                    } else {
                        transferListener.onTransferComplete();
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                            }
                        });
                    }

                } else if (TransferState.FAILED == state) {
                    fileCount += 1;
                    transferListener.onTransferFailed();
                    if (images.size() == fileCount) {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                            }
                        });
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
            }

            @Override
            public void onError(int id, Exception ex) {
                transferListener.onError(ex);
                fileCount += 1;
                if (images.size() == fileCount) {
                    progress.dismiss();
                }
                ex.printStackTrace();
            }
        });
    }

    @SuppressLint("NewApi")
    private String getFilePath(Uri uri) throws URISyntaxException {
        if (uri != null) {

            final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
            String selection = null;
            String[] selectionArgs = null;
            if (needToCheckUri && DocumentsContract.isDocumentUri(mContext, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("image".equals(type)) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    selection = "_id=?";
                    selectionArgs = new String[]{
                            split[1]
                    };
                }
            }
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor = null;
                try {
                    cursor = mContext.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        } else {
            return null;
        }
    }

    public interface ImageTransferListener {
        public void onProgressUpdate(int progress);

        public void onTransferComplete();

        public void onTransferFailed();

        public void onError(Exception ex);
    }

    public interface ImagePickerListener {
        public void onClick();
    }
}
