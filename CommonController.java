package net.medigate.controller.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.medigate.common.constants.Constants;
import net.medigate.common.exception.ApplicationException;
import net.medigate.common.manager.CodeManager;
import net.medigate.common.manager.MenuCodeManager;
import net.medigate.common.util.CacheUtil;
import net.medigate.common.util.CommonUtil;
import net.medigate.common.util.CookieUtil;
import net.medigate.common.util.FileUploadUtil;
import net.medigate.common.util.HttpClientUtil;
import net.medigate.controller.BaseController;
import net.medigate.repository.entity.Code;
import net.medigate.repository.entity.CodeMenu;
import net.medigate.repository.entity.User;
import net.medigate.repository.mapper.medigate.CommonMapper;
import net.medigate.repository.mapper.medigate.UserMapper;
import net.medigate.service.common.CommonService;
import net.medigate.service.user.UserService;

@Controller
public class CommonController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

	@Autowired
	private CommonMapper commonMapper;
	@Autowired
	private CommonService commonService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserService userService;

	@RequestMapping(value="/common/code/empty/{kbn}")
	public ResponseEntity<String> emptyCodeCache(@PathVariable("kbn") String kbn) throws Exception {
		String cacheName = "CODE_" + kbn;
		CacheUtil.getCacheValue60M(cacheName);
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}

	@RequestMapping(value="/common/code/{kbn}")
	public ResponseEntity<List<Code>> getCodeList(@PathVariable("kbn") String kbn) throws Exception {
		return new ResponseEntity<List<Code>>(CodeManager.getCodeList(kbn), HttpStatus.OK);
	}

	@RequestMapping(value="/common/code/{kbn}/{code}")
	public ResponseEntity<Code> getCodeInfo(@PathVariable("kbn") String kbn, @PathVariable("code") String code) throws Exception {
		return new ResponseEntity<Code>(CodeManager.getCodeInfo(kbn, code), HttpStatus.OK);
	}

	@RequestMapping(value="/common/menu/empty")
	public ResponseEntity<String> emptyMenuCodeCache() throws Exception {
		CacheUtil.getCacheValue60M("MENU_CODE");
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}

	@RequestMapping(value="/common/menu")
	public ResponseEntity<Map<String, CodeMenu>> getMenuCodeList() throws Exception {
		return new ResponseEntity<Map<String, CodeMenu>>(MenuCodeManager.getMenuCodeList(), HttpStatus.OK);
	}

	//@RestResource
	@RequestMapping(value={"/common/agree"})
	public ResponseEntity<Integer> agree(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		if(!CookieUtil.isLogin(request)) {
			throw new ApplicationException("로그인이 필요합니다.");
		}

		param.put("menuGroupCode", param.get("menuGroupCode"));
		param.put("menuCode", param.get("menuCode"));
		param.put("boardIdx", param.get("boardIdx"));
		param.put("agreeFlag", param.get("agreeFlag"));
		param.put("usrId", CookieUtil.getUsrId(request));

		//agree
		int rtnValue = commonService.setAgree(param);

		return new ResponseEntity<Integer>(rtnValue, HttpStatus.OK);
	}


	@RequestMapping("/common/editNickForm")
	public String editNickForm(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		int updatableDays = CommonUtil.null2Num(request.getParameter("updatableDays"), 28);
		String updated = CommonUtil.empty2Str(request.getParameter("updated"), "N");

		param.put("usrId", CookieUtil.getUsrId(request));
		param.put("updatableDays", updatableDays);

		User updateInfo = userMapper.selectUpdateInfo(param);

		if(updateInfo != null){
			model.addAttribute("nick", updateInfo.getUsrNick());
			model.addAttribute("lastUpdatedDate", updateInfo.getLastUpdatedDate());
			model.addAttribute("updatableDate", updateInfo.getUpdatableDate());
			model.addAttribute("updatableFlag", updateInfo.getUpdatableFlag());
			model.addAttribute("updated", updated);
		}
		else{
			model.addAttribute("nick", "");
			model.addAttribute("lastUpdatedDate", "");
			model.addAttribute("updatableDate", "");
			model.addAttribute("updatableFlag", "Y");
			model.addAttribute("updated", updated);
		}

		return "/user/member/nick_edit";
	}

	//닉네임 조회
	@RequestMapping("/common/getNick")
	public String getNick(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		return ajaxText(response, CommonUtil.null2Blank(CookieUtil.getUsrNick(request)));
	}

	@RequestMapping("/common/editNick")
	public String editNick(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		String nick = (String)param.get("usrNick");
		param.put("usrId", CookieUtil.getUsrId(request));
		param.put("usrNick", nick);

		int nickCount = userMapper.selectAnonyNickByNick(param);
		if(nickCount > 0){
			return back(response, "입력하신 필명은 이미 사용중이므로 다른 필명을 사용하시기 바랍니다.");
		}

		String msg = "필명 처리가 완료되었습니다.";
		userService.editAnonyNick(param);
		try {
			CookieUtil.setCookieEncode(response, Constants.COOKIE_NICK, nick, Constants.COOKIE_DOMAIN);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.MSG_SYSTEM, msg);
		params.put("updated", "Y");

		return goPost(response, "/common/editNickForm", params);
	}

	@RequestMapping("/download")
	public String download(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		String fileName = request.getParameter("fileName");
		String sysFilePath = request.getParameter("sysFilePath");
		String absolutePath = Constants.IMG_PATH + sysFilePath;
		try {
//			boolean isLogin = CookieUtil.isLogin(request);
//			if(!isLogin){
//				PrintWriter out = response.getWriter();
//				out.println("<script>alert('You need to sign-in'); history.back();</script>");
//				return;
//			}

			File tempFile = new File( absolutePath );
			int filesize = (int) tempFile.length();
			String filetype = "application/x-zip-compressed";
			String agentType = request.getHeader("Accept-Encoding");
			try {
				if (!tempFile.exists() || !tempFile.canRead()) {
					PrintWriter out = response.getWriter();
					out.println("<script>alert('File Not Found'); history.back();</script>");
					return null;
				}
			} catch (Exception e) {
				PrintWriter out = response.getWriter();
				out.println(
					"<script>alert('File Not Found');history.back();</script>");
				return null;
			}

			boolean flag = false;
			if (agentType != null && agentType.indexOf("gzip") >= 0)
				flag = true;

			flag = false;

			if (flag) {
				response.setHeader( "Content-Encoding", "gzip" );
				response.setHeader( "Content-disposition", "attachment;filename=" + CommonUtil.kscToasc(fileName));
				ServletOutputStream servletoutputstream = response.getOutputStream();
				GZIPOutputStream gzipoutputstream = new GZIPOutputStream(servletoutputstream);
				dumpFile(tempFile, gzipoutputstream);

				gzipoutputstream.close();
				servletoutputstream.close();
			}
			else {
				response.setContentType( "application/octet-stream") ;
				response.setHeader( "Content-disposition", "attachment;filename=" + CommonUtil.kscToasc(fileName));
				ServletOutputStream servletoutputstream1 = response.getOutputStream();
				dumpFile(tempFile, servletoutputstream1);

				servletoutputstream1.flush();
				servletoutputstream1.close();
			}

		} catch (IOException e) {
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			out.println("<script>alert('File Not Found');history.back();</script>");
			return null;
		}
		return null;
	}

	private void dumpFile(File realFile, OutputStream outputstream) {
		byte readByte[] = new byte[4096];
		try {
			BufferedInputStream bufferedinputstream =
				new BufferedInputStream(new FileInputStream(realFile));
			int i;
			while ((i = bufferedinputstream.read(readByte, 0, 4096)) != -1) {
				outputstream.write(readByte, 0, i);
			}

			bufferedinputstream.close();
		} catch (Exception _ex) {
		}
	}

	@RequestMapping(value={"/common/uploadFile"})
	public String uploadFile(
			@RequestParam("uploadFile") MultipartFile file,
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, String> fileInfo = FileUploadUtil.uploadFile(file, request);

		//파일 업로드
		String filePath = null;
		String fileName = null;
		String fileSize = null;

		if(fileInfo != null){
			fileName = fileInfo.get("fileName");
			filePath = fileInfo.get("filePath");
			fileSize = fileInfo.get("fileSize");
			logger.info("filePath->" + filePath);
		}

		Map result = new HashMap();
		if(!CommonUtil.isEmpty(filePath)){
			result.put("status", Constants.YES);
			result.put("filePath", filePath);
			result.put("fileSize", fileSize);
		}
		else{
			result.put("status", Constants.NO);
			result.put("error", "업로드 중 에러가 발생하였습니다.");
		}
		return ajaxJson(response, result);
	}
	
	@RequestMapping(value={"/common/get_remote_data"})
	public @ResponseBody String getRemoteData(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		String url = (String)param.get("url");
		if(CommonUtil.isEmpty(url)) {
			return null;
		}
		
		String charset = (String)param.get("charset");
		if(CommonUtil.isEmpty(charset)) {
			charset = "EUC-KR";
		}
		String contentType = (String)param.get("contentType");
		if(CommonUtil.isEmpty(contentType)) {
			contentType = "application/json";
		}

		String data = null;
		try {
			data = HttpClientUtil.getHtml(url, charset, contentType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	@RequestMapping(value={"/common/post_remote_data"})
	public @ResponseBody String postRemoteData(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		String url = (String)param.get("url");
		if(CommonUtil.isEmpty(url)) {
			return null;
		}
		
		String charset = (String)param.get("charset");
		if(CommonUtil.isEmpty(charset)) {
			charset = "EUC-KR";
		}
		String contentType = (String)param.get("contentType");
		if(CommonUtil.isEmpty(contentType)) {
			contentType = "application/json";
		}
		
		String data = null;
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			
	        for( Map.Entry<String, Object> elem : param.entrySet() ){
	            postParams.add(new BasicNameValuePair(elem.getKey(), (String)elem.getValue()));
	        }
		
			data = HttpClientUtil.sendPost(url, charset, contentType, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	@RequestMapping(value= {"/common/ingred"})
	public @ResponseBody String getIngredList(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		String usrId = CookieUtil.getUsrId(request);
		if (CommonUtil.isEmpty(usrId)) {
			return back(response, "로그인이 필요합니다.");
		}
		param.put("search", param.get("searchKeyword"));
		
		List<Map<String, Object>> ingredList = commonMapper.selectIngredList(param);
		
		return ajaxJson(response, ingredList);
	}
	
	
	//이메일 수신거부
	@RequestMapping(value={"/cancel_subscription"})
	public String cancelSubscription(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		
		String result = "";
		int valid = -1;
		
//		String id = (String)param.get("uId"); 
//		String email = (String)param.get("uEmail");
		Boolean idValid = ( param.get("uId") != null ? true : false );
		Boolean emailValid = ( param.get("uEmail") != null ? true : false );
		
		if( (idValid && emailValid) ) {
			valid = commonMapper.selectEmailIdValid(param);
			if(valid == 1) {
				param.put("emailFlag", "N");
				commonService.cancelSubscription(param);
//				result = ajaxText(response, result);
				String mobilePath = "";
				
				if(CommonUtil.isMobile(request)) {
					mobilePath = "_mobile";
				}
				
				result = "/user/email/mail_block" + mobilePath + "_result";
			} else {
				String msg = "잘못된 사용자 정보입니다.";
				result = go(response, "", Constants.URL_WWW);
			}
		}
		
		return result;
	}
}
