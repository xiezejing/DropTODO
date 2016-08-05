package com.benx.droptodo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/5
 *
 */

public class DBHelper extends SQLiteOpenHelper {

    /** 重要常量 */
    public final static int version = 1;
    private final static String DataBaseName = "DropTODO.db";
    public final static String Todo_Table = "Todos";
    public final static String DeletedTodo_Table = "Deletes";

    /** 建表语句 */
    public static final String CREATE_TODOS = "create table " + Todo_Table + " ("
            + " id integer primary key autoincrement,"
            + "Todo text)";
    public static final String CREATE_DELETES = "create table " + DeletedTodo_Table + " ("
            + " id integer primary key autoincrement,"
            + "Deleted text)";


    /**
     *
     * *********** 构造方法 ***********
     *
     */
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public DBHelper(Context context) {
        super(context, DataBaseName, null, version);
    }


    /**
     * 创建时调用
     *
     * @param db 目标数据库
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        db.execSQL(CREATE_TODOS);
        db.execSQL(CREATE_DELETES);

        db.setTransactionSuccessful();
        db.endTransaction();
    }


    /**
     * 数据库升级时调用（版本号不同）
     *
     * @param db 目标数据库
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
