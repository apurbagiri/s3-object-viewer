package com.apurbagiri.apps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("title", "S3 Object Viewer");
		return "index.xhtml";
	}
}
