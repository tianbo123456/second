<script setup>
  import router from "@/router";
  import request from "@/utils/request";
  import {ElMessage} from "element-plus";
  import {onMounted, reactive, ref} from "vue";
  import {useUserStore} from "@/stores/user";
  import '@wangeditor/editor/dist/css/style.css' // 引入 css
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
  import config from "../../../config";

  const userStore = useUserStore()
  const token = userStore.getBearerToken
  const auths =  userStore.getAuths
  const user = userStore.getUser
  const pageNum = ref(1)
  const pageSize = ref(5)
  const total = ref(0)

    //判断用户是否登录
  if(user.id==null){
      router.push('/login')
  }

  const content = ref('')
  const viewShow = ref(false)
  const view = (value) => {
    viewShow.value = true
    content.value = value
  }

  const id = router.currentRoute.value.query.id
  let name = router.currentRoute.value.query.name
  const state = reactive({
    tableData:[],
    form:{},
  })

  const load = () => {
    request.get('/front/preorders/list').then(res => {
      state.tableData = res.data
      state.tableData = state.tableData.filter((item) => item.userId === user.id);

    })
  }
  load()  // 调用 load方法拿到后台数据

  //轮播图
  request.get('/front/banner/list').then(res => {
      state.rotationList = res.data
      state.rotationList = state.rotationList.filter((item) => item.indexRadio === '否');
  })

  //计算购物车总价
  const getTotalPrice =() =>{
      const totalPrice = state.tableData.reduce((sum, item) => {
          return sum + item.price*1;
      }, 0);
      return totalPrice.toFixed(2);
  }

  //修改购物车
  const changeNum = (row) =>{
      request.request({
          url: '/front/preorders/update',
          method: 'post',
          data: {
              id:row.id,
              name:row.name,
              userId:user.id,
              num:row.num,
          }
      }).then(res => {
          if (res.code === '200') {
              ElMessage.success('修改成功')
          } else {
              ElMessage.error(res.msg)
          }
      })
  }

  //删除购物车
  const deleteCart =(id) =>{
      request.delete('/front/preorders/' + id).then(res => {
          if (res.code === '200') {
              ElMessage.success('操作成功')
              load()  // 刷新表格数据
          } else {
              ElMessage.error(res.msg)
          }
      })
  }

  //批量删除购物车
  const deleteBatchCart =(ids) =>{
      ids.forEach((e)=>{
          request.delete('/front/preorders/' + e).then(res => {})
      })
  }



  const validateForm = () =>{
      if(state.tableData.length==0){
          ElMessage.error('购物车为空！请先添加')
          return false;
      }
      return true
  }

  state.orders = {}
  state.pList = ``
  //保存订单
  const saveOrder = () =>{
      const flag = validateForm()
      if(!flag)return;

      const groups = state.tableData.reduce((acc, cur) => {
          const bizUser = cur.bizUserId;
          if (!acc[bizUser]) {
              acc[bizUser] = [];
          }
          acc[bizUser].push(cur);
          return acc;
      }, {});
      const result = Object.values(groups);

      result.forEach((array)=>{
          state.pList = ``

          state.orders.name = generateOrderNumber()
          state.pList += `接单明细：<br/>`;
          state.pList += `<ul>`
          state.pList += '<li>接单人姓名：'+state.form.name+'</li>'
          state.pList += '<li>接单人手机：'+state.form.phone+'</li>'
          const goodids = [];
          array.forEach((e,i)=>{
              goodids.push(e.goodid)
              state.pList += `<li>标题：${e.name}</li>`
          })
          state.orders.goodids = goodids.join(',');
          state.pList += `</ul>`
          state.orders.content = state.pList
          state.orders.stateRadio = '已接单'
          state.orders.userId = user.id
          state.orders.amount = array.reduce((acc, item) => {
              return acc + (item.price * 1);
          }, 0).toFixed(2);

          state.orders.bizUserId = array[0].bizUserId
          request.request({
              url: '/front/orders/update',
              method: 'post',
              data: state.orders
          }).then(res => {
              if (res.code === '200') {
                  //清空购物车车数据
                  const ids = state.tableData.map(obj => obj.id);
                  deleteBatchCart(ids)

                  router.push('/front/orders')
              } else {
                  ElMessage.error(res.msg)
              }
          })
      });

  }

  //生成订单号
  function generateOrderNumber() {
      const date = new Date();
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      const orderNumber = `${year}${month}${day}${hours}${minutes}${seconds}`;
      return orderNumber;
  }

  state.userOptions = []
  request.get('/front/user/list').then(res => state.userOptions = res.data)


</script>

