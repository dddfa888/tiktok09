const { defineConfig } = require("@vue/cli-service");
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");
// process.env.VUE_APP_VERSION = require('./package.json').version

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  productionSourceMap: false,
  configureWebpack: {
    plugins: [new NodePolyfillPlugin()],
    module: {
      rules: [],
    },
  },
  // publicPath: process.env.NODE_ENV === "production" ? "././" : "/",
  // assetsDir: 'assets',
  outputDir:
    process.env.VUE_APP_ITEM_NAME == "Shopee"
      ? "dist"
      : process.env.VUE_APP_ITEM_NAME + "-buyPhone",
  devServer: {
    port: 8022,
    host: "0.0.0.0",
    https: false,
    open: true,
    proxy: {
      "/": {
        // target: "http://192.168.0.133:8081/",
        target: "https://tk06.ffdassadff.shop/",
        ws: false, //是否跨域
        changeOrigin: true,
        // pathRewrite: {
        //   "^/wap": "",
        // },
        onProxyReq(proxyReq, req, res) {
          console.log("Request URL:", req.url); // 调试请求 URL
        },
      },
      // // 可为不同的接口配置不同的代理地址
      // '/wap/api': {
      //     // 服务地址，即你要访问的服务器地址
      //     // target: 'https://rfbhabkjk.com/wap/',
      //     // 路径重写，将'/user/login'重写为'/login'
      //     pathRewrite: {
      //         '^/wap/api': ''
      //     },
      //     // 所有信息都在命令行工具打印
      //     logLevel: 'debug'
      // },
    },
  },
});
