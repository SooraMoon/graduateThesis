<template>
  <div id="look" @mouseup="scrollToNewPage">
    <!--  mouseup 有个bug，位于页面底端点击鼠标后会一直触发事件，.once 修饰暂无法解决  -->
    <!-- 头部内容 -->
    <div id="header">
      <div class="back">
        <i class="iconfont icon-xiangzuo" @click="doBack"></i>
      </div>
      <!--   获取漫画名并置顶   -->
      <div class="Topname">
        <span>{{comic.imgLists.name}}</span>
      </div>
      <div class="give">
        <i class="iconfont icon-xiazai"></i>
        <i class="iconfont icon-fenxiang"></i>
      </div>
    </div>
    <!-- 获取漫画图片 -->
    <div v-if="comic.lookList[comic.page - 1]">
      <!-- 纵向浏览 -->
      <div v-if="!comic.checked">
        <!-- v-bind="comic.imgIndex = index" 切换阅读模式定位到当前位置 -->
        <div class="img vRead" v-for="(item,index) in comic.lookList[comic.page - 1].lookArr" :key="index" v-bind="comic.imgIndex = index" v-lazy="item">
          <img :src="item" alt />
        </div>
      </div>
      <!-- 横向阅读 -->
      <div v-if="comic.checked">
        <div class="img">
          <img :src="comic.lookList[comic.page - 1].lookArr[comic.imgIndex]" alt />
        </div>
      </div>
    </div>
    <!-- 屏幕左侧隐藏的阅读目录：下方弹出导航栏后，点击目录，即可在屏幕左侧弹出-->
    <van-popup v-model:show="comic.show2" position="left" :style="{ height: '100%',width:'70%',}">
      <p v-for="(item,index) in comic.imgLists.imglist"
         :key="index"
         class="esys"
         @click="toSelectedPage(item)"
         :class="{con:item.id === comic.page}">
        {{item.name}}
      </p>
    </van-popup>
    <!-- 中部隐藏的盒子 -> 点击后下方弹出阅读设置栏 -->
    <div class="hid_box"  @click="comic.show = true"></div>
    <!--  左右滑动按钮  -->
    <div class="pre_btn" @click="preBtn"></div>
    <div class="next_btn" @click="nextBtn"></div>
    <!-- 底部隐藏的popup框 -->
    <van-popup v-model:show="comic.show" position="bottom" :style="{ height: '200px'}">
      <div class="bottom">
        <div class="setting Item1">
          <span>目录</span>
          <i class="iconfont icon-mulu" @click="catalogue"></i>
        </div>
        <div class="setting Item3">
          <span>切换阅读模式</span>
          <van-switch v-model="comic.checked" @click="stickyLocation"></van-switch>
        </div>
        <div class="setting Item2">
          <i class="iconfont icon-shangyiye" @click="previousChapter"></i>
          <i class="iconfont icon-xiayiye" @click="newChapter"></i>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
  import {nextTick, onBeforeUnmount, reactive, watchEffect} from "vue"
  import {onBeforeRouteLeave, useRoute, useRouter} from 'vue-router'
  import axios from "axios"
  import {Toast} from "vant"
  //import { Swiper, SwiperSlide } from "swiper/vue"
  import 'swiper/css'
  import {useStore} from "vuex";

  /*
  * @implement 如何加载并显示用户点击的漫画某一话？
  * 思路：创建look组件时，前往looklist.json加载所有漫画信息（json存储的是漫画id和图片list网址）
  *     并赋值给look页相应变量
  * */

  export default {
    name: 'ComicLook',
    emits: ['showNav', 'hideNav'],
    // 必须两个参数都写，或者用ES6占位符
    setup (_, context) {
      const store = useStore()
      const route = useRoute()
      const router = useRouter()
      let comic = reactive({
        lookList: [],//漫画图片的数组
        page: 0,//话数
        scrollTop: 0,// 滚动条滚动时，距离顶部的距离
        scrollHeight: 0,// 滚动条总的高度
        clientHeight: 0,// 可视区的高度
        liveHeight: 0, //实时高度
        flag: false,//前往下一章的状态
        show: false,//隐藏盒子的状态
        show2: false,//目录的隐藏盒子
        checked: false,   //阅读按钮切换状态
        imgLists: [],  // 章节信息
        imgIndex: 0 // 每一话中每一张图片的索引，初始置为 0 , 即第一话的第一页
      })
      /*
      * @created
      * */

      // look页面创建时隐藏FooterNav.vue底部导航条
      context.emit('hideNav')

      // 获取 localStorage 里的请求来源信息
      let reqArr = JSON.parse(localStorage.getItem('reqArr'))

      if (reqArr !== null) {
        console.log(`@@@session.reqArr`, reqArr)

        // comic.imgsLists为漫画总体信息
        comic.imgLists = JSON.parse(route.params.imgsLists)
        /*
        * @update 已修改，格式均为各自从一开始编排
        * 由于params是由JSON格式转化得来的字符串（vue3中不用JSON转化接收无法传递正确数据）
        * 故一开始就将需将lookid转化为数值形式再赋值给page
        * */
        comic.page = Number(route.params.lookid)

        // 请求来之 homeComic 时
        if (reqArr.isDetail.isHome) {
          console.log('@@@isHome: ', reqArr.isDetail.isHome)
          axios.get('./cartoon/homeLookList.json').then(
              response => {
                /*
                * 由于lookList.json存储漫画图片的格式问题，所以取值赋值较麻烦（具体看lookList.json文件）
                * -1 是因为每部漫画id由 1 开始编排，而 response.data 是数组
                * */
                comic.lookList = response.data[comic.imgLists.id - 1]
                console.log('这是主页的comic.lookList',comic.lookList)
              },
              error => {
                console.log(error.message)
              }
          )
        }
        // 请求来自 boyComic 时
        if (reqArr.isDetail.isBoyComic) {
          console.log(`@@@isBoyComic: ${reqArr.isDetail.isBoyComic}`)
          axios.get('./cartoon/boyLookList.json').then(
              response => {
                /*
                * 由于lookList.json存储漫画图片的格式问题，所以取值赋值较麻烦（具体看lookList.json文件）
                * -1 是因为每部漫画id由 1 开始编排，而 response.data 是数组
                * */
                comic.lookList = response.data[comic.imgLists.id - 1]
                console.log('这是少年漫的comic.lookList',comic.lookList)
              },
              error => {
                console.log(error.message)
              }
          )
        }
        // 请求来自 girlComic 时
        if (reqArr.isDetail.isGirlComic) {
          console.log(`@@@isGirlComic: ${reqArr.isDetail.isGirlComic}`)
          axios.get('./cartoon/GirlLookList.json').then(
              response => {
                /*
                * 由于lookList.json存储漫画图片的格式问题，所以取值赋值较麻烦（具体看lookList.json文件）
                * -1 是因为每部漫画id由 1 开始编排，而 response.data 是数组
                * */
                comic.lookList = response.data[comic.imgLists.id - 1]
                console.log('这是少女漫的comic.lookList',comic.lookList)
              },
              error => {
                console.log(error.message)
              }
          )
        }
      }
      // 请求来自 ComicSearch 时
      if (store.getters.getReqArr.isSearch) {
        // 简单处理 直接取第一话
        comic.page = 1
        // 获取漫画简介信息
        comic.imgLists = JSON.parse(route.params.imgLists)
        axios.get('./cartoon/imgSrc.json').then(
            response => {
              comic.lookList = response.data[0]
            },
            error => {
              console.log(`error: ${error}`)
            }
        )
      }

      // 设置网页标题
      watchEffect(() => {
        if (reqArr.isDetail !== null) {
          document.title = comic.imgLists.imglist[comic.page - 1].name + ' | ' + comic.imgLists.name
        }
        if (store.getters.getReqArr.isSearch) {
          document.title = comic.imgLists[2].name
          console.log(comic.imgLists[2].name)
        }
      })
      /*
      * @methods
      * */
      // 阅读模式切换后定位到当前位置
      function stickyLocation () {
        // 切换阅读模式定位到当前位置（纵向浏览使用了 v-bind="comic.imgIndex = index"）
        if (comic.checked) {
          // 考虑除零问题（一张图片时）同时将实时滚动高度赋值给 liveHeight
          const imgIndex = document.scrollingElement.scrollHeight/(comic.imgIndex + 1)
          comic.liveHeight = document.scrollingElement.scrollTop
          // 每一张图片高度 = 滚动条总高度除以渲染图片数（粗略计算）
          let everyHeight = Math.floor(imgIndex)
          // 计算已滚动高度除以每一张图片高度
          const indexNum = Math.floor((document.scrollingElement.scrollTop/everyHeight))
          // 滚动高度为空应置为 0 即第一张图片
          if (indexNum === 0) {
            comic.imgIndex = 0
          } else {
            // 滚动高度不为空则粗略计算当前图片下标索引值
            comic.imgIndex = indexNum
            console.log(`这是当前页面索引 ${comic.imgIndex}`)
          }
        }
        // 从横向阅读变为纵向浏览（开启一个异步任务，等 DOM 更新后才滚动，因为此时才有 DOM 对象）
        async function toVerticalRead () {
          await nextTick()
          // 按指定像素滚动
          await window.scrollBy(0, comic.liveHeight)
        }
        if (!comic.checked) {
          toVerticalRead()
        }
      }
      /*
      * @explain 点击切换共有四个注意点
      * 1、中间页面的下一页切换：非下一章直接页数加一，否则章节数加一同时页数置为 0
      * 2、中间页面的上一页切换：非上一章节直接页数减一，否则返回上一章
      * 3、首章的第一页 -> 需判断是不是首章、首页再做处理
      * 4、尾章的最后一页 -> 需判断是不是尾章、尾页再做处理
      * */
      // 点击返回上一页
      function preBtn () {
        // 判断是否为横向阅读模式
        if (comic.checked) {
          // 判断是否为第一页
          if (comic.imgIndex === 0) {
            // 判断是否为 首章
            if (comic.imgLists.imglist[comic.page - 1].id === 1) {
              Toast('这已经是第一页了')
            } else {
              // 非首章 直接返回上一章开头
              previousChapter()
            }
          } else {
            // 上一页
            comic.imgIndex -= 1
          }
        }
      }
      // 点击下一页
      function nextBtn () {
        if (comic.checked) {
          // 判断是否为最后一页
          if (comic.imgIndex === comic.lookList[comic.page - 1].lookArr.length - 1) {
            // 最后一章的最后一页需特别处理
            if (comic.page === comic.imgLists.imglist.length) {
              Toast('这已经是最后一页了')
            } else {
              // 非尾章 跳到下一章节（简单处理）
              newChapter()
            }
          } else {
            comic.imgIndex += 1
          }
        }
      }
      // 滚动前往下一章
      function scrollToNewPage () {
        if (!comic.checked) {
          // 滚动条滚动时距离顶部的高度
          //comic.scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
          comic.scrollTop = document.scrollingElement.scrollTop
          // 滚动条的总高度
          comic.scrollHeight = document.scrollingElement.scrollHeight
          // 当前屏幕高度
          comic.clientHeight = document.scrollingElement.clientHeight
          // 这里不 + 1 要么移动端的 touchmove事件 失效，要么滚动无法前往下一章
          if (comic.scrollTop + comic.clientHeight + 1 >= comic.scrollHeight) {
            Toast.loading({
              duration: 1000, // 展示时长(ms)，值为 0 时，toast 不会消失
              forbidClick: true,  // 是否禁止背景点击
              message: "加载中",
            })

            // 清除Toast
            /*let second = 1
            const timer = setInterval(() => {
              second--
              if (second) {
                toast.message = `倒计时 ${second} 秒`
              } else {
                clearInterval(timer)
                // 手动清除 Toast，否则一直转圈（duration设置为 0 时需手动清除 Toast）
                Toast.clear()
              }
            }, 1000)*/

            comic.flag = true

            setTimeout(() => {
              if (comic.flag === false) {
                return
              }
              newChapter()
              comic.flag = false
            }, 1000)
          }
        }

      }
      function doBack () {
        // 顶部返回箭头
        router.back()
      }
      // 点击目录跳转某话观看
      function toSelectedPage (item) {
        comic.page = item.id
        // 定位到顶部（使用 goTop() 会有动画上划加载，不好）
        window.scrollTo(0, 0)
        //goTop()
        // 目录popup同时隐藏
        comic.show2 = false
        comic.show  = false
      }
      /*
        * item.id === number | comic.page === string
        * string - 0 实现字符转数字，反过来，number + '' 数字转字符
        * 注意：字符转数字只能是减，数字转字符只能是加
        * */
      // 显示屏幕左侧目录popup框（此时底部popup要隐藏，否则会被原遮罩层遮盖）
      function catalogue () {
        comic.show = false
        comic.show2 = true
      }
      // 返回顶部
      function goTop () {
        // 使用setInterval
        const timer = setInterval(() => {
          // 取得当前滚动的距离
          // 桌面端documentElement 移动端body -> 合二为一scrollingElement
          let current = document.scrollingElement.scrollTop
          // 当滚动距离为0 的时候，要清除这个间隔函数
          if (current === 0) clearInterval(timer)
          document.scrollingElement.scrollTop = current - 100
        }, 16)
      }
      // 上一章
      function previousChapter () {
        // 通过章节id判断是否是第一章
        if (comic.page === 1) {
          console.log(comic.page)
          Toast('这已经是第一章了，前面没有章节咯')
        }
        if(comic.page > 1) {
          comic.page -= 1
          Toast(`当前为第${comic.page}话`)
          // 定位到顶部（使用 goTop() 会有动画上划加载，不好）
          window.scrollTo(0, 0)
          //goTop()
        }
      }
      // 下一章
      function newChapter () {
        // 判断是否是最后一章，如果否 则章节数组下标 +1
        if (comic.page < comic.imgLists.imglist.length) {
          comic.page += 1
          // 置为第一页
          comic.imgIndex = 0
          Toast(`当前为第${comic.page}话`)
          // 定位到顶部（使用 goTop() 会有动画上划加载，不好）
          window.scrollTo(0, 0)
          //goTop()
        } else {
          Toast('这已经是最新一章了')
        }
      }

      /*
      * @beforeUnmount
      * */
      onBeforeUnmount(() => {
        // 卸载前显示底部导航条
        context.emit('showNav')
      })
      onBeforeRouteLeave((to, from, next) => {
        // 路由离开前请求状态置为 false
        console.log(`balabala`)
        next()
      })
      return {
        comic,
        stickyLocation,
        preBtn,
        nextBtn,
        scrollToNewPage,
        doBack,
        toSelectedPage,
        catalogue,
        previousChapter,
        newChapter,
        goTop
      }
    }
  }
