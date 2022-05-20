<template>
  <div class="storyTypeBody">
    <div class="bodyHead">
      <h2>漫画列表</h2>
    </div>
    <div class="subBodyHead">
      <span>共{{comic.comicList.length}}部漫画</span>
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
  </div>
</template>

<script>
  import {onBeforeRouteLeave, useRouter} from "vue-router";
  import axios from 'axios'
  import {reactive} from "vue";

  export default {
    name: "BoyComic",
    setup () {
      const router = useRouter()
      const comic = reactive({
        comicList: []
      })
      axios.get('./cartoon/girlTypeLists.json').then(
          response => {
            comic.comicList = response.data
            console.log('这是少女漫里的comicList', comic.comicList)
          },
          error => {
            console.log(error.message)
          }
      )
      // 进入漫画详情页
      function toDetail (item) {
        /* 请务必添加JSON转换（后面收数据方再解析） 否则后面收到的会是[Object Object] 这里还使用了URL编码*/
        router.push({name: 'detail', query: {img: encodeURI(JSON.stringify(item))}})
        console.log('--{...item}--', {...item})
      }
      // 路由离开前
      onBeforeRouteLeave ((to, _, next) => {
        console.log(to.path)
        next()
      })
      return {
        comic,
        toDetail
      }
    }
  }
</script>

<style lang="less">
// 分类查询体
  .storyTypeBody {
  margin-top: 30px;
  padding: 0 10px;
  border-bottom: 1px solid #e4e4e4;
  .bodyHead {
    display: inline-block;
    padding: 20px 0;
  }
  .subBodyHead {
    padding: 10px 0;
  }
  .comicListBody {
    ul {
      display: flex;
      flex-flow: row wrap;
      width: 30%;
      li {
        img {
          display: inline-block;
          width: 50%;
          vertical-align: top;
        }
        div {
          display: inline-block;
          width: 50%;
          height: 100%;
          vertical-align: top;
          h3 {
            padding: 3px 10px 0;
            text-overflow: ellipsis;
          }
          p {
            padding: 3px 10px 0;
            text-overflow: ellipsis;
            font-size: 10px;
          }
          span {
            display: block;
            width: 75%;
            padding: 10px 5px;
            margin-left: 10px;
            border: 1px solid #b3d4fc;
            text-emphasis-color: #b3d4fc;
            text-align: center;
            font-size: 16px;
            cursor: pointer;
          }
        }
      }
    }
  }
}
</style>
