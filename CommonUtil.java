package net.medigate.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.medigate.common.constants.CodeConstants;
import net.medigate.common.constants.Constants;
import net.medigate.common.constants.FrameWorkConstants;
import net.medigate.common.constants.SpcSubjConstants;
import net.medigate.common.manager.MenuCodeManager;
import net.medigate.repository.entity.CodeCtg;
import net.medigate.repository.entity.CodeLoc;
import net.medigate.repository.entity.CodeMaster;
import net.medigate.repository.entity.CodeMenu;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * <p>Description: DAO에서 공통으로 사용하는 process를 담은 클래스</p>
 * <p>Copyright: MediC&C</p>
 *
 * @author HSJUNG
 * @version 1.0
 */
public class CommonUtil extends AdUtil{

    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static boolean isEmpty(String value) {
    	return (value == null || "".equals(value.trim()) || value.equals("null"));
    }

    public static boolean isEmpty(int value) {
		return ( value == 0 );
	}

	public static boolean isEmpty(Object obj){
		if( obj instanceof String ) return obj==null || "".equals(obj.toString().trim());
		else if( obj instanceof List ) return obj==null || ((List)obj).isEmpty();
		else if( obj instanceof Map ) return obj==null || ((Map)obj).isEmpty();
		else if( obj instanceof Object[] ) return obj==null || Array.getLength(obj)==0;
		else return obj==null;
	}

	public static boolean isMatch(Object src, Object dst) {
		if(src == null && dst == null) return true;
		if(src == null) return false;
		if( src instanceof String && dst instanceof String ) {
			//logger.info("isMatch String/String - " + src + " / " + dst + " -> " +  src.equals(dst));
			return src.equals(dst);
		}
		else if( src instanceof String && dst instanceof Character ) {
			//logger.info("isMatch String/Character - " + src + " / " + dst.toString() + " -> " +  src.equals(dst));
			return src.equals(dst.toString());
		}
		else if( src instanceof Character && dst instanceof Character ) {
			//logger.info("isMatch Character/Character - " + src.toString() + " / " + dst.toString() + " -> " +  src.toString().equals(dst.toString()));
			return (src.toString()).equals(dst.toString());
		}
		else if( src instanceof Integer && dst instanceof Integer ) {
			//logger.info("isMatch Integer/Integer - " + src + " / " + dst + " -> " +  src.equals(dst));
			return src == dst;
		}
		return false;
	}

	public static boolean isContains(String key, String[] outParamNames){
		for (int i = 0; i < outParamNames.length; i++) {
			if(outParamNames[i] != null && outParamNames[i].equals(key)){
				return true;
			}
		}
		return false;
	}

	public static boolean isMatch(int src, int dst) {
		return src == dst;
	}


	public static boolean isEmpty(Integer value) {
		return (value == null || value == 0);
	}

	public static boolean isEmpty(Double value) {
		return (value == null || value == 0);
	}

    public static String ascToksc(String str){
  		if(str==null || str =="") return "";
  		try {
  			return new String(str.getBytes("8859_1"),"KSC5601");
		} catch (Exception e) {
			return str;
		}
  	}

