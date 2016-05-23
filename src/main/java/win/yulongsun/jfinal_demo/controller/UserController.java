package win.yulongsun.jfinal_demo.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import javafx.geometry.Pos;
import win.yulongsun.jfinal_demo.model.User;
import win.yulongsun.jfinal_demo.util.Response;
import win.yulongsun.jfinal_demo.util.Token;
import win.yulongsun.jfinal_demo.util.TokenUtil;
import win.yulongsun.jfinal_demo.util.ValidateUtils;

import java.util.List;

/**
 * Created by yulongsun on 2016/5/5.
 */
public class UserController extends Controller {

    private Response response;


    public void register() {
        response = new Response();
        String  user_name = getPara("user_name");
        String  user_pwd  = getPara("user_pwd");
        boolean isNull    = ValidateUtils.validatePara(user_name, user_pwd);
        if (isNull) {
            response.setFailureResponse(Response.ErrorCode.REQUEST_NULL);
            renderJson(response);
            return;
        }
        List<User> userList = User.dao.find("SELECT * From user WHERE user_name=?", user_name);
        if (userList == null || userList.size() != 0) {
            response.setFailureResponse(Response.ErrorCode.USER_REGISTERED);
        } else {
            User user = new User();
            user.setUserName(user_name);
            user.setUserPwd(user_pwd);
            user.save();
            response.setSuccessResponse("注册成功");
        }
        renderJson(response);
    }

    public void login() {
        response = new Response();
        String  user_name = getPara("user_name");
        String  user_pwd  = getPara("user_pwd");
        boolean isNull    = ValidateUtils.validatePara(user_name, user_pwd);
        if (isNull) {
            response.setFailureResponse(Response.ErrorCode.REQUEST_NULL);
            renderJson(response);
            return;
        }
        User user = User.dao.findByName(user_name);
        if (user == null) {
            response.setFailureResponse(Response.ErrorCode.USER_NULL);
        } else {
            if (user.getUserPwd().equals(user_pwd)) {
                String signature = TokenUtil.generateToken(user.getId().toString(), user.getId()).getSignature();
                user.setToken(signature);
                user.update();
                response.setSuccessResponse("登陆成功", signature);
            } else {
                response.setFailureResponse(Response.ErrorCode.ERROR_PWD);
            }
        }
        renderJson(response);
    }

    @Before(POST.class)
    public void upload() {
        UploadFile user_img = getFile("user_img");
        String     user_id  = getPara("user_id");
        boolean    isNull   = ValidateUtils.validatePara(user_id, user_img.toString());
        if (isNull) {
            response.setFailureResponse(Response.ErrorCode.REQUEST_NULL);
            renderJson(response);
            return;
        }
        User user = User.dao.findById(user_id);
    }

}
