<template>
  <div class="page-container">
    <div class="page-header">
      <h2>个人信息</h2>
    </div>

    <el-card class="profile-card">
      <div class="profile-content">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <img v-if="form.avatar" :src="form.avatar" class="avatar-img" />
            <div v-else class="avatar-placeholder">
              <el-icon :size="48"><User /></el-icon>
            </div>
          </div>
          <el-button type="primary" text @click="triggerUpload" style="margin-top: 12px">
            <el-icon><Upload /></el-icon> 更换头像
          </el-button>
          <input ref="fileInput" type="file" accept="image/*" style="display: none" @change="handleFileChange" />
        </div>

        <div class="form-section">
          <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" maxlength="50" show-word-limit />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="收货地址">
              <el-input v-model="form.address" placeholder="请输入收货地址" type="textarea" :rows="2" />
            </el-form-item>
           <el-form-item>
              <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'

const userStore = useUserStore()
const formRef = ref(null)
const submitLoading = ref(false)
const fileInput = ref(null)

const form = reactive({
  id: '',
  username: '',
  nickname: '',
  phone: '',
  address: '',
  avatar: ''
})

const formRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const loadUserInfo = () => {
  form.id = userStore.userId || ''
  form.username = userStore.userInfo?.username || ''
  form.nickname = userStore.userInfo?.nickname || ''
  form.phone = userStore.userInfo?.phone || ''
  form.address = userStore.userInfo?.address || ''
  form.avatar = userStore.userInfo?.avatar || ''
}

const triggerUpload = () => {
  fileInput.value?.click()
}

const handleFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  try {
    const result = await request.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.avatar = result.data
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('上传失败', error)
    ElMessage.error('头像上传失败')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await request.put('/user', {
      id: form.id,
      nickname: form.nickname,
      phone: form.phone,
      address: form.address,
      avatar: form.avatar
    })
    ElMessage.success('保存成功')
    // 更新store中的用户信息
    userStore.setUserInfo({ ...userStore.userInfo, nickname: form.nickname, phone: form.phone, address: form.address, avatar: form.avatar })
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.page-container { padding: 0; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.profile-card { border-radius: 12px; border: none; }

.profile-content {
  display: flex;
  gap: 40px;
  padding: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #f0f0f0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

.form-section { flex: 1; max-width: 500px; }
</style>