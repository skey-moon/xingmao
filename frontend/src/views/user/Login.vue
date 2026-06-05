<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="bg-shape shape1"></div>
      <div class="bg-shape shape2"></div>
      <div class="bg-shape shape3"></div>
    </div>
    <el-card class="login-card" shadow="hover">
      <div class="login-header">
        <div class="logo-icon">
          <el-icon :size="40" color="#409EFF"><Food /></el-icon>
        </div>
        <h2>兴茂食材配送管理系统</h2>
        <p>安全 · 高效 · 便捷</p>
      </div>

      <!-- 登录类型选择 -->
      <div class="login-type-selector" v-if="!isRegister">
        <div class="login-type-label">选择登录身份</div>
        <div class="login-type-buttons">
          <el-button :class="{ active: loginType === 'user' }" @click="loginType = 'user'">
            <el-icon><User /></el-icon>
            客户
          </el-button>
          <el-button :class="{ active: loginType === 'employee' }" @click="loginType = 'employee'">
            <el-icon><Briefcase /></el-icon>
            公司员工
          </el-button>
          <el-button :class="{ active: loginType === 'admin' }" @click="loginType = 'admin'">
            <el-icon><Key /></el-icon>
            管理员
          </el-button>
        </div>
      </div>

      <!-- 登录表单 -->
      <el-form v-if="!isRegister" :model="loginForm" :rules="loginRules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" size="large" prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" size="large" prefix-icon="Lock"
            show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
        <div class="switch-mode">
          还没有账号？
          <el-button type="text" @click="isRegister = true">立即注册</el-button>
        </div>
      </el-form>

      <!-- 注册表单 -->
      <el-form v-else :model="registerForm" :rules="registerRules" ref="registerFormRef">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名" size="large" prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码" size="large" prefix-icon="Lock"
            show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" size="large" prefix-icon="Lock"
            show-password @keyup.enter="handleRegister" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="昵称" size="large" prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="registerForm.phone" placeholder="手机号（选填）" size="large" prefix-icon="Phone" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleRegister">
            注 册
          </el-button>
        </el-form-item>
        <div class="switch-mode">
          已有账号？
          <el-button type="text" @click="isRegister = false">去登录</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login as loginApi, register as registerApi } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const registerFormRef = ref(null)
const loading = ref(false)
const isRegister = ref(false)
const loginType = ref('user')

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: ''
})

// 根据登录类型设置默认用户名提示
watch(loginType, (newType) => {
  if (newType === 'admin') {
    loginForm.username = 'admin'
  } else if (newType === 'employee') {
    loginForm.username = 'employee1'
  } else {
    loginForm.username = ''
  }
  loginForm.password = ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await loginApi({
      username: loginForm.username,
      password: loginForm.password,
      loginType: loginType.value
    })
    // res = {code: 200, msg: "...", data: {token, role, userId, ...}}
    // 检查响应状态
    if (res.code !== 200) {
      ElMessage.error(res.msg || '登录失败')
      return
    }
    const result = res.data
    console.log('Login result:', result)
    // 保存登录结果
    userStore.setLoginResult(result)
    ElMessage.success('登录成功，欢迎回来！')
    // 根据用户实际角色跳转到不同页面
    if (result.role === 'admin' || result.role === 'employee') {
      router.push('/home')
    } else {
      router.push('/order/create')
    }
  } catch (error) {
    console.error('Login failed:', error)
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await registerApi({
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname,
      phone: registerForm.phone,
      role: 'user'
    })
    ElMessage.success('注册成功，请登录！')
    isRegister.value = false
    loginForm.username = registerForm.username
    loginForm.password = ''
    loginType.value = 'user'
  } catch (error) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  width: 100%;
  height: 100%;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.shape1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  top: -100px;
  right: -100px;
  animation: float 6s ease-in-out infinite;
}

.shape2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #E6A23C, #F56C6C);
  bottom: -50px;
  left: -50px;
  animation: float 8s ease-in-out infinite reverse;
}

.shape3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #909399, #409EFF);
  bottom: 20%;
  right: 10%;
  animation: float 5s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

.login-card {
  width: 420px;
  padding: 10px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: none;
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-type-selector {
  margin-bottom: 20px;
}

.login-type-label {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-bottom: 12px;
}

.login-type-buttons {
  display: flex;
  gap: 8px;
}

.login-type-buttons .el-button {
  flex: 1;
  height: 40px;
  border-radius: 8px;
}

.login-type-buttons .el-button.active {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  border-color: #409EFF;
  color: #fff;
}

.login-type-buttons .el-button:not(.active):hover {
  border-color: #409EFF;
  color: #409EFF;
}

.logo-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #e8f4fd 0%, #d4e9fc 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
}

.login-header h2 {
  margin: 0 0 8px 0;
  color: #1a1a2e;
  font-size: 24px;
  font-weight: 600;
}

.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #409EFF 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.switch-mode {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}
</style>