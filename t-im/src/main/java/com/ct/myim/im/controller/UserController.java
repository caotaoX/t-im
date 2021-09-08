package com.ct.myim.im.controller;

import cn.hutool.core.util.StrUtil;
import com.ct.myim.framework.web.controller.BaseController;
import com.ct.myim.framework.web.entity.AjaxResult;
import com.ct.myim.im.entity.User;
import com.ct.myim.im.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 注册用户
     * @return
     */
    @PostMapping("registerUser")
    public AjaxResult registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

    @GetMapping("getUserIfon")
    public AjaxResult getUserInfo(){
        return userService.getUserInfo();
    }

    @PutMapping("update")
    public AjaxResult update(@RequestBody User user){
        return  userService.update(user);
    }

    /**
     * 根据账号获取用户
     * @return
     */
    @GetMapping("getUser")
    public AjaxResult getUser(String userName){
        return userService.getUser(userName);
    }

    /**
     * 添加用户申请
     * @return
     */
    @PostMapping("addUserFrieds")
    public AjaxResult addUserFrieds(String userName,String explain){
       return userService.addUserFrieds(userName,explain);
    }

    @GetMapping("getFriedsList")
    public AjaxResult getFriedsList(int pageSize){
        return userService.getFriedsList(pageSize);
    }

    @PostMapping("handleFried")
    public AjaxResult handleFried(String id,boolean type){
        return userService.handleFried(id,type);
    }

    @PostMapping("deleteUser")
    public AjaxResult deleteUser(String userName){
        return userService.deleteUser(userName);
    }

    /**
     * 新增群
     * @return
     */
    @PostMapping("registerGroup")
    public AjaxResult registerGroup(@RequestBody User user){
        return userService.registerGroup(user);
    }

    /**
     * 新增群
     * @return
     */
    @PostMapping("editGroup")
    public AjaxResult editGroup(@RequestBody User user){
        return userService.editGroup(user);
    }

    @GetMapping("getGoupUser")
    public AjaxResult getGoupUser(String id,String searchValue, boolean all){
        if(StrUtil.isNotBlank(searchValue)){
            all = true;
        }
        return userService.getGoupUser(id,searchValue,all);
    }

    /**
     * 删除组用户
     *
     * @return
     */
    @DeleteMapping("deleteGroupUser")
    public AjaxResult deleteGroupUser(String groupId,String userId) {
        return  userService.deleteGroupUser(groupId,userId);
    }

    @GetMapping("getNoGroupUser")
    public AjaxResult getNoGroupUser(String groupId,String searchValue){
        return userService.getNoGroupUser(groupId,searchValue);
    }

    @PostMapping("addGroupUserList")
    public AjaxResult addGroupUserList(String groupId,String ids){
        return userService.addGroupUserList(groupId,ids);
    }
}
