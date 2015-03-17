package kr.hyosang.andbatis.data;

import java.util.Map;

import kr.hyosang.andbatis.AndBatisException;
import kr.hyosang.andbatis.util.Util;

public class Statement {
    public String id = null;
    public String sql = null;
    
    private enum ParseState {
        QUERY, STRING, PARAMNAME
    }
    
    public String build(Map<String, Object> p) throws AndBatisException {
        StringBuffer sb = new StringBuffer();
        
        char [] chs = new char[sql.length()];
        sql.getChars(0, sql.length(), chs, 0);
        
        int idx = 0;
        String pn = "";
        ParseState state = ParseState.QUERY;
        
        while(idx < chs.length) {
            char ch = chs[idx];
            
            if(ch == '#') {
                if(state == ParseState.QUERY) {
                    //파라메터 시작
                    state = ParseState.PARAMNAME;
                    pn = "";
                }else if(state == ParseState.PARAMNAME) {
                    //파라메터 끝
                    state = ParseState.QUERY;
                    
                    if(p != null && p.containsKey(pn)) {
                        Object val = p.get(pn);
                        
                        if(val instanceof String) {
                            sb.append("'")
                            .append(Util.escapeString((String)val))
                            .append("'");
                        }else {
                            sb.append(val);
                        }
                    }else {
                        throw new AndBatisException("Parameter value not exists : " + pn);
                    }
                }else {
                    //그대로 붙임
                    sb.append(ch);
                }
            }else {
                if(state == ParseState.PARAMNAME) {
                    //파라메터명
                    pn += ch;
                }else {
                    //그 외는 쿼리에 붙임
                    sb.append(ch);
                }
            }
                            
            idx++;
        }
        
        return sb.toString();
        
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", this.id, this.sql);
    }
}
