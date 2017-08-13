package token;

import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;

/**
 * 用于检测用户是否登陆的过滤器，如果未登录，则重定向到指的登录页面
 * 配置参数:
 * sessionToken 需检查的在 Session 中保存的关键字
 * redirectURL 如果用户未登录，则重定向到指定的页面，URL不包括 noCheckURLList
 * 不做检查的URL列表，以逗号分开
 */
public class TokenChecker implements Filter {
	
    @SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(TokenChecker.class);
    
	protected FilterConfig filterConfig = null;
	private String redirectURL = null;//过滤重定向页面
	private final Set<String> noCheckURLList = new HashSet<String>();//白名单
	private String sessionToken = null;//保存在session中的用户信息名称


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		redirectURL = filterConfig.getInitParameter("redirectURL");
		System.out.println(">? TokenChecker 过滤重定向:"+redirectURL);
		sessionToken = filterConfig.getInitParameter("sessionToken");
		System.out.println(">? TokenChecker 检测会话令牌:"+sessionToken);
		{
			String notCheckURLListStr = filterConfig.getInitParameter("noCheckURLList");
			if (notCheckURLListStr != null) {
				System.out.println(">? TokenChecker 白名单配置:"+notCheckURLListStr);
				String[] params=notCheckURLListStr.split(",");
				for(String param:params)
					noCheckURLList.add(param.trim());
			}
		}
	}
	@Override
	public void destroy() {
		noCheckURLList.clear();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession();
		if (this.sessionToken == null) {
			filterChain.doFilter(request, response);
			return;
		}
		System.out.print(">? TokenChecker:doFilter > ");
		if (!checkNoCheckURLList(request)//不在白名单中
				&& session.getAttribute(this.sessionToken)==null//而且令牌不正确
				){
			//过滤重定向
			System.out.println("不在白名单且无令牌,重定向到:"+redirectURL);
			response.sendRedirect(request.getContextPath() + this.redirectURL);
			return;
		}
		System.out.println("通过");
		filterChain.doFilter(servletRequest, servletResponse);
	}

	private boolean checkNoCheckURLList(HttpServletRequest request) {
		String url=request.getRequestURI().substring(request.getContextPath().length() + 1);
		boolean res=noCheckURLList.contains(url);
		return res;
	}


}
