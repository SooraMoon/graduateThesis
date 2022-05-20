<template>
  <div id="details">
    <!-- 头部 -->
    <div id="header">
      <div class="back">
        <i class="iconfont icon-xiangzuo" @click="toBack"></i>
      </div>
      <div class="Topname">
      </div>
      <div class="give">
        <i class="iconfont icon-xiazai"></i>
        <i class="iconfont icon-fenxiang"></i>
      </div>
    </div>
    <!-- 背景图片 -->
    <div class="img">
      <img :src="comic.imgsLists.img_bg" alt />
    </div>
    <!-- 介绍 -->
    <!-- 名片 -->
    <div class="user">
      <div class="name">
        <p>
          <van-sticky :offset-top="0">
            <span>{{comic.imgsLists.name}}</span>
          </van-sticky>
        </p>
      </div>
      <div class="introduce">
        <p>{{comic.imgsLists.info}}</p>
      </div>
      <div class="author">
        <span>作者:</span>
        <span>{{comic.imgsLists.author}}</span>
      </div>
    </div>
    <!-- 目录 -->
    <div class="catalogueBox">
      <!-- 目录 -->
      <div class="catalogue">
        <div class="page">
          <span class="page_top">目录</span>
          <span class="page_next">连载中,已更新{{comic.imgsLists.imglist.length}}话</span>
        </div>
        <!-- 展开目录 -->
        <div class="expandCatalog" @click="catalogPopup">
          <span>展开目录</span>
          <i class="iconfont icon-xiayiye"></i>
        </div>
      </div>
      <!-- 目录隐藏盒子 -->
      <!--   @nakoruru vant的popup弹出层 该处功能为：点击 展开目录 底部弹出slide内容漫画的目录  -->
      <!--   另外请认真按照官方的写法来配置，如官方文档就是这么写v-model:show="show"   -->
      <van-popup v-model:show="show" position="bottom" :style="{ height: '70%'}">
        <div class="cataloguaSum">
          <div class="top">
            <span>目录</span>
          </div>
          <div class="cataloguaSumBox">
            <div class="title">
              <span>全部章节({{comic.imgsLists.imglist.length}})</span>
            </div>
            <div class="titleBox">
              <div class="titleList" v-for="item in comic.imgsLists.imglist"
                   :key="item.id"
                   @click="goLook(item)">
                <div class="img">
                  <img :src="item.img" alt />
                </div>
                <div class="titleFont">
                  <p style="padding-left:10px">{{item.name}}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </van-popup>
      <!-- 目录列表 -->
      <div class="chapter_list">
        <div class="swiper">
          <div class="swiper-wrapper">
            <div
                class="swiper-slide"
                v-for="item in comic.imgsLists.imglist"
                :key="item.id"
                v-lazy="item"
                @click="toReadComic(item)"
            >
              <!-- lookid为漫画某一话的id（看v-for），imgsLists为漫画总体信息  -->
              <img :src="item.img" alt />
              <p>{{item.name}}</p>
            </div>
          </div>
        </div>
      </div>
      <!-- 异能排行榜 -->
<!--      <div class="powerCard">
        <div class="powerCardName">
          <p>异能排行榜</p>
        </div>
        <div class="powerCardList">
          <div class="swiper-container-02">
            <div class="swiper-wrapper">
              <div class="swiper-slide" v-for="item in powerCardList" :key="item.id">
                <img :src="item.img" alt />
                <p>{{item.name}}</p>
              </div>
            </div>
          </div>
        </div>
      </div>-->
    </div>
    <!--  章节列表  -->
    <div class="chapterList">
      <div class="chapterListHead">
        <h2>
          <span>章节</span>
          <em>列表</em>
        </h2>
        <ul>
          <li>排序:</li>
          <li @click="positiveOrder" :class="{active : comic.sortShow}">正序</li>
          <li>|</li>
          <li @click="invertedOrder" :class="{active : !comic.sortShow}">倒序</li>
        </ul>
      </div>
      <div class="chapterListBody">
        <ul>
          <li
              v-for="item in comic.imgsLists.imglist"
              :key="item.id"
              v-lazy="item"
              @click="toReadComic(item)"
          >
            第{{item.id}}话
          </li>
        </ul>
      </div>
    </div>
    <!-- 评论 -->
