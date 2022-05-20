const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  //lintOnSave: false
  /*devServer: {
    proxy: {
      '/get': {
        target: 'http://localhost:5000',
        pathRewrite: {'^/get': ''},    //过滤前缀
        ws: true,   //用于支持websocket
        changeOrigin: true  //用于控制请求头中的host值
      }
    }
  }*/
})
