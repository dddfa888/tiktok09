<template>
  <div class="bank-container pb-12">
    <fx-header>
      <template #title>{{$t('银行卡绑定')}}</template>
    </fx-header>
    <van-form @submit="onSubmit" class="mt-4">
  <van-cell-group inset>
    <van-field
      v-model="bankName"
      name="bankName"
      :label="t('开户行')"
      :placeholder="t('请输入开户行名称')"
      :rules="[{ required: true, message: t('请输入开户行名称') }]"
    />
    <van-field
      v-model="accountName"
      name="accountName"
      :label="t('姓名')"
      :placeholder="t('entryName')"
      :rules="[{ required: true, message: t('entryName') }]"
    />
    <van-field
      v-model="cardNo"
      name="cardNo"
      :label="t('卡号')"
      :placeholder="t('请输入卡号')"
      :rules="[{ required: true, message: t('请输入卡号') }]"
    />
  </van-cell-group>
  <div style="margin: 16px;">
    <van-button round block type="primary" native-type="submit">
      {{ t('保存') }}
    </van-button>
  </div>
</van-form>

  </div>

</template>

<script setup>
import { onBeforeMount, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { bindBankCard } from '@/service/user.api.js'
import { Toast} from "vant";
const {t} = useI18n();
const route = useRoute()
const router = useRouter()

const bankName = ref('')
const accountName = ref('')
const cardNo = ref('')
const onSubmit = async () => {
  try {
    await bindBankCard({
      bankName: bankName.value,
      accountName: accountName.value,
      cardNo: cardNo.value,
      partyId: localStorage.getItem('sellerId') || ''
    })
    Toast.success(t(`bindSuccess`))
    router.back()
  } catch (error) {
    console.log(error);
  }
};

</script>
<style lang="scss" scoped>
.bank-container {}
</style>