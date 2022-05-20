<template>
  <div id="search">
    <div class="searchInput">
      <div class="input">
        <i class="iconfont icon-sousuo"></i>
        <input type="text"
               placeholder="赛马娘"
        />
      </div>
      <span  @click="$router.back()">取消</span>
    </div>

    <!-- 历史记录 -->
<!--    <div class="history" >
      <div class="historyName">历史记录</div>
      <div class="historylist"  v-for="(item,index) in historyList" :key="index">
        <span @click="name">{{item.name}}</span>
        <i class="iconfont icon-chacha" @click="del(index)"></i>
      </div>
    </div>-->
    <!-- 热门搜索 -->
    <div class="hotSearch">
      <div class="title">搜索结果</div>
      <div class="searchKeep"  @click="name" v-for="item in hotList" :key="item.id">{{item.name}}</div>
    </div>
    <!--  搜索结果列表  -->
    <div class="searchList">
      <div class="contentList">
        <!-- v-lazy 如果配置lazyComponent属性的话是只能懒加载图片的 -->
        <div
            class="itemDetail"
            v-for="(item, index) in comic.infoList"
            :key="index"
            v-lazy="item"
            @click="toRead"
        >
          <img :src="item.imgLink" :alt="item.title"/>
          <div>
            <span>{{item.title}}</span>
            <span>{{item.author}}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import axios from "axios";
  import {reactive} from "vue";
  import {onBeforeRouteLeave, useRouter} from "vue-router";
  import store from "@/store";

  export default {
    name: "ComicSearch",
    emits: ['hideNav'],
    setup (_, context) {
      const router = useRouter()
      const comic = reactive({
        infoList: []
      })

      context.emit('hideNav')
      axios.get('./cartoon/comicInfo.json').then(
          response => {
            comic.infoList = response.data
            console.log(`this is comic.infoList ${comic.infoList}`)
          },
          error => {
            console.log(`something error ${error.message}`)
          }
      )

      function toRead () {
        router.push({name: 'look', params: {imgLists:JSON.stringify(comic.infoList), isSearch: true}})
      }

      onBeforeRouteLeave((to, from, next) => {
        if (to.name === 'look') {
          if (store.state.cachePath.indexOf('look') === -1) {
            store.commit('NEW_CACHE_PATH', to.name)
          }
        }
        next()
      })

      return {
        comic,
        toRead
      }
    }
  }
</script>

<style lang="less">
  #search {
    .searchInput {
      padding: 20px 15px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 1px solid #e2e2e2;
      i {
        font-size: 30px;
      }
      .input {
        width: 75%;
        position: relative;
        display: flex;
        align-items: center;
        padding-left: 5px;
        i {
          position: absolute;
          font-size: 20px;
          padding-left: 10px;
        }
        input {
          width: 100%;
          padding: 6px 0;
          border-radius: 15px;
          border: transparent;
          background-color: #eff0f2;
          text-indent: 38px;
        }
      }
    }
    //历史记录
    .history{
      border-bottom: 1px solid #e2e2e2;
      padding-top: 20px;
      padding-bottom:10px ;
      margin: 0 8px;
      .historyName{
        padding-left: 15px;
        font-size: 14px;
        color: #666;
        padding-bottom: 10px;
      }
      .historylist{
        display: flex;
        justify-content: space-between;
        padding: 0  16px 10px 16px;
      }
    }
    //热门搜索
    .hotSearch{
      padding: 0 30px;
      .title{
        padding: 20px 0;
        font-size: 14px;
        color: #666;
      }
      .searchKeep{
        display: inline-block;
        padding: 3px 5px;
        background-color: #f4f4f4;
        font-size: 14px;
        margin: 4px 6px;
      }
    }
    // 搜索列表
    .searchList {
      .contentList {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        padding: 0 20px;
        .itemDetail {
          width: 15%;
          padding: 0 10px;
          img {
            width: 100%;
            height: 150px;
            border-radius: 5px;
          }
          div {
            width: 100%;
            span {
              font-size: 12px;
              padding: 0 3px;
              display: block;
              text-overflow: ellipsis;
              overflow: hidden;
              white-space: nowrap;
            }
          }
        }
      }
    }
}
</style>