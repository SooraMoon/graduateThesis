<template>
  <div id="head">
    <!-- 头部 -->
    <div class="nav">
      <!-- logo -->
      <!--    <div class="logo">
            <img src="https://css99tel.cdndm5.com/v202111301550/blue/images/mobile/top-main-logo.png" alt />
          </div>-->
      <div class="search" @click="search">
        <i class="iconfont icon-sousuo"></i>
        <p>搜索作品名</p>
      </div>
      <div class="nav_right">
        <i class="iconfont icon-31wode" @click="$router.push({name: 'my'})"></i>
      </div>
    </div>
    <!--  分类栏  -->
    <div class="storyType">
      <ul>
        <li @click="HomeComicList" :style="showArr.homeComicShow ? {backgroundColor: '#e4e4e4'} : ''">
          <router-link :to="{name: 'home'}">首页</router-link>
        </li>
        <li @click="BoyComicList" :style="showArr.boyComicShow ? {backgroundColor: '#e4e4e4'} : ''">
          <router-link :to="{name: 'boyComic'}">少年</router-link>
        </li>
        <li @click="GirlComicList" :style="showArr.girlComicShow ? {backgroundColor: '#e4e4e4'} : ''">
          <router-link :to="{name: 'girlComic'}">少女</router-link>
        </li>
        <li>青春</li>
        <li>校园</li>
        <li>恋爱</li>
        <li>热血</li>
        <li>奇幻</li>
        <li @click="toUploadComic" :style="{display: 'none'}">上传漫画</li>
      </ul>
    </div>
    <!--  分类栏组件显示位置  -->
<!--    <div class="storyTypeBody" v-show="showArr.boyComicShow">
      <div class="bodyHead">
        <h2>漫画列表</h2>
      </div>
      <div class="subBodyHead">
        <span>共12800部漫画</span>
      </div>
      <div class="comicListBody">
        <ul>
          <li
            v-for="item in comic.comicList"
            :key="item.id"
            v-lazy="item"
          >
            <img :src="item.img" alt="#">
            <div>
              <h3>少年时代</h3>
              <p>作者: {{item.author}}</p>
              <p>类型: {{item.type}}</p>
              <p>状态: 连载中</p>
              <p>最新: 第{{comic.comicList[item.id-1].imglist.length}}话</p>
              <span @click="toDetail(item)">
                开始阅读
              </span>
            </div>
          </li>
        </ul>
      </div>
    </div>-->
    <router-view v-slot="{ Component }">
      <transition>
        <keep-alive>
          <component :is="Component"/>
        </keep-alive>
      </transition>
    </router-view>
    <!--  上传文件弹出框  -->
    <!-- 推荐 -->
<!--    <div class="recommend" v-show="showArr.homeComicShow">
      &lt;!&ndash; 标题 &ndash;&gt;
      <div class="title">
        <p>为你推荐</p>
      </div>
      &lt;!&ndash; 推荐内容 &ndash;&gt;
      <div class="recommend_content">
        &lt;!&ndash; 漫画盒子 &ndash;&gt;
        &lt;!&ndash;    不是comic啊，是comic里的comicList 此处的id是不同漫画间的唯一标识id   &ndash;&gt;
        <div class="content" @click="toDetail(item)" v-for="item in comic.comicList" :key="item.id">
          <div class="img">
            <img :src="item.img" alt />
            <div>
              <span>{{item.follow}}万人气</span>
            </div>
          </div>
          <div class="text">
            <p>{{item.name}}</p>
            <p>{{item.type}}</p>
          </div>
        </div>
        &lt;!&ndash; 换一换盒子 &ndash;&gt;
        <div class="change">
          <button>查看更多</button>
          <button>换一换</button>
        </div>
      </div>
    </div>-->
  </div>
</template>

