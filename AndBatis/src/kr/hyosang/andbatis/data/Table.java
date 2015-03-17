package kr.hyosang.andbatis.data;

import java.util.ArrayList;

public class Table {
    public String mName = null;
    private ArrayList<Column> mCols = new ArrayList<Column>();
    
    public Table(String tableName) {
        mName = tableName;
    }   
    
    public void addColumn(Column c) {
        mCols.add(c);
    }
    
    public String getCreateStatement() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("CREATE TABLE ").append(mName);
        sb.append("(");
        
        for(int i=0;i<mCols.size();i++) {
            sb.append(mCols.get(i).getCreateStatement());
            
            if(i < (mCols.size() - 1)) {
                sb.append(",");
            }
        }
        
        sb.append(")");
        
        return sb.toString();
        
    }
    
    public Column getPrimaryKeyColumn() {
        for(Column c : mCols) {
            if(c.mbPrimaryKey) {
                return c;
            }
        }
        
        return null;
    }
    
    public Column getColumnByName(String name) {
        if(name != null) {
            for(Column c : mCols) {
                if(name.equals(c.mName)) {
                    return c;
                }
            }
        }
        
        return null;
    }       
}