<!--    <div class="centerBox">
      <div class="title">
        <p>精彩评论</p>
      </div>
      &lt;!&ndash; 评论列表 &ndash;&gt;
      <div class="content">
        &lt;!&ndash; 评论列表 &ndash;&gt;
        <div class="commentLists" v-for="(item,index) in commentLists" :key="index">
          &lt;!&ndash; 个人信息 &ndash;&gt;
          <div class="commentUser">
            <div>
              <img :src="item.img" alt="">
              <span>{{item.name}}</span>
            </div>
            <div>
              <span>{{item.num}}</span>
              <i class="iconfont icon-dianzan1" v-show="!item.like" @click="like(item)"></i>
              <i class="iconfont icon-dianzan11" style="color:red" v-show="item.like" @click="like(item)"></i>
            </div>
          </div>
          <div class="tallContent">
            {{item.info}}
          </div>
        </div>
      </div>
      &lt;!&ndash; 发表评论 &ndash;&gt;
      <div class="investment">
        <input type="text" v-model="text" @keydown="textIsSet">
      </div>
    </div>-->
    <!-- 底部 -->
    <div class="bottom">
      <!-- 主页按钮 -->
      <div class="home" @click="$router.push('/comic-vue3/home')">
        <i class="iconfont icon-zhuye"></i>
        <p>前往首页</p>
      </div>
      <!-- 收藏 -->
      <!--<div class="Collection">
        <span></span>
      </div>-->
      <!-- 开始阅读 -->
      <div class="startReading" @click="toRead">
        <span>开始阅读</span>
      </div>
    </div>
  </div>
</template>

<script>
  import {onBeforeRouteLeave, useRoute, useRouter} from 'vue-router'
  import {ref, reactive, onMounted, watchEffect} from "vue"
  import store from "@/store"
  // 引入Swiper核心模块 (以下两步是Swiper7 for vue3 的使用前配置方法）
  import {Swiper} from "swiper"
  // Import Swiper styles
  import 'swiper/less'

  /*
  * @implement 如何实现点击漫画图片跳转内部观看
  * 思路：点击图片，携带当前话id和漫画信息（图片List)，经路由跳转到look页
  *     look页获取以上数据进行渲染
  * */

  export default {
    name: "ComicDetail",
    setup () {
      //console.log('两个参数prop和context', prop, context)
      /*
      * @var 定义变量
      * */
      const route = useRoute()
      const router = useRouter()
      const show = ref(false)
      const comic = reactive({imgsLists: [], sortShow: true})
      // 正序
      function positiveOrder () {
        document.querySelector('.chapterListBody ul').setAttribute('style', 'flex-flow: row wrap;')
        comic.sortShow = true
      }
      // 倒序
      function invertedOrder () {
        document.querySelector('.chapterListBody ul').setAttribute('style', 'flex-flow: row-reverse wrap-reverse;')
        comic.sortShow = false
      }

      onMounted(() => {
        // 展示漫画swiper-slide的配置信息
        new Swiper(".swiper", {
          slidesPerView: 2.5, //显示个数
          observer: true,
          observeParents: true,
          freeMode: true, //起始位置
          spaceBetween: 10, //div之间间距
        })
      })

      /*
      * @created created时执行的代码
      * */
      /*
      * 此处接收的是具体某个漫画的整体信息（不包含漫画图片（地址）
      * imglist只存储每一话的id、展示图和名称信息，图片地址在lookList.json文件）
      **/
      comic.imgsLists = JSON.parse(decodeURI(route.query.img))
      // 设置为网页标题
      watchEffect(() => {
        document.title = comic.imgsLists.name
      })
      /*
      * @method 方法
      * */
      // 返回箭头里的方法
      function toBack () {
        router.back()
      }
      // popup隐藏的漫画目录
      function catalogPopup () {
        show.value = true
      }
      // 进入漫画内部方法
      function goLook (item) {
        show.value = false
        router.push({name: 'look', params: {lookid:item.id, imgsLists:JSON.stringify(comic.imgsLists)}})
      }
      // 滑动组阅读漫画
      function toReadComic (item) {
        router.push({name:'look', params:{lookid:item.id, imgsLists:JSON.stringify(comic.imgsLists)}})
        show.value = false
      }
      // 底部 开始阅读 方法
      function toRead () {
        router.push({name: 'look', params: {lookid:1, imgsLists:JSON.stringify(comic.imgsLists)}})
      }

      onBeforeRouteLeave((to, _, next) => {
        if (to.name === 'look') {
          if (store.state.cachePath.indexOf('look') === -1) {
            store.commit('NEW_CACHE_PATH', to.name)
          }
        }
        console.log('这是cachePath', store.state.cachePath)
        next()
      })
      return {
        show,
        comic,
        toBack,
        catalogPopup,
        goLook,
        toRead,
        toReadComic,
        positiveOrder,
        invertedOrder
      }
    }
  }
</script>