<script>
  import {onMounted, reactive, ref, watchEffect} from "vue"
  import {useStore} from 'vuex'
  import {onBeforeRouteLeave, useRouter} from "vue-router";
  export default {
    name: "ComicHome",
    emits: ['showNav', 'hideNav'],
    setup (_, context) {
      // 使用store, route
      const store = useStore()
      const router = useRouter()
      const show = ref(false)
      // 显示or隐藏数组
      const showArr = reactive({
        // 主页显隐Boolean值
        homeComicShow: true,
        // 少年漫显隐Boolean值
        boyComicShow: false,
        // 少女漫显隐Boolean值
        girlComicShow: false
      })
      context.emit('showNav')
      /*
      * @store.reqArr 分类栏中，点击某一项，该项的请求来源就设为 true ，其余来源 设置为 false
      * 公共组件如 ComicDetail 不需要设置来源布尔值，只关注传值就好
      * 因为漫画数据资源的存储与分类相对应
      * */
      // 主页漫画列表
      function HomeComicList () {
        showArr.homeComicShow = true
        showArr.boyComicShow = false
        showArr.girlComicShow = false
        // 更新请求来源
        store.commit('UPDATE_REQARR_ISHOME', showArr.homeComicShow)
        store.commit('UPDATE_REQARR_ISBOYCOMIC', showArr.boyComicShow)
        store.commit('UPDATE_REQARR_ISGIRLCOMIC', showArr.girlComicShow)
      }
      // 少年漫列表
      function BoyComicList () {
        showArr.homeComicShow = false
        showArr.boyComicShow = true
        showArr.girlComicShow = false
        store.commit('UPDATE_REQARR_ISHOME', showArr.homeComicShow)
        store.commit('UPDATE_REQARR_ISBOYCOMIC', showArr.boyComicShow)
        store.commit('UPDATE_REQARR_ISGIRLCOMIC', showArr.girlComicShow)
      }
      // 少女漫列表
      function GirlComicList () {
        showArr.homeComicShow = false
        showArr.boyComicShow = false
        showArr.girlComicShow = true
        store.commit('UPDATE_REQARR_ISHOME', showArr.homeComicShow)
        store.commit('UPDATE_REQARR_ISBOYCOMIC', showArr.boyComicShow)
        store.commit('UPDATE_REQARR_ISGIRLCOMIC', showArr.girlComicShow)
      }
      // 上传漫画
      function toUploadComic () {
        show.value = true
        router.push({name: 'upload'})
      }

      /*// 进入搜索页
      async function search() {
        const failure = await router.push({name: 'search'})

        if(isNavigationFailure(failure)) {
          console.log('这是failure', failure)
        }
      }*/
      /* TODO:下次改成用 provide 和 inject 实现 */
      watchEffect(() => {
        // 监听并存入请求源
        localStorage.setItem('reqArr', JSON.stringify(store.getters.getReqArr))
      })
      // 挂载完毕时获取 localStorage 里的请求源数据（为了解决分类栏图标点亮问题）
      onMounted(() => {
        let reqArr = JSON.parse(localStorage.getItem('reqArr'))
        showArr.homeComicShow = reqArr.isDetail.isHome
        showArr.boyComicShow = reqArr.isDetail.isBoyComic
        showArr.girlComicShow = reqArr.isDetail.isGirlComic
      })
      // 路由离开前存入请求源信息
      onBeforeRouteLeave((to, _, next) => {
        console.log(to)
        context.emit('hideNav')
        next()
      })
      return {
        showArr,
        show,
        HomeComicList,
        BoyComicList,
        toUploadComic,
        GirlComicList
      }
    }
  }
</script>

<style lang="less">
  #head {
  padding-top: 40px;
  padding-bottom: 50px;
  max-width: 750px;
  //头部
  .nav {
    width: calc(100% - 36px);
    height: 46px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 18px;
    position: fixed;
    background-color: rgba(252, 249, 249, 0.979);
    top: 0;
    min-width: 320px;
    max-width: 750px;
    margin: 0 auto;
    z-index: 10;
    .logo {
      height: 100%;
      display: inline-flex;
      align-items: center;
      img {
        height: 50%;
      }
    }
    .search {
      position: relative;
      width: 58%;
      background-color: rgba(230, 230, 230, 0.644);
      border-radius: 20px;
      height: 65%;
      display: flex;
      align-items: center;
      justify-content: center;
      p {
        text-align: center;
        font-size: 14px;
        color: #666;
      }
      i {
        font-size: 16px;
        margin-right: 5px;
        color: #666;
      }
    }
    .nav_right {
      display: flex;
      justify-content: space-between;
      i {
        font-size: 25px;
        color: #666;
      }
    }
  }
  // 分类栏
  .storyType {
    padding-top: 20px;
    ul {
      li {
        display: inline;
        padding: 15px 15px;
        font-size: 24px;
        border-radius: 10%;
        cursor: default;
      }
      li:hover {
        background-color: #e4e4e4;
      }
    }
  }
  //轮播图
  .Rotation {
    margin-top: 10px;
    .my-swipe .van-swipe-item {
      img {
        width: 100%;
      }
      color: #fff;
      font-size: 20px;
      line-height: 140px;
      text-align: center;
    }
  }
  //图片
  .alien_banner {
    width: 100%;
    margin-top: 10px;
    img {
      width: 96%;
      margin: auto;
    }
  }
  //推荐
  .recommend {
    padding: 5px 7.5px;
    margin-top: 20px;
    //标题
    .title {
      font-family: "Helvetica Neue", Helvetica, "PingFang SC",
      "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
      font-size: 18px;
      color: #333;
      font-weight: bold;
    }
    //推荐内容
    .recommend_content {
      display: flex;
      justify-content: space-between;
      flex-wrap: wrap;
      margin-bottom: 10px;
      .content {
        margin-top: 8px;
        width: 32.5%;
        .text {
          p {
            padding: 3px 0;
          }
          p:nth-child(1) {
            margin-top: 0.15rem;
            height: 1.45rem;
            line-height: 1.45rem;
            font-size: 14px;
            text-overflow: ellipsis;
            overflow: hidden;
            white-space: nowrap;
          }
          p:nth-child(2) {
            height: 0.6rem;
            line-height: 0.6rem;
            font-size: 12px;
            color: #999;
            text-overflow: ellipsis;
            overflow: hidden;
            white-space: nowrap;
          }
        }
        .img {
          position: relative;
          height: calc(100% - 60px);
          img {
            width: 100%;
            border-radius: 5px;
          }
          div {
            position: absolute;
            bottom: 20px;
            font-size: 10px;
            color: #fff;
            width: 100%;
            height: 20px;
            position: relative;
            box-shadow: inset -10px -10px 5px #35353550;
            span {
              position: absolute;
              bottom: 0;
              right: 0;
            }
          }
        }
      }
      .change {
        width: 100%;
        display: flex;
        margin-top: 10px;
        justify-content: space-between;
        color: #666;
        button {
          background-color: rgba(214, 213, 213, 0.295);
          width: 49%;
          padding: 10px;
          border: none;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
