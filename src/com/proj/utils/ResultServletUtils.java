package com.proj.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hla 验证输入验证码是否正确
 */
public class ResultServletUtils extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=utf-8");
		String validateC = (String) request.getSession().getAttribute(
				"validateCode");
		String veryCode = request.getParameter("c");
		veryCode = veryCode.toUpperCase();
		PrintWriter out = response.getWriter();
		if (veryCode == null || "".equals(veryCode)) {
			out.print("null");
		} else {
			if (validateC.equals(veryCode)) {
				out.print("1");
			} else {
				out.print("0");
			}
		}
		out.flush();
		out.close();
	}
}