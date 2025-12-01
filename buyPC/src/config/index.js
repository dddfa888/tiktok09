// let env_url = 'thsjbvh.site'
// let env_url = 'mall.tiktokvv.xyz'
// let env_url = "tk04.khkjhkh.top/wap";
let env_url = "127.0.0.1:8081";
const pro_url = window.location.hostname;

let ENV_DEV = "http://" + env_url; // dev
let ENV_PRO = "https://" + pro_url + "/wap/";

let HOST_URL = "";
let BASE_URL = "";
let WS_URL = "";
if (process.env.NODE_ENV === "development") {
  HOST_URL = "https://" + env_url;
  BASE_URL = ENV_DEV;
  WS_URL = `wss://${env_url}/data/websocket`;
} else {
  HOST_URL = "https://" + pro_url;
  BASE_URL = ENV_PRO;
  WS_URL = `wss://${pro_url}/data/websocket`; // 演示环境
}

export default {
  HOST_URL,
  BASE_URL,
  WS_URL,
};
