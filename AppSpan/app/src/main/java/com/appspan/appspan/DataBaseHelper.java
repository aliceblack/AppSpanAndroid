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
        db.execSQL(" CREATE TABLE  "+tableName+" ("+column_1+" text primary key, "+column_2+" long ); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    public void addLimit(String pkg, long limit){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_1, pkg);
        contentValues.put(column_2, limit);
        Long result = database.insert(tableName, null, contentValues);
        Log.wtf("INSERT", String.valueOf(result));
    }

    public Cursor getAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + tableName , null );
        cursor.moveToFirst();
        if(cursor!=null && cursor.getCount()>0)
            {Log.wtf("DBGETALL ", String.valueOf("got result from db"));}
        else
            {Log.wtf("DBGETALL ", String.valueOf("no result from db"));}
        return  cursor;
    }

    public Long getLimit(String pkg){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select minutes from " + tableName + " where package='" + pkg + "';", null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex("minutes");
        if(cursor!=null && cursor.getCount()>0){
            return cursor.getLong(index);
        }
        return -1L;

    }

    public void updateLimit(String pkg, Long limit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_1, pkg);
        contentValues.put(column_2, limit);
        //run time string substitution in place of "?"
        db.update(tableName, contentValues, "package = ?" , new String[] { pkg });
    }

    public void deleteLimit(String pkg){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, "package = ?" , new String[] { pkg });
    }

}
