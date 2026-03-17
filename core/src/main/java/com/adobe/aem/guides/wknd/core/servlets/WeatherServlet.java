package com.adobe.aem.guides.wknd.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

@Component(service = Servlet.class)
@SlingServletPaths(value = "/bin/wknd/weather")
public class WeatherServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(org.apache.sling.api.SlingHttpServletRequest request,
            org.apache.sling.api.SlingHttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        String weatherCard = "<div style=\"border: 1px solid #ddd; border-radius: 8px; padding: 20px; max-width: 300px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); font-family: Arial, sans-serif; text-align: center; background: linear-gradient(to bottom, #87CEEB, #E0F6FF);\">"
                +
                "<h3 style=\"margin: 0; color: #333;\">Kigali, Rwanda</h3>" +
                "<p style=\"font-size: 14px; color: #666; margin-top: 5px;\">Thursday, March 12</p>" +
                "<div style=\"font-size: 48px; font-weight: bold; color: #ff8c00; margin: 15px 0;\">26C</div>" +
                "<p style=\"font-size: 16px; color: #444; margin: 0;\"> Sunny & Clear</p>" +
                "</div>";
        response.getWriter().write(weatherCard);
    }
}
