<template>
  <div class="page-container">
    <div class="page-header">
      <h2>食材管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增食材
      </el-button>
    </div>

    <el-card class="table-card">
      <!-- 搜索工具栏 -->
      <div class="toolbar">
        <el-input v-model="searchName" placeholder="搜索食材名称" style="width: 200px" clearable @clear="loadData"
          @keyup.enter="loadData" prefix-icon="Search" />
        <el-select v-model="searchCategory" placeholder="选择分类" style="width: 150px" clearable @change="loadData">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
        <el-select v-model="sortField" placeholder="排序字段" style="width: 120px" @change="loadData">
          <el-option label="按名称" value="name" />
          <el-option label="按价格" value="price" />
          <el-option label="按库存" value="stock" />
          <el-option label="按分类" value="category" />
          <el-option label="按时间" value="createTime" />
        </el-select>
        <el-select v-model="sortOrder" placeholder="排序方式" style="width: 100px" @change="loadData">
          <el-option label="升序" value="asc" />
          <el-option label="降序" value="desc" />
        </el-select>
        <el-button type="primary" @click="loadData">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" stripe class="data-table" v-loading="loading" sort-change="handleSortChange">
        <el-table-column prop="id" label="ID" width="80" sortable />
        <el-table-column prop="name" label="食材名称" min-width="120" sortable>
          <template #default="{ row }">
            <div class="food-name">
              <img v-if="row.image" :src="row.image" class="food-thumb" />
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" sortable>
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="120" sortable>
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" sortable>
          <template #default="{ row }">
            <span :class="['stock', { 'low-stock': row.stock < 10 }]">{{ row.stock }} {{ row.unit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" size="small" text @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination background layout="total, prev, pager, next" v-model:current-page="pageNum"
          v-model:page-size="pageSize" :total="total" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" :close-on-click-modal="false">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入食材名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="如: 斤/kg/个" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload class="food-image-uploader" action="#" :http-request="handleImageUpload" :show-file-list="false"
            accept="image/*">
            <img v-if="form.image" :src="form.image" class="food-image-preview" />
            <el-icon v-else class="food-image-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">点击上传食材图片</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFoodList, addFood, updateFood, deleteFood, getFoodCategories, uploadFoodImage } from '@/api/food'

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchName = ref('')
const searchCategory = ref('')
const sortField = ref('createTime')
const sortOrder = ref('desc')
const categories = ref(['蔬菜', '肉类', '水果', '粮油', '调料', '禽蛋'])
const loading = ref(false)
const submitLoading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  category: '',
  price: 0,
  stock: 0,
  unit: '斤',
  description: '',
  image: ''
})

const formRules = {
  name: [{ required: true, message: '请输入食材名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const result = await getFoodList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      name: searchName.value,
      category: searchCategory.value,
      sortField: sortField.value,
      sortOrder: sortOrder.value
    })
    tableData.value = result.data.records
    total.value = result.data.total
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const result = await getFoodCategories()
    if (result.data && result.data.length > 0) {
      categories.value = result.data
    }
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增食材'
  Object.assign(form, { id: null, name: '', category: '', price: 0, stock: 0, unit: '斤', description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑食材'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (form.id) {
      await updateFood(form)
      ElMessage.success('更新成功')
    } else {
      await addFood(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('操作失败', error)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除食材「' + row.name + '」吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteFood(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => { })
}

const handleImageUpload = async (options) => {
  const { file } = options
  const formData = new FormData()
  formData.append('file', file)
  try {
    const result = await uploadFoodImage(formData)
    form.image = result.data.url
    ElMessage.success('图片上传成功')
  } catch (error) {
    console.error('图片上传失败', error)
    ElMessage.error('图片上传失败')
  }
}

onMounted(() => {
  loadData()
  loadCategories()
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
  flex-wrap: wrap;
}

.food-name { font-weight: 500; color: #303133; display: flex; align-items: center; gap: 8px; }
.food-thumb { width: 40px; height: 40px; border-radius: 4px; object-fit: cover; }
.price { color: #F56C6C; font-weight: 600; }
.stock { color: #67C23A; font-weight: 500; }
.low-stock { color: #E6A23C; }

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.food-image-uploader {
  width: 120px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}

.food-image-uploader:hover {
  border-color: #409EFF;
}

.food-image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.food-image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
</style>