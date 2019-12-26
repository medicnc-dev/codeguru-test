package net.medigate.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.medigate.common.constants.Constants;
import net.medigate.common.util.CommonUtil;
import net.medigate.common.util.CookieUtil;
import net.medigate.controller.BaseController;


@Controller
@RequestMapping("/common/mailConn")
public class MailConnController extends BaseController
{
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * 신규 이메일 건수 가져오기
	 */
	@RequestMapping(value={"/getEmailCnt"})
	public String getEmailCnt(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		StringBuffer url = new StringBuffer();
		url.append("http://www.medigate.net/php/mail/mail_connector.php");
		url.append("?operation=1");
		url.append("&usrEmail=").append(CommonUtil.encodeValue(request.getParameter("email")));
		//url.append("&usrEmail=").append(CommonUtil.encodeValue(CookieUtil.getUsrEmail(request)));
		String cnt = CommonUtil.empty2Blank(CommonUtil.getHtml(url.toString())).replaceAll(" ", "");

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("mailCnt", cnt);

		return ajaxJson(response, m);
	}

	/**
	 * 계정 생성
	 */
	@RequestMapping(value={"/addEmail"})
	public String addEmail(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		StringBuffer url = new StringBuffer();
		url.append(Constants.URL_MAIL_CONNECTOR);
		url.append("?operation=2");
		url.append("&usrEmail=").append(CommonUtil.encodeValue((String)param.get("email")));
		url.append("&usrPasswd=").append(CommonUtil.encodeValue((String)param.get("passwd")));
		url.append("&usrName=").append(CommonUtil.encodeValue(CookieUtil.getUsrName(request)));
		url.append("&usrKind=").append(CookieUtil.getUsrKind(request));

		String result = CommonUtil.getHtml(url.toString());
		if(result != null && result.indexOf("Success") != -1) {
			result = "true";
		} else {
			result = "false";
		}

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("result", result);

		return ajaxJson(response, m);
	}

	/**
	 * 계정 패스워드 변경
	 */
	@RequestMapping(value={"/editPasswd"})
	public String editPasswd(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		StringBuffer url = new StringBuffer();
		url.append(Constants.URL_MAIL_CONNECTOR);
		url.append("?operation=3");
		url.append("&usrEmail=").append(CommonUtil.encodeValue(CookieUtil.getUsrEmail(request)));
		url.append("&usrOldPasswd=").append(CommonUtil.encodeValue((String)param.get("oldPasswd")));
		url.append("&usrNewPasswd=").append(CommonUtil.encodeValue((String)param.get("newPasswd")));

		String result = CommonUtil.getHtml(url.toString());
		if(result != null && result.indexOf("Success") != -1) {
			result = "true";
		} else {
			result = "false";
		}

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("result", result);

		return ajaxJson(response, m);
	}

	/**
	 * 계정 삭제
	 */
	@RequestMapping(value={"/removeMail"})
	public String removeMail(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		StringBuffer url = new StringBuffer();
		url.append(Constants.URL_MAIL_CONNECTOR);
		url.append("?operation=5");
		url.append("&usrEmail=").append(CommonUtil.encodeValue(CookieUtil.getUsrEmail(request)));
		url.append("&usrPasswd=").append(CommonUtil.encodeValue((String)param.get("passwd")));

		String result = CommonUtil.getHtml(url.toString());
		if(result != null && result.indexOf("Success") != -1) {
			result = "true";
		} else {
			result = "false";
		}

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("result", result);

		return ajaxJson(response, m);
	}

	/**
	 * 계정 전환 :: 학생->의사
	 */
	@RequestMapping(value={"/changeMail"})
	public String changeMail(
			@RequestParam HashMap<String, Object> param,
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) {

		StringBuffer url = new StringBuffer();
		url.append(Constants.URL_MAIL_CONNECTOR);
		url.append("?operation=4");
		url.append("&usrEmail=").append(CommonUtil.encodeValue(CookieUtil.getUsrEmail(request)));
		url.append("&usrPasswd=").append(CommonUtil.encodeValue((String)param.get("passwd")));
		url.append("&usrName=").append(CommonUtil.encodeValue(CookieUtil.getUsrName(request)));
		url.append("&usrKind=").append(CookieUtil.getUsrKind(request));

		String result = CommonUtil.getHtml(url.toString());
		if(result != null && result.indexOf("Success") != -1) {
			result = "true";
		} else {
			result = "false";
		}

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("result", result);

		return ajaxJson(response, m);
	}
}