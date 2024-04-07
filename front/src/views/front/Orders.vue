<script setup>
  import router from "@/router";
  import request from "@/utils/request";
  import {ElMessage} from "element-plus";
  import {onMounted, reactive, ref, nextTick} from "vue";
  import {useUserStore} from "@/stores/user";
  import '@wangeditor/editor/dist/css/style.css' // 引入 css
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

  const userStore = useUserStore()
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

  const state = reactive({
    tableData:[],
    form:{},
  })

  const load = () => {
    request.get('/front/orders/list').then(res => {
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

  //删除订单
  const deleteOrders =(id) =>{
      request.delete('/front/orders/' + id).then(res => {
          if (res.code === '200') {
              ElMessage.success('操作成功')
              load()  // 刷新表格数据
          } else {
              ElMessage.error(res.msg)
          }
      })
  }

  //取消订单
  const cancelOrders =(id) =>{
      request.put('/front/orders/cancel/' + id).then(res => {
          if (res.code === '200') {
              ElMessage.success('操作成功')
              load()  // 刷新表格数据
          } else {
              ElMessage.error(res.msg)
          }
      })
  }

  //确认订单
  const confirmOrders =(id) =>{
    request.put('/front/orders/confirm/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()  // 刷新表格数据
      } else {
        ElMessage.error(res.msg)
      }
    })
  }

  state.userOptions = []
  request.get('/front/user/list').then(res => state.userOptions = res.data)

  //弹出评论
  state.goodids = ''
  const dialogFormVisible = ref(false)
  const handlerCommentAdd = (id,goodids,bizUserId) =>{
      dialogFormVisible.value = true
      state.goodids = goodids
      state.form.ordersId = id
      state.form.bizUserId = bizUserId
  }
  //保存评价
  state.orders = {}
  const saveComment = () => {
      let array = state.goodids.split(',')
      array.forEach((e,i)=> {
          state.form.repairsId = e  // 当前模块的id
          state.form.userId = user.id //用户id
          // 发送数据
          request.post('/front/comments/update', state.form).then(res => {
              if (res.code === '200') {
                  dialogFormVisible.value = false
              } else {
                  ElMessage.error(res.msg)
                  return
              }
          })
      });
      //修改订单状态为'已评价'
      state.orders.id = state.form.ordersId
      state.orders.stateRadio = '已评价'
      request.post('/front/orders/update',state.orders).then(res => {
          if (res.code === '200') {
              load()  // 刷新表格数据
          } else {
              ElMessage.error(res.msg)
          }
      })
      ElMessage.success("评论成功")
  }

  //查询评价
  const viewShowComments = ref(false)
  state.comments = null;
  const showComments =(id) =>{
    request.get('/front/comments/orders/'+id).then(res => {
      viewShowComments.value = true
      state.comments = res.data
    })
  }
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
            <span style="font-size: 14px;margin-right: 20px;">当前位置：首页 > 维修接单记录</span>
        </div>

          <div style="padding-bottom: 15px ;border-bottom: 3px solid #CC9999; text-align: left;display: flex;">
              <span style="font-weight: bold; font-size: 24px;float: left;flex: 3;color: #CC9999;">维修接单记录</span>
          </div>

          <el-table :data="state.tableData" style="width: 100%;margin-top: 20px;" stripe border :header-cell-class-name="'headerBg'">
                <el-table-column prop="id" label="序号">
                  <template #default="scope">
                    {{ scope.$index + 1 }}
                  </template>
                </el-table-column>
                <el-table-column prop="name" label="单号"></el-table-column>
                <el-table-column label="发单用户" width="250px;">
                    <template #default="scope">
                        <span v-if="scope.row.bizUserId">{{ state.userOptions.find(v => v.id === scope.row.bizUserId) ? state.userOptions.find(v => v.id === scope.row.bizUserId).name : '' }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="明细"><template #default="scope"><el-button @click="view(scope.row.content)">查看</el-button></template></el-table-column>
              <el-table-column prop="createTime" label="接单时间"></el-table-column>
                <el-table-column prop="stateRadio" label="状态"></el-table-column>
              <el-table-column label="操作" width="150px;">
                  <template #default="scope">
                      <a href="javascript:void(0)" @click="deleteOrders(scope.row.id)" class="delete-link" style="margin-right: 15px;" v-if="scope.row.stateRadio=='已接单'">删除</a>
                      <a href="javascript:void(0)" @click="cancelOrders(scope.row.id)" class="delete-link" style="margin-right: 15px;" v-if="scope.row.stateRadio=='已接单'">取消</a>
                      <a href="javascript:void(0)" @click="confirmOrders(scope.row.id)" class="delete-link" v-if="scope.row.stateRadio=='已接单'">完成</a>
                      <a href="javascript:void(0)" @click="showComments(scope.row.id)" class="delete-link" v-if="scope.row.stateRadio=='已评价'">查看评价</a>
                  </template>
              </el-table-column>
        </el-table>

    </div>

      <el-dialog v-model="viewShow" title="预览" width="40%">
          <div  id="editor-content-view" class="editor-content-view" v-html="content" style="padding: 0 20px"></div>
          <template #footer>
      <span class="dialog-footer">
        <el-button @click="viewShow = false">关闭</el-button>
      </span>
          </template>
      </el-dialog>

          <!-- 评论窗口 -->
          <el-dialog v-model="dialogFormVisible" title="评论" width="30%">
              <el-form :model="state.form" label-width="50px" style="padding: 0 20px" status-icon>
                  <el-form-item label="评分">
                      <el-rate v-model="state.form.score">
                      </el-rate>
                  </el-form-item>
                  <el-form-item label="内容">
                      <el-input type="textarea" :rows="5" v-model="state.form.content" autocomplete="off"></el-input>
                  </el-form-item>
              </el-form>
              <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveComment">
          确定
        </el-button>
      </span>
              </template>
          </el-dialog>

    <el-dialog v-model="viewShowComments" title="查看评论" width="40%">
      <div  id="editor-content-view2" class="editor-content-view" v-html="state.comments" style="padding: 0 20px"></div>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="viewShowComments = false">关闭</el-button>
      </span>
      </template>
    </el-dialog>
  </div>
</template>

<style>

.total-container {
  margin-top: 20px;
  text-align: right;
}

.total-label {
  font-weight: bold;
}

.total-price {
  color: red;
  font-weight: bold;
}

a.delete-link {
    color: #ff0000;
    text-decoration: none;
}

a.delete-link:hover {
    text-decoration: underline;
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