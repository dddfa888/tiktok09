/*!
 * @author atongmu <zhounianlai@teacher.com.cn>
 * date 11/07/2019
 * description The project global config.
 */
const ENV_DEV = 'https://tk04.khkjhkh.top/wap' // dev

const ENV_PRO = window.location.hostname//
let base_url = ''
if (process.env.NODE_ENV === 'development') {
    base_url = 'http://' + ENV_DEV
} else {
    base_url = 'https://' + ENV_PRO + '/wap'
}
export default {
    /**
     * @description Request api base url.
     */
    baseUrl: {
        dev:'https://tiktokvvip.shop/wap/',
        // dev: "http://stu.teacher.com.cn",
        pro: "/wap/",
    },

    /**
     * @description Index page name.
     */
    homePage: "myProject",

    /**
     * @description Public image base url.
     */
    imageBaseUrl: "",

    /**
     * @description Cookie's expire date, default 7 days.
     */
    cookieExpires: 7,
  }
