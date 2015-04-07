package kr.hyosang.andbatis;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.hyosang.andbatis.data.Column;
import kr.hyosang.andbatis.data.Statement;
import kr.hyosang.andbatis.data.Table;
import kr.hyosang.andbatis.data.SqlResultSet;
import kr.hyosang.andbatis.util.Logger;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlMap {
    private SqlHelper mHelper = null;
    private String mFilename = null;
    private int mVersion = 0;
    private List<Table> mCreateTables = new ArrayList<Table>();
    private HashMap<String, Statement> mStatements = new HashMap<String, Statement>();
    
    protected void setFilename(String fn) {
        mFilename = fn;
    }
    
    protected void setVersion(int v) {
        mVersion = v;
    }
    
    protected void setTables(List<Table> t) {
        mCreateTables = t;
    }
    
    protected void addStatement(Statement stmt) throws AndBatisException {
        if(mStatements.containsKey(stmt.id)) {
            throw new AndBatisException("Already defined statement id=" + stmt.id);
        }else {
            mStatements.put(stmt.id, stmt);
        }
    }
    
    protected void open(Context context) throws AndBatisException {
        if(mFilename == null) {
            throw new AndBatisException("No database filename");
        }
        
        mHelper = new SqlHelper(context, mFilename, null, mVersion);
        
        //onCreate() 실행을 위해
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.rawQuery("SELECT 1", null);
        db.releaseReference();
    }
    
    public SqlResultSet select(String sqlId, Map<String, Object> params) throws AndBatisException {
        String query = buildQuery(sqlId, params);
        
        SQLiteDatabase db = mHelper.getReadableDatabase();
        
        Cursor c = db.rawQuery(query, null);
        
        SqlResultSet result = new SqlResultSet(c);
        
        c.close();
        
        db.releaseReference();
        
        return result;
        
    }
    
    public void insert(String tableName, Object dataHolder) throws AndBatisException {
        Class<?> holderCls = dataHolder.getClass();
        
        if(tableName == null) {
            throw new AndBatisException("Insert table name is null");
        }
        
        Table table = null;
        
        //find table class
        for(Table t : mCreateTables) {
            if(tableName.equals(t.mName)) {
                table = t;
                break;
            }
        }
        
        if(table == null) {
            throw new AndBatisException("Table [" + tableName + "] is not declared in this sqlmap. Please declare table in create-table node");
        }
        
        ContentValues values = new ContentValues();
        
        Field[] fields = holderCls.getDeclaredFields();
        for(Field f : fields) {
            String fn = f.getName();
            if(!fn.equals("this$0")) {
                Column c = table.getColumnByName(fn);
                if(c != null) {
                    Object obj = null;
                    
                    try {
                        obj = f.get(dataHolder);
                    
                        if(obj instanceof String) {
                            values.put(fn, (String)obj);
                        }else if(obj instanceof Integer) {
                            values.put(fn, (Integer)obj);
                        }else if(obj instanceof Float) {
                            values.put(fn, (Float) obj);
                        }else if(obj instanceof Double) {
                            values.put(fn, (Double) obj);
                        }else if(obj instanceof Long) {
                            values.put(fn, (Long) obj);
                        }else {
                            //그 외에는 toString()...
                            Logger.i("Cannot determine type of " + fn +". Sets as string...");
                            values.put(fn, obj.toString());
                        }
                    }catch(IllegalAccessException e) {
                        //직접 접근 불가. getter 이용
                        String getterName = "get" + fn.substring(0, 1).toUpperCase() + fn.substring(1);
                        Method getter = null;
                        
                        try { getter = holderCls.getMethod(getterName); }catch(NoSuchMethodException ee) { }
                        
                        if(getter == null) {
                            Logger.v("Field " + fn + " is private or cannot found getter (type=" + c.mType + ")");
                        }else {
                            try {
                                switch(c.mType) {
                                case BLOB:
                                    //???????
                                    break;
                                    
                                case INTEGER:
                                    values.put(fn, (Integer)getter.invoke(dataHolder));
                                    break;
                                    
                                case NUMERIC:
                                case REAL:
                                    values.put(fn, (Double) getter.invoke(dataHolder));
                                    break;
                                    
                                case TEXT:
                                    values.put(fn, (String) getter.invoke(dataHolder));
                                    break;
                                }
                            }catch(InvocationTargetException ee) {
                                Logger.w(ee);
                            }catch(IllegalAccessException ee) {
                                Logger.w(ee);
                            }
                        }
                    }
                }
            }
        }
        
        mHelper.insert(tableName, values);
    }
    
    private String buildQuery(String sqlId, Map<String, Object> params) throws AndBatisException {
        Statement stmt = mStatements.get(sqlId);
        
        
        
        return stmt.build(params);
    }
    
    
    private class SqlHelper extends SQLiteOpenHelper {
        public SqlHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Logger.v("Creating tables...");
            
            for(Table t : mCreateTables) {
                String query = t.getCreateStatement();
                db.execSQL(query);
                
                Logger.v(query);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            
        }
        
        private void insert(String tableName, ContentValues values) {
            SQLiteDatabase db = getWritableDatabase();
            
            long rowid = db.insert(tableName, null, values);
            
            db.releaseReference();
            
            Logger.d("INSERTED ROWID = " + rowid);
        }
    }
}
