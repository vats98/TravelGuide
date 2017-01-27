package sujeet.traveller_guide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DBHandler  extends SQLiteOpenHelper{

    private Context context;

    private static final int VERSION = 2;
    private static final String DATABASE = "MyDB";
    private static final String TABLE = "Places";
    public static final String COL0 = "Id";
    public static final String COL1 = "Name";
    public static final String COL2 = "Address";
    public static final String COL3 = "Type";
    public static final String COL4 = "Lat";
    public static final String COL5 = "Lon";
    public static final String COLS[] = {COL0, COL1, COL2, COL3, COL4, COL5};

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, DATABASE, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query =  "CREATE TABLE "+TABLE+
                " ( "+
                COL0+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL1+" TEXT, "+
                COL2+" TEXT, "+
                COL3+" TEXT, "+
                COL4+" REAL, "+
                COL5+" REAL "+
                " );";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String query = "DROP TABLE IF EXISTS "+TABLE+" ; ";
        db.execSQL(query);
        onCreate(db);
    }

    public void addRow(ContentValues cv) {

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE, null, cv);
        db.close();
    }

    public ArrayList<ContentValues> getRows(String type) {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cr = db.query(TABLE, COLS, COL3+" = ?", new String[]{type}, null, null, null);
        ArrayList<ContentValues> places = new ArrayList<>();
        for(cr.moveToFirst();!cr.isAfterLast();cr.moveToNext()) {
            ContentValues cv = new ContentValues();
            cv.put(COL1, cr.getString(cr.getColumnIndex(COL1)));
            cv.put(COL2, cr.getString(cr.getColumnIndex(COL2)));
            cv.put(COL4, cr.getDouble(cr.getColumnIndex(COL4)));
            cv.put(COL5, cr.getDouble(cr.getColumnIndex(COL5)));
            places.add(cv);
        }
        //Toast.makeText(context, ""+places.size(), Toast.LENGTH_SHORT).show();
        cr.close();
        db.close();
        return places;
    }

    public void deleteRow(String type) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, COL3+" = ?", new String[]{type});
        db.close();
    }
}
