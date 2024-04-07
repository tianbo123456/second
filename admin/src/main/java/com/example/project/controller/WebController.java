package com.example.project.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.example.project.common.Result;
import cn.hutool.core.collection.CollectionUtil;
import com.example.project.common.*;
import com.example.project.common.annotation.AutoLog;
import com.example.project.controller.domain.LoginDTO;
import com.example.project.controller.domain.UserRequest;
import com.example.project.entity.*;
import com.example.project.mapper.MessagesMapper;
import com.example.project.service.IUserService;
import com.example.project.service.INoticeService;
import com.example.project.utils.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.example.project.utils.SpringContextUtil;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.example.project.service.IMemberService;
import com.example.project.entity.Member;
import com.example.project.mapper.MemberMapper;
import com.example.project.service.IWorkerService;
import com.example.project.entity.Worker;
import com.example.project.mapper.WorkerMapper;
import com.example.project.service.ICategoryService;
import com.example.project.entity.Category;
import com.example.project.service.IRepairsService;
import com.example.project.entity.Repairs;
import com.example.project.service.IPreordersService;
import com.example.project.entity.Preorders;
import com.example.project.service.IOrdersService;
import com.example.project.entity.Orders;
import com.example.project.service.IMessagesService;
import com.example.project.entity.Messages;
import com.example.project.service.ICommentsService;
import com.example.project.entity.Comments;
import com.example.project.service.IMaterialService;
import com.example.project.entity.Material;
import com.example.project.service.IBannerService;

@Api(tags = "无权限接口列表")
@RestController
@Slf4j
public class WebController {

    @Resource
    IUserService userService;

    @Resource
    INoticeService noticeService;

    @Resource
    IMemberService memberService;
    @Resource
    IWorkerService workerService;

    @Resource
    ICategoryService categoryService;
    @Resource
    IRepairsService repairsService;
    @Resource
    IPreordersService preordersService;
    @Resource
    IOrdersService ordersService;
    @Resource
    IMessagesService messagesService;
    @Resource
    ICommentsService commentsService;
    @Resource
    IMaterialService materialService;

    @Resource
    IBannerService bannerService;


    @GetMapping("/getCurrentUser")
    public User getLoginUser() {
        return SessionUtils.getUser();
    }

    @GetMapping(value = "/")
    @ApiOperation(value = "版本校验接口")
    public String version() {
        String ver = "partner-back-0.0.1-SNAPSHOT";  // 应用版本号
        Package aPackage = WebController.class.getPackage();
        String title = aPackage.getImplementationTitle();
        String version = aPackage.getImplementationVersion();
        if (title != null && version != null) {
            ver = String.join("-", title, version);
        }
        return ver;
    }

    @AutoLog("用户登录")
    @ApiOperation(value = "用户登录接口")
    @PostMapping("/login")
    public Result login(@RequestBody UserRequest user) {
        long startTime = System.currentTimeMillis();
        LoginDTO res = userService.login(user);
        log.info("登录花费时间 {}ms", System.currentTimeMillis() - startTime);
        return Result.success(res);
    }

    @AutoLog("用户退出登录")
    @ApiOperation(value = "用户退出登录接口")
    @GetMapping("/logout/{uid}")
    @SaIgnore
    public Result logout(@PathVariable String uid) {
        userService.logout(uid);
        return Result.success();
    }

    @AutoLog("用户注册")
    @ApiOperation(value = "用户注册接口")
    @PostMapping("/register")
    public Result register(@RequestBody UserRequest user) {
        userService.register(user);
        return Result.success();
    }

    @AutoLog("用户重置密码")
    @ApiOperation(value = "密码重置接口")
    @PostMapping("/password/reset")
    public Result passwordReset(@RequestBody UserRequest userRequest) {
        String newPass = userService.passwordReset(userRequest);
        return Result.success(newPass);
    }


    @AutoLog("用户修改密码")
    @PostMapping("/password/change")
    public Result passwordChange(@RequestBody UserRequest userRequest) {
        userService.passwordChange(userRequest);
        return Result.success();
    }


    @AutoLog("编辑用户")
    @PutMapping("/updateUser")
    public Result updateUser(@RequestBody User user) {
        Object loginId = StpUtil.getLoginId();
        if (!loginId.equals(user.getUid())) {
            return Result.error("无权限");
        }
        User dbUser = SessionUtils.getUser();
        if (dbUser.getRole().equals("member")) {
            MemberMapper mapper = SpringContextUtil.getBean(MemberMapper.class);
            QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", dbUser.getId());
            Member member = mapper.selectOne(queryWrapper);
            member.setName(user.getName());
            mapper.updateById(member);
        }
        if (dbUser.getRole().equals("worker")) {
            WorkerMapper mapper = SpringContextUtil.getBean(WorkerMapper.class);
            QueryWrapper<Worker> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", dbUser.getId());
            Worker worker = mapper.selectOne(queryWrapper);
            worker.setName(user.getName());
            mapper.updateById(worker);
        }
        userService.updateById(user);
        return Result.success(user);
    }

