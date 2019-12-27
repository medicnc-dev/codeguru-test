package net.medigate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

//	@RequestMapping(value={"", "/"})
//	public String index() throws Exception {
//		return "/device";
//	}

	@RequestMapping(value={"/main"})
	public String main() throws Exception {
		return "/main/index";
	}

	@RequestMapping(value={"/main2"})
	public String main2() throws Exception {
		return "/main/index2";
	}
}
