<template>
  <div class="page-container">
    <div class="page-header">
      <h2>创建订单</h2>
    </div>

    <div class="order-content">
      <!-- 食材选择区 -->
      <el-card class="food-card">
        <template #header>
          <div class="card-header">
            <span>选择食材</span>
            <el-input v-model="searchName" placeholder="搜索食材" style="width: 200px" clearable @change="loadFoods" prefix-icon="Search" />
          </div>
        </template>
        <div class="food-list" v-loading="loadingFoods">
          <div v-for="food in foodOptions" :key="food.id" class="food-item">
            <img v-if="food.image" :src="food.image" class="food-img" />
            <div v-else class="food-img food-img-placeholder"><el-icon><Food /></el-icon></div>
            <div class="food-info">
              <div class="food-name">{{ food.name }}</div>
              <div class="food-meta">
                <span class="price">¥{{ food.price }}</span>
                <span>{{ food.stock }}{{ food.unit }}可售</span>
              </div>
            </div>
            <div class="food-action">
              <el-input-number v-model="cart[food.id]" :min="0" :max="food.stock" size="small" @change="updateCart(food.id)" />
              <el-button type="primary" size="small" :disabled="!cart[food.id]" @click="addToCart(food)">
                <el-icon><Plus /></el-icon> 添加
              </el-button>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 购物车 -->
      <el-card class="cart-card">
        <template #header>
          <div class="card-header">
            <span>购物车</span>
            <el-button text type="primary" @click="clearCart">清空</el-button>
          </div>
        </template>
        <div v-if="cartItems.length === 0" class="empty-cart">
          <el-icon size="48"><ShoppingCart /></el-icon>
          <p>购物车为空</p>
        </div>
        <div v-else class="cart-list">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <img v-if="item.image" :src="item.image" class="cart-img" />
            <div class="cart-info">
              <div class="cart-name">{{ item.name }}</div>
              <div class="cart-price">¥{{ item.price }} × {{ item.quantity }} = <span class="subtotal">¥{{ item.subtotal }}</span></div>
            </div>
            <el-button type="danger" size="small" text @click="removeFromCart(item.id)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
          <el-divider />
          <div class="cart-total">
            <span>总计：</span>
            <span class="total-price">¥{{ totalPrice }}</span>
          </div>
        </div>
      </el-card>

      <!-- 订单信息 -->
      <el-card class="order-form-card">
        <template #header>
          <span>订单信息</span>
        </template>
        <el-form :model="orderForm" :rules="formRules" ref="formRef" label-width="100px">
          <el-form-item label="配送地址" prop="address">
            <div class="address-select-wrapper">
              <el-select v-model="selectedAddressId" placeholder="选择已保存的地址" clearable style="width: 100%; margin-bottom: 8px" @change="handleAddressChange">
                <el-option v-for="addr in addressList" :key="addr.id" :label="formatAddressLabel(addr)" :value="addr.id" />
              </el-select>
              <el-input v-model="orderForm.address" placeholder="或输入新的详细配送地址" type="textarea" :rows="2" @blur="handleAddressInput" />
            </div>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="orderForm.remark" placeholder="特殊要求或备注信息" type="textarea" :rows="2" />
          </el-form-item>
        </el-form>
        <div class="order-submit">
          <div class="submit-info">
            <span>共 {{ totalCount }} 件商品</span>
            <span class="submit-total">合计：<strong>¥{{ totalPrice }}</strong></span>
          </div>
          <el-button type="primary" size="large" :disabled="cartItems.length === 0" :loading="submitLoading" @click="handleSubmit">
            提交订单
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFoodList } from '@/api/food'
import { createOrder } from '@/api/order'
import { getMyAddresses } from '@/api/address'

const searchName = ref('')
const foodOptions = ref([])
const loadingFoods = ref(false)

const addressList = ref([])
const selectedAddressId = ref(null)

const cart = ref({})
const orderForm = reactive({
  address: '',
  remark: ''
})

const formRef = ref(null)
const submitLoading = ref(false)

const formRules = {
  address: [{ required: true, message: '请输入配送地址', trigger: 'blur' }]
}

const formatAddressLabel = (addr) => {
  return `${addr.receiverName} ${addr.phone} - ${addr.province || ''}${addr.city || ''}${addr.district || ''}${addr.detailAddress}`
}

