package cn.edu.sdjzu.xg.bysj.controller.basic;

import cn.edu.sdjzu.xg.bysj.domain.User;
import cn.edu.sdjzu.xg.bysj.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import util.JSONUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/user.ctl")
public class UserController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //读取参数id
        String id_str = request.getParameter("id");
        String username = request.getParameter("username");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        try {
            //如果id值不为空，调用根据id值响应一个用户对象方法，否则，调用根据用户名响应一个用户对象的方法
            if (id_str != null){
                //强制类型转换成int型
                int id = Integer.parseInt(id_str);
                this.responseUser(id,response);
            }else if (username != null){
                this.responseUserByUsername(username,response);
            }else{
                responseUsers(response);
            }
        } catch (SQLException e) {
            message.put("message", "数据库操作异常");
            e.printStackTrace();
        } catch (Exception e) {
            message.put("message", "网络异常");
            e.printStackTrace();
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //根据request对象，获得代表参数的JSON字串
        //String user_json = JSONUtil.getJSON(request);
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //在数据库表中增加Department对象
        try {
            UserService.getInstance().login(username,password);
            message.put("message", "登录成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            e.printStackTrace();
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        String user_json = JSONUtil.getJSON(request);
        //将JSON字串解析为Department对象
        User passwordTochange = JSON.parseObject(user_json, User.class);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");

        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //到数据库表修改Department对象对应的记录
        try {
            UserService.getInstance().change(passwordTochange);
            message.put("message", "修改成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            e.printStackTrace();
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    private void responseUserByUsername(String username, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据school的USERNAME查找所属的系
        User users = UserService.getInstance().findByUsername(username);
        //将对象转为json字符串
        String user_json = JSON.toJSONString(users);
        //响应message到前端
        response.getWriter().println(user_json);
    }
    //响应一个user对象
    private void responseUser(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找user
        User user = UserService.getInstance().find(id);
        String user_json = JSON.toJSONString(user);

        //响应USER_json到前端
        response.getWriter().println(user_json);
    }
    //响应所有user对象
    private void responseUsers(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有user
        Collection<User> users = UserService.getInstance().findAll();
        String users_json = JSON.toJSONString(users, SerializerFeature.DisableCircularReferenceDetect);

        //响应departments_json到前端
        response.getWriter().println(users_json);
    }
}
