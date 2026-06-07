<template>
  <div class="page-container">
    <div class="page-header">
      <h2>收货地址</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增收货地址
      </el-button>
    </div>

    <el-card class="table-card">
      <div v-if="tableData.length === 0 && !loading" class="empty-address">
        <el-icon size="64"><Location /></el-icon>
        <p>暂无收货地址</p>
        <el-button type="primary" @click="handleAdd">添加地址</el-button>
      </div>
      <div v-else class="address-list">
        <div v-for="addr in tableData" :key="addr.id" class="address-item">
          <div class="address-main">
            <div class="address-tag" v-if="addr.isDefault === 1">
              <el-tag type="success" effect="plain">默认</el-tag>
            </div>
            <div class="address-detail">
              <div class="receiver">{{ addr.receiverName }} {{ addr.phone }}</div>
              <div class="full-address">
                {{ addr.province || '' }}{{ addr.city || '' }}{{ addr.district || '' }}{{ addr.detailAddress }}
              </div>
            </div>
          </div>
          <div class="address-actions">
            <el-button type="primary" text @click="handleEdit(addr)" v-if="addr.isDefault !== 1">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="warning" text @click="handleSetDefault(addr.id)" v-if="addr.isDefault !== 1">
              <el-icon><Star /></el-icon> 设为默认
            </el-button>
            <el-button type="danger" text @click="handleDelete(addr.id)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑地址' : '新增地址'" width="500px" :close-on-click-modal="false">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" placeholder="请输入详细地址" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyAddresses, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const currentId = ref(null)

const form = reactive({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: ''
})

const formRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const result = await getMyAddresses()
    tableData.value = result.data || []
  } catch (error) {
    console.error('加载地址失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  Object.assign(form, { receiverName: '', phone: '', province: '', city: '', district: '', detailAddress: '' })
  dialogVisible.value = true
}

const handleEdit = (addr) => {
  isEdit.value = true
  currentId.value = addr.id
  Object.assign(form, {
    receiverName: addr.receiverName,
    phone: addr.phone,
    province: addr.province || '',
    city: addr.city || '',
    district: addr.district || '',
    detailAddress: addr.detailAddress
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateAddress(currentId.value, form)
      ElMessage.success('修改成功')
    } else {
      await addAddress(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('保存地址失败', error)
    ElMessage.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

const handleSetDefault = async (id) => {
  try {
    await ElMessageBox.confirm('确定将此地址设为默认地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await setDefaultAddress(id)
    ElMessage.success('设置成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('设置默认失败', error)
    }
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAddress(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

onMounted(() => {
  loadData()
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

.table-card { border-radius: 12px; border: none; }

.empty-address {
  text-align: center;
  padding: 60px 0;
  color: #909399;
}

.empty-address p { margin: 16px 0; font-size: 16px; }

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  transition: box-shadow 0.3s;
}

.address-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.address-main { flex: 1; }

.address-tag { margin-bottom: 8px; }

.receiver {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.full-address {
  font-size: 14px;
  color: #909399;
}

.address-actions {
  display: flex;
  gap: 8px;
}
</style>