<script setup>
import { RouterView } from 'vue-router'
import router from "@/router";
import {useUserStore} from "@/stores/user";
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import {nextTick, ref} from "vue";
import config from "../../config";

const userStore = useUserStore()
let user = userStore.getUser
const activePath = router.currentRoute.value.path
const name = ref('')  
name.value = router.currentRoute.value.query.name || ''
const childRef = ref()
const menus = ref([])


const logout = () => {
  request.get('/logout/' + user.uid).then(res => {
    if (res.code === '200') {
      userStore.logout()
	  window.location.href="/front"
    } else {
      ElMessage.error(res.msg)
    }
  })
}


const getAvatarHandler = (avatar) => {
  user.avatar = avatar
}

const search = () => {
  router.push('/front/repairs?name=' + name.value)
}

//消息通知
const unread = ref([])
const getUnRead = () => {
  request.get('/messages/unread').then(res => {
    unread.value = res.data
  })
}
getUnRead()

const clickRead = () => {
  router.push('/front/messages?t=' + new Date().getTime())
}

</script>

<template>
  <div>
    <div style="height: 60px; line-height: 60px; border-bottom: 1px solid #eee;text-align: right;">
      <div style="font-size: 16px; color: #CC9999;" v-if="user.id">
        <el-badge class="badge" :value="unread.length" :max="10" style="cursor: pointer" @click="clickRead">
          <el-icon style="color: #666" size="25"><Bell /></el-icon>
        </el-badge>
        <el-avatar :size="30" :src="user.avatar" style="margin-top: 10px;" />
        你好，{{ user.name }}
        <a href="javascript:void(0)" @click="router.push('/person')" class="loginCls">修改个人资料</a>
        <a href="javascript:void(0)" @click="router.push('/password')" class="loginCls">重置密码</a>
        <a href="javascript:void(0)" @click="logout" class="loginCls">退出登录</a>
      </div>
      <div style="flex: 3; font-size: 16px; color: #CC9999; margin-left: 180px;" v-else>
        <a href="javascript:void(0)" @click="router.push('/login')" class="loginCls">登录</a>
        <a href="javascript:void(0)" @click="router.push('/register-member')" class="loginCls">用户注册</a>
        <a href="javascript:void(0)" @click="router.push('/register-worker')" class="loginCls">维修工注册</a>
      </div>
    </div>

    <div style="display: flex; height: 60px; line-height: 60px; border-bottom: 1px solid #eee;background-color: #CC9999;">
      <div style="flex: 1;width: 220px; padding-left: 30px">
        <div style="font-size: 20px; color: #FFFFFF; font-weight: bold">{{config.projectName}}</div>
      </div>
      <div style="flex: 3; display: flex">
        <el-menu :default-active="activePath" mode="horizontal" router style="border: none; height: 100%; width: 100%;background-color: #CC9999;">
          <el-menu-item index="/front/home">首页</el-menu-item>
          <el-menu-item index="/front/notice">系统公告</el-menu-item>
          <el-menu-item index="/front/repairs">维修单列表</el-menu-item>
          <el-dropdown v-if="user.id!=null && user.role!='ADMIN'">
            <el-menu-item>个人中心</el-menu-item>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="user.id!=null && user.role=='member'"><div @click="router.push('/repairs')">发布维修单</div></el-dropdown-item>
                <el-dropdown-item v-if="user.id!=null && user.role=='member'"><div @click="router.push('/orders')">查看订单情况</div></el-dropdown-item>
                <el-dropdown-item v-if="user.id!=null && user.role=='worker'"><div @click="router.push('/front/orders')" >维修接单记录</div></el-dropdown-item>
                <el-dropdown-item v-if="user.id!=null && user.role=='worker'"><div @click="router.push('/material')" >管理维修材料</div></el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-menu-item index="/home" v-if="user.id!=null && user.role=='ADMIN'">后台管理</el-menu-item>
        </el-menu>
      </div>
      <div style="flex: 2;">
        <!-- 搜索条 -->
          <el-input style="width: 250px" placeholder="搜索维修单" v-model="name" clearable size="large"></el-input>
          <el-button style="margin-left: 5px;background-color: #FFFFFF;color: #CC9999;" @click="search" size="large">搜索</el-button>
      </div>
    </div>

    <div style="width: 100%; margin: 0 auto;min-height: 600px;">
      <router-view v-slot="{ Component }">
        <component @getAvatar="getAvatarHandler" @getUnread="getUnRead" ref="childRef" :is="Component" />
      </router-view>
    </div>


    <div style="height: 100px; line-height: 100px; border-top: 1px solid rgba(208,208,208,0.08);text-align: center;background-color: #CC9999;color: #FFFFFF;">
      <span>{{config.projectName}}</span>
    </div>
  </div>
</template>

<style scoped>
  .badge {
    margin-top: 10px;
    margin-right: 40px;
  }
  :deep(.el-badge__content.is-fixed) {
    top: 10px !important;
  }

  .content {
    text-align: center;
  }

  .el-menu-item{
    background-color: #CC9999;
    color: #FFFFFF;
  }

  .el-menu--horizontal>.el-menu-item.is-active{
    color: #ffffff !important;
    border-bottom: 2px solid #ffffff !important;
  }

  .loginCls{
    margin-left: 25px;
    text-decoration: none;
    color: #CC9999;
  }
  .loginCls a {
    text-decoration: none;
    color: #333;
    transition: color 0.3s;
  }

  .loginCls:hover {
    color: #f00;
  }

  .loginCls:visited {
    color: #999;
  }

  .loginCls::after {
    content: '';
    width: 0;
    height: 1px;
    background-color: #f00;
    transition: width 0.3s;
  }

  .loginCls:hover::after {
    width: 100%;
  }
</style>