</script>

<style lang="less">
  #header {
    width: 100%;
    height: 50px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: fixed;
    min-width: 320px;
    max-width: 750px;
    margin: 0 auto;
    z-index: 20;
    background-color: transparent;
    .back {
      i {
        font-size: 30px;
        position: relative;
        z-index: 9999;
      }
    }
    .give {
      i {
        margin-right: 15px;
        font-size: 25px;
        position: relative;
        z-index: 9999;
      }
    }
    .Topname{
      display: flex;
      justify-content: flex-start;
    }
  }
  #look {
    .img {
      img {
        width: 100%;
      }
    }
    .hid_box{
      width: 50%;
      height: 50vh;
      position: fixed;
      // background-color: #fff;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      margin: auto;
      z-index: 21;
    }
    // 左右滑动按钮
    .pre_btn {
      width: 25%;
      position: fixed;
      height: 100%;
      margin-right: 75%;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
    }
    .next_btn {
      width: 25%;
      position: fixed;
      height: 100%;
      margin-left: 75%;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
    }
    //目录
    .esys{
      font-size: 14px;
      padding: 17px;
      color: #666;
    }
    .con{
      color: red;
    }
    //底部导航栏
    .bottom {
      position: fixed;
      width: 100%;
      height: 200px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      background-color: #fff;
      .setting {
        height: 30%;
        display: flex;
        flex-wrap: nowrap;
        justify-content: space-evenly;
        align-items: center;
      }
      .Item2 {
        i {
          padding: 20px;
          font-size: 18px;
        }
      }

    }
  }
</style>