const handleAddressChange = (addrId) => {
  if (addrId) {
    const addr = addressList.value.find(a => a.id === addrId)
    if (addr) {
      orderForm.address = `${addr.province || ''}${addr.city || ''}${addr.district || ''}${addr.detailAddress}`
    }
  }
}

const handleAddressInput = () => {
  // 用户手动输入地址时，清空选择
  selectedAddressId.value = null
}

const loadAddresses = async () => {
  try {
    const result = await getMyAddresses()
    addressList.value = result.data || []
    // 自动填充默认地址
    const defaultAddr = addressList.value.find(a => a.isDefault === 1)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
      orderForm.address = `${defaultAddr.province || ''}${defaultAddr.city || ''}${defaultAddr.district || ''}${defaultAddr.detailAddress}`
    }
  } catch (error) {
    console.error('加载地址失败', error)
  }
}

const cartItems = computed(() => {
  return Object.entries(cart.value)
    .filter(([_, qty]) => qty > 0)
    .map(([foodId, quantity]) => {
      const food = foodOptions.value.find(f => f.id === Number(foodId))
      if (!food) return null
      return {
        id: food.id,
        name: food.name,
        price: food.price,
        image: food.image,
        quantity,
        subtotal: (food.price * quantity).toFixed(2)
      }
    })
    .filter(Boolean)
})

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + Number(item.subtotal), 0).toFixed(2)
})

const totalCount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const loadFoods = async () => {
  loadingFoods.value = true
  try {
    const result = await getFoodList({ pageNum: 1, pageSize: 100, name: searchName.value })
    foodOptions.value = result.data.records || []
  } catch (error) {
    console.error('加载食材失败', error)
  } finally {
    loadingFoods.value = false
  }
}

const updateCart = (foodId) => {
  // Force reactivity update
}

const addToCart = (food) => {
  ElMessage.success(`已将「${food.name}」加入购物车`)
}

const clearCart = () => {
  cart.value = {}
}

const removeFromCart = (foodId) => {
  cart.value[foodId] = 0
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (cartItems.value.length === 0) {
    ElMessage.warning('请先选择食材')
    return
  }

  submitLoading.value = true
  try {
    const items = cartItems.value.map(item => ({
      foodId: item.id,
      quantity: item.quantity
    }))
    const result = await createOrder({
      address: orderForm.address,
      remark: orderForm.remark,
      items
    })
    ElMessage.success('订单创建成功！')
    // Reset
    clearCart()
    orderForm.address = ''
    orderForm.remark = ''
  } catch (error) {
    console.error('创建订单失败', error)
    ElMessage.error('创建订单失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadFoods()
  loadAddresses()
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

.order-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  grid-template-rows: auto auto;
  gap: 20px;
}

.food-card { grid-row: 1 / 2; }
.cart-card { grid-row: 1 / 3; align-self: start; }
.order-form-card { grid-row: 2 / 3; }

.address-select-wrapper { width: 100%; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.food-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  max-height: 400px;
  overflow-y: auto;
  padding: 8px;
}

.food-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  transition: box-shadow 0.3s;
}

.food-item:hover {
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.food-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}

.food-img-placeholder {
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

.food-info {
  flex: 1;
  min-width: 0;
}

.food-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.food-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 8px;
}

.food-meta .price {
  color: #F56C6C;
  font-weight: 600;
}

.food-action {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.empty-cart {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.empty-cart p { margin-top: 12px; }

.cart-list {
  max-height: 300px;
  overflow-y: auto;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.cart-img {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
}

.cart-info { flex: 1; }
.cart-name { font-weight: 500; font-size: 14px; }
.cart-price { font-size: 12px; color: #909399; margin-top: 4px; }
.subtotal { color: #F56C6C; font-weight: 600; }

.cart-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  font-size: 16px;
}

.total-price {
  font-size: 20px;
  color: #F56C6C;
  font-weight: 700;
}

.order-submit {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  margin-top: 16px;
}

.submit-info {
  display: flex;
  gap: 16px;
  align-items: center;
}

.submit-total {
  font-size: 18px;
  color: #F56C6C;
}

.submit-total strong {
  font-size: 24px;
}
</style>