import request from "@/request";

// 获取消息列表
export const _getMsg = (params) => {
    let sessionUUID = localStorage.getItem("sessionUUID");
    if (!sessionUUID) {
        sessionUUID = crypto.randomUUID();
        localStorage.setItem("sessionUUID", sessionUUID);
    }
    return request({
        url: "api/newOnlinechat!list.action",
        // loading: true,
        method: "GET",
        params: {
            token: params.token,
            message_id: params.message_id || '',  // 翻页用到
            show_img: params.show_img || true,
            anonymousId: sessionUUID,
        }
    })
};

// 未读消息
export const _getUnreadMsg = () => {
    return request({
        url: "api/newOnlinechat!unread.action",
        loading: true,
        method: "GET",
    })
};

// 发送消息
export const _sendMsg = (type = 'text', content = '', token = '') => {
    let sessionUUID = localStorage.getItem("sessionUUID");
    if (!sessionUUID) {
        sessionUUID = crypto.randomUUID();
        localStorage.setItem("sessionUUID", sessionUUID);
    }
    return request({
        url: "api/newOnlinechat!send.action",
        // loading: true,
        method: "GET",
        params: {
            type, // 消息类型， img / text
            content,
            token,
            anonymousId: sessionUUID,
        }
    })
};
// 发送消息
export const _sendMsg2 = (type = 'text', content = '', token = '') => {
    return request({
        url: "api/newOnlinechat!send.action",
        // loading: true,
        method: "GET",
        params: {
            type, // 消息类型， img / text
            content,
            token
        }
    })
};

// 获取客服在线时间
export const _getOnlineTime = () => {
    return request({
        url: "api/customerServiceTimeRange",
        // loading: true,
        method: "GET",
    })
}
