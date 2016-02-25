package com.changhong.localmedia;

public class Utils {

	private Utils(){		
	}
	/**
	 * ����ʱ��������[0,range)�������
	 * @param range
	 * @return
	 */
	public static int getRamdomInt(int range){
		int result=0;
		result = (int) (System.currentTimeMillis()%range);	
		return result;
	}
	
	/**
	 * ������ת����HH:MM:SS����ʽ
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
	   * desc:��16���Ƶ�����תΪ����
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
	      int int_ch;  // ��λ16������ת�����10������
	      char hex_char1 = hexString.charAt(i); ////��λ16�������еĵ�һλ(��λ*16)
	      int int_ch1;
	      if(hex_char1 >= '0' && hex_char1 <='9')
	        int_ch1 = (hex_char1-48)*16;   //// 0 ��Ascll - 48
	      else if(hex_char1 >= 'A' && hex_char1 <='F')
	        int_ch1 = (hex_char1-55)*16; //// A ��Ascll - 65
	      else
	        return null;
	      i++;
	      char hex_char2 = hexString.charAt(i); ///��λ16�������еĵڶ�λ(��λ)
	      int int_ch2;
	      if(hex_char2 >= '0' && hex_char2 <='9')
	        int_ch2 = (hex_char2-48); //// 0 ��Ascll - 48
	      else if(hex_char2 >= 'A' && hex_char2 <='F')
	        int_ch2 = hex_char2-55; //// A ��Ascll - 65
	      else
	        return null;
	      int_ch = int_ch1+int_ch2;
	      retData[i/2]=(byte) int_ch;//��ת�����������Byte��
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
