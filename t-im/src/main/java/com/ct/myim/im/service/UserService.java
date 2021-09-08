package com.ct.myim.im.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.alibaba.fastjson.JSON;
import com.ct.myim.common.constant.Constants;
import com.ct.myim.common.constant.MsgType;
import com.ct.myim.common.utils.DateUtils;
import com.ct.myim.common.utils.IdUtils;
import com.ct.myim.common.utils.PasswordUtils;
import com.ct.myim.common.utils.ServletUtils;
import com.ct.myim.framework.redis.TokenService;
import com.ct.myim.framework.web.entity.AjaxResult;
import com.ct.myim.framework.web.entity.LoginUser;
import com.ct.myim.im.entity.*;
import com.ct.myim.sockent.manager.WsClientManager;
import com.mongodb.client.result.UpdateResult;
import io.netty.channel.Channel;
import org.apache.tomcat.PeriodicEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Value("${file.download.abspath.prefix}")
    private String fileUrl;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private MsgService msgService;
    @Autowired
    private NoticeService noticeService;

    public AjaxResult registerUser(User user) {
        List<User> list = mongoTemplate.find(new Query(Criteria.where("userName").is(user.getUserName())), User.class);
        if (list != null && list.size() > 0) {
            return AjaxResult.error("注册账号已存在！");
        }
        user.setId(IdUtils.fastSimpleUUID());
        user.setRegisterDate(new Date());
        user.setPassword(PasswordUtils.encryptPwd(user.getPassword()));
        user.setAvatar(Constants.AVATAR_ID);
        String index = PinyinUtil.getFirstLetter(user.getNickName().substring(0, 1), ", ").split(",")[0];
        user.setIndex(index);
        mongoTemplate.insert(user);
        if ("admin".equals(user.getUserName())) {
            return AjaxResult.success("注册成功");
        }
        ContactsUser contactsUser = new ContactsUser();
        contactsUser.setUserName("admin");
        contactsUser.setContactsUserName(user.getUserName());
        contactsUser.setTime(new Date());
        mongoTemplate.insert(contactsUser);
        ContactsUser contactsUser2 = new ContactsUser();
        contactsUser2.setUserName(user.getUserName());
        contactsUser2.setContactsUserName("admin");
        contactsUser2.setTime(new Date());
        mongoTemplate.insert(contactsUser2);

        ContactsUser contactsUser3 = new ContactsUser();
        contactsUser3.setUserName("admingroup");
        contactsUser3.setContactsUserName(user.getUserName());
        contactsUser3.setTime(new Date());
        mongoTemplate.insert(contactsUser3);

        ContactsUser contactsUser4 = new ContactsUser();
        contactsUser4.setUserName(user.getUserName());
        contactsUser4.setContactsUserName("admingroup");
        contactsUser4.setTime(new Date());
        mongoTemplate.insert(contactsUser4);

        ContactsUser contactsUser5 = new ContactsUser();
        contactsUser5.setUserName("friendLog");
        contactsUser5.setContactsUserName(user.getUserName());
        contactsUser5.setTime(new Date());
        mongoTemplate.insert(contactsUser5);

        ContactsUser contactsUser6 = new ContactsUser();
        contactsUser6.setUserName(user.getUserName());
        contactsUser6.setContactsUserName("friendLog");
        contactsUser6.setTime(new Date());
        mongoTemplate.insert(contactsUser6);

        return AjaxResult.success("注册成功");
    }

    /**
     * 根据账号获取用户
     *
     * @param userName
     * @return
     */
    public User getUserByuserName(String userName) {
        User user = mongoTemplate.findOne(new Query(Criteria.where("userName").is(userName)), User.class);
        if (user != null) {
            user.setAvatar(fileUrl + user.getAvatar());
        }
        return user;
    }


    public AjaxResult getUserInfo() {
        return AjaxResult.success(getUserByuserName(tokenService.getLoginUser(ServletUtils.getRequest()).getUser().getUserName()));
    }

    public AjaxResult update(User user) {
        Update update = new Update();
        update.set("nickName", user.getNickName());
        update.set("avatar", user.getAvatar());
        String index = PinyinUtil.getFirstLetter(user.getNickName().substring(0, 1), ", ").split(",")[0];
        update.set("index", index);
        UpdateResult b = mongoTemplate.upsert(new Query(Criteria.where("userName").is(user.getUserName())), update, User.class);
        if (b.getModifiedCount() > 0) {
            noticeService.updateContact(user.getUserName());
        }
        return AjaxResult.success();
    }

    public AjaxResult getUser(String userName) {
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        if (LoginUser.getUserName().equals(userName)) {
            return AjaxResult.success("不能添加自己！");
        }
        User user = mongoTemplate.findOne(new Query(Criteria.where("userName").is(userName).and("isGroup").is(false)), User.class);
        if (user != null) {
            User user1 = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
            ContactsUser contactsUsers = mongoTemplate.findOne(new Query(Criteria.where("userName").is(user1.getUserName()).and("contactsUserName").is(user.getUserName())), ContactsUser.class);
            if (contactsUsers == null) {
                user.setAvatar(fileUrl + user.getAvatar());
                user.setPassword("");
                user.setLoginIp("");
                user.setLoginDate(null);
                return AjaxResult.success(user);
            } else {
                return AjaxResult.success("查询用户已是好友无需添加");
            }
        }
        return AjaxResult.success("用户不存在！");
    }

    public AjaxResult addUserFrieds(String userName, String explain) {
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        User user = mongoTemplate.findOne(new Query(Criteria.where("userName").is(userName).and("isGroup").is(false)), User.class);
        SocketAddUser socketAddUser = new SocketAddUser();
        socketAddUser.setFromUserName(LoginUser.getUserName());
        socketAddUser.setToUserName(user.getUserName());
        socketAddUser.setExplain(explain);
        socketAddUser.setTime(new Date());
        mongoTemplate.insert(socketAddUser);
        //通知被添加人
        noticeService.systemNotification(user.getUserName(), "主人，【" + LoginUser.getNickName() + "】添加你为好友了，快去查看吧！");
        return AjaxResult.success("已发送申请");
    }

    public AjaxResult getFriedsList(int pageSize) {
        List<Dict> dictList = new ArrayList<>();
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        Query query = new Query();
        Criteria criteria = Criteria.where("toUserName").is(LoginUser.getUserName());
        query.addCriteria(criteria);
        PageRequest pageRequest = new PageRequest(pageSize / 10, 10, new Sort(Sort.Direction.DESC, "time"));
        query.with(pageRequest);
        List<SocketAddUser> list = mongoTemplate.find(query, SocketAddUser.class);
        for (SocketAddUser socketAddUser : list) {
            Dict dict = new Dict();
            dict.set("id", socketAddUser.getId());
            User user = mongoTemplate.findOne(new Query(Criteria.where("userName").is(socketAddUser.getFromUserName())), User.class);
            dict.set("url", fileUrl + user.getAvatar());
            dict.set("name", user.getNickName());
            dict.set("explain", socketAddUser.getExplain());
            dict.set("time", DateUtil.formatDateTime(socketAddUser.getTime()));
            dict.set("type", socketAddUser.getType());
            dict.set("state", socketAddUser.getState());
            dictList.add(dict);
        }
        return AjaxResult.success(dictList);
    }


    public AjaxResult handleFried(String id, boolean type) {
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        SocketAddUser socketAddUsers = mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), SocketAddUser.class);
        if (type) {
            ContactsUser contactsUser = new ContactsUser();
            contactsUser.setUserName(socketAddUsers.getFromUserName());
            contactsUser.setContactsUserName(socketAddUsers.getToUserName());
            contactsUser.setTime(new Date());
            mongoTemplate.insert(contactsUser);
            ContactsUser contactsUser2 = new ContactsUser();
            contactsUser2.setUserName(socketAddUsers.getToUserName());
            contactsUser2.setContactsUserName(socketAddUsers.getFromUserName());
            contactsUser2.setTime(new Date());
            mongoTemplate.insert(contactsUser2);
            //通知被添加人
            noticeService.systemNotification(socketAddUsers.getFromUserName(), "主人，【" + LoginUser.getNickName() + "】同意你为好友了，快去查看吧！");
            noticeService.appendContact(socketAddUsers.getToUserName(), socketAddUsers.getFromUserName());
            noticeService.appendContact(socketAddUsers.getFromUserName(), socketAddUsers.getToUserName());
            Update update = new Update();
            update.set("state", MsgType.ADD_FRIEND_YES);
            update.set("type", MsgType.ADD_FRIEND_YES);
            mongoTemplate.updateMulti(new Query(Criteria.where("fromUserName").is(socketAddUsers.getFromUserName())
                    .and("toUserName").is(socketAddUsers.getToUserName())
                    .and("state").is(MsgType.ADD_FRIEND_NO)
            ), update, SocketAddUser.class);
        } else {
            //通知被添加人
            noticeService.systemNotification(socketAddUsers.getFromUserName(), "主人，【" + LoginUser.getNickName() + "】不同意你添加他为好友！");
            Update update = new Update();
            update.set("state", MsgType.ADD_FRIEND_YES);
            update.set("type", MsgType.ADD_FRIEND_NO);
            mongoTemplate.updateMulti(new Query(Criteria.where("fromUserName").is(socketAddUsers.getFromUserName())
                    .and("toUserName").is(socketAddUsers.getToUserName())
                    .and("state").is(MsgType.ADD_FRIEND_NO)
            ), update, SocketAddUser.class);
        }
        return AjaxResult.success();
    }

    /**
     * 删除联系人
     *
     * @param userName
     * @return
     */
    public AjaxResult deleteUser(String userName) {
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        Query query = new Query();
        Criteria criteria = Criteria.where("userName").is(LoginUser.getUserName())
                .and("contactsUserName").is(userName);
        Criteria criteria2 = Criteria.where("userName").is(userName)
                .and("contactsUserName").is(LoginUser.getUserName());
        Criteria criteria3 = new Criteria();
        criteria3.orOperator(criteria, criteria2);
        query.addCriteria(criteria3);
        long count = mongoTemplate.remove(query, ContactsUser.class).getDeletedCount();
        if (count > 0) {
            noticeService.removeContact(userName, LoginUser.getUserName());
        }
        return AjaxResult.success();
    }

    public AjaxResult registerGroup(User user) {
        List<User> list = mongoTemplate.find(new Query(Criteria.where("userName").is(user.getUserName())), User.class);
        if (list != null && list.size() > 0) {
            return AjaxResult.error("群账号已存在！");
        }
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        user.setId(IdUtils.fastSimpleUUID());
        user.setRegisterDate(new Date());
        user.setGroup(true);
        user.setIndex("[2]群组");
        user.setRoot(LoginUser.getUserName());
        mongoTemplate.insert(user);
        ContactsUser contactsUser1 = new ContactsUser();
        contactsUser1.setUserName(user.getUserName());
        contactsUser1.setContactsUserName(LoginUser.getUserName());
        contactsUser1.setTime(new Date());
        mongoTemplate.insert(contactsUser1);
        ContactsUser contactsUser2 = new ContactsUser();
        contactsUser2.setUserName(LoginUser.getUserName());
        contactsUser2.setContactsUserName(user.getUserName());
        contactsUser2.setTime(new Date());
        mongoTemplate.insert(contactsUser2);
        noticeService.appendContact(LoginUser.getUserName(), user.getUserName());
        return AjaxResult.success();
    }

    public AjaxResult getGoupUser(String id, String searchValue, boolean all) {
        List<Dict> list = new ArrayList<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(id));
        if (!all) {
            PageRequest pageRequest = new PageRequest(0, 12, new Sort(Sort.Direction.DESC, "time"));
            query.with(pageRequest);
        } else {
            if(StrUtil.isNotBlank(searchValue)){
                Criteria criteria3 = new Criteria();
                Pattern pattern = Pattern.compile("^.*" + searchValue + ".*$", Pattern.CASE_INSENSITIVE);
                Criteria criteria = Criteria.where("contactsUserName").regex(pattern);
                Criteria criteria2 = Criteria.where("contactsUserName").regex(pattern);
                criteria3.orOperator(criteria, criteria2);
                query.addCriteria(criteria3);
            }
            PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.DESC, "time"));
            query.with(pageRequest);
        }
        List<ContactsUser> userList = mongoTemplate.find(query, ContactsUser.class);
        for (ContactsUser contactsUser : userList) {
            User user = getUserByuserName(contactsUser.getContactsUserName());
            Dict dict = new Dict();
            dict.set("id", user.getUserName());
            dict.set("name", user.getNickName());
            dict.set("avatar", user.getAvatar());
            list.add(dict);
        }
        return AjaxResult.success(list);
    }

    public AjaxResult editGroup(User user) {
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        Update update = new Update();
        update.set("nickName",user.getNickName());
        update.set("avatar",user.getAvatar());
        long count = mongoTemplate.updateFirst(new Query(Criteria.where("userName").is(user.getUserName())), update, User.class).getModifiedCount();
        if(count > 0){
            noticeService.appendContact(LoginUser.getUserName(), user.getUserName());
            return AjaxResult.success();
        }
        return AjaxResult.error();
    }

    /**
     * 删除组用户
     *
     * @return
     */
    public AjaxResult deleteGroupUser(String groupId,String userId) {
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        User user = getUserByuserName(groupId);
        if(!user.getRoot().equals(LoginUser.getUserName())){
            return AjaxResult.error("没有权限删除此用户！");
        }
        Query query = new Query();
        Criteria criteria = Criteria.where("userName").is(groupId)
                .and("contactsUserName").is(userId);
        Criteria criteria2 = Criteria.where("userName").is(userId)
                .and("contactsUserName").is(groupId);
        Criteria criteria3 = new Criteria();
        criteria3.orOperator(criteria, criteria2);
        query.addCriteria(criteria3);
        long count = mongoTemplate.remove(query, ContactsUser.class).getDeletedCount();
        if (count > 0) {
            return AjaxResult.success();
        }
        return AjaxResult.error();
    }

    /**
     * 获取非群组好友
     * @return
     */
    public AjaxResult getNoGroupUser(String groupId,String searchValue){
        List<Dict> list = new ArrayList<>();
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        Query query = new Query(Criteria.where("userName").is(LoginUser.getUserName()));
        if(StrUtil.isNotBlank(searchValue)){
            Criteria criteria3 = new Criteria();
            Pattern pattern = Pattern.compile("^.*" + searchValue + ".*$", Pattern.CASE_INSENSITIVE);
            Criteria criteria = Criteria.where("contactsUserName").regex(pattern);
            Criteria criteria2 = Criteria.where("contactsUserName").regex(pattern);
            criteria3.orOperator(criteria, criteria2);
            query.addCriteria(criteria3);
        }
        List<ContactsUser> userList = mongoTemplate.find(query, ContactsUser.class);
        for (ContactsUser contactsUser : userList) {
            User user = getUserByuserName(contactsUser.getContactsUserName());
            if(!user.isGroup() && !user.getUserName().equals("admin")){
                ContactsUser user1 = mongoTemplate.findOne(new Query(Criteria.where("userName").is(groupId).and("contactsUserName").is(user.getUserName())), ContactsUser.class);
                if(user1== null){
                    Dict dict = new Dict();
                    dict.set("id", user.getUserName());
                    dict.set("name", user.getNickName());
                    dict.set("avatar", user.getAvatar());
                    list.add(dict);
                }
            }
        }
        return AjaxResult.success(list);
    }

    public AjaxResult addGroupUserList(String groupId,String ids){
        if(StrUtil.isBlank(ids) || StrUtil.isBlank(groupId)){
            return AjaxResult.error();
        }
        User LoginUser = tokenService.getLoginUser(ServletUtils.getRequest()).getUser();
        User group = getUserByuserName(groupId);
        List<String> split = StrUtil.split(ids, ",");
        for (String userId : split) {
            ContactsUser contactsUser = new ContactsUser();
            contactsUser.setUserName(userId);
            contactsUser.setContactsUserName(groupId);
            contactsUser.setTime(new Date());
            mongoTemplate.insert(contactsUser);
            ContactsUser contactsUser2 = new ContactsUser();
            contactsUser2.setUserName(groupId);
            contactsUser2.setContactsUserName(userId);
            contactsUser2.setTime(new Date());
            mongoTemplate.insert(contactsUser2);
            noticeService.systemNotification(userId,"主人，【" + LoginUser.getNickName() + "】拉你进【 " + group.getNickName() + "】群了，快去查看吧！");
            noticeService.appendContact(userId,groupId);
        }
        return AjaxResult.success();
    }
}