    public static String kscToasc(String str){
  		try {
			return new String(str.getBytes("KSC5601"),"8859_1");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
  	}

    public static String utfToksc(String str){
  		if(str==null || str =="") return "";
	    try {
			return new String(str.getBytes("UTF-8"),"KSC5601");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
  	}

    public static boolean isMultipartType(HttpServletRequest request){
    	String contentType = request.getContentType();
        if (contentType == null || contentType.toLowerCase().indexOf("multipart/form-data") == -1) {
            return false;
        } else {
        	return true;
        }
    }

    public static boolean isValidDate(String date, String delimeter){
    	if(CommonUtil.isEmpty(date)) return false;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + delimeter + "MM" + delimeter + "dd");
        Date testDate = null;
        try {
    		testDate = sdf.parse(date);
        }
    	catch (ParseException e) {
        	return false;
        }
        return sdf.format(testDate).equals(date);
    }


    public static String getAge(String usrBirthday) {
		if(CommonUtil.isEmpty(usrBirthday) || !(usrBirthday.length() ==  4 || usrBirthday.length() ==  8 || usrBirthday.length() ==  10)){ return ""; }
		String birthYear = usrBirthday.substring(0,4);
		return String.valueOf(Integer.parseInt(DateUtil.getCurrentYear()) - Integer.parseInt(birthYear) + 1);
	}

    //내국인과 외국인 주민번호를 동시 체크
    public boolean validSid(String sid){
    	boolean isKorean = true;
    	int check = 0;

    	if( sid == null || sid.length() != 13 ) return false;
    	if( Character.getNumericValue( sid.charAt( 6 ) ) > 4 && Character.getNumericValue( sid.charAt( 6 ) ) < 9 ) {
    		isKorean = false;
    	}

    	for( int i = 0 ; i < 12 ; i++ ) {
    		if( isKorean ) check += ( ( i % 8 + 2 ) * Character.getNumericValue( sid.charAt( i ) ) );
    		else check += ( ( 9 - i % 8 ) * Character.getNumericValue( sid.charAt( i ) ) );
    	}

    	if( isKorean ) {
    		check = 11 - ( check % 11 );
    		check %= 10;
    	}else {
    		int remainder = check % 11;
    		if ( remainder == 0 ) check = 1;
    		else if ( remainder==10 ) check = 0;
    		else check = remainder;

    		int check2 = check + 2;
    		if ( check2 > 9 ) check = check2 - 10;
    		else check = check2;
    	}

    	if( check == Character.getNumericValue( sid.charAt( 12 ) ) ) return true;
    	else return false;
    }

    public static String getBirthday(String sid, String delimeter) {
    	if(isEmpty(sid)){
    		return Constants.BLANK;
    	}
    	sid = sid.replaceAll("-", "");
    	if(sid.length() < 13){
    		return Constants.BLANK;
    	}

    	String sid1 = sid.substring(0, 6);
    	String sid2 = sid.substring(6, 13);
    	String yy = sid1.substring(0, 2);        //년도
    	String mm = sid1.substring(2, 4);        //월
    	String dd = sid1.substring(4, 6);        //일
    	String gender = sid2.substring(0,1);      //성별

        // 연도 계산
        // 1 또는 2: 1900년대(주민번호)
        // 3 또는 4: 2000년대(주민번호)
        // 5 또는 6: 1900년대(외국인번호)
        // 7 또는 8: 2000년대(외국인번호)
    	String preBirthYear = "";
        if(gender.equals("1") || gender.equals("2")){
        	preBirthYear = "19";
        }
        else if(gender.equals("3") || gender.equals("4")){
        	preBirthYear = "20";
        }
        else if(gender.equals("5") || gender.equals("6")){
        	preBirthYear = "19";
        }
        else if(gender.equals("7") || gender.equals("8")){
        	preBirthYear = "20";
        }

        String birthDay = preBirthYear + yy + delimeter + mm + delimeter + dd;

        // 생년월일(YYYYMMDD) 형식
    	return isValidDate(birthDay, delimeter) ? birthDay : Constants.BLANK;
	}

    public static double round(double d){
    	return Math.round(d*10)/10.00;
    }

    public static double percent(int a, int b){
    	if(b == 0) return 0;
    	return round(((double)a/(double)b)*100);
    }

    public static double percent(long a, long b){
    	if(b == 0) return 0;
    	System.out.println("CommonUtil.percent()" + round((a/b)*100));
    	System.out.println("CommonUtil.percent()" + round(((double)a/(double)b)*100));
    	return round(((double)a/(double)b)*100);
    }

    public static double percent(String a, String b){
    	if(a == null || b == null) return 0;
    	double a1= 0;
    	double b1= 0;
    	try {
    		a1= Double.parseDouble(a);
    		b1= Double.parseDouble(b);
    		if(b1 == 0) return 0;
		} catch (Exception e) {}

    	return round((a1/b1)*100);
    }

    public static double percent(double a, double b){
    	if(b == 0) return 0;
    	return round((a/b)*100);
    }

    public static double round(double d, int intNumDigits) {
    	return Double.parseDouble(String.format("%." + Integer.toString(intNumDigits) + "f", d));
    }

    private static final Pattern HTML_SCRIPT = Pattern.compile("\\<script[^>]*?>.*?\\<\\/script\\>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_STYLE = Pattern.compile("\\<style[^>]*?>.*?\\<\\/style\\>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_OPTION = Pattern.compile("\\<option[^>]*?>.*?\\<\\/option\\>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_HEAD = Pattern.compile("\\<head\\>(.*?)\\<\\/head\\>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_TAG = Pattern.compile("\\<.*?\\>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_IMG= Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>", Pattern.CASE_INSENSITIVE);

    public static String getImgSrc(String str) {
		String result = "";
		Matcher matcher = HTML_IMG.matcher(str);
		if(matcher.find()) {
			result =matcher.group(1);
		}
		return result;
	}

    public static boolean isLimitedImg(String imgUrl, int minBytes) {
    	try{
	    	 URL url = new URL(imgUrl);
		     ByteArrayOutputStream baos = new ByteArrayOutputStream();
		     InputStream is = url.openStream();
		     byte[] b = new byte[2^16];
		     int read = is.read(b);
		     while (read>-1) {
		         baos.write(b,0,read);
		         read = is.read(b);
		     }
		     int countInBytes = baos.toByteArray().length;
		     if(countInBytes < minBytes){
		    	 //logger.info("imgUrl:"+imgUrl);
		    	 return false;
		     }
	   	}catch (Exception e) {
	   		return false;
	   		//logger.info("isLimitedImg:오류");
			//e.printStackTrace();
		}
	   	return true;
    }

    public static boolean isLimitedImg(String imgUrl) {
    	return isLimitedImg(imgUrl, 1000);
    }

    public static String getImgSrcLimitedSize(String str) {
		String result = "";
		Matcher matcher = HTML_IMG.matcher(str);
		while(matcher.find()) {
			result =matcher.group(1);
			if(isLimitedImg(result, 3000)){
				break;
			}else{
				result="";
			}
		}
		return result;
	}


    public static String getEscapeHtml(String html){
    	if(html == null) return "";
    	//return str.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("\r|\n|&nbsp;|&lt;|&gt;", " ");
    	//html = HTML_SCRIPT.matcher(html).replaceAll("").replaceAll("\r|\n|&nbsp;|&lt;|&gt;", " ");
    	html = HTML_SCRIPT.matcher(html).replaceAll("").replaceAll("\r|\n|&nbsp;", " ");
    	html = HTML_STYLE.matcher(html).replaceAll("");
    	html = HTML_OPTION.matcher(html).replaceAll("");
    	html = HTML_HEAD.matcher(html).replaceAll("");
    	html = HTML_TAG.matcher(html).replaceAll("");
    	html = html.trim();
    	return html;
    }

    public static boolean isMobile(HttpServletRequest request){
    	String ua = request.getHeader("User-Agent").toLowerCase();
    	if(ua.matches("(?i).*((android|ipad|playbook|silk|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino).*")||ua.substring(0,4).matches("(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-")){
    		return true;
    	}
    	return false;
	}

	public static String enter2Br(String value){
		return !isEmpty(value)?value.replaceAll("(\r\n|\r|\n|\n\r)", "<br/>"):"";
	}

	public static String enter2Blank(String value){
		return !isEmpty(value)?value.replaceAll("(\r\n|\r|\n|\n\r)", " "):"";
	}

	public static String enter2blank(String value){
    	return !isEmpty(value)?value.replaceAll("(\r\n|\r|\n|\n\r)", " "):"";
	}

	public static String enter2Str(String value, String dst){
    	return !isEmpty(value)?value.replaceAll("(\r\n|\r|\n|\n\r)", dst):"";
	}

	public static String br2enter(String src){
		String replaceStr ="";
		if(src.indexOf("<br>")!=-1){
			replaceStr = src.replaceAll("<br>","");
		}else{
			replaceStr = src;
		}
		return replaceStr;
	}

	public static String null2Blank(String src){
		return src==null?"": src;
	}

	public static String[] null2Blank(String[] src){
		return src==null?(new String[]{}): src;
	}

	public static String empty2Blank(String src){
		return !isEmpty(src)?src:"";
	}

	public static String empty2Str(String src, String dst){
		return !isEmpty(src)?src:dst;
	}

	public static int empty2Int(int src, int dst){
		return !isEmpty(src)?src:dst;
	}

	public static String encodeValue(String src){
		if(src == null) return "";
		try {
			return URLEncoder.encode(src, "euc-kr");
		} catch (UnsupportedEncodingException e) {
			return src;
		}
	}

	public static String encodeValue(String src, String charSet){
		if(src == null) return "";
		try {
			return URLEncoder.encode(src, charSet);
		} catch (UnsupportedEncodingException e) {
			return src;
		}
	}

	public static String decodeValue(String src){
		if(src == null) return "";
		try {
			return URLDecoder.decode(src, "euc-kr");
		} catch (UnsupportedEncodingException e) {
			return src;
		}
	}

	public static String decodeValue(String src, String charSet){
		if(src == null) return "";
		try {
			return URLDecoder.decode(src, charSet);
		} catch (UnsupportedEncodingException e) {
			return src;
		}
	}

	public static String getParams(HttpServletRequest request, String[] outParamNames) {
		return getParams(request, outParamNames, null);
	}

	public static String getParams(HttpServletRequest request, String[] outParamNames, String[] skipParamNames) {
		StringBuffer param = new StringBuffer();
		int idx = 0;
		if(outParamNames != null && outParamNames.length > 0){
			for (int i = 0; i < outParamNames.length; i++) {
				if("menuGroupCode".equals(outParamNames[i]) || "menuCode".equals(outParamNames[i])) continue;
				if(skipParamNames != null && CommonUtil.isContains(outParamNames[i], skipParamNames)) continue;
				if(idx++ > 0) param.append("&");
				//param.append(outParamNames[i]).append("=").append(!"pageNo".equals(outParamNames[i])?CommonUtil.encodeValue(request.getParameter(outParamNames[i]), "UTF-8"):CommonUtil.empty2Str(request.getParameter(outParamNames[i]), "1"));
				param.append(outParamNames[i]).append("=").append(!"pageNo".equals(outParamNames[i])?CommonUtil.encodeValue(request.getParameter(outParamNames[i])):CommonUtil.empty2Str(request.getParameter(outParamNames[i]), "1"));
			}
		}
		System.out.println("params ===> " + param.toString());
		return param.toString();
	}

	public static String getParamEncodedUrl(String url){
		if(url != null){
			url = url.trim();
			int qIdx = url.indexOf("?");
			if(qIdx != -1){
				//System.out.println("case1");
				StringBuffer sb = new StringBuffer();
				sb.append(url.substring(0, qIdx + 1));
				String paramStr = url.substring(qIdx + 1);
				if(paramStr != null && paramStr.length() > 0){
					String[] params = null;
					String delimeter = null;
					if(paramStr.indexOf("&amp;") != -1){
						delimeter = "&amp;";
					}
					else{
						delimeter = "&";
					}
					params = paramStr.split(delimeter);
					for (int i = 0; i < params.length; i++) {
						if(params[i] != null && params[i].length() > 0){
							String[] units = params[i].split("=");
							//System.out.println(units[0] + "->" + units[1]);
							if(i > 0){ sb.append("&"); }
							sb.append(units[0]).append("=").append(units.length>1?encodeValue(units[1]):"");
						}
					}
					return sb.toString();
				}
				else{
					//System.out.println("case2");
					return url;
				}
			}
			else{
				//System.out.println("case3");
				return url;
			}
		}
		return "";
	}

	public static String null2Str(String org, String dst){
		return org == null ? dst: org;
	}

	public static int null2Zero(String org){
		return org == null ? 0: Integer.parseInt(org);
	}

	public static int null2Num(String org, int dst){
		return org == null ? dst: Integer.parseInt(org);
	}

    public static String replaceStr(String src, String orgStr, String dstStr){
		if (src == null) return "";
		String dest = "";
		int  len = orgStr.length();
		int  srclen = src.length();
		int  pos = 0;
		int  oldpos = 0;

		while ((pos = src.indexOf(orgStr, oldpos)) >= 0) {
			dest += src.substring(oldpos, pos) + dstStr;
			oldpos = pos + len;
		}
		dest += src.substring(oldpos, srclen);
		return dest;
	}

    public static String filteredXml(String xml){
		if (xml == null) return "";
		String dest = "";
		dest = replaceStr(xml, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		dest = replaceStr(dest, "<statistics>", "");
		dest = replaceStr(dest, "</statistics>", "");
		dest = enter2blank(dest);
		return dest;
	}

    public static int getYear(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter =new java.text.SimpleDateFormat("yyyy", java.util.Locale.KOREA);
		return Integer.parseInt(formatter.format(date));
	}
    public static String getYYYY(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter =new java.text.SimpleDateFormat("yyyy", java.util.Locale.KOREA);
		return formatter.format(date);
	}
    public static String getMM(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter =new java.text.SimpleDateFormat("MM", java.util.Locale.KOREA);
		return formatter.format(date);
	}
    public static String getDD(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter =new java.text.SimpleDateFormat("dd", java.util.Locale.KOREA);
		return formatter.format(date);
	}

    public static String getFristDateOfWeek(){
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(new java.util.Date());
    	cal.add(Calendar.DAY_OF_WEEK, -1);
    	cal.setFirstDayOfWeek(Calendar.SUNDAY);
    	cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    	return new java.text.SimpleDateFormat("yyyyMMdd").format(cal.getTime());
    }

    public static Date getStrToDate(String inputDate) throws ParseException{
    	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
    	return formatter.parse(inputDate);
    }

    public static String getFristDateOfWeek(String inputDate) throws ParseException{
    	Date newDate = getStrToDate(inputDate);
    	//System.out.println("newDate-->" + newDate);
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(newDate);
    	cal.add(Calendar.DAY_OF_WEEK, -1);
    	cal.setFirstDayOfWeek(Calendar.SUNDAY);
    	cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    	return new java.text.SimpleDateFormat("yyyyMMdd").format(cal.getTime());
    }

    public static String getYM(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter =new java.text.SimpleDateFormat("yyyyMM", java.util.Locale.KOREA);
		return formatter.format(date);
	}

    public static String getYMD(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter =new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		return formatter.format(date);
	}

    public static int getRandom(int digitLength) {
		String num = "1";
		for (int i = 0; i < digitLength; i++) {
			num += "0";
		}
		return (int) (Math.random() * Integer.parseInt(num));
    }

    public static String getRandomPassword(int charLen, int numLen ){
        char[] charAlpha = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        char[] charNumeric = {'0','1','2','3','4','5','6','7','8','9'};
        StringBuilder sb = new StringBuilder("");
        Random rn = new Random();
        for( int i = 0; i < charLen ; i++ ){
            sb.append( charAlpha[ rn.nextInt( charAlpha.length ) ] );
        }
        for( int i = 0; i < numLen ; i++ ){
            sb.append( charNumeric[ rn.nextInt( charNumeric.length ) ] );
        }
        return sb.toString();
    }

    public static String[][] getRandArray(String[][] orgArray, int tgtArraySize){
		if(orgArray == null) return new String[0][0];
    	int orgArraySize = orgArray.length;
		if(tgtArraySize > orgArraySize) tgtArraySize = orgArraySize;

		ArrayList<Integer> list = new ArrayList<Integer>(orgArraySize);
		for(int i = 1; i <= orgArraySize; i++) {
			list.add(i);
		}

		Random rand = new Random();
		int[] randNums = new int[orgArraySize];
		int idx = 0;
		while(list.size() > 0) {
			int index = rand.nextInt(list.size());
			randNums[idx] = list.remove(index) - 1;
			System.out.println("Selected: "+ randNums[idx]);
			idx++;
		}

		String[][] dstArray = new String[tgtArraySize][2];
		for(int i = 0; i < dstArray.length; i++) {
			dstArray[i][0] = orgArray[randNums[i]][0];
			dstArray[i][1] = orgArray[randNums[i]][1];
			//System.out.println(i + "/" + dstArray[i][0] + "-->" + dstArray[i][1]);
		}
		return dstArray;
	}

    public static String getDateMillSec(String format, int digitLength) {
		DateFormat formatter = new SimpleDateFormat (format);
        return formatter.format(new java.util.Date()) + getRandom(digitLength);
    }

    public static String getDateMillSec() {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat ("yyyyMMddHHmmssSSS", java.util.Locale.KOREA);
        return formatter.format(new java.util.Date());
    }

    public static int getDateByInteger(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(sdf.format(date));
    }

    public static String getDateByString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String getDateByNonDashString(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	return sdf.format(date);
    }

    public static String getDate(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
		return formatter.format(date);
	}

    public static String getToday(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.KOREA);
		return formatter.format(date);
	}

    public static String getMonth(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.KOREA);
		return formatter.format(date);
	}

    public static String getMD(){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("M/d", java.util.Locale.KOREA);
		return formatter.format(date);
	}

    public static String getDate(String dateFormat){
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(dateFormat, java.util.Locale.KOREA);
		return formatter.format(date);
	}

    public static String getDateTime() {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat ("yyyyMMddHHmmss", java.util.Locale.KOREA);
        return formatter.format(new java.util.Date());
    }

    public static String getOnlyDateTime() {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat ("HH:mm:ss", java.util.Locale.KOREA);
        return formatter.format(new java.util.Date());
    }

    public static String getRawDateStr(String dateStr){
    	return dateStr == null ? "": replaceStr(dateStr, ".", "");
	}

    public static int getRawDateInt(String dateStr){
    	return Integer.parseInt(getRawDateStr(dateStr));
	}

    public static int getIntValue(String str){
    	return (str==null || str.equals("")) ? 0: Integer.parseInt(str);
	}

    public static int getInitValue(String str, int initVal){
    	return (str==null || str.equals("")) ? initVal: Integer.parseInt(str);
	}

    public static String getDecimalNum(int num){
		if(num < 10) return String.valueOf(0) + num;
		else return String.valueOf(num);
	}

	public static String getDateForAddYear(int addedYear){
		Calendar calendar = Calendar.getInstance(Locale.KOREA);
		calendar.add(Calendar.YEAR, addedYear);
		//calendar.add(Calendar.DATE, -1);

		String yyyy = getDecimalNum(calendar.get(Calendar.YEAR));
		String mm = getDecimalNum(calendar.get(Calendar.MONTH)+1);
		String dd = getDecimalNum(calendar.get(Calendar.DATE));

		return yyyy + mm + dd;
	}

	public static String getYearForAddYear(int addedYear){
		Calendar calendar = Calendar.getInstance(Locale.KOREA);
		calendar.add(Calendar.YEAR, addedYear);
		//calendar.add(Calendar.DATE, -1);

		String yyyy = getDecimalNum(calendar.get(Calendar.YEAR));

		return yyyy;
	}

	public static String getDateForAddMonth(int addedMonth){
		Calendar calendar = Calendar.getInstance(Locale.KOREA);
		calendar.add(Calendar.MONDAY, addedMonth);
		//calendar.add(Calendar.DATE, -1);

		String yyyy = getDecimalNum(calendar.get(Calendar.YEAR));
		String mm = getDecimalNum(calendar.get(Calendar.MONTH)+1);
		String dd = getDecimalNum(calendar.get(Calendar.DATE));

		return yyyy + mm + dd;
	}

	public static String getAddedDate(String inputDate, int addedDate){
		return getAddedDate(inputDate, addedDate, "");
	}

	public static String getAddedDate(String inputDate, int addedDate, String rtnDelimeter){
		inputDate =  inputDate.replaceAll(FrameWorkConstants.DATE_DELIMITER, "");
		Date newDate = null;
		try {
			newDate = getStrToDate(inputDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//Calendar calendar = Calendar.getInstance(Locale.KOREA);
		Calendar calendar = Calendar.getInstance();
    	calendar.setTime(newDate);
		calendar.add(Calendar.DATE, addedDate);
		//calendar.add(Calendar.DATE, -1);

		String yyyy = getDecimalNum(calendar.get(Calendar.YEAR));
		String mm = getDecimalNum(calendar.get(Calendar.MONTH)+1);
		String dd = getDecimalNum(calendar.get(Calendar.DATE));

		return yyyy + rtnDelimeter + mm + rtnDelimeter + dd;
	}

	public static String getAddedDate(int inputDate, int addedDate){
		Date newDate = null;
		try {
			newDate = getStrToDate(String.valueOf(inputDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//Calendar calendar = Calendar.getInstance(Locale.KOREA);
		Calendar calendar = Calendar.getInstance();
    	calendar.setTime(newDate);
		calendar.add(Calendar.DATE, addedDate);
		//calendar.add(Calendar.DATE, -1);

		String yyyy = getDecimalNum(calendar.get(Calendar.YEAR));
		String mm = getDecimalNum(calendar.get(Calendar.MONTH)+1);
		String dd = getDecimalNum(calendar.get(Calendar.DATE));

		return yyyy + mm + dd;
	}

	public static String getDateForAddDate(int addedDate){
		Calendar calendar = Calendar.getInstance(Locale.KOREA);
		calendar.add(Calendar.DATE, addedDate);
		//calendar.add(Calendar.DATE, -1);

		String yyyy = getDecimalNum(calendar.get(Calendar.YEAR));
		String mm = getDecimalNum(calendar.get(Calendar.MONTH)+1);
		String dd = getDecimalNum(calendar.get(Calendar.DATE));

		return yyyy + mm + dd;
	}

	public static String getDateForAddYear(String fromDate, int addedYear){
		Calendar calendar = Calendar.getInstance(Locale.KOREA);
		calendar.set(Calendar.YEAR, getIntValue(fromDate.substring(0, 4)));
		calendar.set(Calendar.MONTH, getIntValue(fromDate.substring(4, 6))-1);
		calendar.set(Calendar.DATE, getIntValue(fromDate.substring(6)));

		calendar.add(Calendar.YEAR, addedYear);
		//calendar.add(Calendar.DATE, -1);

		String yyyy = getDecimalNum(calendar.get(Calendar.YEAR));
		String mm = getDecimalNum(calendar.get(Calendar.MONTH)+1);
		String dd = getDecimalNum(calendar.get(Calendar.DATE));

		return yyyy + mm + dd;
	}

	public static Date getStrToDate(String inputDate, String dateFormat){
    	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(dateFormat, java.util.Locale.KOREA);
    	try {
			return formatter.parse(inputDate);
		} catch (ParseException e) {
			return null;
		}
    }

	public static String getFormattedDate(String inputDate, String fromDateFormat, String toDateFormat){
		if(inputDate == null) return "";
		if(fromDateFormat == null) return "";
		if(inputDate.length() != fromDateFormat.length()) return "";
		Date date = null;
		try {
			date = getStrToDate(inputDate, fromDateFormat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(date == null) return "";
		SimpleDateFormat formatter = new SimpleDateFormat(toDateFormat, java.util.Locale.KOREA);
		return formatter.format(date);
	}

    public static String getDateForAddMonth(String fromDate, int addedMonth){
		Calendar calendar = Calendar.getInstance(Locale.KOREA);
		calendar.set(Calendar.YEAR, getIntValue(fromDate.substring(0, 4)));
		calendar.set(Calendar.MONTH, getIntValue(fromDate.substring(4, 6))-1);
		calendar.set(Calendar.DATE, getIntValue(fromDate.substring(6)));

		calendar.add(Calendar.MONTH, addedMonth);
		//calendar.add(Calendar.DATE, -1);

		String yyyy = getDecimalNum(calendar.get(Calendar.YEAR));
		String mm = getDecimalNum(calendar.get(Calendar.MONTH)+1);
		String dd = getDecimalNum(calendar.get(Calendar.DATE));

		return yyyy + mm + dd;
	}

	public static String getDateForAddDate(String fromDate, int addedDate){
		Calendar calendar = Calendar.getInstance(Locale.KOREA);
		calendar.set(Calendar.YEAR, getIntValue(fromDate.substring(0, 4)));
		calendar.set(Calendar.MONTH, getIntValue(fromDate.substring(4, 6))-1);
		calendar.set(Calendar.DATE, getIntValue(fromDate.substring(6)));
		calendar.add(Calendar.DATE, addedDate);
		//calendar.add(Calendar.DATE, -1);

		String yyyy = getDecimalNum(calendar.get(Calendar.YEAR));
		String mm = getDecimalNum(calendar.get(Calendar.MONTH)+1);
		String dd = getDecimalNum(calendar.get(Calendar.DATE));

		return yyyy + mm + dd;
	}

	public static String getKrDateTime(Date date){
		if(date != null){
			Calendar cal = Calendar.getInstance();  // 현재 날짜/시간 등의 각종 정보 얻기
			cal.setTime(date);

		    int hour = cal.get(Calendar.HOUR_OF_DAY);
		    String krDate = cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1) + "월 " + cal.get(Calendar.DAY_OF_MONTH) + "일 ";
		    krDate += (hour <= 12 ? "오전 " + hour : "오후" + (hour-12)) + ":" + (cal.get(Calendar.MINUTE) < 10 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE) );
		    //System.out.println("krDate: " +  krDate);

		    return krDate;
		}
		return null;
	}

	public static int getDateDiff(String from, String dateFormat){
		Date fromDate = getStrToDate(from, dateFormat);
		//Date today = new Date();
		Date today = getStrToDate(getDate(dateFormat), dateFormat);

		// Get msec from each, and subtract.
		long diffTime = today.getTime() - fromDate.getTime();
		long diffDays = (diffTime / (1000 * 60 * 60 * 24));

		return (int)diffDays;
	}

    /*
    public static String getAddYears(String s, int year) {
        return getAddYears(s, year, "yyyyMMdd");
    }

    public static java.util.Date check(String s, String format){
		if ( s == null ) return null;
		if ( format == null ) return null;

		java.text.SimpleDateFormat formatter =
            new java.text.SimpleDateFormat (format, java.util.Locale.KOREA);
		java.util.Date date = null;
		try {
			date = formatter.parse(s);
		}
		catch(java.text.ParseException e) {
            e.printStackTrace();
		}

		if ( ! formatter.format(date).equals(s) ){
			System.out.println("Out of bound date:\"" + s + "\" with format \"" + format + "\"");
		}
        return date;
	}

    public static java.util.Date check(String s){
		return check(s, "yyyyMMdd");
	}

    public static String getAddYears(String s, int year, String format) {
 		java.text.SimpleDateFormat formatter =
		    new java.text.SimpleDateFormat (format, java.util.Locale.KOREA);
		java.util.Date date = check(s, format);
        date.setTime(date.getTime() + ((long)year * 1000 * 60 * 60 * 24 * (365)));
        return formatter.format(date);
    }
    */
    public static String getNextLibName(String prevLibName){
		String nextLibName = null;
		try{
			int cutNo = prevLibName.lastIndexOf(" ");
			if(cutNo != -1){
				int seqNo = Integer.parseInt(prevLibName.substring(cutNo).trim());
				nextLibName = prevLibName.substring(0, cutNo)+ " " + (seqNo+1);
			}
			return nextLibName;
		}catch(Exception e){
			return "";
		}
	}

    public static String getSelected(String val1, String val2){
    	return (val1 == null || val2 == null) ? "" : (val1.equals(val2)?"selected":"");
    }

    public static String getSelected(int val1, int val2){
    	return val1 == val2?"selected":"";
    }

    public static String getSelected(String val1, String[] val2){
    	if(val1 == null || val2 == null) return "";
    	for(int i = 0; i < val2.length; i++){
    		if(val2[i].length() == 4){
    			if(isMatch(val2[i].substring(0,  2), val1)){
    				return "selected";
    			}
    		}else{
    			if(isMatch(val2[i], val1)){
    				return "selected";
    			}
    		}
		}
		return "";
	}

    public static String getChecked(String val1, String val2){
    	return (val1 == null || val2 == null) ? "" : (val1.equals(val2)?"checked":"");
    }

    public static String getChecked(String val1, String[] val2){
    	if(val1 == null || val2 == null) return "";
    	for(int i = 0; i < val2.length; i++){
    		if(val2[i].length() == 4){
    			if(isMatch(val2[i].substring(0,  2), val1)){
    				return "checked";
    			}
    		}else{
    			if(isMatch(val2[i], val1)){
    				return "checked";
    			}
    		}
		}
		return "";
	}

    public static String getMatched(String val1, String val2, String out){
    	return (val1 == null || val2 == null) ? "" : (val1.equals(val2)?out:"");
    }

    public static boolean isTest(String val1, String[] val2){
    	if(val1 == null || val2 == null) return false;
    	for(int i=0; i<val2.length;i++){
			if(isMatch(val2[i], val1)){
				return true;
			}
		}
		return false;
	}

    public static boolean isContained(String val1, String[] val2){
    	if(val1 == null || val2 == null) return false;
    	for(int i=0; i<val2.length;i++){
			if(isMatch(val2[i], val1)){
				return true;
			}
		}
		return false;
	}

    public static int getIntFromCurrency(String currency){
    	if(currency==null || currency.equals("")) return 0;
    	currency = currency.replaceAll(",", "");
		return Integer.parseInt(currency);
    }

    public static long getLongFromCurrency(String currency){
    	if(currency==null) return 0;
    	return Long.parseLong(currency.replaceAll(",", ""));
    }

	public static int getCharCnt(String str) throws Exception
	{
		if(str == null) return 0;

		String sChar[] = {","};
		int cnt = 0;
		for (int j=0;  j< sChar.length; j++){
			cnt++;
		}
		return cnt;
	}

	public static String[] getSplitValues(String str, String delimeter){
		if(isEmpty(str)) return new String[0];
		return str.split(delimeter);
	}

	public static String[] getSplitValues(String str){
		if(str == null) return new String[0];
		String[] codeValues = str.split(",");
		System.out.println(codeValues.length);
		String[] rtn = new String[codeValues.length];

		for(int i=0; i<codeValues.length; i++){
			String codeValue = codeValues[i];
			String[] values = codeValue.split("#");
			rtn[i] = values[1];
			//System.out.println("value : " + rtn[i]);
		}
		return rtn;
	}

	public static String[][] getSplitCodeValues(String str){
		if(str == null) return new String[0][0];
		String[] codeValues = str.split(",");
		String[][] rtn = new String[codeValues.length][3];

		for(int i=0; i<codeValues.length; i++){
			String codeValue = codeValues[i];
			String[] values = codeValue.split("#");

			if(values!=null && values.length>0) {
				for(int j=0; j<values.length; j++) {
					rtn[i][j] = values[j];
				}
			}

			//System.out.println("code : " + rtn[i][0]);
			//System.out.println("value : " + rtn[i][1]);
		}
		return rtn;
	}

	public static String getZipCodeFormat(String str) {
		String retVal = null;
		if(str != null) {
			StringBuffer sb = new StringBuffer();
			retVal = sb.append(str.substring(0,3)).append("-").append(str.substring(3,6)).toString();
		}
		return retVal;
	}

	public static String getWorkNumFormat(String str) {
		String retVal = null;
		if(str != null) {
			StringBuffer sb = new StringBuffer();
			retVal = sb.append(str.substring(0,3)).append("-").append(str.substring(3,5)).append("-").append(str.substring(5,10)).toString();
		}
		return retVal;
	}

	public static String getCurrencyFormat(double val) {
		NumberFormat df = new DecimalFormat("#,###.##");
		return df.format(val);
	}

	public static String getCurrencyFormat(String str) {
		if(str==null || str.equals("") || str.equals("0")) return "0";
		String customStr = str.replaceAll(",", "").replaceAll(" ", "");
		try{
			long lVal = Long.parseLong(customStr);
			NumberFormat df = new DecimalFormat("#,###.##");
			return df.format(lVal);
		}
		catch(Exception e){
			return str;
		}
	}

	/**
	 * 파일 업로드 + DTO 에 변수값 세팅
	 * @param fileInfoList
	 * @return
	 */
	public static List parseUploadFiles(String fintCnt, String fileInfoList){
		if(CommonUtil.isEmpty(fintCnt) || "0".equals(fintCnt) || CommonUtil.isEmpty(fileInfoList)) return null;
		List fileList = null;
		try {
			fileList = new ArrayList();
			StringTokenizer st = new StringTokenizer(fileInfoList, "|");
			while (st.hasMoreTokens()) {
				String fileInfo = st.nextToken();
				String[] fileInfoArr = fileInfo.split(":");
				fileList.add(fileInfoArr);
	        }
			return fileList;
		} catch (Exception e) {
			//throw new ApplicationException(Message.ERROR_FILE_UPLOAD);
			return null;
		}
	}

	public static String getFileExt( String str ) {
		return (str.lastIndexOf(".")>0)?str.substring(str.lastIndexOf(".")).toLowerCase():"";
	}

	public static String getFileSize(int fileSize){
		Double d = Double.parseDouble(String.valueOf(fileSize));
		if (fileSize < 1024) {
			return CommonUtil.getCurrencyFormat(d) + "B";
		}
		else if (fileSize >= 1024 && fileSize < 1024 * 1024) {
			return CommonUtil.getCurrencyFormat(d/1024) + " KB";
		}
		else if (fileSize >= 1024 * 1024) {
			return CommonUtil.getCurrencyFormat(d/(1024*1024)) + " MB";
		}
		else{
			return CommonUtil.getCurrencyFormat(d) + "B";
		}
	}

	public static String getCurrencyFormat(Long lng) {
		if(lng.equals("0")) return "0";
		long lVal = lng.longValue();
		NumberFormat df = new DecimalFormat("#,###.##");
		//System.out.println("======>" + df.format(lVal));
		return df.format(lVal);
	}

	public static boolean removeDir(String dir){
		if(dir == null || dir.equals("")) return false;
		return removeDir(new File(dir));
	}

	public static boolean removeDir(File dir){
		if(dir == null) return false;
		if(dir.list() == null) return true; //파일이 없을 경우

		String[] list = dir.list();
	    for (int i = 0; i < list.length; i++) {
	        String s = list[i];
	        File f = new File(dir, s);
	        if (f.isDirectory()) {
	            removeDir(f);
	        } else {
	            if (!f.delete()) {
	                System.out.println("Unable to delete file "+ f.getAbsolutePath());
	                return false;
	            }
	        }
	    }
	    if (!dir.delete()) {
	    	System.out.println("Unable to delete directory "+ dir.getAbsolutePath());
	    	return false;
	    }
	    return true;
	}

	public static String getURL(HttpServletRequest request){
		String port = request.getServerPort()==80?"":":"+request.getServerPort();
		String currentUrl = request.getScheme()+"://"+request.getServerName()+ port + request.getContextPath() + request.getRequestURI();
		Enumeration urlEnum = request.getParameterNames();
		String urlParams = "";
		int idx = 0;
		String key = null;
		while(urlEnum.hasMoreElements()) {
			if(idx == 0) {
				urlParams += "?";
			}
			else{
				urlParams += "&";
			}
			key = (String)urlEnum.nextElement();
			urlParams += key + "=" + request.getParameter(key);
			idx++;
		}
		return encodeValue(currentUrl + urlParams);
	}

	public static String getAbsoluteURL(HttpServletRequest request){
		String currentUrl = request.getContextPath() + request.getRequestURI();
		Enumeration urlEnum = request.getParameterNames();
		String urlParams = "";
		int idx = 0;
		String key = null;
		while(urlEnum.hasMoreElements()) {
			if(idx == 0) {
				urlParams += "?";
			}
			else{
				urlParams += "&";
			}
			key = (String)urlEnum.nextElement();
			urlParams += key + "=" + encodeValue(CommonUtil.null2Blank(request.getParameter(key)));
			idx++;
		}
		return encodeValue(currentUrl + urlParams);
	}

	public static void go(HttpServletResponse response, String msg, String redirectUrl, String target, String popMsg, String closeFlag) {
		try {
			response.setContentType("text/html;charset=euc-kr");
			PrintWriter out = response.getWriter();
			StringBuffer sb = new StringBuffer();
			sb.append("<html>\n");
			sb.append("<body>\n");
			sb.append("<script language='javascript'>\n");
			if(!CommonUtil.isEmpty(msg)){
				sb.append(" alert('").append(msg).append("');\n");
			}
			if(!CommonUtil.isEmpty(popMsg)){
				logger.info("go popMsg : " + popMsg);
				sb.append(popMsg);
			}

			if(Constants.YES.equals(closeFlag)){
				sb.append(" if(opener){ \n");
				sb.append(" 	self.close();  \n");
				sb.append(" } else { \n");
				if(!CommonUtil.isEmpty(target)){
					sb.append("	").append(target).append(".");
				}
				if(!CommonUtil.isEmpty(redirectUrl)){
					sb.append("location.href='").append(redirectUrl).append("';\n");
				}
				else{
					sb.append("location.href='").append(Constants.URL_WWW).append("';\n");
				}
				sb.append(" } \n");
			}
			else{
				if(!CommonUtil.isEmpty(target)){
					sb.append("	").append(target).append(".");
				}
				if(!CommonUtil.isEmpty(redirectUrl)){
					sb.append("location.href='").append(redirectUrl).append("';\n");
				}
				else{
					sb.append("location.href='").append(Constants.URL_WWW).append("';\n");
				}
			}
			sb.append("</script>\n");
			sb.append("</body>\n");
			sb.append("</html>\n");
			out.print(sb.toString());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getMenuUri(String uri) {
		//logger.info("getMenuUri in - " + uri);
		if(CommonUtil.isEmpty(uri)) { return ""; }
		int slashCnt = StringUtils.countMatches(uri, "/");
		if(uri.startsWith("/") && slashCnt > 0) {
			if(slashCnt == 3) {
				uri = uri.substring(0, uri.lastIndexOf("/"));
			}
			else if(slashCnt > 3) {
				String[] arr = uri.split("/");
				uri = "/" + arr[1] + "/" + arr[2];
			}
			else {
				boolean lastSlash = (uri.length() - 1) == uri.lastIndexOf("/");
				uri = lastSlash ? uri.substring(0, uri.length() -1) : uri;
			}
		}
		//logger.info("getMenuUri out - " + uri);
		
		return uri;
	}

	public static void goPost(HttpServletResponse response, String action, Map<String, String> params, String msg, String target) {
		try {
			response.setContentType("text/html;charset=euc-kr");
			PrintWriter out = response.getWriter();
			StringBuffer sb = new StringBuffer();
			sb.append("<html>\n");
			sb.append("<body>\n");
			sb.append("<form name='tmpForm' action='").append(action).append("' method='post'>\n");

			Set<String> keys = params.keySet();
			String val = null;
			for (String key : keys){
				val = params.get(key);
				sb.append("<input type='hidden' name='").append(key).append("' value='").append(val).append("'>\n");
			}
			sb.append("</form>\n");

			sb.append("<script language='javascript'>\n");
			if(!CommonUtil.isEmpty(msg)){
				sb.append(" alert('").append(msg).append("'); \n");
			}
			if(!CommonUtil.isEmpty(target)){
				sb.append(" document.tmpForm.target = '").append(target).append("'; \n");
			}
			sb.append("	document.tmpForm.submit(); \n");
			sb.append("</script>\n");
			sb.append("</body>\n");
			sb.append("</html>\n");
			out.print(sb.toString());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getDecodeURL(HttpServletRequest request){
		return decodeValue(getURL(request));
	}

	public static boolean verifyURL(String urlStr) {
		boolean retVal = true;

		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
//			connection.setConnectTimeout(1500);
			connection.connect();
			if (connection.getResponseCode() / 100 != 2) {
				retVal = false;
			}
		} catch (Exception e) {
			retVal = false;
			e.printStackTrace();
		}
		finally{
			if(connection != null) connection.disconnect();
		}
		return retVal;
	}

	public static String getFileSizeFromUrl(String urlStr) {
		int size = 0;

		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
//			connection.setConnectTimeout(1500);
			connection.connect();
			int rtn = connection.getResponseCode();
			if (rtn == 200) {
				InputStream is = connection.getInputStream();
				size = is.available();
				if(is != null) is.close();
			}
		} catch (Exception e) {
			size = 0;
			e.printStackTrace();
		}
		finally{
			if(connection != null) connection.disconnect();
		}
		return getFileSize(size) + " / " + size;
	}

	public static String getDHMS(String seconds) {
		if(isEmpty(seconds)) return seconds;
		String formatedTime = "";
		try{
			int totalSec = Integer.parseInt(seconds);
			int day = totalSec/(60*60*24);
			int hour = (totalSec - day*60*60*24)/(60*60);
			int minute = (totalSec - day*60*60*24 - hour*3600)/60;
			int second = totalSec%60;
			//formatedTime = day + "일 " + hour + "시 " + minute + "분 " + second + "초";
			if(day > 0) formatedTime += day + "일 ";
			if(hour > 0) formatedTime += hour + "시 ";
			if(minute > 0) formatedTime += minute + "분 ";
			if(second > 0) formatedTime += second + "초";
		}
		catch(Exception e){
			formatedTime = seconds;
		}
		return formatedTime;
	}

	//baseTime Format : yyyyMMdd
	public static boolean isAfterDay(String timeStr) {
		boolean retVal = false;

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		long curTime = cal.getTimeInMillis();
		//logger.info("현재 시간 : " + curTime);
		cal.set(Integer.parseInt(timeStr.substring(0,4)),
				Integer.parseInt(timeStr.substring(4,6))-1,
				Integer.parseInt(timeStr.substring(6,8)));
		long baseTime = cal.getTimeInMillis();
		//logger.info("비교 대상  시간 : " + baseTime);
		if(baseTime>curTime) {
			retVal = true;
		}
		return retVal;
	}

	public static boolean stringArrayContains(String [] array, String item) {
		boolean retVal = false;

		if(array!=null && array.length>0) {
			for(int i=0; i<array.length; i++) {
				if(array[i].equals(item)) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	public static boolean doHttpFileDownload(String filePath, String fileName, String outFileName, HttpServletRequest request, HttpServletResponse response) {
		boolean retVal = false;
		try {
        	String encodedURL = new StringBuffer(filePath).append(fileName).toString();
        	//logger.info("filePath : " + encodedURL);

			File file = new File(encodedURL);
	        long fSize = file.length();
			if (fSize > 0 && file.isFile())
			{
				String oFileName = new String(outFileName.getBytes(),"KSC5601");

				byte b[] = new byte[1024];
				String strClient= request.getHeader("User-Agent");
		        if(strClient.indexOf("MSIE 5.5")>-1) {
					response.setHeader("Content-Disposition", "filename=" + new String(oFileName.getBytes("euc-kr"),"8859_1")+ ";");
		        } else {
					response.setHeader("Content-Disposition", "attachment;filename=" + new String(oFileName.getBytes("euc-kr"),"8859_1") + ";");
				}
				//System.out.println(oFileName);
		        BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file));
				BufferedOutputStream outs = new BufferedOutputStream(response.getOutputStream());
				int read = 0;
				try {
					while ((read = fin.read(b)) != -1){
					   outs.write(b,0,read);
					}
				} catch (Exception e) {

					logger.error("CommonUtils.doHttpFileDownload에서 Exception 발생", e);
				} finally {
					if(outs!=null) outs.close();
					if(fin!=null) fin.close();
				}
				retVal = true;
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}

		return retVal;
	}

	public static String getCurrencyFormat(int val){
		double result = Double.parseDouble(String.valueOf(val));
		NumberFormat nf = NumberFormat.getInstance();
		if(nf instanceof DecimalFormat) {
			((DecimalFormat)nf).setDecimalSeparatorAlwaysShown(false);
		}
		return nf.format((int)result);
	}

//	public void write(Document document, String xmlFileName) throws IOException {
//		// lets write to a file
//		XMLWriter writer = new XMLWriter(new FileWriter( xmlFileName ));
//		writer.write( document );
//		writer.close();
//
//		// Pretty print the document to System.out
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		writer = new XMLWriter( System.out, format );
//		writer.write( document );
//		// Compact format to System.out
//		format = OutputFormat.createCompactFormat();
//		writer = new XMLWriter( System.out, format );
//		writer.write( document );
//	}

	public static String getConstantArrValue(String[][] arrs, String value) {
		if(value == null || value.equals("")){return "";}
    	for (int i = 0; i < arrs.length; i++) {
			if(arrs[i][0].equals(value)){
				return arrs[i][1];
			}
		}
    	return "";
    }

	public static String getFileExtension( String str ) {
		return (str.lastIndexOf(".")>0)?str.substring(str.lastIndexOf(".")).toLowerCase():"";
	}

	public static String getFileName(String str) {
		return (str.lastIndexOf("/")>0)?str.substring(str.lastIndexOf("/")+1):(str.lastIndexOf("\\")>0)?str.substring(str.lastIndexOf("\\")+1):"";
	}

	public static boolean isMatch(String src, String dst) {
		if(src == null) return false;
		//if(dst == null) dst = "";
		return src.equals(dst);
	}

	public static boolean isTrue(String src) {
		if(src == null) return false;
		return src.equals(Constants.YES);
	}

	public static boolean isFalse(String src) {
		if(src == null) return false;
		return src.equals(Constants.NO);
	}

	public static boolean isMatchLike(String src, String dst) {
		if(src == null || dst == null) return false;
		return dst.indexOf(src)!=-1?true:false;
	}

	public static boolean isMaster(String userId) {
		if(userId == null) return false;
		return FrameWorkConstants.MASTER_ID.equals(userId);
	}


//	public static String getScreenTypeName(String screenType){
//		for (int i = 0; i < Constants.SCREEN_TYPE.length; i++) {
//			if(Constants.SCREEN_TYPE[i][0].equals(screenType)) return Constants.SCREEN_TYPE[i][1];
//		}
//		return "";
//	}

	public static String getCodeFromName(String name, String[][] codeList){
		for (int i = 0; i < codeList.length; i++) {
			if(codeList[i][1].equals(name)) return codeList[i][0];
		}
		return "";
	}

	public static String getCodeName(String code, String[][] codeList){
		if(isEmpty(code)) return "";
		for (int i = 0; i < codeList.length; i++) {
			if(codeList[i][0].equals(code)) return codeList[i][1];
		}
		return code;
	}

	public static String getStandardCode(String medigateCode, String[][] standardCodeList){
		if(isEmpty(medigateCode)) return "";
		for (int i = 0; i < standardCodeList.length; i++) {
			if(standardCodeList[i][0].equals(medigateCode)) return standardCodeList[i][1];
		}
		return "";
	}

	public static Object getFieldObject(String classPackage, String fieldName){
		try{
			Class clazz = Class.forName(classPackage);
			Object clzObject = clazz.newInstance();
			Field field = clazz.getField(fieldName);
			return field.get(clzObject);
		}
		catch(Exception e){
			return null;
		}
	}

	/* 세부전공 목록 */
	public static String[][] getDetailMajors(String spcCode){
		return CommonUtil.isMatch(CodeConstants.SPC_CODE_03, spcCode)?CodeConstants.IM_SPC_CODE:null;
	}

//	public static String[][] getDetailMajors(String spcCode){
//		if(isEmpty(spcCode)) return null;
//		String dtlArrCode = "SD_CODE_" + spcCode.substring(4);
//		return (String[][])getFieldObject("net.medigate.application.common.SpcDtlConstants", dtlArrCode);
//	}

	/* 진료과목 목록 */
	public static String[][] getSubjCodes(String spcCode, String spcDetailCode){
		return SpcSubjConstants.getSubjCodes(spcCode, spcDetailCode);
	}

	/* 세부 전공명 */
	public static String getDetailMajorName(String spcCode, String detailSpcCode){
		return getCodeName(detailSpcCode, getDetailMajors(spcCode));
	}

	public static String getCodeName(String code, String[] codeList){
		for (int i = 0; i < codeList.length; i++) {
			if(codeList[i].equals(code)) return codeList[i];
		}
		return "";
	}

	/**
	 * Code Master
	 */
	public static List<CodeMaster> getCodeList(HttpServletRequest request, String groupCode){
		Object obj = request.getSession().getServletContext().getAttribute(groupCode);
		//logger.info("groupCode : " + groupCode + "->" + obj);
		if(obj != null){
			return (List)obj;
		}
		return (new ArrayList<CodeMaster>());
	}

	public static String getCodeName(HttpServletRequest request, String groupCode, String code){
		if(CommonUtil.isEmpty(groupCode) || CommonUtil.isEmpty(code)){
			return null;
		}
		List<CodeMaster> codeMasterList = getCodeList(request, groupCode);
		for( CodeMaster codeMaster : codeMasterList ) {
			if(CommonUtil.isMatch(code, codeMaster.getCode())){
				return codeMaster.getCodeName();
			}
		}
		return null;
	}

	public static String getCodeFromMGCode(HttpServletRequest request, String groupCode, String mgCode){
		List<CodeMaster> codeMasterList = getCodeList(request, groupCode);
		for( CodeMaster codeMaster : codeMasterList ) {
			if(CommonUtil.isMatch(mgCode, codeMaster.getMgCode())){
				return codeMaster.getCode();
			}
		}
		return null;
	}

	/**
	 * Code Menu
	 */
	public static List<CodeMenu> getCodeMenuList(HttpServletRequest request, String groupCode){
		return MenuCodeManager.getSubMenuList(groupCode);
	}

	public static CodeMenu getCodeMenu(HttpServletRequest request, String groupCode, String code){
		List<CodeMenu> codeMenuList = getCodeMenuList(request, groupCode);
		//logger.info("--" + groupCode + "/" + code +"-------------");
		for( CodeMenu codMenu : codeMenuList ) {
			//logger.info("--" + codMenu.getMenuCode() +"-------------");
			if(CommonUtil.isMatch(code, codMenu.getMenuCode())){
				return codMenu;
			}
		}
		return null;
	}

	public static String getCodeMenuName(HttpServletRequest request, String groupCode, String code){
		if(CommonUtil.isEmpty(groupCode) || CommonUtil.isEmpty(code)){
			return null;
		}
		CodeMenu codeMenu = getCodeMenu(request, groupCode, code);
		if(codeMenu != null){
			return codeMenu.getMenuName();
		}
		return null;
	}

	/**
	 * Code Ctg
	 */
	public static List<CodeCtg> getCodeCtgList(HttpServletRequest request, String groupCode){
		Object obj = request.getSession().getServletContext().getAttribute(groupCode);
		//logger.info("groupCode : " + groupCode + "->" + obj);
		if(obj != null){
			return (List)obj;
		}
		return (new ArrayList<CodeCtg>());
	}

	public static CodeCtg getCodeCtg(HttpServletRequest request, String groupCode, String code){
		List<CodeCtg> codeCtgList = getCodeCtgList(request, groupCode);
		//logger.info("--" + groupCode + "/" + code +"-------------");
		for( CodeCtg codCtg : codeCtgList ) {
			//logger.info("--" + codCtg.getCtgCode() +"-------------");
			if(CommonUtil.isMatch(code, codCtg.getCtgCode())){
				return codCtg;
			}
		}
		return null;
	}

	public static String getCodeCtgName(HttpServletRequest request, String groupCode, String code){
		if(CommonUtil.isEmpty(groupCode) || CommonUtil.isEmpty(groupCode) || CommonUtil.isEmpty(code)){
			return "";
		}
		CodeCtg codeCtg = getCodeCtg(request, groupCode, code);
		if(codeCtg != null){
			return codeCtg.getCtgName();
		}
		return "";
	}

	/**
	 * Code Loc
	 */
	public static List<CodeLoc> getCodeLocList(HttpServletRequest request, String locCode){
		Object obj = request.getSession().getServletContext().getAttribute(locCode);
		//logger.info("locCode : " + locCode + "->" + obj);
		if(obj != null){
			return (List)obj;
		}
		return (new ArrayList<CodeLoc>());
	}

	public static CodeLoc getCodeLoc(HttpServletRequest request, String locCode, String code){
		List<CodeLoc> codeLocList = getCodeLocList(request, locCode);
		//logger.info("--" + locCode + "/" + code +"-------------");
		for( CodeLoc codLoc : codeLocList ) {
			//logger.info("--" + codLoc.getLocCode() +"-------------");
			if(CommonUtil.isMatch(code, codLoc.getLocCode())){
				return codLoc;
			}
		}
		return null;
	}

	public static String getCodeLocName(HttpServletRequest request, String locCode, String code){
		CodeLoc codeLoc = getCodeLoc(request, locCode, code);
		if(codeLoc != null){
			return codeLoc.getLocName();
		}
		return null;
	}

	public static String getImgPathOfEditor(String contents){
		String mark = Constants.EDITOR_IMG_ID;
		try{
			//에디터를 통한 이미지 업로드
			int markIdx = contents.indexOf(mark);
			if(markIdx == -1){
				return null;
			}
			String fromMark = "src=\"";
			int idx = contents.substring(markIdx).indexOf(fromMark);
			if(idx == -1){
				return null;
			}
			int fromIdx = markIdx + idx;
			String toMark = "\"";
			idx = contents.substring(fromIdx + fromMark.length()).indexOf(toMark);
			if(idx == -1){
				return null;
			}
			int toIdx = fromIdx + fromMark.length() + idx;
			String result = CommonUtil.null2Blank(contents.substring(fromIdx + fromMark.length(), toIdx));
			result = result.replaceAll(Constants.URL_IMAGE, "").replaceAll("http:", "");
			System.out.println("[editor]image path->" + result);

			return result;
		}
		catch(Exception e){
			System.out.println("CommonUtil.getImgPath Error : " + e.toString());
			return null;
		}
	}

	public static String getImgPath(String contents){
		try{
			//에디터 내 이미지 업로드
			String result = getImgPathOfEditor(contents);

			//에디터 내 이미지 복사
			if(isEmpty(result)){
				result = CommonUtil.getImageUrl(contents);
			}

			//이미지 url 길이 제한
			if(!CommonUtil.isEmpty(result) && result.length() > 200){
				result = "";
			}

			return result;
		}
		catch(Exception e){
			System.out.println("CommonUtil.getImgPath Error : " + e.toString());
			return null;
		}
	}

	public static String getUrlData(String contents, String fromMark, String toMark){
		try{
			int idx = contents.indexOf(fromMark);
			if(idx == -1){
				return null;
			}
			int fromIdx = idx;

			idx = contents.substring(fromIdx + fromMark.length()).indexOf(toMark);
			if(idx == -1){
				return contents.substring(fromIdx + fromMark.length());
			}
			int toIdx = fromIdx + fromMark.length() + idx;
			String result = contents.substring(fromIdx + fromMark.length(), toIdx);
			//System.out.println("[between data]->" + result);
			return result;
		}
		catch(Exception e){
			System.out.println("CommonUtil.getBetweenData Error : " + e.toString());
			return null;
		}
	}

	public static String getBetweenData(String contents, String fromMark, String toMark){
		try{
			int idx = contents.indexOf(fromMark);
			if(idx == -1){
				return null;
			}
			int fromIdx = idx;

			idx = contents.substring(fromIdx + fromMark.length()).indexOf(toMark);
			if(idx == -1){
				return null;
			}
			int toIdx = fromIdx + fromMark.length() + idx;
			String result = contents.substring(fromIdx + fromMark.length(), toIdx);
			//System.out.println("[between data]->" + result);
			return result;
		}
		catch(Exception e){
			System.out.println("CommonUtil.getBetweenData Error : " + e.toString());
			return null;
		}
	}

	public static String getDataFromHtml(String str, String tag) {
		if(isEmpty(str)) return null;
		org.jsoup.nodes.Document doc = Jsoup.parse(str);
		if(doc != null){
			//org.jsoup.nodes.Element image = doc.select(tag).first(); //첫번째 하나만 추출
			//return image.absUrl("src");
			org.jsoup.select.Elements elements = doc.select(tag);
			if(elements != null){
				for (org.jsoup.nodes.Element ele : elements) {
					String src = ele.absUrl("src");
					System.out.println("src : " + src);
					if(!isLimitedImg(src)) continue;
					return src;
				}
			}
		}
		return null;
	}

	public static String getImageUrl(String str) {
		return getDataFromHtml(str,"img");
	}

	public static boolean doesExistsUrl(String url){
		if(CommonUtil.isEmpty(url)) {
			return false;
		}

		if(url.startsWith("//")) {
			url = "http:" + url;
		}
		else if(!url.startsWith("http://")) {
			url = "http://" + url;
		}

		HttpURLConnection con = null;
		try {
			HttpURLConnection.setFollowRedirects(false);
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally{
			if(con != null) con.disconnect();
		}
	}

	//default
	public static String getHtml(String url) {
		try {
			return HttpClientUtil.getHtml(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	//charset 지정
	public static String getHtml(String url, String charset){
		try {
			return HttpClientUtil.getHtml(url, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getHtml(String url, String charset, String contentType){
		try {
			return HttpClientUtil.getHtml(url, charset, contentType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getHtml(String url, String charset, String contentType, int timeout){
		try {
			return HttpClientUtil.getHtml(url, charset, contentType, null, null, timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	//Cooke를 함께 전송
	public static String getHtmlWithCookie(String url, String charset, String cookieName, String cookieValue) throws Exception{
		return getHtmlWithCookie(url, charset, "text/html", cookieName, cookieValue);
	}

	//Cooke를 함께 전송
	public static String getHtmlWithCookie(String url, String charset, String contentType, String cookieName, String cookieValue) throws Exception{
		try {
			return HttpClientUtil.getHtmlWithCookie(url, charset, contentType, cookieName, cookieValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getHtmlOfHttps(String url, String params, String charset){
		try {
			return HttpClientUtil.getHtmlForHttps(url, params, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getPostHtml(String actionUrl, String params) {
		StringBuffer retVal = new StringBuffer();
		HttpURLConnection connection = null;
		PrintWriter pw = null;
		try {
			URL url = new URL(actionUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(5000);
			connection.setAllowUserInteraction(true);
			connection.connect();

			pw = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "euc-kr"));
			pw.write(params);
			pw.flush();

			int resCode = connection.getResponseCode();
			if(resCode >= 400){
				return "";
			}
		} catch (Exception e) {
			return "";
		}

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "euc-kr"));
			String inputLine;
			int i = 1;
			while ((inputLine = in.readLine()) != null) {
				retVal.append(inputLine).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				if(pw != null) pw.close();
				if(in != null) in.close();
				try {
					if(connection != null) connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {}
		}
		return retVal.toString();
	}

	public static String publishJson(Object value){
		String jsonTxt = null;
		if(value instanceof List) {
			JSONArray list = JSONArray.fromObject(value);
			jsonTxt = list.toString();
		} else {
			JSONObject obj = JSONObject.fromObject(value);
			jsonTxt = obj.toString();
		}
		return jsonTxt;
	}

	public static String parseJson(String jsonTxt, String parseKey){
		JSONObject jsonObject = JSONObject.fromObject( jsonTxt );
		if(jsonObject == null) return "";
		//String
		String parseValue = (String)jsonObject.get(parseKey);
		System.out.println("Json parse " + parseKey + " : " + parseValue);
		return parseValue;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static String parseFromJson(String url, String parseKey){
		InputStream is = null;
		try {
			is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String jsonText = readAll(rd);
			System.out.println("jsonText->" + jsonText);
			return parseJson(jsonText, parseKey);
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if(is != null){
				try { is.close(); } catch (IOException e) {}
			}
		}
		return null;
	}

	public static String getCutStr(String str, int strLen){
		if(str == null) return "";
		int len = str.length();
		if(len > strLen){
			return str.substring(0, strLen) + "..";
		}
		return str;
	}

	public static String getCutStrByBytes(String str, int byteLen){
		return getCutStrByBytes(str, 0, byteLen);
	}

	public static String getCutStrByBytes(String str, int startIndex, int byteLen){
		byte[] b1 = null;
		byte[] b2 = null;

		try{
			if (str == null){
				return "";
			}
			b1 = str.getBytes();
			System.out.println(b1.length);
			if(byteLen >= b1.length){
				return str;
			}
			b2 = new byte[byteLen];

			if (byteLen > (b1.length - startIndex)){
				byteLen = b1.length - startIndex;
			}
			System.arraycopy(b1, startIndex, b2, 0, byteLen);
		}
		catch (Exception e){
			return str;
		}
		return new String(b2);
	}

	public static String arrToStr(String[] arr) {
		return arrToStr(arr, ",");
	}

	public static String arrToStr(String[] arr, String delimeter){
		if(arr == null || arr.length == 0) return "";
		StringBuffer dst = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			dst.append(arr[i]);
			if(i < (arr.length-1)){
				dst.append(delimeter);
			}
		}
		return dst.toString();
	}

	public static String arrToStr(int[] arr){
		if(arr == null || arr.length == 0) return "";
		StringBuffer dst = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			dst.append(arr[i]);
			if(i < (arr.length-1)){
				dst.append(",");
			}
		}
		return dst.toString();
	}

	public static String[] strToArr(String str){
		if(str == null || str.equals("")) return new String[0];
		return str.split(",");
	}

	public static String[] strToArr(String str, int initSize){
		String[] arr = null;
		if(str == null || str.equals("") || str.equals(",")) {
			arr = new String[initSize];
			for(int i=0;i<initSize; i++){
				arr[i] = "";
			}
			return arr;
		}
		arr = str.split(",");
		if(arr.length < initSize){
			String[] newArr = new String[initSize];
			for(int i=0;i<arr.length; i++){
				newArr[i] = CommonUtil.null2Blank(arr[i]);
			}
			for(int i=arr.length;i<initSize; i++){
				newArr[i] = "";
			}
			return newArr;
		}
		for(int i=0;i<initSize; i++){
			arr[i] = CommonUtil.null2Blank(arr[i]);
		}
		return arr;
	}

	public static String[] strToArr(String str, String delimeter){
		if(str == null || str.equals("")) return new String[0];
		return str.split(delimeter);
	}

	public static String getFileSize(double number) {
		try{
			double size = 1024;
			if (number >= (size*size*size)) {
				return CommonUtil.getCurrencyFormat(number / (size*size*size)) + " GB";
			} else if (number >= (size * size)) {
				return CommonUtil.getCurrencyFormat(number / (size * size)) + " MB";
			} else if (number >= (size)) {
				return CommonUtil.getCurrencyFormat(number / (size)) + " KB";
			} else {
				return CommonUtil.getCurrencyFormat(number) + " byte";
			}
		}catch(Exception e){
			return "";
		}
	}

	public static void sleep(int errCount, String errMsg){
		logger.error("[Service Layer Error]" + errCount + ":" + errMsg);
		try {
			Thread.sleep(300);
		} catch (InterruptedException ie) {}
    }

	public static String[] listToArr(List list){
		//if(list == null || list.size() == 0) return null;
		if(list == null) return null;
		String[] arr = new String[list.size()];
		arr = (String[])list.toArray(arr);
		return arr;
	}

	public static List arrToList(String[] arr){
		//if(list == null || list.size() == 0) return null;
		if(arr == null) return null;
		return Arrays.asList(arr);
	}

	public static List mergeList(List mergeList, List addList){
		if(mergeList == null){
			mergeList = new ArrayList();
		}
		if(addList != null){
			mergeList.addAll(addList);
		}
		return mergeList;
	}

	public static String[] getArrEmail(String str){
		String[] arr = null;
		if(!CommonUtil.isEmpty(str) && str.indexOf("@")!= -1){
			arr = str.split("@");
			if(arr.length == 2){
				return arr;
			}
		}
		return new String[]{"",""};
	}

	public static String[] getArrPhoneNo(String str){
		String[] arr = null;
		if(!CommonUtil.isEmpty(str) && str.indexOf("-")!= -1){
			arr = str.split("-");
			if(arr.length == 3){
				return arr;
			}
		}
		return new String[]{"","",""};
	}

	public static String[] getArrSidNo(String str){
		String[] arr = null;
		if(!CommonUtil.isEmpty(str) && str.indexOf("-")!= -1){
			arr = str.split("-");
			if(arr.length == 2){
				return arr;
			}
		}
		return new String[]{"",""};
	}

	public static String[] getArrZipCode(String str){
		String[] arr = null;
		if(!CommonUtil.isEmpty(str) && str.indexOf("-")!= -1){
			arr = str.split("-");
			if(arr.length == 2){
				return arr;
			}
		}
		return new String[]{"",""};
	}

	public static String[] getArrBizNo(String str){
		String[] arr = null;
		if(!CommonUtil.isEmpty(str) && str.indexOf("-")!= -1){
			arr = str.split("-");
			if(arr.length == 3){
				return arr;
			}
		}
		return new String[]{"","",""};
	}

	//MENU_GROUP_CODE, MENU_CODE를 통해 서비스 명을 리턴한다.
	public static String getMenuName(String menuGroupCode, String menuCode, String[][][] menuList){
		return getMenuName(menuGroupCode, menuCode, menuList, "TEXT");
	}

	public static String getMenuName(String menuGroupCode, String menuCode, String[][][] menuList, String type){
		boolean isMatch = false;
		String menuName = " - ";
		for(int i = 0; i < menuList.length; i++) {
			for(int j = 0 ; j <menuList[i].length ; j++){
				if(isMatch) break;
				if(!menuList[i][j][0].equals(menuGroupCode)) {
					continue;
				}
				for(int k = 0; k < menuList[i][j].length; k++){
					//System.out.println( "[" + i + "][" + j + "][" + k + "] : " + menuList[i][j][2]);
					if(menuList[i][j][1].equals(menuCode)) {
						if("IMG".equals(type)) {
							menuName = "".equals(menuList[i][j][3]) ? menuList[i][j][2] : menuList[i][j][3];
						} else {
							menuName = menuList[i][j][2];
						}
						isMatch = true;
						break;
					}
				}
			}
		}
		return menuName;
	}

	public static String getTopUrl(String url, String topCmd){
		if(CommonUtil.isEmpty(url)){return "";}
		if(url.indexOf("cmd=") != -1){
			String listCmd = getUrlData(url, "cmd=", "&");
			if(listCmd != null){
				return url.replaceAll(listCmd, topCmd);
			}
		}
		return "";
	}

	public static String getTopUrl(String listUrl){
		return getTopUrl(listUrl, "top");
	}

	public static Map<String, String> getQueryMap(String url){
		if(isEmpty(url)) return null;
	    String paramStr = null;
	    if(url.indexOf("?") != -1){
	    	paramStr = url.substring(url.indexOf("?")+1);
	    }
	    if(paramStr == null || url.indexOf("=") == -1){ return null; }

	    Map<String, String> map = new HashMap<String, String>();
	    String[] params = paramStr.split("&");
		for (String param : params){
			String[] paramArr = param.split("=");
			if(paramArr.length != 2) continue;
	        String name = param.split("=")[0];
	        String value = param.split("=")[1];
	        map.put(name, value);
	    }
		return map;
	}

	public static String getUrlParam(String url, String param){
		Map<String, String> map = getQueryMap(url);
		if(map == null) return Constants.BLANK;
		Set<String> keys = map.keySet();
		String val = null;
		for (String key : keys){
			if(isMatch(key, param)){
				val = map.get(key);
				break;
			}
		}
		if(val != null) {
			return val;
		}
		else{
			for (String key : keys){
				if(key.lastIndexOf(param) != -1){
					val = map.get(key);
					break;
				}
			}
		}
		if("menuCode".equals(param)){
			return empty2Str(val, Constants.MENU_GROUP_CODE_MAIN);
		}
		else{
			return empty2Str(val, Constants.BLANK);
		}
	}

	public static String getValueByXpath(org.jdom.Document doc, String path) throws JDOMException {
		XPath xPath = XPath.newInstance(path);
		return xPath.valueOf(doc);
	}

	private static List getValuesByXpath(org.jdom.Document doc, String path) throws JDOMException {
		XPath xPath = XPath.newInstance(path);
		return xPath.selectNodes(doc);
	}

	private static Element getTgtElementByXpath(org.jdom.Document doc, String path) throws JDOMException {
		XPath xPath = XPath.newInstance(path);
		return (Element)xPath.selectSingleNode(doc, path);
	}

	private static String getAttributeByXpath(org.jdom.Document doc, String path) throws JDOMException {
		XPath xPath = XPath.newInstance(path);
		return ((Attribute)xPath.selectSingleNode(doc, path)).getValue();
	}

	public static org.jdom.Document getXmlDocument(String xml) throws JDOMException {
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			return saxBuilder.build(new ByteArrayInputStream(new String(xml).getBytes()));
		} catch (IOException e) {
			return null;
		}
	}

	public static org.jdom.Document getXmlDocument(String xml, String charset) throws JDOMException {
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			return saxBuilder.build(new InputStreamReader(new ByteArrayInputStream(xml.getBytes()), charset));
		} catch (IOException e) {
			return null;
		}
	}

	public static String getXmlElementByString(String xml, String xpath){
		org.jdom.Document doc = null;
		try {
			doc = getXmlDocument(xml);
			return getValueByXpath(doc, xpath);
		} catch (JDOMException e) {
			return null;
		}
	}

	public static String getXmlElementByUrl(String url, String xpath){
		org.jdom.Document doc = null;
		try {
			String xml = getHtml(url);
			doc = getXmlDocument(xml);
			return getValueByXpath(doc, xpath);
		} catch (JDOMException e) {
			return null;
		}
	}

	public static String getIndexUrl(String contentUrl){
		if(contentUrl == null) return "/";
		//String menuGroupCode = getUrlParam(contentUrl, "menuGroupCode");
		String menuCode = getUrlParam(contentUrl, "menuCode");
		String cmd = getUrlParam(contentUrl, "cmd");
		String idx = getUrlParam(contentUrl, "Idx");

		StringBuffer url = new StringBuffer();
		url.append(contentUrl.substring(0, contentUrl.lastIndexOf("/") + 1));
		url.append("?");
		url.append("menuCode=").append(menuCode);
		if(!isEmpty(cmd) && !isMatch(cmd, "list")){
			url.append("&cmd=").append(cmd);
		}
		if(!isEmpty(idx)){
			url.append("&idx=").append(idx);
		}
		return url.toString();
	}

	public static String base64Encode(String s){
		if(isEmpty(s)){return "";}
		byte[] byteArray = Base64.encodeBase64(s.getBytes());
		String encodedString = encodeValue(new String(byteArray));
		return encodedString;
	}

	public static String base64Decode(String s){
		if(isEmpty(s)){return "";}
		byte[] byteArray = Base64.decodeBase64(decodeValue(s).getBytes());
		String decocedString = new String(byteArray);
		return decocedString;
	}

	public static Object getJson(Object value) {
		try {
			if(value instanceof List) {
				JSONArray l = JSONArray.fromObject(value);
				return l;
			} else if(value instanceof String[]) {
				JSONArray l = JSONArray.fromObject(value);
				return l;
			} else if(value instanceof String[][]) {
				JSONArray l = JSONArray.fromObject(value);
				return l;
			} else {
				JSONObject m = JSONObject.fromObject(value);
				return m;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject toJson(String value) {
		try {
			JSONObject m = JSONObject.fromObject(value);
			return m;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getDelimeterDate(String str, String delimeter){
		if(isEmpty(str)) return "";
		if(str.length() == 8){
			return str.substring(0, 4) + delimeter + str.substring(4, 6) + delimeter + str.substring(6, 8);
		}
		else if(str.length() == 10){
			return str;
		}
		else{
			return str;
		}
	}

	public static String getDelimeterDate(String val){
		return getDelimeterDate(val, "-");
	}

	public static String getDelimeterDate(int val){
		return getDelimeterDate(String.valueOf(val), "-");
	}

	public static String getRawDate(String str, String delimeter){
		if(isEmpty(str)) return "";
		if(str.length() == 8){
			return str;
		}
		else if(str.length() == 10){
			return str.replaceAll(delimeter, "");
		}
		else{
			return str;
		}
	}

	public static boolean isBetweenDate(int fromDate, int toDate){
		return isBetweenDate(getDate(), fromDate, toDate);
	}

	public static boolean isBetweenDate(String someDate, int fromDate, int toDate){
		if(CommonUtil.isEmpty(someDate)) return false;
		someDate = someDate.replaceAll("-", "");
		boolean isBetweenDate = false;
		try {
			isBetweenDate = (Integer.parseInt(someDate) >= fromDate && Integer.parseInt(someDate) <= toDate);
		} catch (Exception e) {
			isBetweenDate = false;
		}
		return isBetweenDate;
	}

	public static boolean isBetweenDate(String fromDate, String toDate){
		return isBetweenDate(getDate(), fromDate, toDate);
	}

	public static boolean isBetweenDate(String someDate, String fromDate, String toDate){
		if(CommonUtil.isEmpty(someDate)) return false;
		if(CommonUtil.isEmpty(fromDate)) return false;
		if(CommonUtil.isEmpty(toDate)) return false;
		someDate = someDate.replaceAll("-", "");
		fromDate = fromDate.replaceAll("-", "");
		toDate = toDate.replaceAll("-", "");
		boolean isBetweenDate = false;
		try {
			isBetweenDate = (Integer.parseInt(someDate) >= Integer.parseInt(fromDate) && Integer.parseInt(someDate) <= Integer.parseInt(toDate));
		} catch (Exception e) {
			isBetweenDate = false;
		}
		return isBetweenDate;
	}

	public static String getImgNumber(int val){
		String sVal = Integer.toString(val);
		int lastIdx = 0;
		String imgVal = "";
		for(int i=1; i<=sVal.length();i++){
			imgVal += "<img src='http://image.medigate.net/v2/images/num_"+(sVal.substring(lastIdx, i))+".png' class='img'/>";
			lastIdx = i;
		}
		return imgVal;
	}

	public static String getSidoShortName(String fullAddress){
		if(isEmpty(fullAddress)) return "";
		if(fullAddress.indexOf(' ') == -1) return fullAddress;
		String fullSidoName = fullAddress.split(" ")[0];
		//alert("'"+fullSidoName+"'");

		if("서울특별시".equals(fullSidoName)){
			return "서울";
		}
		else if("부산광역시".equals(fullSidoName)){
			return "부산";
		}
		else if("인천광역시".equals(fullSidoName)){
			return "인천";
		}
		else if("대구광역시".equals(fullSidoName)){
			return "대구";
		}
		else if("광주광역시".equals(fullSidoName)){
			return "광주";
		}
		else if("대전광역시".equals(fullSidoName)){
			return "대전";
		}
		else if("울산광역시".equals(fullSidoName)){
			return "울산";
		}
		else if("경기도".equals(fullSidoName)){
			return  "경기";
		}
		else if("강원도".equals(fullSidoName)){
			return "강원";
		}
		else if("충청북도".equals(fullSidoName)){
			return "충북";
		}
		else if("충청남도".equals(fullSidoName)){
			return "충남";
		}
		else if("전라북도".equals(fullSidoName)){
			return "전북";
		}
		else if("전라남도".equals(fullSidoName)){
			return "전남";
		}
		else if("경상북도".equals(fullSidoName)){
			return "경북";
		}
		else if("경상남도".equals(fullSidoName)){
			return "경남";
		}
		else if("제주도".equals(fullSidoName)){
			return "제주";
		}
		else if("세종특별자치시".equals(fullSidoName)){
			return "세종";
		}
		return "";
	}

	public static String[] getPosFromAddr(String addr){

		String[] pos = new String[2];

		String endPoint = Constants.URL_WWW;
		endPoint += "/inc/map/naver_map_json.jsp";
		endPoint += "?query=" + URLEncoder.encode(addr);

		String data = CommonUtil.getHtml(endPoint, "UTF-8", "application/json", 1000);
		if(CommonUtil.isEmpty(data)){
			return new String[]{"", ""};
		}
		JSONObject jo = JSONObject.fromObject(data);
		if(jo == null) return new String[]{"", ""};
		JSONObject result = (JSONObject)jo.get("result");
		if(result == null) return new String[]{"", ""};
		JSONArray items = null;
		try{
			items = result.getJSONArray("items"); //multi item
		}
		catch(Exception ex){
			JSONObject singleItem = (JSONObject)result.get("items"); //single item
			items = new JSONArray();
			items.add(singleItem);
		}
		if(items == null || items.size() == 0) return new String[]{"", ""};

		logger.info("hiradata - " + "item size : " + items.size());

		for (Iterator it = items.iterator(); it.hasNext();) {
			JSONObject ob = (JSONObject) it.next();
			if(ob == null) return new String[]{"", ""};
			JSONObject point = (JSONObject)ob.get("point");
			if(point == null) return new String[]{"", ""};
			String pointX = point.getString("x");
			String pointY = point.getString("y");

			pos[0] = pointX;
			pos[1] = pointY;
		}
		return pos;
	}

	public static String getEscaptedHtml(String str) {
		if(str == null || "".equals(str)) return "";
		return str
				.replaceAll("</p>", "\n")
				.replaceAll("<br>", "\n")
				.replaceAll("&nbsp;", " ")
				.replaceAll("style=", "styleX=")
				.replaceAll("(?i)<(?!img|/img).*?>", "");
	}
	
	public static String getIp(String ip) {
		if(ip == null) return null;
		if(ip.indexOf(",") != -1){
			return ip.substring(0, ip.indexOf(",")).trim();
		}
		return ip;
	}
	
	public static String getRemoteAddr(HttpServletRequest request){
    	String ip = getIp(request.getHeader("X-Forwarded-For"));
    	//logger.info("ip_test 0 : " + ip);
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = getIp(request.getHeader("Proxy-Client-IP"));
	        //logger.info("ip_test 1 : " + ip);
	    }
	    else {
	    	return ip;
	    }

	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = getIp(request.getHeader("HTTP_CLIENT_IP"));
	        //logger.info("ip_test 2 : " + ip);
	    }
	    else {
	    	return ip;
	    }

	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = getIp(request.getHeader("HTTP_X_FORWARDED_FOR"));
	        //logger.info("ip_test 3 : " + ip);
	    }
	    else {
	    	return ip;
	    }

	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = getIp(request.getRemoteAddr());
	        //logger.info("ip_test 4 : " + ip);
	    }
	    else {
	    	return ip;
	    }

	    //logger.info("ip_test : " + ip);
	    return ip;
	}

	public static void sendSystemErrorMail(HttpServletRequest request, Exception e) {
		try {
			String userID = null;
			String serverHost = null;
			try {
				userID = CookieUtil.getUsrId(request);
				serverHost = InetAddress.getLocalHost().getHostName();
			} catch (Exception e1) {}

			String errMsg = e.getMessage();
			StringWriter s = new StringWriter();
	        PrintWriter p = new PrintWriter(s);
	        e.printStackTrace(p);
			String errDescription = CommonUtil.enter2Br(s.toString());
			String url = CommonUtil.getDecodeURL(request);
			
			if(errDescription != null && errDescription.indexOf("org.apache.catalina.connector.ClientAbortException: java.io.IOException") == -1 && url.indexOf("FCKeditor") == -1) {
				StringBuffer msg = new StringBuffer();
				msg.append("<font size='2'>\n");
				msg.append("<hr>\n");
				msg.append(" - Medigate System Error Description -\n");
				msg.append("<hr>\n");
				msg.append("<b>- Server : </b>").append(serverHost).append("\n");
				msg.append("<b>- Date : </b>").append(CommonUtil.getDate("yyyy.MM.dd HH:mm:ss")).append("\n");
				msg.append("<b>- URL : </b>").append(url).append("\n");
				msg.append("<b>- Referer : </b>").append(request.getHeader("referer")).append("\n");
				msg.append("<hr>\n");
				msg.append("<b>- User ID : </b>").append(userID).append("\n");
				msg.append("<b>- User IP : </b>").append(CommonUtil.getRemoteAddr(request)).append("\n");
				msg.append("<b>- User Agent : </b>").append(request.getHeader("User-Agent")).append("\n");
				msg.append("<hr>\n");
				msg.append("<b>- Message : </b>").append(errMsg).append("\n");
				msg.append("<b>- Description : </b>").append(errDescription).append("\n");
				msg.append("</font>\n");
				/*
				if(logger.isDebugEnabled()) {
					logger.debug(msg.toString());
				}
				*/
				String userAgent = CommonUtil.null2Blank(request.getHeader("User-Agent"));
				if(	userAgent.toLowerCase().indexOf("googlebot") == -1 &&
					userAgent.toLowerCase().indexOf("bingbot") == -1
				){
					//email
					try {
						EmailUtil sender = new EmailUtil();
						sender.setFromAddress("MEDIGATE-SYSTEM" + (Constants.SERVER_TYPE.equals("1")?"":"-TEST") + "<system@medicnc.co.kr>");
						sender.setToAddress("development@medicnc.co.kr");
						sender.setSubject("System-Error Message" + (Constants.SERVER_TYPE.equals("1")?"":"-TEST"));
						sender.setBody(msg.toString());
						sender.start();
						//logger.info("ThreadUtil Finish");
					} catch (Exception ex) {
						logger.error("send mail error");
					}
				}	
			}
		}
		catch (Exception ex) {
			e.printStackTrace();
		}
	}
//	public static void main(String args[]) throws ParseException {

//	}
	
	public static String numberGen(int len, int dupCd ) {
        Random rand = new Random();
        String numStr = ""; //난수가 저장될 변수
        
        for(int i=0;i<len;i++) {
            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));
            
            if(dupCd==1) {
                //중복 허용시 numStr에 append
                numStr += ran;
            }else if(dupCd==2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if(!numStr.contains(ran)) {
                    //중복된 값이 없으면 numStr에 append
                    numStr += ran;
                }else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i-=1;
                }
            }
        }
        return numStr;
    }
}
