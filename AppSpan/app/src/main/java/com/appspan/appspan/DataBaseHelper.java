package com.appspan.appspan;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{
    private static final String databaseName = "Limits.db";
    private static final String tableName = "minutestable";
    private static final String column_1 = "package";
    private static final String column_2 = "minutes";


    public DataBaseHelper(Context context) {
        super(context, databaseName, null, 20);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE  "+tableName+" ("+column_1+" text primary key, "+column_2+" text ); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    public void addLimit(String pkg, String limit){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_1, pkg);
        contentValues.put(column_2, limit);
        Long result = database.insert(tableName, null, contentValues);
        Log.wtf("INSERT", String.valueOf(result));
    }

    public Cursor getAll(){ //delete
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + tableName , null );
        return  cursor;
    }

    public String getLimit(String pkg){//aggiungere controllo su cursore vuoto
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select minutes from " + tableName + " where package='" + pkg + "';", null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex("minutes");
        return cursor.getString(index);
    }

    public void updateLimit(String pkg, String limit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_1, pkg);
        contentValues.put(column_2, limit);
        db.update(tableName, contentValues, "package = ?" , new String[] { pkg });
    }

    public void deleteLimit(String pkg){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, "package = ?" , new String[] { pkg });
    }
}
