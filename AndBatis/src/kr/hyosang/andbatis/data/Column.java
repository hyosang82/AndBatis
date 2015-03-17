package kr.hyosang.andbatis.data;

import kr.hyosang.andbatis.util.Util;

public class Column {
    public static enum ColumnType {
        INTEGER,
        NUMERIC,
        REAL,
        TEXT,
        BLOB
    };

    public String mName;
    public ColumnType mType;
    public boolean mbPrimaryKey = false;
    public boolean mbAutoIncrement = false;
    public String mDefaultValue = null;
    
    public Column(String name, ColumnType t) {
        mName = name;
        mType = t;
    }
    
    public String getCreateStatement() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(mName).append(" ");
        switch(mType) {
        case INTEGER:
            sb.append("INTEGER");
            break;
            
        case NUMERIC:
            sb.append("NUMERIC");
            break;
            
        case REAL:
            sb.append("REAL");
            break;
            
        case TEXT:
            sb.append("TEXT");
            break;
            
        case BLOB:
            sb.append("BLOB");
            break;
        }
        
        sb.append(" ");
        
        if(mbPrimaryKey) {
            sb.append("PRIMARY KEY").append(" ");
        }
        
        if(mbAutoIncrement) {
            sb.append("AUTOINCREMENT").append(" ");
        }
        
        //DEFAULTS
        if(mDefaultValue != null) {
            if(mDefaultValue.startsWith("(") && mDefaultValue.endsWith(")")) {
                //defaults as expression
                sb.append("DEFAULT ").append(mDefaultValue).append(" ");
            }else {
                switch(mType) {
                case INTEGER:
                case NUMERIC:
                case REAL:
                    sb.append("DEFAULT ").append(mDefaultValue).append(" ");
                    break;
                    
                case TEXT:
                    sb.append("DEFAULT ").append("'").append(Util.escapeString(mDefaultValue)).append("' ");
                    break;
                    
                case BLOB:
                    //??
                    break;
                }
            }
        }
                
        return sb.toString();        
    }
    
}
