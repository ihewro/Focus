package com.ihewro.focus.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private Context _context;
    private static final int VERSION = 1;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        _context = context;

    }

    public DatabaseHelper(Context context, String name) {
        this(context, name, null, VERSION);
        db = this.getReadableDatabase();
        // File file = new File(Environment.getExternalStorageDirectory()
        // + "/mypackage" + "/" + name + ".sqllite");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.disableWriteAheadLogging();
    }

    public void insertTmpData() {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("db upgrade");
    }

    // 数据库的查询函数
    public Cursor rawQuery(String sql) {
        return db.rawQuery(sql, null);
    }

    public boolean execSQL(String sql) {
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            Toast toast = Toast.makeText(_context,
                    "android.database.sqlite.SQLiteException",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.i("sqlerr_log------->", e.toString());
            Log.i("err_sql------->", sql);
            return false;
        }
        return true;
    }

    public boolean execSQL(String sql, boolean Throw) {
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            if (Throw)
                throw e;
            return false;
        }
        return true;
    }

    // 封装系统的执行sql语句的函数
    public boolean execSQL(String sql, Object[] object) {
        try {
            db.execSQL(sql, object);
        } catch (SQLException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(_context, e.getMessage(),
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        return true;
    }

    public void beginTransaction() {
        db.beginTransaction();
    }

    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        db.endTransaction();
    }

    public void close() {
        db.close();
    }
}
