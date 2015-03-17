package kr.hyosang.andbatis;

public class AndBatisException extends Exception {

    /**
     * generated
     */
    private static final long serialVersionUID = -1080807446915218093L;
    
    private String mMessage = null;
    
    
    public AndBatisException(String msg) {
        mMessage = msg;
    }
    
    @Override
    public String getMessage() {
        if(mMessage == null) {
            return super.getMessage();
        }else {
            return mMessage;
        }
    }
}
