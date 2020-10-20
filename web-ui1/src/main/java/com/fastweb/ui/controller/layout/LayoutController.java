package com.fastweb.ui.controller.layout;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@ControllerAdvice
public class LayoutController {
    @ModelAttribute(name = "layout")
    public String layoutData() {
        return "我是全局公共数据";
    }

    @ModelAttribute
    public void layoutData2(HttpServletRequest request, Model model) {
        if (DispatcherType.ERROR.equals(request.getDispatcherType())) {
            System.out.println("错误逻辑");
        }
        HttpSession session = request.getSession();
        System.out.println(session.getId());
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.add(Calendar.DATE, 1);
        cal2.add(Calendar.DATE, 2);

        Date[] arr = new Date[]{cal1.getTime(), cal2.getTime()};
        List list = Arrays.asList(arr);
        Set set = new HashSet(list);

        model.addAttribute("date", date);
        model.addAttribute("cal", cal);
        model.addAttribute("arr", arr);
        model.addAttribute("list", list);
        model.addAttribute("set", set);

        model.addAttribute("userName", "我是全局公共数据_湖可亮");
    }
}
