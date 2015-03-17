package kr.hyosang.andbatis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.hyosang.andbatis.data.Column;
import kr.hyosang.andbatis.data.Column.ColumnType;
import kr.hyosang.andbatis.data.Statement;
import kr.hyosang.andbatis.data.Table;
import kr.hyosang.andbatis.util.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

public class SqlMapLoader extends SqlMap {
    private static final String TAG_CREATE = "default-create";
    private static final String TAG_TABLE = "table";
    private static final String TAG_COLUMN = "column";
    private static final String TAG_STATEMENT = "statement";
    
    private static final String ATTR_VERSION = "version";
    private static final String ATTR_FILE = "file";
    private static final String ATTR_ID = "id";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_KEY = "key";
    private static final String ATTR_AUTO_INCREMENT = "auto_increment";
    private static final String ATTR_DEFAULT = "default";
    
    private static final String OPT_PRIMARY = "primary";
    
    private static final String OPT_INTEGER = "integer";
    private static final String OPT_NUMERIC = "numeric";
    private static final String OPT_REAL = "real";
    private static final String OPT_TEXT = "text";
    private static final String OPT_BLOB = "blob";
            
    
    public static SqlMap load(Context context, XmlResourceParser xml) throws AndBatisException {
        SqlMap inst = null;
        
        try {
            int next;
            
            if((xml.next() == XmlPullParser.START_DOCUMENT) &&
                    (xml.next() == XmlPullParser.START_TAG)) {      //xml문서 시작 및 root tag 시작
                inst = new SqlMap();
                
                //db파일명
                String fn = xml.getAttributeValue(null, ATTR_FILE);
                if(fn == null) {
                    throw new AndBatisException("No filename attribute on root element");
                }else {
                    inst.setFilename(fn);
                    Logger.v("Database file = " + fn);
                }
                
                //db버전
                int ver = xml.getAttributeIntValue(null, ATTR_VERSION, 0);
                inst.setVersion(ver); 
                
                while( (next = xml.next()) != XmlPullParser.END_DOCUMENT) {
                    if(next == XmlPullParser.START_TAG) {
                        String name = xml.getName();
                        
                        if(TAG_STATEMENT.equals(name)) {
                            Statement stmt = loadStatement(xml);
                            inst.addStatement(stmt);
                        }else if(TAG_CREATE.equals(name)) {
                            List<Table> tables = loadCreate(xml);
                            inst.setTables(tables);
                        }
                    }
                }
                
                //xml 파싱 완료
                inst.open(context);
            }
        }catch(IOException e) {
            throw new AndBatisException("IOException : " + e.getMessage());
        }catch(XmlPullParserException e) {
            throw new AndBatisException("XmlPullParserException : " + e.getMessage());
        }
        
        return inst;
    }
    
    private static Column getAsColumn(String name, String type, String key, boolean auto_incre, String def) throws AndBatisException {
        ColumnType ct = ColumnType.TEXT;
        
        if(OPT_INTEGER.equalsIgnoreCase(type)) {
            ct = ColumnType.INTEGER;
        }else if(OPT_BLOB.equalsIgnoreCase(type)) {
            ct = ColumnType.BLOB;
        }else if(OPT_NUMERIC.equalsIgnoreCase(type)) {
            ct = ColumnType.NUMERIC;
        }else if(OPT_REAL.equalsIgnoreCase(type)) {
            ct = ColumnType.REAL;
        }else if(OPT_TEXT.equalsIgnoreCase(type)) {
            ct = ColumnType.TEXT;
        }else {
            throw new AndBatisException("Unknown column type : " + type);
        }
        
        Column c = new Column(name, ct);
        
        c.mbPrimaryKey = (OPT_PRIMARY.equalsIgnoreCase(key));
        c.mbAutoIncrement = auto_incre;
        c.mDefaultValue = def;
        
        return c;
    }
    
    private static List<Table> loadCreate(XmlResourceParser xml) throws IOException, XmlPullParserException, AndBatisException {
        ArrayList<Table> tables = new ArrayList<Table>();
        
        if(!xml.isEmptyElementTag()) {
            int n;
            String name;
            Table table = null;
            
            while(true) {
                n = xml.next();
                name = xml.getName();
                
                if(n == XmlPullParser.START_TAG) {
                    if(TAG_TABLE.equals(name) && (xml.getDepth() == 3)) {
                        String tableName = xml.getAttributeValue(null, ATTR_NAME);
                                
                        table = new Table(tableName);
                    }else if(TAG_COLUMN.equals(name) && (xml.getDepth() == 4)) {
                        if(TAG_COLUMN.equals(name)) {
                            String colname = xml.getAttributeValue(null, ATTR_NAME);
                            String type = xml.getAttributeValue(null, ATTR_TYPE);
                            String key = xml.getAttributeValue(null, ATTR_KEY);
                            boolean autoinc = xml.getAttributeBooleanValue(null, ATTR_AUTO_INCREMENT, false);
                            String defValue = xml.getAttributeValue(null, ATTR_DEFAULT);
                            
                            if(table != null) {
                                table.addColumn(getAsColumn(colname, type, key, autoinc, defValue));
                            }
                        }else {
                            throw new AndBatisException("Unacceptable element name : " + name);
                        }
                    }
                }else if((n == XmlPullParser.END_TAG) && (xml.getDepth() == 3)) {
                    if(TAG_TABLE.equals(name)) {
                        tables.add(table);
                        
                        table = null;
                    }
                }else if((n == XmlPullParser.END_TAG) && (xml.getDepth() == 2)) {
                    break;
                }
            }
        }
        
        return tables;
    }
    
    public static Statement loadStatement(XmlResourceParser xml) throws IOException, XmlPullParserException {
        Statement stmt = new Statement();
        stmt.id = xml.getAttributeValue(null, ATTR_ID);
        
        int n;
        while( (n = xml.next()) != XmlPullParser.END_TAG) {
            if(n == XmlPullParser.TEXT) {
                stmt.sql = xml.getText();
            }
        }
        
        Logger.v("Load statement [" + stmt.id + "]");
        
        return stmt;
    }
}
