package com.nec.lib.android.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nec.lib.android.application.MyApplication;
import com.nec.lib.android.boost.CommonProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DownloadUtil {

    private String mLocalPath;          // 客户端下载的路径名,Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Download/"
    private String mLocalFilename;      // 客户端下载的文件名
    private String mDownloadUrl;        //下载地址

    private CommonProgressDialog pBar;
    private boolean mDownloadingVisible;

    private DownloadListener mDownloadListener;

    /**下载工具类，构造后调用非静态方法showDialog()或executeDownload()*/
    public DownloadUtil(String localPath, String localFilename, String downloadUrl, boolean downloadingVisible, DownloadListener downloadListener) {
        if(!localPath.endsWith("/"))
            localPath = localPath + "/";
        this.mLocalPath = localPath;
        this.mLocalFilename = localFilename;
        this.mDownloadUrl = downloadUrl;
        this.mDownloadingVisible = downloadingVisible;
        this.mDownloadListener = downloadListener;
    }

    public interface DownloadListener {
        /**下载失败触发事件*/
        void onDownloadFail(String localFilePath, String msg);
        /**下载完成触发事件*/
        void onDownloaded(String localFilePath);
    }

    public void showDialog(String title, String msg, String btnText) {
        new android.app.AlertDialog.Builder(MyApplication.getCurrentActivity())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        executeDownload();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void executeDownload() {
        if(!mDownloadingVisible) {
            final DownloadTask downloadTask = new DownloadTask(MyApplication.getCurrentActivity());
            downloadTask.execute(mDownloadUrl);
            return;
        }
        pBar = new CommonProgressDialog(MyApplication.getCurrentActivity());
        pBar.setCanceledOnTouchOutside(false);
        pBar.setTitle("正在下载");
        //动态构造对话框Layout
        View rootView = MyApplication.getCurrentActivity().getWindow().getDecorView().getRootView();
        LinearLayout mLinearLayout = new LinearLayout(rootView.getContext());
        mLinearLayout.setGravity(Gravity.CENTER);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mLayoutParams1 = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        mLinearLayout.setLayoutParams(mLayoutParams1);
        TextView textView = new TextView(rootView.getContext());
        LinearLayout.LayoutParams mLayoutParams2 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(14);
        textView.setText("正在下载");
        mLinearLayout.addView(textView);
        //设置对话框
        pBar.setCustomTitle(mLinearLayout);
        pBar.setMessage("正在下载");
        pBar.setIndeterminate(true);
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setCancelable(true);
        // downFile(URLData.DOWNLOAD_URL);
        final DownloadTask downloadTask = new DownloadTask(MyApplication.getCurrentActivity());
        downloadTask.execute(mDownloadUrl);
        pBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error
                // report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    file = new File(mLocalPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    if (!file.exists()) {
                        // 判断父文件夹是否存在
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }
                    file = new File(file, mLocalFilename);
                } else {
                    AndroidUtil.showToastLong("存储未挂载");
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    ignored.toString();
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            if(mDownloadingVisible)
                pBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            if(mDownloadingVisible) {
                // if we get here, length is known, now set indeterminate to false
                pBar.setIndeterminate(false);
                pBar.setMax(100);
                pBar.setProgress(progress[0]);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if(mDownloadingVisible)
                pBar.dismiss();
            if (result != null) {
                Log.d(this.getClass().getName(), result);
                AndroidUtil.showToastLong("失败：" + result);
                mDownloadListener.onDownloadFail(mLocalPath + mLocalFilename, result);
            } else {
                if(mDownloadListener != null)
                    mDownloadListener.onDownloaded(mLocalPath + mLocalFilename);
            }
        }
    }

}