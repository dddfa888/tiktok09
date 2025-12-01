<template>
  <div class="upload" :style="{ width: this.width + 'px', height: this.height + 'px' }" v-loading="loading">
    <!-- 上传按钮 -->
    <el-upload v-if="showUpload" class="upload-content" :action="uploadUrl" :show-file-list="false"
      :before-upload="beforeAvatarUpload" :data="{ deadline: 0, file_size: 512 * 1024 }" ref="upload"
      accept="image/png,image/jpg,image/jpeg" :limit="1" @click.native.prevent="handleClick">
      <el-image v-if="imageUrl" :src="imageUrl" class="upload-content-image" lazy>
        <div slot="error" class="image-slot">
          <i class="el-icon-picture-outline"></i>
        </div>
      </el-image>
      <div class="upload-content-icon" v-else>
        <el-image :src="iconImg" class="upload-icon">
          <div slot="error" class="image-slot">
            <i class="el-icon-picture-outline"></i>
          </div>
        </el-image>
        <div class="upload-content-text">{{ addText }}</div>
      </div>
    </el-upload>

    <!-- 裁剪弹窗 -->
    <vue-img-cutter v-if="dialogVisible" :showChooseBtn="true" :img="tempImgSrc" :cutWidth="960" :cutHeight="150"
      :modalTitle="$t('图片裁剪')" :label="$t('选择图片')" :imgMove="true" :fixed="false" :fixed-number="[32, 5]" @cutDown="handleCutDown">
      <template #cancel>
        <button type="default" class="btn btn-default" style="width: 100px;border: 1px solid #eaeaea;height: 40px"
          @close="dialogVisible = false">{{
            $t('取消') }}</button>
      </template>
      <template #confirm>
        <button type="default" class="btn btn-default" style="width: 100px;border: 1px solid #eaeaea;height: 40px;margin-left: 15px"
        @cutDown="handleCutDown">{{
            $t('确定') }}</button>
      </template>
    </vue-img-cutter>
  </div>
</template>

<script>
import { imageUpload } from "@/api/user";
import { i18n } from "@/lang";
import Toast from "@/utils/toast";
import { Notification } from "element-ui";
import VueImgCutter from "vue-img-cutter";
// import "vue-img-cutter/dist/style.css";

export default {
  name: "SoUpload",
  components: {
    VueImgCutter,
  },
  data() {
    return {
      dialogVisible: false, // 控制裁剪弹窗显示
      imageUrl: "", // 最终显示的图片 URL
      tempImgSrc: "", // 临时图片源，用于裁剪
      loading: false,
      iconImg: require("@/assets/images/bxs_camera-plus.png"),
      showUpload: true,
    };
  },
  props: {
    value: {
      type: [String, Object],
      default: "",
    },
    uploadUrl: {
      type: String,
      default: "",
    },
    width: {
      type: Number,
      default: 345,
    },
    height: {
      type: Number,
      default: 95,
    },
    moduleName: {
      type: String,
      default: "",
    },
    addText: {
      type: String,
      default: i18n.t("添加图片"),
    },
  },
  watch: {
    value: {
      handler: function (val) {
        this.imageUrl = val;
      },
      immediate: true,
    },
  },
  mounted() {
    this.loading = false;
  },
  methods: {
    // 文件上传之前的处理
    beforeAvatarUpload(file) {
      this.dialogVisible = true;
      // const isJPG = file.type === "image/jpeg" || file.type === "image/png" || file.type === "image/jpg";
      // const isLt2M = file.size / 1024 / 1024 < 4;

      // // 图片格式校验
      // if (!isJPG) {
      //   Notification.error({
      //     title: i18n.t("上传图片只能是 JPG/JPEG/PNG 格式!"),
      //     message: i18n.t("当前上传图片格式为：") + file.type,
      //   });
      //   this.$refs.upload.clearFiles();
      //   return false;
      // }

      // // 图片大小校验
      // if (!isLt2M) {
      //   Notification.error({
      //     title: i18n.t("上传图片大小不能超过 4MB!"),
      //     message: i18n.t("当前上传图片大小为：") + (file.size / 1024 / 1024).toFixed(2) + "MB",
      //   });
      //   this.$refs.upload.clearFiles();
      //   return false;
      // }

      // // 读取图片并显示裁剪弹窗
      // const reader = new FileReader();
      // reader.onload = (e) => {
      //   this.tempImgSrc = e.target.result; // 设置临时图片源
      //   this.dialogVisible = true; // 打开裁剪弹窗
      // };
      // reader.readAsDataURL(file);

      // return false; // 阻止默认上传行为
    },

    handleClick() {
      console.log("点击事件触发，但不打开文件夹！");
      // 这里可以执行其他逻辑，例如打开裁剪框
      this.dialogVisible = true;
    },

    // 裁剪完成回调
    handleCutDown(cutImg) {
      console.log(cutImg,"传参");
      this.dialogVisible = false; // 关闭裁剪弹窗
      this.imageUrl = cutImg.dataURL; // 设置裁剪后的图片 URL
      this.uploadImg(cutImg.file); // 上传裁剪后的图片
    },

    // 上传图片
    uploadImg(file) {
      const that = this;
      that.loading = true;
      const formData = new FormData();
      formData.append("file", file);
      formData.append("deadline", 0);
      formData.append("file_size", 1920 * 300);
      formData.append("moduleName", that.moduleName);

      imageUpload(formData)
        .then((res) => {
          Toast.clear();
          that.loading = false;
          that.$emit("input", res.data);
          that.$emit("success", res.data);
        })
        .catch((err) => {
          console.log(err);
          that.loading = false;
          Toast.clear();
          Toast(that.$t("添加失败"));
        });
    },

    // 初始化上传组件
    initUpload() {
      this.showUpload = false;
      setTimeout(() => {
        this.showUpload = true;
      }, 100);
    },
  },
};
</script>

<style lang="scss" scoped>

.upload {
  display: inline-block;

  .upload-content {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #ffffff;
    position: relative;
    border: 1px solid #dddddd;
    border-radius: 4px;

    .upload-content-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      position: absolute;
      top: 0;
      left: 0;
    }

    .upload-content-icon {
      width: 100%;
      height: 100%;
      display: block;
      font-size: 36px;
      color: #999;
      line-height: 36px;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;

      .upload-icon {
        width: 36px;
        height: 36px;
        margin-bottom: 12px;
      }
    }

    .upload-content-text {
      font-family: "Roboto";
      font-style: normal;
      font-weight: 400;
      font-size: 12px;
      line-height: 14px;
      text-align: center;
      letter-spacing: -0.3px;
      color: #aaaaaa;
    }
  }
}
</style>