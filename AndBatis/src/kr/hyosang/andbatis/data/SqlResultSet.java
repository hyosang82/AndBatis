package kr.hyosang.andbatis.data;

import java.util.Iterator;

import android.database.Cursor;

public class SqlResultSet implements Iterable<String[]> {
    private String [] mColumn;
    private String [][] mData;
    
    
    public SqlResultSet(Cursor c) {
        int colCount = c.getColumnCount();

        //컬럼정보 저장
        mColumn = new String[colCount];
        
        for(int i=0;i<colCount;i++) {
            mColumn[i] = c.getColumnName(i);
        }
        
        //데이터 저장
        
        if(c.moveToFirst()) {
            int rows = c.getCount();
            
            mData = new String[rows][colCount];
            
            int row = 0;
            do {
                for(int i=0;i<colCount;i++) {
                    mData[row][i] = c.getString(i);
                }
                row++;
            }while(c.moveToNext());
        }else {
            //데이터 없음
            mData = null;
        }
    }
    
    
    public int getCount() {
        if(mData == null) {
            return 0;
        }else {
            return mData.length;
        }
    }
    
    public int getColumnCount() {
        return mColumn.length;
    }
    
    public int getColumnIndex(String colName) {
        for(int i=0;i<mColumn.length;i++) {
            if(colName.equals(mColumn[i])) {
                return i;
            }
        }
        
        return -1;
    }
    
    public String getString(int row, String colName) {
        return mData[row][getColumnIndex(colName)];
    }
    
    public int getInt(int row, String colName, int defValue) {
        String val = getString(row, colName);
        
        try {
            return Integer.parseInt(val, 10);
        }catch(NumberFormatException e) {
            return defValue;
        }
        
    }


    @Override
    public Iterator<String[]> iterator() {
        return new ResultSetIterator();
    }
    
    public class ResultSetIterator implements Iterator<String[]> {
        private int mCurrIndex = -1;
        
        @Override
        public boolean hasNext() {
            return ((mCurrIndex+1) < mData.length);
        }

        @Override
        public String[] next() {
            if(hasNext()) {
                mCurrIndex++;
                return mData[mCurrIndex];
            }else {
                return null;
            }
        }

        @Override
        public void remove() {
        }
    }
}