<template>
  <div>

          <!-- 轮播图 -->
          <div>
                  <div style="width: 100%">
                      <el-carousel :interval="5000" arrow="always" height="200px">
                          <el-carousel-item v-for="item in state.rotationList" :key="item">
                              <a :href="item.url" target="_blank"><img :src="item.img" alt="" style="width: 100%; height: 100%"></a>
                          </el-carousel-item>
                      </el-carousel>
                  </div>
          </div>


      <div style="width:85%;margin: 0 auto;margin-bottom: 50px;">
        <div style="padding-bottom: 15px ;margin-top: 20px;text-align: left;">
            <span style="font-size: 14px;margin-right: 20px;">当前位置：首页 > 维修接单</span>
        </div>

        <div style="padding-bottom: 15px ;border-bottom: 3px solid #CC9999; text-align: left;display: flex;">
            <span style="font-weight: bold; font-size: 24px;float: left;flex: 3;color: #CC9999;">维修接单</span>
        </div>

          <el-table :data="state.tableData" style="width: 100%;margin-top: 20px;" stripe border :header-cell-class-name="'headerBg'">
            <el-table-column prop="id" label="序号" width="80px;">
                <template #default="scope">
                    {{ scope.$index + 1 }}
                </template>
                </el-table-column>
                <el-table-column label="设备情况图片" width="200px;">
                    <template #default="scope" >
                        <img :src="scope.row.img" alt="设备情况图片" style="width: 200px; height: 150px;">
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="标题" width="320px;"></el-table-column>
                <el-table-column label="发单用户" width="160px;">
                    <template #default="scope">
                        <span v-if="scope.row.bizUserId">{{ state.userOptions.find(v => v.id === scope.row.bizUserId) ? state.userOptions.find(v => v.id === scope.row.bizUserId).name : '' }}</span>
                    </template>
                </el-table-column>

                <el-table-column label="操作">
                    <template #default="scope">
                        <a href="javascript:void(0)" @click="deleteCart(scope.row.id)" class="delete-link">删除</a>
                    </template>
                </el-table-column>
        </el-table>

        <!-- 填写订单信息 -->
        <div class="order-list" style="margin-top: 30px;">
          <div style="padding-bottom: 15px ;border-bottom: 3px solid #CC9999; text-align: left;display: flex;">
            <span style="font-weight: bold; font-size: 20px;float: left;flex: 3;color: #CC9999;">填写接单信息</span>
          </div>
          <div style="margin-top: 20px;margin-left: 50px;">
            <span>接单人姓名：</span>
            <span><el-input v-model="state.form.name" placeholder="请填写接单人姓名" style="width: 400px;"></el-input></span>
          </div>
          <div style="margin-top: 20px;margin-left: 50px;">
            <span>接单人手机：</span>
            <span><el-input v-model="state.form.phone" placeholder="请填写接单人手机" style="width: 400px;"></el-input></span>
          </div>
        </div>



        <div class="detail-btn" style="display: flex; justify-content: center; align-items: center; height: 100%;margin-top: 30px;">
              <div @click="saveOrder"><i class="el-icon-date"></i>确认接单</div>
          </div>
    </div>

      <!-- 支付框 -->
    <el-dialog v-model="dialogFormVisible" title="订单支付" width="40%">
        <div style="text-align: center;">
            总金额：<span style="font-size: 25px;color: red;">{{ getTotalPrice() }}</span><span style="margin-left: 20px;">请扫码下方二维码支付</span>
        </div>
        <div style="margin-top: 10px;text-align: center;">
            <img :src="state.qrcode" alt="支付二维码" style="width: 200px;height: 220px;">
        </div>
        <div style="margin-top: 10px;text-align: center;">
            <el-button type="primary" @click="saveOrder">确认购买</el-button>
        </div>
    </el-dialog>

  </div>
</template>

<style>
    .total-container {
        margin-top: 20px;
        text-align: right;
        margin-right: 20px;
    }

    .total-label {
        font-weight: bold;
    }

    .total-price {
        color: red;
        font-size: 25px;
        font-weight: bold;

    }

    a.delete-link {
        color: #ff0000;
        text-decoration: none;
    }

    a.delete-link:hover {
        text-decoration: underline;
    }


    .pay-list {
        width: 100%;
        margin: 0 auto;
        display: flow;
    }

    .detail-btn{
        width: 100%;
        margin: 0 auto;
        display: flow;
    }

    .detail-btn div {
        width: 160px;
        height: 50px;
        line-height: 14px;
        padding: 18px 0;
        font-size: 16px;
        box-sizing: border-box;
        background: #CC9999;
        color: #fff;
        text-align: center;
        margin-right: 15px;
        cursor: pointer;
        border-radius: 20px;
    }


    .el-dialog {
        background-color: #fff; /* 设置对话框的背景颜色 */
        border: 1px solid #ccc; /* 设置对话框的边框颜色 */
        border-radius: 4px; /* 设置对话框的边框圆角 */
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15); /* 设置对话框的阴影效果 */
        padding: 20px; /* 设置对话框的内边距 */
        box-sizing: border-box; /* 防止内边距和边框超出容器 */
    }

    .el-dialog__header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px;
        background-color: #CC9999;
        border-bottom: 1px solid #ccc;
    }

    .el-dialog__header > * {
        margin-right: 10px;
        color: #ffffff;
    }

    .el-dialog__title {
        font-size: 18px;
        font-weight: bold;
    }

    .el-dialog__body {
        padding: 20px;
    }

    .el-dialog__footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px;
        border-top: 1px solid #ccc;
    }

    .el-dialog__footer > * {
        margin-left: 10px;
    }

    .headerBg {
        background: #CC9999!important;
        color: #ffffff!important;
    }

</style>