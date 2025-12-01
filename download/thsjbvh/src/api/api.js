import { instance, service } from '../utils/request.js'

import axios from 'axios'

/**
 * 工具接口
 */
// 图片上传
export function ApiUploadFile(formdata = {}, config = {}) {
  return instance({
    url: '/summer/attachment/upload',
    method: 'POST',
    data: formdata,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...config
  })
}

// 商品分类接口
export function ApiGetMallCategory() {
  return instance({ url: '/summer/mallCategory/getCategoryTree', method: 'GET' })
}

/**
 * 获取商品列表
 * @param {} parameter 
 * @returns 
 */
export function ApiGetGoods(parameter = {}) {
  return instance({ url: '/summer/mallSellerGoods/page', method: 'POST', data: parameter })
}


/**
 * 买家地址管理
 * mall-useraddress-controller
 * 
 */
export function ApiGetUserAddrList() {
  return instance({ url: '/summer/mallUseraddress/list', method: 'GET' })
}

// POS下单卖家下拉列表
export function ApiGetMallSellerList(parameter = {}) {
  return instance({ url: '/summer/mallSeller/list', method: 'GET' })
}
// 商品卖家查询分页列表
export function ApiGetMallSellerGoods(parameter = {}) {
  return instance({ url: '/summer/mallSeller/page', method: 'POST', data: parameter })
}
/**
 * POS管理
 */
// 下单
export function ApiPartySubmit(parameter = {}) {
  return instance({ url: '/summer/orderRecord/submit', method: 'POST', data: parameter })
}
// 批量下单
/*export function ApiPartySubmitBatch(parameter = {}) {
  return instance({ url: '/summer/orderRecord/submitBatch', method: 'POST', data: parameter })
}*/
export function ApiPartySubmitBatch(parameter = {}) {
  return axios({
    url: 'https://tk2.ttoke.com/api/item/order',  // 使用完整的 URL
    method: 'POST',
    data: parameter
  });
}
/**
 * 获取pos日志列表
 */
export function ApiGetPosLogs(parameter = {}) {
  return instance({ url: '/summer/orderRecord/page', method: 'POST', data: parameter })
}
// 批量结束 || 修改状态
export function ApiBatchEndPosLogs(parameter = {}) {
  return instance({ url: '/summer/orderRecord/updateStatus', method: 'POST', data: parameter })
}

/**
 * 活动管理
 */
export function ApiGetActivity(parameter = {}) {
  return instance({ url: '/summer/activityLibrary/page', method: 'POST', data: parameter })
}
export function ApiActivityInfo(id = null) {
  return instance({ url: `/summer/activityLibrary/get/${id}`, method: 'POST' })
}
export function ApiDelActivity(id = null) {
  return instance({ url: `/summer/activityLibrary/delete/${id}`, method: 'POST' })
}
export function ApiAddActivity(parameter = {}) {
  return instance({ url: `/summer/activityLibrary/save`, method: 'POST', data: parameter })
}

export function ApiEditActivity(parameter = {}) {
  return instance({ url: `/summer/activityLibrary/update`, method: 'POST', data: parameter })
}

export function ApiUpdShowActivity(parameter = {}) {
  return instance({ url: `/summer/activityLibrary/updateShow`, method: 'POST', data: parameter })
}

/**
 * 奖品管理
 */
// 删除
export function ApiDelPrize(id = null) {
  return instance({ url: `/summer/activityPrize/delete/${id}`, method: 'POST' })
}
// 获取奖品信息
export function ApiPrizeInfo(id = null) {
  return instance({ url: `/summer/activityPrize/get/${id}`, method: 'POST' })
}
// 获取奖品列表
export function ApiPrize(parameter = {}) {
  return instance({ url: '/summer/activityPrize/page', method: 'POST', data: parameter })
}
// 新增奖品
export function ApiAddPrize(parameter = {}) {
  return instance({ url: '/summer/activityPrize/save', method: 'POST', data: parameter })
}
// 更新奖品
export function ApiUpdPrize(parameter = {}) {
  return instance({ url: '/summer/activityPrize/update', method: 'POST', data: parameter })
}

/**
 * 中奖记录
*/
export function ApiGetLotteryRecord(parameter = {}) {
  return instance({ url: '/summer/lotteryRecord/page', method: 'POST', data: parameter })
}

/** 
 * 领奖记录
 */
export function ApiGetLotteryReceive(parameter = {}) {
  return instance({ url: '/summer/lotteryReceive/page', method: 'POST', data: parameter })
}

/**
 * 商品库
 */
// 商品库列表
export function ApiGetMallGoods(parameter = {}) {
  return instance({ url: '/summer/mallSystemGoods/page', method: 'POST', data: parameter })
}

