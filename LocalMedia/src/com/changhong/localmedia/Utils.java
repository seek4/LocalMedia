package com.changhong.localmedia;

public class Utils {

	private Utils(){		
	}
	/**
	 * 根据时间来生成[0,range)的随机数
	 * @param range
	 * @return
	 */
	public static int getRamdomInt(int range){
		int result=0;
		result = (int) (System.currentTimeMillis()%range);	
		return result;
	}
	
	/**
	 * 将毫秒转化成HH:MM:SS的形式
	 */
	public static String convertMill2HMS(long milliseconds){
		int seconds = 0;
		int minutes = 0;
		int hours = 0;
		String HMS = "";
		
		seconds = (int) ((milliseconds/1000)%60);
		minutes = (int) ((milliseconds/(1000*60))%60);
		
		
		if(hours>0){
			HMS = ""+hours+":"+formatMinuteSecond(minutes)+":"+formatMinuteSecond(seconds);
		}else {
			HMS = ""+formatMinuteSecond(minutes)+":"+formatMinuteSecond(seconds);
		}
		
		return HMS;
	}
	
	private static String formatMinuteSecond(int time){
		String formatTime = "";
		if(time>=0&&time<10){
			formatTime = "0"+time;
		}else {
			formatTime = ""+time;
		}
		return formatTime;
	}
	
	/**
	   * desc:将16进制的数据转为数组
	   * @param data
	   * @return
	   * modified:	
	   */
	  public static byte[] StringToBytes(String data){
	    String hexString=data.toUpperCase().trim();
	    if (hexString.length()%2!=0) {
	      return null;
	    }
	    byte[] retData=new byte[hexString.length()/2];
	    for(int i=0;i<hexString.length();i++)
	    {
	      int int_ch;  // 两位16进制数转化后的10进制数
	      char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
	      int int_ch1;
	      if(hex_char1 >= '0' && hex_char1 <='9')
	        int_ch1 = (hex_char1-48)*16;   //// 0 的Ascll - 48
	      else if(hex_char1 >= 'A' && hex_char1 <='F')
	        int_ch1 = (hex_char1-55)*16; //// A 的Ascll - 65
	      else
	        return null;
	      i++;
	      char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
	      int int_ch2;
	      if(hex_char2 >= '0' && hex_char2 <='9')
	        int_ch2 = (hex_char2-48); //// 0 的Ascll - 48
	      else if(hex_char2 >= 'A' && hex_char2 <='F')
	        int_ch2 = hex_char2-55; //// A 的Ascll - 65
	      else
	        return null;
	      int_ch = int_ch1+int_ch2;
	      retData[i/2]=(byte) int_ch;//将转化后的数放入Byte里
	    }
	    return retData;
	  }
	
	public static String bytesToHexString(byte[] bArray) {
	    if(bArray == null){
	      return null;
	    }
	    if(bArray.length == 0){
	      return "";
	    }
	    StringBuffer sb = new StringBuffer(bArray.length);
	    String sTemp;
	    for (int i = 0; i < bArray.length; i++) {
	      sTemp = Integer.toHexString(0xFF & bArray[i]);
	      if (sTemp.length() < 2)
	        sb.append(0);
	      sb.append(sTemp.toUpperCase());
	    }
	    return sb.toString();
	  }
	
}
