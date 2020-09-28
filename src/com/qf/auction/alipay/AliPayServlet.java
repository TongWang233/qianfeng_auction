package com.qf.auction.alipay;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.qf.auction.biz.AuctionBIZ;
import com.qf.auction.bizimpl.AuctionBIZImpl;
import com.qf.auction.entity.Auction;
import com.qf.auction.entity.User;
import com.qf.auction.enums.AuctionStateEnum;
import com.qf.auction.util.AES;

public class AliPayServlet extends HttpServlet {

	public static final String sKey = "www.qianfeng.com";

	/**
	 * Constructor of the object.
	 */
	public AliPayServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest httpServletRequest,
			HttpServletResponse httpResponse) throws ServletException,
			IOException {
		String auctionPrice = httpServletRequest.getParameter("auctionPrice");
		String auctionId = httpServletRequest.getParameter("auctionid");
		String pageIndex = httpServletRequest.getParameter("pageIndex");
		User user = (User) httpServletRequest.getSession().getAttribute("user");
		int userId;
		if (user == null) {
			httpResponse.sendRedirect("user_login.jsp");
			return;
		} else {
			userId = user.getId();
		}
		AuctionBIZ auctionBIZ = new AuctionBIZImpl();
		Auction auction = auctionBIZ.findAuctionById(Integer
				.parseInt(auctionId));
		String APP_ID = "2016101500691514";
//		String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCA5/Cyxu04JCSfzks4t9YZk3Ji+COcM1NB940wzztFYPVHOwvBlCfFcV7CYHChuP30aHvw+a2q/M9O0vSpwXSXuQ9wUkNVhiYZdVEnlXpZpIGiLwmUtkOc7i0Hc1vW50kYZ5n494s8ZzRREO14lk1PZK9YrlEExGgXGjBBh86nLZ6yHze51YvBYi/yOVD9rmJnw6g8umKhF/yCcpmkfr5EYBvAtrS5ncVQKjJE7H94tfO9yhaK1tae2wsnN2/5vfEm/rHbgyhqYgo1wfBkKqlTeppPV5yNZWVpsDtpEj69AbpyW5f5k3sJzUlwtRVK/Km2vk+Zy35IjrGTOUCpdq5pAgMBAAECggEARLljfizIM/IxBHH7cJWyM5iEl3crpQ9ICBdu0uLo+qginINCVUYjngQ/POololL6Md+ylFrI2CxIagHWJtrYyjsDnCJ/e401qsT9K1lqDXjDFDB3ry0tBvGEWECMahqMwdIaKL3xIZb0MXQQ5wVa2kUXpvPohd4gkJCJwaYh6saJj8rrSAD7IK4uiBqxpm2b3vvl1XoWKgz8FF4v+4W6YjXTIp5frPPZMZM+Phh/rdXt0wbhqai1RPXEhqdue/IOXVLvnfBdVeRmZ1ABRmyTju/dGSGO+8+pW+stPOwSb6m73Ug8HjhpjKLqr24740JcP+rn3x0u4yTG26usnXFn8QKBgQDBMTmi+yq5NCzC5rFftVbyMWyJ8d/b6OAXIOWBNfw5Dr1icDeVdgjhceKhyyCglmf3/VflsbmNitV+XbVMs9PSAZBbZNDFsB/NfR7hBe3wuOupX7WZu4Ra9VoX9msRvKRyTtPT4DgxAqyvxrFk0C53hDPhNZ0oGEBkgxyrSJUOCwKBgQCq0F/5t3SrNXtq8o1Z/VlxldrR2UI/aepNw5jYWmv4EMtzh8JdopIAlD7zBtUtaSLYbelKfPRhVR7U0lSG87uQioCtYgkGCoTZyBf2f2ZLM+2aj1awsMWKvGY/whECa3PLF2V3UzSa6JXsNLCyI6h7J/eLTYxID2Vo7lS7/1Th2wKBgBpeTqCZKYanwWhEtnb6uEhMSKU3VzebvGJLwid1p9xdz6mM6XlD6AhlU+qDYAurG7u0X3fhXxMbnPIndiRm8wVZ40smInypAl44sqtB9O0DQF8Yyh+WgYUsscRNhOWh2swjSDRGCUVfORSwyOuaooz7hV06xR2A2eEeKB6UxItrAoGBAIuU73iuE2Z8s7e3MQ7iWp7Y6BEnSl1rfY3CUjop2aTChJ6Jx6igWlKi0bXiVX1gNptJZaP66/BFkIY0weHtdc4tf+0u+VS4b8tPGVXS0ZKOgpEUpLCKI0K1pZBgU2n4yIyjN8UEdy5G9YwpmmJXhSvugviQjBuuiR3v88oUYhaVAoGANPGIy1HeF/3yYL6Jy0PMZAO1PSlNzCsAFZCW8HvWslDjGwuonTmOMIzrneyONUjT0j2LIpeSqU9DywrAuZRCwsZ3IYfWEHy6Vy7HtvZBBpzZPpNumnLDDxsMn24kqP3NeLbWu1nLFmd15dOwVeNmmcFnIYJ+vcraL38NyGaYQEA=";
	    String APP_PRIVATE_KEY  = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCPvgeSwETzZvXcSQN9vNi/CxIZDJ9S0OfQF7ujy1b9LWUs6k7aUG6PfW25v7if/HoOS2rIhT8SPKgfmFde4bM75YQRa50+2FOw1UUoQvO3rIKTLE6Pte1nAhmcolsAlvnZN6MPOVoh3siSjHflANiP03ninxeQRN6teAHkGFSHg5vEQdAOJEvnECvTjzGU3rioT0kyp7v33YgL7mxmmw/KDf6eIpSv7cFSGz1mlZjOCFUGV8eZQP36beOSoQZv59Xlidae7QBd20CbhFJ8kKmUNBVeZ01qZ0TYhzyIBxamib6WOs7EITZFStRGpK0DuS9BqQ2of4+2cKTO25sB8FKxAgMBAAECggEAM8t2sSOE9Ovxxbmtf15P/67+i3Yn5tCN50bV/zRjGQGFhoS7eyEmZZ/9rMPvw/5XANWeGbkhu/GNZy0etnq/eeK/DwPm+mE67uo6bEKLzXBk8KEzgcc9TzPLhMxf3DuKtgG9SNwSpUfAoEXrYeJHJrQ+JDHNbNfUIU+v5r/5nYZ7Y/4/GchBDhRIcNhrTk+29l5qDH3lS7sFuHKZ3VKM2E+LpB0nqz14A1BdjzxVkRIpeve3gg4bBRSuhpg9yH9BvlDLO+wpffvgBjBJ4XEkbBBYNp8l1DCHwJkPxx6Il36Uj8RGnClDfcvIoV4wkSIAmgrY4I6eg3RrQK2JX/qJkQKBgQDlSjRtE2GMRzPP/X+8q07dlbl7JDVm4YwZE/HqVr81UO1YLijBh+Hpbg0nfw6t9l8+b8cX1qyrfpUKKbOWD4WX08WAk1RdqILrTmHotSn42rHfQUL4eLYJTzqqq5VppecPMZ1Uiegow2CdFEeaR+tlrF3QPjyFDvd68Gh6LdCiDQKBgQCgfKhwMaHAaYYxOHNtQED0g7aX1TsPAchZf9Cv5Y3t+hJlM9s2otM1nujnXsN5k2Z/3s35VmULsV7EXYaXJSwRzRjSUFT8ZtKlsbtDsuxNQpygeJQEbXSGqPotaRNKRCucmkGLnGhBoNcOQtfaqaaMJjHlQVVW+JKDEGfESbZeNQKBgQDIDieR5/AbseBOIBM8rYdBJKrR+3SwkChplRJk8U0hmruLTbL3sLR2tDO7+0r2k9jkJCjk7sR3WRl25Y2wZ1ibcQWIuoNIuIeKATjCDpdRbpb1gP6Kxt+lXcCFOvJBXKQuiI6KPU0Xi+iXBSO4Q/nqGys7T6IEQlvBo1K1D5ZSXQKBgCarQq6d1tqU0oejkXReiggggGt/LlbEWDDwcikfx53ypmIKJ6S3gOqnmni9RZ5SnNzso3aTkY9Ksonf+yEKDN9RKszqHEAgylqwQ23he7x182VbCFc0xGH57yXn7oTzfgqixG5ORnShu4+tHXOLcxzLyzPxFQ36oNrBpi8Oj8mNAoGAEIOOgsGNIpi1TFUsB99gclKim8HSC9t62oYvHf+3W/whJeZzZYfKlsCA79qIYjI0x/4auq/Fl5690Mai5X2p7kqvmgMlHnyaWHd1kFG8Cao1gGiDNSkjs2kjVD1ddIFuN7bUfkPxlzdrbqyueMoZuqi7mX0eoMEwZuLwxhMC1O8=";

		String CHARSET = "utf-8";
//		String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgOfwssbtOCQkn85LOLfWGZNyYvgjnDNTQfeNMM87RWD1RzsLwZQnxXFewmBwobj99Gh78PmtqvzPTtL0qcF0l7kPcFJDVYYmGXVRJ5V6WaSBoi8JlLZDnO4tB3Nb1udJGGeZ+PeLPGc0URDteJZNT2SvWK5RBMRoFxowQYfOpy2esh83udWLwWIv8jlQ/a5iZ8OoPLpioRf8gnKZpH6+RGAbwLa0uZ3FUCoyROx/eLXzvcoWitbWntsLJzdv+b3xJv6x24MoamIKNcHwZCqpU3qaT1ecjWVlabA7aRI+vQG6cluX+ZN7Cc1JcLUVSvyptr5Pmct+SI6xkzlAqXauaQIDAQAB";
		String ALIPAY_PUBLIC_KEY  ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlFqwKikQllgHJzqFm2jKSBhhsb8fvWKqIE0bzD47lIli+dhkVFGwfY+fX7Uq8CIA7YIRrBoko5ghT+WoIHeKFTXqoTDvHNtB66RV2HjRtBHuATkT6VPvyQG9Gz1Il7gpC+vzBn1+MZbunxdnTKRAOrqBkCNTp9Je/ctZPdveI626Nxdw7i7sd4C1yIRDLcr1ljq2yKFURAjmJg27jUHH2lg8BvdY1onxsERc/XqoKZJurzbrf58vJkC6/l7uq0ItOiSKa/hglw7cxiVYAOC/IRL4GN0lObW9YrzZ+xNp5SaGhGnneUXotMTnXOTzezy9DRzC7tY7RFE5m/EkaKw1IwIDAQAB";

		// 这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
		String GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do";
		String FORMAT = "JSON";
		// 签名方式
		String SIGN_TYPE = "RSA2";
		// 支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址 http://fx3pp5.natappfree.cc
		String NOTIFY_URL = "http://saj3pg.natappfree.cc/qianfeng_auction/AliPayReturnServlet";
		// 支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
		// 支付成功后 回显到拍卖品 详情页
		String RETURN_URL = "http://saj3pg.natappfree.cc/qianfeng_auction/AuctionRecordServlet?auctionId="
				+ auctionId
				+ "&msg="
				+ AuctionStateEnum.AUCTION_SUCCESS.getValue()
				+ "&pageIndex="
				+ pageIndex + "";

		// 实例化客户端,填入所需参数
		AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,
				APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY,
				SIGN_TYPE);
		AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
		// 在公共参数中设置回跳和通知地址
		request.setReturnUrl(RETURN_URL);
		request.setNotifyUrl(NOTIFY_URL);
		// 根据订单编号,查询订单相关信息
		// 商户订单号，商户网站订单系统中唯一订单号，必填 后续可以通过这个订号 去支付宝查询你的购买详情
		String out_trade_no = UUID.randomUUID().toString()
				+ new Random().nextInt(10000);
		// 付款金额，必填
		String total_amount = auctionPrice;
		// 订单名称，必填
		String subject = auction.getAuctionName();
		// 商品描述，可空 在字符串中 拼接字符串的 技巧是 现在 “” 然后在 “” 再写 ++ 然后在 ++ 拼接变量
		String body = null;
		try {
			// 给回调的关键参数进行 对称加密
			body = "" + AES.Encrypt(out_trade_no, sKey) + ","
					+ AES.Encrypt(String.valueOf(userId), sKey) + ","
					+ AES.Encrypt(String.valueOf(auctionId), sKey) + ","
					+ AES.Encrypt(String.valueOf(total_amount), sKey) + ",";
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// 发送给阿里的数据
		request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
				+ "\"total_amount\":\"" + total_amount + "\","
				+ "\"subject\":\"" + subject + "\"," + "\"body\":\"" + body
				+ "\"," + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		String form = "";
		try {
			form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		httpResponse.setContentType("text/html;charset=" + CHARSET);
		httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
		httpResponse.getWriter().flush();
		httpResponse.getWriter().close();
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
