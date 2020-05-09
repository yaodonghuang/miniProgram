package com.akuida.controller.interceptor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.akuida.utils.JSONResult;
import com.akuida.utils.JsonUtils;
import com.akuida.utils.RedisOperator;
import com.alibaba.druid.util.StringUtils;

public class MiniInterceptor implements HandlerInterceptor {
	@Autowired
	private RedisOperator redis;
	public static final String USER_REDIS_SESSION = "user_redis_session";

	/**
	 * 拦截请求，controller之前
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String userId = request.getHeader("userId");
		String userToken = request.getHeader("userToken");
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userToken)) {
			// 未登录
			returnErrorResponse(response, new JSONResult().errorTokenMsg("请登录"));
			return false;
		} else {
			String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);
			if (StringUtils.isEmpty(uniqueToken)) {
				// token过期
				returnErrorResponse(response, new JSONResult().errorTokenMsg("请重新登录"));
				return false;
			} else {
				if (!userToken.equalsIgnoreCase(uniqueToken)) {
					// 在别的手机登录
					returnErrorResponse(response, new JSONResult().errorTokenMsg("账号被挤出"));
					return false;
				}
			}
		}
		return true;
	}

	public void returnErrorResponse(HttpServletResponse response, JSONResult result)
			throws IOException, UnsupportedEncodingException {
		OutputStream out = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/json");
			out = response.getOutputStream();
			out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 请求controller之后，渲染视图之前
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * 请求结束，视图渲染之后
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
