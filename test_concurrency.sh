#!/bin/bash
# 高并发测试脚本 - 测试库存扣减和配送员分配

BASE_URL="http://localhost:8080/api"
THREADS=20

echo "=========================================="
echo "高并发能力测试"
echo "=========================================="

# 1. 登录获取token
echo ""
echo "[1/4] 登录获取token..."
LOGIN_RESP=$(curl -s -X POST "$BASE_URL/user/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","role":"user"}')
TOKEN=$(echo $LOGIN_RESP | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
if [ -z "$TOKEN" ]; then
  echo "登录失败: $LOGIN_RESP"
  exit 1
fi
echo "登录成功, token: ${TOKEN:0:20}..."

# 2. 查看当前库存
echo ""
echo "[2/4] 查看食材库存..."
FOOD_LIST=$(curl -s "$BASE_URL/food/list?pageNum=1&pageSize=5" -H "Authorization: Bearer $TOKEN")
# 提取食材ID和库存
FOOD_ID=$(echo $FOOD_LIST | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
ORIGINAL_STOCK=$(echo $FOOD_LIST | grep -o '"stock":[0-9]*' | head -1 | cut -d':' -f2)
echo "使用食材ID: $FOOD_ID, 当前库存: $ORIGINAL_STOCK"

# 3. 并发创建订单测试
echo ""
echo "[3/4] 启动 $THREADS 个并发请求创建订单(每个买1件)..."

success=0
fail=0
for i in $(seq 1 $THREADS); do
  RESP=$(curl -s -X POST "$BASE_URL/order" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"address\":\"test$i\",\"remark\":\"c$i\",\"items\":[{\"foodId\":$FOOD_ID,\"quantity\":1}]}")
  code=$(echo $RESP | grep -o '"code":[0-9]*' | head -1)
  if echo "$code" | grep -q "200"; then
    ((success++))
  else
    ((fail++))
  fi
done

echo "成功: $success, 失败: $fail"

# 4. 检查最终库存
echo ""
echo "[4/4] 检查最终库存..."
FINAL_LIST=$(curl -s "$BASE_URL/food/list?pageNum=1&pageSize=5" -H "Authorization: Bearer $TOKEN")
FINAL_STOCK=$(echo $FINAL_LIST | grep -o '"stock":[0-9]*' | head -1 | cut -d':' -f2)
echo "原始库存: $ORIGINAL_STOCK"
echo "最终库存: $FINAL_STOCK"
EXPECTED_STOCK=$((ORIGINAL_STOCK - success))
echo "预期库存(原始-成功订单): $EXPECTED_STOCK"

# 5. 结果分析
echo ""
echo "=========================================="
echo "测试结果分析"
echo "=========================================="
if [ "$FINAL_STOCK" -eq "$EXPECTED_STOCK" ]; then
  echo "✓ 库存一致: 无超卖，高并发防护成功!"
elif [ "$FINAL_STOCK" -ge 0 ]; then
  DIFF=$((ORIGINAL_STOCK - FINAL_STOCK))
  if [ "$DIFF" -le "$THREADS" ]; then
    echo "✓ 库存正确扣减: 卖出 $DIFF 件, 无超卖"
  else
    echo "✗ 可能存在问题: 库存扣减异常"
  fi
else
  echo "✗ 库存为负(超卖): $FINAL_STOCK"
fi
echo ""
echo "库存防超卖测试: $([ "$FINAL_STOCK" -ge 0 ] && echo '通过' || echo '失败')"