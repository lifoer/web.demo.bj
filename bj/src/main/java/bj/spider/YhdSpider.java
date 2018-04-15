package bj.spider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bj.mapper.GoodsMapper;
import bj.pojo.Goods;
import bj.utils.FileDown;

@Service
public class YhdSpider {

	@Autowired
	private GoodsMapper goodsMapper;
	
	private final static Logger logger = Logger.getLogger(YhdSpider.class);

	public void insertYhdGoods(String keyword) {

		// 转换编码
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
		}
		// 页数，一页60条，只获取一页
		int page = 1;
		// 前30条
		beforeThirtyYhdCrawler(keyword, page);
		// 后30条url
		String url = "https://search.yhd.com/searchPage/c0-0/mbname-b/a-s2-v4-p" + page
				+ "-price-d0-f0b-m1-rt0-pid-mid0-color-size-k" + keyword + "/?callback=jQuery&isGetMoreProducts=1";
		afterThirtyYhdCrawler(url);
	}

	// 前30条，正常请求
	private void beforeThirtyYhdCrawler(String keyword, int page) {
		try {
			String url = "https://search.yhd.com/c0-0/mbname-b/a-s2-v4-p" + page
					+ "-price-d0-f0b-m1-rt0-pid-mid0-color-size-k" + keyword;
			Connection conn = Jsoup.connect(url);
			conn.header("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			conn.header("Accept-Encoding", "gzip, deflate, br");
			conn.header("Accept-Language", "zh-CN,zh;q=0.9");
			conn.header("Cache-Control", "max-age=0");
			conn.header("Connection", "keep-alive");
			conn.header("Cookie",
					"provinceId=2; cityId=2817; yhd_location=2_2817_51973_0; __jdv=218172059|direct|-|none|-|1523642570790; cart_num=0; cart_cookie_uuid=c1b891e7-38e9-4357-9fc5-4b7e4f78959f; __jdc=218172059; __jda=218172059.1523642570790152043283.1523642571.1523682662.1523688295.3; JSESSIONID=2EBDE5018F1E9E4BCDB5A610753CFA66.s1; __jdb=218172059.12.1523642570790152043283|3.1523688295");
			conn.header("Host", "search.yhd.com");
			conn.header("Upgrade-Insecure-Requests", "1");
			conn.header("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
			Document doc = conn.post();
			FileDown.docWrite(doc);
			for (int i = 0; i < 30; i++) {
				try {
					// 获取商品名称
					String title = "";
					try {
						Element ele1 = doc.select("#itemSearchList .mod_search_pro .itemBox .proName")
								.select(".clearfix a").get(i);
						title = ele1.attr("title");
					} catch (IndexOutOfBoundsException e) {
						logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
						// 前30条如有异常，也采用ajax
						String url1 = "https://search.yhd.com/searchPage/c0-0/mbname-b/a-s2-v4-p" + page
								+ "-price-d0-f0b-m1-rt0-pid-mid0-color-size-k" + keyword + "/?callback=jQuery";
						afterThirtyYhdCrawler(url1);
						break;
					}
					// 获取商品价格
					Element ele2 = doc.select("#itemSearchList .mod_search_pro .itemBox .proPrice em").get(i);
					Double price = Double.parseDouble(ele2.attr("yhdprice"));
					// 获取商品详情
					Element ele3 = doc.select("#itemSearchList .mod_search_pro .itemBox .proPrice .comment a").get(i);
					String comment = ele3.attr("experiencecount");
					// 获取商品描述
					Element ele4 = doc.select("#itemSearchList .mod_search_pro .itemBox .storeName")
							.select(".limit_width a").get(i);
					String des = ele4.text();
					// 获取商品链接
					Element ele5 = doc.select("#itemSearchList .mod_search_pro .itemBox .proName").select(".clearfix a")
							.get(i);
					String link = "http:" + ele5.attr("href");
					// 获取商品图片链接
					Element ele6 = doc.select("#searchProImg a img").get(i);
					String image = "";
					if (ele6.attr("original") != null && !ele6.attr("original").equals("")) {
						image = "http:" + ele6.attr("original");
					} else if (ele6.attr("src") != null && !ele6.attr("src").equals("")) {
						image = "http:" + ele6.attr("src");
					}
					// 添加商城名称
					String mall = "一号店";
					// 商品序号，uuid
					String no = UUID.randomUUID().toString();
					Goods goods = new Goods(no, title, price, comment, des, link, image, mall);
					goodsMapper.insertGoods(goods);
				} catch (Exception e) {
					logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
					continue;
				}
			}
		} catch (IOException e) {
			logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
		}

	}

	// 后30条，滚动ajax请求
	private void afterThirtyYhdCrawler(String url) {
		try {
			Connection conn = Jsoup.connect(url);
			conn.header("Accept",
					"text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
			conn.header("Accept-Encoding", "gzip, deflate, sdch, br");
			conn.header("Accept-Language", "zh-CN,zh;q=0.8");
			conn.header("Connection", "keep-alive");
			conn.header("Host", "search.yhd.com");
			conn.header("Cookie",
					"provinceId=2; cityId=2817; yhd_location=2_2817_51973_0; __jda=218172059.1523642570790152043283.1523642571.1523642571.1523642571.1; __jdc=218172059; __jdv=218172059|direct|-|none|-|1523642570790; cart_num=0; cart_cookie_uuid=c1b891e7-38e9-4357-9fc5-4b7e4f78959f; JSESSIONID=3AB8E5BF95458887A281F0952CB605BC.s1; __jdb=218172059.7.1523642570790152043283|1.1523642571");
			conn.header("Referer", "https://search.yhd.com");
			conn.header("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36 LBBROWSER");
			Document doc = conn.get();
			for (int i = 0; i < 30; i++) {
				try {
					// 商品序号，uuid
					String no = UUID.randomUUID().toString();
					// 正则替换特殊字符
					String str = doc.toString().replaceAll("\\\\&quot;", "");
					Element html = doc.html(str);
					// 商品标题
					Element ele1 = html.select(".mod_search_pro .itemBox .proName").select("a").get(i);
					String title = ele1.text().replaceAll("\\\\n", "").replaceAll("\\\\t", "").substring(2);
					// 获取商品价格
					Element ele2 = doc.select(".mod_search_pro .itemBox .proPrice em").get(i);
					Double price = Double.parseDouble(ele2.attr("yhdprice"));
					// 获取商品详情
					Element ele3 = doc.select(".mod_search_pro .itemBox .proPrice .comment a").get(i);
					String comment = ele3.attr("experiencecount");
					// 获取商品描述
					Element ele4 = doc.select(".mod_search_pro .itemBox .storeName a").get(i);
					String des = ele4.attr("title");
					// 获取商品链接
					Element ele5 = doc.select(".mod_search_pro .itemBox .proName a").get(i);
					String link = "http:" + ele5.attr("href");
					// 获取商品图片链接
					Element ele6 = doc.select("#searchProImg a img").get(i);
					String image = "";
					if (ele6.attr("original") != null && !ele6.attr("original").equals("")) {
						image = "http:" + ele6.attr("original");
					} else if (ele6.attr("src") != null && !ele6.attr("src").equals("")) {
						image = "http:" + ele6.attr("src");
					}
					// 添加商城名称
					String mall = "一号店";
					Goods goods = new Goods(no, title, price, comment, des, link, image, mall);
					goodsMapper.insertGoods(goods);
				} catch (Exception e) {
					logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
					continue;
				}
			}
		} catch (IOException e) {
			logger.error(e.getStackTrace()[0] + " " +e.toString() + "\n");
		}

	}
}