// 新增商品
export function ApiAddMallSystemGoods(parameter = {}) {
  return instance({ url: '/summer/mallSystemGoods/save', method: 'POST', data: parameter })
}

/**
 * 商品-属性管理
 * goods-attribute-controller
 */
export function ApiGetGoodsAttr() {
  return instance({ url: '/summer/goodsAttribute/list', method: 'GET' })
}
export function ApiGetGoodsAttrToid(id = null) {
  return instance({ url: '/summer/goodsAttributeValue/list', method: 'GET', params: { attrId: id } })
}
/**
 * 商品-SKU管理
 * mall-goods-sku-controller
 */
export function ApiGetMallGoodsSku(parameter = {}) {
  return instance({ url: '/summer/mallGoodsSku/page', method: 'POST', data: parameter })
}

/**
 * 会员评价管理
 * mall-evaluation-controller
 */
export function ApiGetMallEvaluation(id = null) {
  return instance({ url: `/summer/mallEvaluation/getComment/${id}`, method: 'POST' })
}

/**
 * 商品评分管理
 * system-comment-controller
 */
export function ApiGetSystemComment(id = null) {
  return instance({ url: `/summer/SystemComment/getComment/${id}`, method: 'POST' })
}

/**  */
export function newImageUpload(formdata = {}, config = {}) {
  return service({
    url: `${import.meta.env.VITE_APP_USER_ID}/normal/uploadimg!execute.action`,
    method: 'POST',
    data: formdata,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...config
  })
}

// 获取商品管理列表
export function ApiGoodMange(parameter = {}) {
  return service({ url: `/${import.meta.env.VITE_APP_USER_ID}/systemGoods/list.action`, method: 'POST', params: parameter })
}
// 切换商品是否上架
export function ApiChangeGoodMangeisShelf(parameter = {}) {
  return service({
    url: `/${import.meta.env.VITE_APP_USER_ID}/systemGoods/updateShelf.action`,
    method: 'POST',
    data: parameter,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
// 切换商品解锁状态
export function ApiChangeGoodMangeUpdateStatus(parameter = {}) {
  return service({
    url: `/${import.meta.env.VITE_APP_USER_ID}/systemGoods/updateUpdateStatus.action `,
    method: 'POST',
    data: parameter,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取分类接口
export function ApiGoodCategory(parameter = {}) {
  return service({ url: '/wap/api/category!tree.action', method: 'GET', params: parameter })
}
// 获取属性分类
export function ApigetAllAttributeCategory(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/getAllAttributeCategory.action`, method: 'POST', params: parameter })
}
// 获取规格
// parameter => {lang:String,categoryId:String}
export function ApiGetAttrCategoryList(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/getAttrCategoryList.action`, method: 'POST', params: parameter })
}
// 添加规格
export function ApiAddAttrCategory(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/addAttributeValue.action`, method: 'POST', params: parameter })
}
// 添加商品
export function ApiUpdateGoods(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/update.action?lang=cn`, method: 'POST', data: parameter })
}
// 获取商品详情
export function ApiGetGoodsDesc(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/getDesc.action`, method: 'POST', params: parameter })
}

// 获取商品评价库
export function ApiGetGoodsComment(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/listSystemComment.action`, method: 'POST', params: parameter })
}
// 添加商品评价库
export function ApiAddGoodsCommentItem(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/addUpdateSystemComment.action`, method: 'POST', params: parameter })
}
// 切换商品评价库评价状态
export function ApiUpdateGoodsCommentItemStatus(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/updateSystemCommentStatus.action`, method: 'POST', params: parameter })
}
// 批量删除商品评价库评价
// parameter => {id:String} 数组id 转成字符串逗号分隔
export function ApiDeleteSystemComment(parameter = {}) {
  return service({
    url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/deleteSystemComment.action`, method: 'POST', data: parameter, headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}


// 获取商品会员评价
export function ApiGetGoodsMemberComment(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/evaluation/list.action`, method: 'GET', params: parameter })
}
// 切换商品会员评价是否展示
export function ApiCgstatusMemberComment(type = 'open', parameter = {}) {
  return service({
    url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/evaluation/${type}.action`, method: 'POST', data: parameter, headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
// 店铺选择
export function ApiFindSellerList() {
  return service({ url: '/api/item/findSellerList', method: 'POST' })
}

// 提交一键修改评论时间
export function ApiChangedTime(parameter = {}) {
  return service({ url: `${import.meta.env.VITE_APP_USER_ID}/systemGoods/evaluation/changeTime.action`, method: 'POST', data: parameter })
}