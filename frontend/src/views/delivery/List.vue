<template>
  <div class="page-container">
    <div class="page-header">
      <h2>配送管理</h2>
      <el-button type="primary" @click="openPersonDialog()">
        <el-icon><Plus /></el-icon> 配送员管理
      </el-button>
    </div>

    <el-card class="table-card">
      <div class="toolbar">
        <el-select v-model="filterStatus" placeholder="配送状态" style="width: 150px" clearable @change="loadData">
          <el-option label="待取货" :value="0" />
          <el-option label="配送中" :value="1" />
          <el-option label="已送达" :value="2" />
        </el-select>
        <el-select v-model="filterDeliveryPerson" placeholder="配送员" style="width: 150px" clearable @change="loadData">
          <el-option v-for="person in deliveryPersons" :key="person.id" :label="person.name" :value="person.id" />
        </el-select>
        <el-button @click="loadData">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>

      <el-table :data="tableData" stripe class="data-table" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderId" label="订单ID" width="100">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">#{{ row.orderId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" width="160">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deliveryName" label="配送员" width="120">
          <template #default="{ row }">
            <div class="delivery-info" v-if="row.deliveryName">
              <el-icon><User /></el-icon>
              <span>{{ row.deliveryName }}</span>
            </div>
            <el-tag v-else type="warning" effect="plain">
              <el-icon><Warning /></el-icon> 待分配
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deliveryPhone" label="联系电话" width="140">
          <template #default="{ row }">
            <span v-if="row.deliveryPhone">{{ row.deliveryPhone }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info" effect="plain">
              <el-icon><Clock /></el-icon> 待取货
            </el-tag>
            <el-tag v-if="row.status === 1" type="warning" effect="plain">
              <el-icon><Van /></el-icon> 配送中
            </el-tag>
            <el-tag v-if="row.status === 2" type="success" effect="plain">
              <el-icon><CircleCheck /></el-icon> 已送达
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="takeTime" label="取货时间" width="170">
          <template #default="{ row }">{{ row.takeTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="deliveryTime" label="送达时间" width="170">
          <template #default="{ row }">{{ row.deliveryTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button v-if="!row.deliveryName" type="primary" size="small" text @click="openAssignDialog(row)">
              <el-icon><UserPlus /></el-icon> 分配
            </el-button>
            <el-button v-if="row.status === 0" type="warning" size="small" text @click="handleUpdateStatus(row, 1)">
              <el-icon><Van /></el-icon> 取货
            </el-button>
            <el-button v-if="row.status === 1" type="success" size="small" text @click="handleUpdateStatus(row, 2)">
              <el-icon><CircleCheck /></el-icon> 送达
            </el-button>
            <el-tag v-if="row.status === 2" type="success" size="small">已完成</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background layout="total, prev, pager, next" v-model:current-page="pageNum"
          v-model:page-size="pageSize" :total="total" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 分配配送员对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配配送员" width="500px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="订单ID">
          <el-input v-model="assignForm.orderId" disabled />
        </el-form-item>
        <el-form-item label="选择配送员">
          <el-select v-model="assignForm.deliveryPersonId" placeholder="请选择配送员" style="width: 100%" @change="onPersonSelect">
            <el-option v-for="person in availablePersons" :key="person.id" :label="person.name" :value="person.id">
              <span>{{ person.name }}</span>
              <span class="person-status" v-if="person.status === 1"> - 配送中({{ person.orderCount }}单)</span>
              <span class="person-status" v-else> - 空闲</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="配送员">
          <el-input v-model="assignForm.deliveryName" placeholder="自动填充或手动输入" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="assignForm.deliveryPhone" placeholder="自动填充或手动输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssign">确认分配</el-button>
        <el-button type="success" @click="handleAutoAssign" v-if="assignForm.orderId">自动分配</el-button>
      </template>
    </el-dialog>

    <!-- 配送员管理对话框 -->
    <el-dialog v-model="personDialogVisible" title="配送员管理" width="600px">
      <div class="person-toolbar">
        <el-button type="primary" size="small" @click="openAddPersonDialog">
          <el-icon><Plus /></el-icon> 添加配送员
        </el-button>
      </div>
      <el-table :data="deliveryPersons" stripe size="small" class="person-table">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="success" size="small">空闲</el-tag>
            <el-tag v-if="row.status === 1" type="warning" size="small">配送中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderCount" label="当前配送" width="80" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="openEditPersonDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" text @click="handleDeletePerson(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加/编辑配送员对话框 -->
    <el-dialog v-model="personFormDialogVisible" :title="personFormTitle" width="400px">
      <el-form :model="personForm" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="personForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="电话" required>
          <el-input v-model="personForm.phone" placeholder="请输入电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="personFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePerson">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeliveryList, updateDeliveryStatus, assignDelivery, autoAssignDelivery, getDeliveryPersons, getAvailableDeliveryPersons, addDeliveryPerson, updateDeliveryPerson, deleteDeliveryPerson } from '@/api/delivery'

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const filterDeliveryPerson = ref('')
const loading = ref(false)

// 配送员相关
const deliveryPersons = ref([])
const availablePersons = ref([])
const personDialogVisible = ref(false)

// 分配对话框
const assignDialogVisible = ref(false)
const assignForm = ref({
  orderId: null,
  deliveryPersonId: null,
  deliveryName: '',
  deliveryPhone: ''
})

// 配送员表单
const personFormDialogVisible = ref(false)
const personFormTitle = ref('添加配送员')
const personForm = ref({
  id: null,
  name: '',
  phone: '',
  status: 0,
  orderCount: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (filterStatus.value !== '') {
      params.status = filterStatus.value
    }
    if (filterDeliveryPerson.value) {
      params.deliveryPersonId = filterDeliveryPerson.value
    }
    const result = await getDeliveryList(params)
    tableData.value = result.data.records
    total.value = result.data.total
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

const loadDeliveryPersons = async () => {
  try {
    const result = await getDeliveryPersons()
    deliveryPersons.value = result.data || []
  } catch (error) {
    console.error('加载配送员列表失败', error)
  }
}

const loadAvailablePersons = async () => {
  try {
    const result = await getAvailableDeliveryPersons()
    availablePersons.value = result.data || []
  } catch (error) {
    console.error('加载可用配送员失败', error)
  }
}

const openAssignDialog = async (row) => {
  await loadAvailablePersons()
  assignForm.value = {
    orderId: row.orderId,
    deliveryPersonId: null,
    deliveryName: row.deliveryName || '',
    deliveryPhone: row.deliveryPhone || ''
  }
  assignDialogVisible.value = true
}

const onPersonSelect = (personId) => {
  const person = availablePersons.value.find(p => p.id === personId)
  if (person) {
    assignForm.value.deliveryName = person.name
    assignForm.value.deliveryPhone = person.phone
  }
}

const handleAssign = async () => {
  if (!assignForm.value.deliveryPersonId && !assignForm.value.deliveryName) {
    ElMessage.warning('请选择或输入配送员信息')
    return
  }
  try {
    await assignDelivery({
      orderId: assignForm.value.orderId,
      deliveryPersonId: assignForm.value.deliveryPersonId,
      deliveryName: assignForm.value.deliveryName,
      deliveryPhone: assignForm.value.deliveryPhone
    })
    ElMessage.success('配送员分配成功')
    assignDialogVisible.value = false
    loadData()
    loadDeliveryPersons()
  } catch (error) {
    console.error('分配失败', error)
  }
}

const handleAutoAssign = async () => {
  try {
    await autoAssignDelivery(assignForm.value.orderId)
    ElMessage.success('自动分配成功')
    assignDialogVisible.value = false
    loadData()
    loadDeliveryPersons()
  } catch (error) {
    console.error('自动分配失败', error)
  }
}

const handleUpdateStatus = async (row, status) => {
  const action = status === 1 ? '取货' : '确认送达'
  try {
    await ElMessageBox.confirm(`确定要${action}吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateDeliveryStatus({ id: row.id, status })
    ElMessage.success(`${action}成功`)
    loadData()
    loadDeliveryPersons()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败', error)
    }
  }
}

// 配送员管理
const openPersonDialog = async () => {
  await loadDeliveryPersons()
  personDialogVisible.value = true
}

const openAddPersonDialog = () => {
  personForm.value = {
    id: null,
    name: '',
    phone: '',
    status: 0,
    orderCount: 0
  }
  personFormTitle.value = '添加配送员'
  personFormDialogVisible.value = true
}

const openEditPersonDialog = (row) => {
  personForm.value = {
    id: row.id,
    name: row.name,
    phone: row.phone,
    status: row.status,
    orderCount: row.orderCount
  }
  personFormTitle.value = '编辑配送员'
  personFormDialogVisible.value = true
}

const handleSavePerson = async () => {
  if (!personForm.value.name || !personForm.value.phone) {
    ElMessage.warning('请填写姓名和电话')
    return
  }
  try {
    if (personForm.value.id) {
      await updateDeliveryPerson(personForm.value)
      ElMessage.success('配送员更新成功')
    } else {
      await addDeliveryPerson(personForm.value)
      ElMessage.success('配送员添加成功')
    }
    personFormDialogVisible.value = false
    loadDeliveryPersons()
  } catch (error) {
    console.error('保存失败', error)
  }
}

const handleDeletePerson = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该配送员吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteDeliveryPerson(row.id)
    ElMessage.success('删除成功')
    loadDeliveryPersons()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

onMounted(() => {
  loadData()
  loadDeliveryPersons()
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

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.delivery-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #303133;
}

.text-muted { color: #c0c4cc; }

.order-no {
  font-family: monospace;
  color: #409EFF;
}

.person-status {
  color: #909399;
  font-size: 12px;
}

.person-toolbar {
  margin-bottom: 12px;
}

.person-table {
  max-height: 400px;
  overflow-y: auto;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