    @AutoLog("修改头像")
    @PutMapping("/updateAvatar")
    public Result updateAvatar(@RequestBody User user) {
        Object loginId = StpUtil.getLoginId();
        if (!loginId.equals(user.getUid())) {
            return Result.error("无权限");
        }
        User dbUser = userService.getById(user.getId());
        dbUser.setAvatar(user.getAvatar());
        userService.updateById(dbUser);
        return Result.success(user);
    }

    @GetMapping("/front/user/list")
    @SaIgnore
    public Result findAllUser() {
        return Result.success(userService.list());
    }

    @AutoLog("公告浏览")
    @GetMapping("/front/notice/{id}")
    @SaIgnore
    public Result findOneNotice(@PathVariable Integer id) {
        return Result.success(noticeService.getById(id));
    }

    @AutoLog("所有公告")
    @GetMapping("/front/notice/list")
    @SaIgnore
    public Result findAllNotice() {
        return Result.success(noticeService.list());
    }


    @AutoLog("系统公告查询")
    @GetMapping("/front/notice/page")
    @SaIgnore
    public Result findPageNotice(
            @RequestParam(defaultValue = "") String name,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<Notice>().orderByDesc("id");
        queryWrapper.like(!"".equals(name), "name", name);
        return Result.success(noticeService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @AutoLog("系统公告保存更新")
    @PutMapping("/front/notice/update")
    @SaIgnore
    public Result saveOrUpdateNotice(@RequestBody Notice notice) {
        return Result.success(noticeService.saveOrUpdate(notice));
    }

    @AutoLog("维修单列表查询")
    @GetMapping("/front/repairs/page")
    @SaIgnore
    public Result findPageRepairs(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String category_id,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        QueryWrapper<Repairs> queryWrapper = new QueryWrapper<Repairs>().orderByDesc("id");
        queryWrapper.like(!"".equals(name), "name", name);
        queryWrapper.like(!"".equals(category_id), "category_id", category_id);
        return Result.success(repairsService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @AutoLog("维修单列表保存更新")
    @PutMapping("/front/repairs/update")
    @SaIgnore
    public Result saveOrUpdateRepairs(@RequestBody Repairs repairs) {
        return Result.success(repairsService.saveOrUpdate(repairs));
    }

    @AutoLog("待接单记录查询")
    @GetMapping("/front/preorders/page")
    @SaIgnore
    public Result findPagePreorders(
            @RequestParam(defaultValue = "") String name,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        QueryWrapper<Preorders> queryWrapper = new QueryWrapper<Preorders>().orderByDesc("id");
        queryWrapper.like(!"".equals(name), "name", name);
        return Result.success(preordersService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @AutoLog("待接单记录保存更新")
    @PutMapping("/front/preorders/update")
    @SaIgnore
    public Result saveOrUpdatePreorders(@RequestBody Preorders preorders) {
        return Result.success(preordersService.saveOrUpdate(preorders));
    }

    @AutoLog("维修单接单列表查询")
    @GetMapping("/front/orders/page")
    @SaIgnore
    public Result findPageOrders(
            @RequestParam(defaultValue = "") String name,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<Orders>().orderByDesc("id");
        queryWrapper.like(!"".equals(name), "name", name);
        return Result.success(ordersService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @AutoLog("维修单接单列表保存更新")
    @PutMapping("/front/orders/update")
    @SaIgnore
    public Result saveOrUpdateOrders(@RequestBody Orders orders) {
        return Result.success(ordersService.saveOrUpdate(orders));
    }


    @AutoLog("用户浏览")
    @GetMapping("/front/member/{id}")
    @SaIgnore
    public Result findOneMember(@PathVariable Integer id) {
        return Result.success(memberService.getById(id));
    }

    @AutoLog("根据userId查询用户")
    @GetMapping("/front/member/user/{id}")
    @SaIgnore
    public Result findOneMemberByUser(@PathVariable Integer id) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        return Result.success(memberService.getOne(queryWrapper));
    }

    @AutoLog("更新用户信息")
    @PutMapping("/front/member/update")
    @SaIgnore
    public Result updateMember(@RequestBody Member member) {
        return Result.success(memberService.saveOrUpdate(member));
    }

    @GetMapping("/front/member/list")
    @SaIgnore
    public Result findAllMember() {
        return Result.success(memberService.list());
    }

    @AutoLog("维修工浏览")
    @GetMapping("/front/worker/{id}")
    @SaIgnore
    public Result findOneWorker(@PathVariable Integer id) {
        return Result.success(workerService.getById(id));
    }

    @AutoLog("根据userId查询维修工")
    @GetMapping("/front/worker/user/{id}")
    @SaIgnore
    public Result findOneWorkerByUser(@PathVariable Integer id) {
        QueryWrapper<Worker> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        return Result.success(workerService.getOne(queryWrapper));
    }

    @AutoLog("更新维修工信息")
    @PutMapping("/front/worker/update")
    @SaIgnore
    public Result updateWorker(@RequestBody Worker worker) {
        return Result.success(workerService.saveOrUpdate(worker));
    }

    @GetMapping("/front/worker/list")
    @SaIgnore
    public Result findAllWorker() {
        return Result.success(workerService.list());
    }

    @AutoLog("维修类型浏览")
    @GetMapping("/front/category/{id}")
    @SaIgnore
    public Result findOneCategory(@PathVariable Integer id) {
        return Result.success(categoryService.getById(id));
    }

    @AutoLog("维修类型列表")
    @GetMapping("/front/category/list")
    @SaIgnore
    public Result findAllCategory() {
        return Result.success(categoryService.list());
    }

    @AutoLog("维修单浏览")
    @GetMapping("/front/repairs/{id}")
    @SaIgnore
    public Result findOneRepairs(@PathVariable Integer id) {
        return Result.success(repairsService.getById(id));
    }

    @AutoLog("维修单列表")
    @GetMapping("/front/repairs/list")
    @SaIgnore
    public Result findAllRepairs() {
        return Result.success(repairsService.list());
    }

    @AutoLog("待接单记录浏览")
    @GetMapping("/front/preorders/{id}")
    @SaIgnore
    public Result findOnePreorders(@PathVariable Integer id) {
        return Result.success(preordersService.getById(id));
    }

    @AutoLog("待接单记录列表")
    @GetMapping("/front/preorders/list")
    @SaIgnore
    public Result findAllPreorders() {
        return Result.success(preordersService.list());
    }

    @AutoLog("订单浏览")
    @GetMapping("/front/orders/{id}")
    @SaIgnore
    public Result findOneOrders(@PathVariable Integer id) {
        return Result.success(ordersService.getById(id));
    }

    @AutoLog("订单列表")
    @GetMapping("/front/orders/list")
    @SaIgnore
    public Result findAllOrders() {
        return Result.success(ordersService.list());
    }

    @AutoLog("消息通知浏览")
    @GetMapping("/front/messages/{id}")
    @SaIgnore
    public Result findOneMessages(@PathVariable Integer id) {
        return Result.success(messagesService.getById(id));
    }

    @AutoLog("消息通知列表")
    @GetMapping("/front/messages/list")
    @SaIgnore
    public Result findAllMessages() {
        return Result.success(messagesService.list());
    }

    @AutoLog("订单评论浏览")
    @GetMapping("/front/comments/{id}")
    @SaIgnore
    public Result findOneComments(@PathVariable Integer id) {
        return Result.success(commentsService.getById(id));
    }

    @AutoLog("订单评论列表")
    @GetMapping("/front/comments/list")
    @SaIgnore
    public Result findAllComments() {
        return Result.success(commentsService.list());
    }

    @AutoLog("维修材料浏览")
    @GetMapping("/front/material/{id}")
    @SaIgnore
    public Result findOneMaterial(@PathVariable Integer id) {
        return Result.success(materialService.getById(id));
    }

    @AutoLog("维修材料列表")
    @GetMapping("/front/material/list")
    @SaIgnore
    public Result findAllMaterial() {
        return Result.success(materialService.list());
    }

    @GetMapping("/front/banner/list")
    @SaIgnore
    public Result findAllBanner() {
        return Result.success(bannerService.list());
    }

    @AutoLog("修改preorders")
    @PostMapping("/front/preorders/update")
    @SaIgnore
    public Result updatePreorders(@RequestBody Preorders preorders) {
        //先删除当前用户数据
        preordersService.remove(new QueryWrapper<Preorders>().eq("user_id", preorders.getUserId()));

        Preorders dbPreorders = preordersService.getOne(new QueryWrapper<Preorders>().eq("name", preorders.getName()).eq("user_id", preorders.getUserId()));
        if (dbPreorders == null) {
            return Result.success(preordersService.save(preorders));
        }
        return Result.success();
    }

    @AutoLog("删除preorders")
    @DeleteMapping("/front/preorders/{id}")
    @SaIgnore
    public Result deletePreorders(@PathVariable Integer id) {
        return Result.success(preordersService.removeById(id));
    }


    @AutoLog("更新orders")
    @PostMapping("/front/orders/update")
    @SaIgnore
    public Result updateOrders(@RequestBody Orders orders) {

        //接单处理：发送接单通知给用户，更新维修单状态
        if(orders.getId()==null){
            User user = SessionUtils.getUser();
            Repairs repairs = repairsService.getById(orders.getGoodids());
            MessagesMapper messagesMapper = SpringContextUtil.getBean(MessagesMapper.class);
            //发送接单消息
            Messages messages = new Messages();
            messages.setContent("你的维修单已被接单："+repairs.getName());
            messages.setUserId(orders.getBizUserId());
            messages.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            messagesMapper.insert(messages);

            //修改维修单状态
            repairs.setStateRadio("已接单");
            repairsService.updateById(repairs);
        }


        return Result.success(ordersService.saveOrUpdate(orders));
    }

    @AutoLog("删除orders")
    @DeleteMapping("/front/orders/{id}")
    @SaIgnore
    public Result deleteOrders(@PathVariable Integer id) {
        return Result.success(ordersService.removeById(id));
    }

    @AutoLog("取消orders")
    @PutMapping("/front/orders/cancel/{id}")
    @SaIgnore
    public Result cancelOrders(@PathVariable Integer id) {
        Orders orders = ordersService.getById(id);
        orders.setStateRadio("已取消");
        return Result.success(ordersService.updateById(orders));
    }


    @AutoLog("确认订单")
    @PutMapping("/front/orders/confirm/{id}")
    @SaIgnore
    public Result confirmOrders(@PathVariable Integer id) {
        Orders orders = ordersService.getById(id);
        orders.setStateRadio("已完成");
        return Result.success(ordersService.updateById(orders));
    }



    @AutoLog("查询订单评价列表")
    @GetMapping("/front/comments/tree")
    @SaIgnore
    public Result commentsTree(@RequestParam Integer repairsId) {
        List<User> userList = userService.list();
        List<Comments> list = commentsService.list(new QueryWrapper<Comments>().eq("repairs_id", repairsId));
        // 给comments里的每个对象设置一个user属性
        list = list.stream().peek(comments -> userList.stream()
                .filter(user -> user.getId().equals(comments.getUserId())).findFirst().ifPresent(comments::setUser))
                .collect(Collectors.toList());
        List<Comments> first = list.stream().filter(comments -> comments.getPid() == null).collect(Collectors.toList());// 一级评论
        for (Comments comments : first) {
            Integer pid = comments.getId();
            List<Comments> second = list.stream().filter(comments1 -> Objects.equals(pid, comments1.getPid())).collect(Collectors.toList());// 二级评论

            // 给second里的每个对象设置一个puser属性
            second = second.stream().peek(comments1 -> userList.stream()
                    .filter(user -> user.getId().equals(comments1.getPuserId())).findFirst()
                    .ifPresent(comments1::setPUser)).collect(Collectors.toList());
            comments.setChildren(second);  // 一级评论设置二级评论
        }
        return Result.success(first);
    }

    @AutoLog("更新单条订单评价")
    @PostMapping("/front/comments/update")
    @SaIgnore
    public Result updateComments(@RequestBody Comments comments) {
        return Result.success(commentsService.saveOrUpdate(comments));
    }


    @AutoLog("更新多条订单评价")
    @PostMapping("/front/comments/update/{goodids}")
    @SaIgnore
    public Result updateMultiComments(@RequestBody Comments comments, @PathVariable String goodids) {
        String[] array = goodids.split(",");
        for (String goodid : array) {
            comments.setRepairsId(Integer.parseInt(goodid));
            commentsService.saveOrUpdate(comments);
            comments.setId(null);
        }
        return Result.success();
    }

    @AutoLog("删除订单评价")
    @DeleteMapping("/front/comments/{id}")
    @SaIgnore
    public Result deleteComments(@PathVariable Integer id) {
        return Result.success(commentsService.removeById(id));
    }


    @AutoLog("根据订单ID查询评论")
    @GetMapping("/front/comments/order/{id}")
    @SaIgnore
    public Result findOneCommentsByOrderId(@PathVariable Integer id) {
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orders_id", id);
        List<Comments> list = commentsService.list(queryWrapper);
        return Result.success(list.get(0));
    }

    @AutoLog("催单")
    @GetMapping("front/chasorder/{id}")
    @SaIgnore
    public Result chasorder(@PathVariable Integer id) {
        Orders order = ordersService.getById(id);
        MessagesMapper messagesMapper = SpringContextUtil.getBean(MessagesMapper.class);
        //发送接单消息
        Messages messages = new Messages();
        messages.setContent("你的接单被用户催促了，抓紧时间咯！单号："+order.getName());
        messages.setUserId(order.getUserId());
        messages.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        messagesMapper.insert(messages);
        return Result.success();
    }

    @AutoLog("查看评论")
    @GetMapping("/front/comments/orders/{id}")
    @SaIgnore
    public Result showComments(@PathVariable Integer id) {
        Comments comments = commentsService.getOne(new QueryWrapper<Comments>().eq("orders_id", id));
        return Result.success(comments.getContent());
    }
}