<style lang="less">
.van-sticky--fixed {
  z-index: 10;
  background-color: #fff;
  padding: 10px 0;
}
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
  #details {
    overflow: hidden;
    max-width: 750px;
    padding-bottom: 60px;
    .img {
      height: 230px;
      overflow: hidden;
      position: relative;
      padding-top: 50px;
      img {
        width: 100%;
        position: absolute;
        margin: auto;
        left: 0;
        bottom: 0;
        right: 0;
        top: 0;
      }
    }
    //名片
    .user {
      max-width: calc(750px - 10%);
      position: absolute;
      top: 150px;
      width: 90%;
      margin: 0 5%;
      height: 200px;
      border-radius: 5px;
      box-shadow: 0 0 20px #e4e4e4;
      background-color: #fff;
      .name {
        // margin-left: 20px;
        margin-top: 10px;
        text-align: center;
        font-size: 20px;
        font-weight: bold;
        .van-sticky.van-sticky--fixed {
          width: 100%;
          margin: auto;
        }
      }
      .introduce {
        font-size: 12px;
        color: #666;
        margin: 10px 20px 20px 20px;
      }
      .author {
        margin-left: 20px;
        span:nth-child(2) {
          margin-left: 10px;
        }
      }
    }
    //目录
    .catalogueBox {
      margin: 80px 20px 0 20px;
      .catalogue {
        display: flex;
        justify-content: space-between;
        //目录
        .page {
          .page_next {
            font-size: 12px;
            color: #666;
            margin-left: 10px;
          }
          .page_top {
            font-size: 20px;
          }
        }
        //展开目录
        .expandCatalog {
          font-size: 16px;
          color: #666;
        }
      }
      .chapter_list {
        padding-top: 10px;
        .swiper-slide {
          border: 0.025rem solid #e8e7e5;
          img {
            padding-bottom: 10px;
            width: 100%;
            height: 150px;
          }
          p {
            margin-left: 5px;
            word-break: break-all;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
            overflow: hidden;
            font-size: 14px;
          }
        }
      }
    }
    // 章节列表
    .chapterList {
      margin-top: 10px;
      border: 1px solid #e4e4e4;
      .chapterListHead {
        padding: 20px 0;
        border-bottom: 0.5px solid #e4e4e4;
        h2 {
          display: inline-block;
          width: 75%;
          span {
            margin-left: 20px;
          }
          cursor: default;
        }
        ul {
          display: inline-block;
          width: 25%;
          li {
            display: inline;
            padding: 0 5px;
            font-size: 16px;
            cursor: default;
          }
          .active {
            color: red;
          }
        }
      }
      .chapterListBody {
        ul {
          display: flex;
          flex-flow: row wrap;
          justify-content: flex-start;
          li {
            width: 25%;
            padding: 10px 0;
            text-align: center;
            cursor: default;
          }
        }
      }
    }
    //异能排行榜
    .powerCard {
      .powerCardName {
        font-size: 20px;
        padding: 20px 0 10px 0;
      }
      .powerCardList {
        .swiper-slide {
          img {
            width: 100%;
            border-radius: 8px;
            padding-bottom: 5px;
          }
        }
      }
    }
    // 评论

    .centerBox{
      margin:10px;
      background-color: #fff;
      box-shadow: 0 0 20px #e4e4e4;
      border-radius: 10px;
      padding: 10px;
      .title{
        font-size: 14px;
        font-weight: bold;
      }
      //评论列表
      .content{
        .commentLists{
          padding: 10px 0;
          .commentUser{
            display: flex;
            justify-content: space-between;
            align-items: center;
            div{
              display: flex;
              align-items: center;
              img{
                width: 40px;
                height: 40px;
                display: inline-block;
                border-radius: 50%;
              }
              span{
                padding: 0 8px;
                font-size: 14px;
              }
            }

          }
          .tallContent{
            margin-left: 48px;
            margin-right: 8px;
            font-size: 14px;
          }
        }
      }
      //发表评论
      .investment{
        display: flex;
        justify-content: center;
        padding-top: 5px;
        input{
          width: 90%;
          padding: 5px 0;
          border-radius: 10px;
          text-indent: 10px;
          border: 1px solid #999;
        }
      }
    }
    //底部导航栏
    .bottom {
      position: fixed;
      width: 750px;
      height: 50px;
      display: flex;
      flex-flow: row wrap;
      justify-content: space-around;
      align-items: center;
      z-index: 100;
      bottom: 0;
      background-color: #fff;
      div {
        text-align: center;
      }
      //前往首页
      .home {
        i {
          font-size: 20px;
        }
        p {
          font-size: 12px;
        }
      }
      //收藏
      .Collection {
        font-size: 14px;
        padding: 8px 8px;
        background-color: #f4f4f4;
        font-weight: bold;
      }
      // 开始阅读
      .startReading {
        font-size: 14px;
        font-weight: bold;
        color: #fff;
        padding: 8px 50px;
        background-color: #ff613e;
        border-radius: 20px;
        cursor: pointer;
      }
    }

  }
  // 目录的隐藏盒子
  .cataloguaSum {
    padding: 0 10px;
    .top {
      text-align: center;
      font-size: 18px;
      padding: 15px 0;
      position: fixed;
      left: 0;
      right: 0;
      margin: auto;
      background-color: #fff;
      z-index: 99;
    }
    .cataloguaSumBox {
      padding-top: 55px;
      .title {
        font-size: 14px;
        font-weight: bold;
        padding-top: 10px;
      }
      .titleBox {
        .titleList {
          height: 60px;
          padding: 20px 0;
          display: flex;
          align-items: center;
          .img {
            height: 30px !important;
            width: 40%;
            img {
              width: 100%;
            }
          }
        }
      }
    }
  }
</style>
