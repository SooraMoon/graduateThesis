<template>
  <div class="box">
    <div class="bottom_nav">
      <!--   vue3写法 自定义router-link   -->
      <router-link to="/comic-vue3/home" custom v-slot="{navigate}">
        <div @click="navigate">
          <i class="iconfont icon-zhuye" ></i>
          <p>主页</p>
        </div>
      </router-link>
      <!--   custom 防止将 div 渲染成 a 标签   -->
      <router-link to="/my" custom v-slot="{navigate}" >
        <div @click="navigate">
          <i class="iconfont icon-31wode" ></i>
          <p>我的</p>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'FooterNav',
    setup () {

    }
  }
</script>

<style scoped lang="less">
  .box {
    position: relative;
  .bottom_nav {
    background-color: #f4f4f4;
    z-index: 10;
    min-width: 320px;
    max-width: 750px;
    width: 100%;
    display: flex;
    align-items: center;
    height: 50px;
    position: fixed;
    bottom: 0;
    text-align: center;
    color: #666;
    font-size: 14px;
  div{
    flex: 1;
  }
  i{
    font-size: 20px;
  }
  .router-link-active{
  // color: lightseagreen;
  span{
    font-size: 16px;
  }
  i{
    font-size: 22px;
  }
  }
  }
  }
  .router-link-active{
    color: lightseagreen;
  }
</style>
