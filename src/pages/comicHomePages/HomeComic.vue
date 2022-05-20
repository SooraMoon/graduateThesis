<template>
  <!-- 推荐 -->
  <div class="recommend">
      <!-- 标题 -->
      <div class="title">
        <p>为你推荐</p>
      </div>
      <!-- 推荐内容 -->
      <div class="recommend_content">
        <!-- 漫画盒子 -->
        <!--    不是comic啊，是comic里的comicList 此处的id是不同漫画间的唯一标识id   -->
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
        <!-- 换一换盒子 -->
        <div class="change">
          <button>查看更多</button>
          <button>换一换</button>
        </div>
      </div>
    </div>
</template>

<script>
  import {reactive, ref} from "vue"
  import {useStore} from 'vuex'
  import {useRouter, onBeforeRouteLeave, useRoute} from 'vue-router'
  import axios from 'axios'
  export default {
    name: "HomeComic",
    setup (_, context) {
      // 使用store, route
      const store = useStore()
      const router = useRouter()
      const route = useRoute()
      //
      const updateTime = ref(null)
      // 漫画列表
      const comic = reactive({
        comicList: []
      })

      // 创建完毕时加载json漫画资源(vue3 : beforeCreate,created => setup)
      axios.get('./cartoon/cartoonLists.json').then(
          response => {
            comic.comicList = response.data
            console.log('这是home组件里的comicList', comic.comicList)
          },
          error => {
            console.log(error.message)
          }
      )
      if (route.path === '/comic-vue3/home') {
        context.emit('showNav')
        console.log('这是route.path', route.path)
      }
      /*
      * @methods
      * */
      // 进入漫画详情页
      function toDetail (item) {
        /* 请务必添加JSON转换（后面收数据方再解析） 否则后面收到的会是[Object Object] 这里还使用了URL编码*/
        router.push({name: 'detail', query: {img: encodeURI(JSON.stringify(item))}})
        console.log('--{...item}--', {...item})
      }
      // 路由离开时触发
      onBeforeRouteLeave((to, _, next) => {
        console.log('这是to.name', to.name)
        console.log('这是to.path', to.path)
        if (to.name === 'detail') {
          if (store.state.cachePath.indexOf('detail') === -1) {
            store.commit('NEW_CACHE_PATH', to.name)
            console.log('这是store.state.cachePath', store.state.cachePath)
          }
        }
        /*
        * 终于知道为什么 router.push 不生效的问题了
        * 原因就是 当你配置了路由守卫后，你必须写跳转路由的 next()
        * 一般来说 next() 不要写在判断逻辑里，next() 单独写在最后面
        * */
        if (to.name === 'search' || to.name === 'my') {
          context.emit('hideNav')
        }

        next()
      })
      // 注意：return 最好写在最后面
      return {
        updateTime,
        comic,
        toDetail,
      }
    }
  }
</script>

<style lang="less">
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
</style>
