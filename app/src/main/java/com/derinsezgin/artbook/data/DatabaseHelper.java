package com.derinsezgin.artbook.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME="ARTBOOKS";
    private static final String DATABASE_NAME="BOOK";
    private static final int DATABASE_VERSION=1;

    private static final String TITLE="title";
    private static final String WRITER="writer";
    private static final String YEAR="year";
    private static final String IMAGE="image";

    SQLiteDatabase sqLiteDatabase;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" (id INTEGER PRIMARY KEY AUTOINCREMENT,"+TITLE+" TEXT,"+WRITER+" TEXT, "+YEAR+" TEXT, "+IMAGE+" TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE İF EXIST "+TABLE_NAME);
        this.onCreate(db);
    }
    public void deleteData(String title){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,TITLE + "=?",new String[]{String.valueOf(title)});
        db.close();
    }
    public void addData(Books model){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(TITLE,model.getTitle().trim());
        cv.put(WRITER,model.getWriterName());
        cv.put(YEAR,model.getYear());
        cv.put(IMAGE,model.getImage());

        long r=db.insert(TABLE_NAME,null,cv);
        if(r>-1)
            Log.i("TAG","Op. Success");
        else
            Log.e("TAG","Op. Error");
        db.close();
    }

    public List<Books> showList(){
        List<Books> list= new ArrayList<>();
        SQLiteDatabase db=this.getWritableDatabase();

        String[] row={TITLE,WRITER,YEAR,IMAGE};
        Cursor cr=db.query(TABLE_NAME,row,null,null,null,null,null);

        while(cr.moveToNext()){
            Books model = new Books();
            model.setTitle(cr.getString(0));
            model.setWriterName(cr.getString(1));
            model.setYear(cr.getString(2));
            model.setImage(cr.getString(3));
            list.add(model);
        }
        return list;
    }
}
