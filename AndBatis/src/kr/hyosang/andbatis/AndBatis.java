package kr.hyosang.andbatis;

import java.util.HashMap;

import android.content.Context;

public class AndBatis {
    private static AndBatis mInstance = null;
    
    public static SqlMap getInstance(Context context, int sqlmapRawResource) throws AndBatisException {
        if(mInstance == null) {
            mInstance = new AndBatis(context);
        }
        
        return mInstance.getSqlmap(sqlmapRawResource);
    }
    
    private Context mContext = null;
    private HashMap<Integer, SqlMap> mSqlmapCache = null;
    
    private AndBatis(Context context) {
        mContext = context;
        mSqlmapCache = new HashMap<Integer, SqlMap>();
    }
    
    private SqlMap getSqlmap(int sqlmapRes) throws AndBatisException {
        SqlMap s = mSqlmapCache.get(sqlmapRes);
        
        if(s == null) {
            //create new instance
            s = SqlMapLoader.load(mContext, mContext.getResources().getXml(sqlmapRes));
            
            mSqlmapCache.put(sqlmapRes, s);
        }
        
        
        return s;
    }

}
