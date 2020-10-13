/**
 * Description: Component input restriction tool,  password encryption tool and Date acquisition tool.
 *
 *
 */
package UI;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Utility {
    /**
     * @Description: Limits the input length of the specified component
     * @param name: the name of component
     * @param length：Maximum length
     * @param e ：press key
     * @return void
     */
    public void lengthLimit(JTextField name,int length,KeyEvent e){
        String jTextField = name.getText();
        if(jTextField.indexOf(".") == -1){
            if(jTextField.length() >= length){
                e.consume();
            }
        }else{
            if(jTextField.length() >= (jTextField.indexOf(".")+3)){
                e.consume();
            }
        }
    }


/**
 * @Description: For serial number, only the numbers can be entered
 * @param name：the name of component
 * @param e ：press key
 * @return void
 */
    public void serialNumLimit(JTextField name,KeyEvent e){
        int keyChar=e.getKeyChar();
        if ((keyChar>=48 && keyChar<=57)|| keyChar == 8 ) {
        } else {
            e.consume();
        }
    }
/**
 * @Description: For For prices, only numbers and decimal can be entered
 * @param name：the name of component
 * @param e ：press key
 * @return void
 */
    public void valueLimit(JTextField name,KeyEvent e){
        int keyChar=e.getKeyChar();
        if ((keyChar>=48 && keyChar<=57)|| keyChar == 8 || keyChar == 46 ) {
        } else {
            e.consume();
        }
    }
    
/**
 *  Restrict the entry of the date text field
 * @param name : 
 * @param e :  
 * @return void
 */    
    public void dateLimit(JTextField name,KeyEvent e){
        int keyChar=e.getKeyChar();
        if ((keyChar>=48 && keyChar<=57)|| keyChar == 8 || keyChar == 45) {
        } else {
            e.consume();
        }
    }

/**
 *  Gets the current date
 * @param dayMonthYearDate :  1 for day,2 for month,3 for year,4 for date
 * @return java.lang.String
 */    
    public String currentDate(int dayMonthYearDate){
        Calendar calendar = Calendar.getInstance();
        String time = "";
        if(dayMonthYearDate == 1){
            time +=calendar.get(Calendar.DATE);
        }
        if(dayMonthYearDate == 2){
            time +=(calendar.get(Calendar.MONTH)+1);
        }
        if(dayMonthYearDate == 3){
            time +=calendar.get(Calendar.YEAR);;
        }
        if(dayMonthYearDate == 4){
            time += calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DATE);
        }
        return time;
    }

/**
 *  Restrict the entry of the account text field
 * @param name : The name of JTextField
 * @param e :  key typed
 * @return void
 */    
    public void accountIDLimit(JTextField name,KeyEvent e){
        int keyChar=e.getKeyChar();
        if ((keyChar>=48 && keyChar<=57)|| keyChar == 8||(keyChar>= 97 && keyChar<=122) || (keyChar>= 65 && keyChar<=90)) {
        } else {
            e.consume();
        }
    }

/**
 *  Separate the year and month from the date
 * @param time : date needed to be separated
 * @param monthOrYear :  1 for month, 2 for year
 * @return int
 */    
    public int getMonthAndYearFromDate(String time,int monthOrYear){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        int monthYear = 0;
        try{
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if(monthOrYear == 1){
                monthYear = calendar.get(Calendar.MONTH)+1;
            }
            if(monthOrYear == 2){
                monthYear = calendar.get(Calendar.YEAR);
            }
            return monthYear;
        }catch (ParseException e){
            e.printStackTrace();
            System.out.println("Date separation failed！");
        }
        return monthYear;
    }


    /**
     * @Description: Use to encrypt password
     * @author Qin.J https://blog.csdn.net/baidu_33615716/article/details/54583595
     * @param password : account password
     * @return java.lang.String
     */
    public  String shaEncrypt(String password) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] strTemp = password.getBytes();

            MessageDigest mdTemp = MessageDigest.getInstance("SHA-1");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}
