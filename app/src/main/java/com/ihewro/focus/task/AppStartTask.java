package com.ihewro.focus.task;

import android.os.AsyncTask;

import com.ihewro.focus.task.listener.TaskListener;

import static java.lang.Thread.sleep;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/03/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AppStartTask extends AsyncTask<Void,Integer,String> {

    TaskListener listener;
    private boolean getOnes = false;
    long beginTime;
    long endTime;

    public AppStartTask(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onFinish(s);
    }
}
