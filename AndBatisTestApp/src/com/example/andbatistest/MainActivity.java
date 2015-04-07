package com.example.andbatistest;

import java.util.HashMap;
import java.util.Map;

import kr.hyosang.andbatis.AndBatis;
import kr.hyosang.andbatis.AndBatisException;
import kr.hyosang.andbatis.SqlMap;
import kr.hyosang.andbatis.data.SqlResultSet;
import kr.hyosang.andbatis.util.Logger;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        log("CREATED");
        
        try {
            SqlMap sql = AndBatis.getInstance(this, R.xml.sqlmap);
            Map<String, Object> params = new HashMap<String, Object>();
            
            //params.put("param1", "String d'sd parameter");
            //params.put("col1data", "Sample data");
            
            //sql.select("insert001", params);
            
            Table01Holder data = new Table01Holder("this data inserted " + System.currentTimeMillis());
            sql.insert("table01", data);
            
            SqlResultSet list = sql.select("select001", null);
            for(String [] row : list) {
                Logger.d("ROW = " + row);
            }
        }catch(AndBatisException e) {
            Logger.w(e);
        }
    }
    
    private void log(String l) {
        Log.d("TEST", l);
    }
    
    private class Table01Holder {
        private int col01;
        private String col02;
        
        public Table01Holder(String c2) {
            col02 = c2;
        }
        
        public String getCol02() {
            return col02;
        }
    }
}
