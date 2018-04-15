package bj.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;

import bj.pojo.Goods;
import bj.service.GoodsService;

@Controller
public class GoodsController {

	@Autowired
	private GoodsService goodsService;

	@RequestMapping("/index.html")
	public String index(Model model) {
		model.addAttribute("checkJd", true);
		model.addAttribute("checkDe", true);
		return "index";
	}

	@RequestMapping("/goods.html")
	public String queryGoods(String word, String[] shop, String sort,
			@RequestParam(required = false, defaultValue = "1", value = "pageNum") int pageNum, Model model,
			HttpSession session) {
		if (word != null && !word.equals("") && shop != null && shop.length > 0) {
			// 返回商品,分页每页60条
			PageInfo<Goods> pageInfo = goodsService.updateGoods(word, shop, sort, pageNum, session);
			model.addAttribute("pageInfo", pageInfo);
			// 返回多选框
			boolean checkJd = false;
			for (String shopStr : shop) {
				// 是否查询yhd
				if (shopStr.equals("yhd")) {
					model.addAttribute("checkYhd", true);
				}
				// 是否查询jd
				if (shopStr.equals("jd")) {
					checkJd = true;
				}
				// 是否查询dd
				if (shopStr.equals("dd")) {
					model.addAttribute("checkDd", true);
				}
				// 是否查询jmyp
				if (shopStr.equals("jmyp")) {
					model.addAttribute("checkJmyp", true);
				}
			}
			model.addAttribute("checkJd", checkJd);
			// 返回单选框
			if (sort != null && sort.equals("default")) {
				model.addAttribute("checkDe", true);
			} else {
				model.addAttribute("checkPr", true);
			}
			// 返回关键字
			model.addAttribute("word", word);
			return "index";
		}
		return "redirect:index.html";
	}
}
