/*
 Navicat MySQL Data Transfer

 Source Server         : cfq
 Source Server Type    : MySQL
 Source Server Version : 80035
 Source Host           : localhost:3306
 Source Schema         : xingmao_food

 Target Server Type    : MySQL
 Target Server Version : 80035
 File Encoding         : 65001

 Date: 03/06/2026 21:49:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for delivery
-- ----------------------------
DROP TABLE IF EXISTS `delivery`;
CREATE TABLE `delivery`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `delivery_person_id` bigint(0) NULL DEFAULT NULL COMMENT '配送员ID',
  `delivery_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配送员姓名',
  `delivery_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配送员电话',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态: 0-待取货, 1-配送中, 2-已送达',
  `take_time` datetime(0) NULL DEFAULT NULL COMMENT '取货时间',
  `delivery_time` datetime(0) NULL DEFAULT NULL COMMENT '送达时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of delivery
-- ----------------------------
INSERT INTO `delivery` VALUES (1, 2, 1, '配送员小李', '13900139001', 1, '2026-06-03 17:57:24', NULL, '2026-06-03 17:57:07', '2026-06-03 17:57:07');
INSERT INTO `delivery` VALUES (2, 1, 2, '配送员小王', '13900139002', 1, '2026-06-03 20:17:53', NULL, '2026-06-03 20:17:29', '2026-06-03 20:17:29');
INSERT INTO `delivery` VALUES (3, 5, 3, '配送员小张', '13900139003', 1, '2026-06-03 21:49:26', NULL, '2026-06-03 21:49:18', '2026-06-03 21:49:18');

-- ----------------------------
-- Table structure for delivery_person
-- ----------------------------
DROP TABLE IF EXISTS `delivery_person`;
CREATE TABLE `delivery_person`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '配送员ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '电话',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态: 0-空闲, 1-配送中',
  `order_count` int(0) NULL DEFAULT 0 COMMENT '当前配送订单数',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配送员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of delivery_person
-- ----------------------------
INSERT INTO `delivery_person` VALUES (1, '配送员小李', '13900139001', 1, 1, 0, '2026-06-03 17:39:04', '2026-06-03 17:39:04');
INSERT INTO `delivery_person` VALUES (2, '配送员小王', '13900139002', 1, 1, 0, '2026-06-03 17:39:04', '2026-06-03 17:39:04');
INSERT INTO `delivery_person` VALUES (3, '配送员小张', '13900139003', 1, 1, 0, '2026-06-03 17:39:04', '2026-06-03 17:39:04');
INSERT INTO `delivery_person` VALUES (4, '配送员小赵', '13900139004', 0, 0, 0, '2026-06-03 17:39:04', '2026-06-03 17:39:04');
INSERT INTO `delivery_person` VALUES (5, '配送员小孙', '13900139005', 1, 2, 0, '2026-06-03 17:39:04', '2026-06-03 17:39:04');
INSERT INTO `delivery_person` VALUES (6, '配送员小钱', '13900139006', 0, 0, 0, '2026-06-03 17:39:04', '2026-06-03 17:39:04');
INSERT INTO `delivery_person` VALUES (7, '配送员小周', '13900139007', 1, 1, 0, '2026-06-03 17:39:04', '2026-06-03 17:39:04');
INSERT INTO `delivery_person` VALUES (8, '配送员小吴', '13900139008', 0, 0, 0, '2026-06-03 17:39:04', '2026-06-03 17:39:04');

-- ----------------------------
-- Table structure for food
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '食材ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '食材名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类: 蔬菜/肉类/水果/粮油/调料',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '斤' COMMENT '单位: 斤/kg/个/箱',
  `stock` int(0) NULL DEFAULT 0 COMMENT '库存数量',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '食材表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food
-- ----------------------------
INSERT INTO `food` VALUES (1, '新鲜猪肉', '肉类', 15.00, '斤', 84, NULL, '本地新鲜猪肉', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (2, '牛肉', '肉类', 38.00, '斤', 28, NULL, '进口牛肉', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (3, '鸡肉', '肉类', 12.00, '斤', 78, NULL, '农家土鸡', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (4, '土鸡蛋', '禽蛋', 12.00, '斤', 151, NULL, '正宗土鸡蛋', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (5, '鸭蛋', '禽蛋', 10.00, '斤', 93, NULL, '新鲜鸭蛋', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (6, '有机大米', '粮油', 5.00, '斤', 491, NULL, '有机认证大米', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (7, '花生油', '粮油', 18.00, '升', 198, NULL, '压榨花生油', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (8, '西红柿', '蔬菜', 3.50, '斤', 296, NULL, '新鲜西红柿', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (9, '西兰花', '蔬菜', 4.00, '斤', 146, NULL, '绿色有机西兰花', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (10, '白菜', '蔬菜', 2.00, '斤', 196, NULL, '新鲜大白菜', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (11, '土豆', '蔬菜', 2.50, '斤', 230, NULL, '黄心土豆', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (12, '胡萝卜', '蔬菜', 3.00, '斤', 142, NULL, '新鲜胡萝卜', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (13, '苹果', '水果', 6.00, '斤', 199, NULL, '红富士苹果', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (14, '香蕉', '水果', 4.50, '斤', 176, NULL, '进口香蕉', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (15, '橙子', '水果', 5.00, '斤', 145, NULL, '新鲜脐橙', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (16, '葡萄', '水果', 12.00, '斤', 78, NULL, '巨峰葡萄', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (17, '西瓜', '水果', 2.00, '斤', 293, NULL, '当季西瓜', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (18, '食盐', '调料', 2.00, '袋', 439, NULL, '加碘食盐', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (19, '酱油', '调料', 8.00, '瓶', 198, NULL, '酿造酱油', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `food` VALUES (20, '醋', '调料', 6.00, '瓶', 148, NULL, '陈醋', 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `food_id` bigint(0) NOT NULL COMMENT '食材ID',
  `food_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '食材名称(冗余)',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int(0) NOT NULL COMMENT '数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 4, '土鸡蛋', 12.00, 32, 384.00, '2026-06-03 00:12:58');
INSERT INTO `order_item` VALUES (2, 1, 11, '土豆', 2.50, 4, 10.00, '2026-06-03 00:12:58');
INSERT INTO `order_item` VALUES (3, 2, 1, '新鲜猪肉', 15.00, 3, 45.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (4, 2, 2, '牛肉', 38.00, 4, 152.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (5, 2, 3, '鸡肉', 12.00, 2, 24.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (6, 2, 4, '土鸡蛋', 12.00, 8, 96.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (7, 2, 7, '花生油', 18.00, 1, 18.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (8, 2, 8, '西红柿', 3.50, 1, 3.50, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (9, 2, 11, '土豆', 2.50, 6, 15.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (10, 2, 14, '香蕉', 4.50, 1, 4.50, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (11, 2, 15, '橙子', 5.00, 1, 5.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (12, 2, 18, '食盐', 2.00, 3, 6.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (13, 2, 19, '酱油', 8.00, 1, 8.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (14, 2, 20, '醋', 6.00, 1, 6.00, '2026-06-03 17:12:38');
INSERT INTO `order_item` VALUES (15, 3, 1, '新鲜猪肉', 15.00, 12, 180.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (16, 3, 2, '牛肉', 38.00, 16, 608.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (17, 3, 4, '土鸡蛋', 12.00, 9, 108.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (18, 3, 5, '鸭蛋', 10.00, 2, 20.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (19, 3, 6, '有机大米', 5.00, 5, 25.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (20, 3, 7, '花生油', 18.00, 1, 18.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (21, 3, 8, '西红柿', 3.50, 1, 3.50, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (22, 3, 9, '西兰花', 4.00, 4, 16.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (23, 3, 10, '白菜', 2.00, 4, 8.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (24, 3, 11, '土豆', 2.50, 5, 12.50, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (25, 3, 12, '胡萝卜', 3.00, 8, 24.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (26, 3, 13, '苹果', 6.00, 1, 6.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (27, 3, 14, '香蕉', 4.50, 1, 4.50, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (28, 3, 15, '橙子', 5.00, 1, 5.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (29, 3, 16, '葡萄', 12.00, 1, 12.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (30, 3, 17, '西瓜', 2.00, 3, 6.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (31, 3, 18, '食盐', 2.00, 4, 8.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (32, 3, 19, '酱油', 8.00, 1, 8.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (33, 3, 20, '醋', 6.00, 1, 6.00, '2026-06-03 20:16:43');
INSERT INTO `order_item` VALUES (34, 4, 5, '鸭蛋', 10.00, 1, 10.00, '2026-06-03 21:48:32');
INSERT INTO `order_item` VALUES (35, 4, 6, '有机大米', 5.00, 4, 20.00, '2026-06-03 21:48:32');
INSERT INTO `order_item` VALUES (36, 4, 11, '土豆', 2.50, 1, 2.50, '2026-06-03 21:48:32');
INSERT INTO `order_item` VALUES (37, 5, 2, '牛肉', 38.00, 2, 76.00, '2026-06-03 21:49:00');
INSERT INTO `order_item` VALUES (38, 5, 5, '鸭蛋', 10.00, 4, 40.00, '2026-06-03 21:49:00');
INSERT INTO `order_item` VALUES (39, 5, 8, '西红柿', 3.50, 2, 7.00, '2026-06-03 21:49:00');
INSERT INTO `order_item` VALUES (40, 5, 11, '土豆', 2.50, 4, 10.00, '2026-06-03 21:49:00');
INSERT INTO `order_item` VALUES (41, 5, 14, '香蕉', 4.50, 2, 9.00, '2026-06-03 21:49:00');
INSERT INTO `order_item` VALUES (42, 5, 15, '橙子', 5.00, 3, 15.00, '2026-06-03 21:49:00');
INSERT INTO `order_item` VALUES (43, 5, 16, '葡萄', 12.00, 1, 12.00, '2026-06-03 21:49:00');
INSERT INTO `order_item` VALUES (44, 5, 17, '西瓜', 2.00, 4, 8.00, '2026-06-03 21:49:00');
INSERT INTO `order_item` VALUES (45, 5, 18, '食盐', 2.00, 54, 108.00, '2026-06-03 21:49:00');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `total_price` decimal(10, 2) NOT NULL COMMENT '总金额',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态: 0-待处理, 1-配送中, 2-已完成,\r\n  3-已取消, 4-已送达待确认',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配送地址',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'XM202606030012587502', 3, 394.00, 1, '江西师范大学', '', 0, '2026-06-03 00:12:58', '2026-06-03 00:12:58');
INSERT INTO `orders` VALUES (2, 'XM202606031712373687', 3, 383.00, 1, '文清路小学', '', 0, '2026-06-03 17:12:37', '2026-06-03 17:12:37');
INSERT INTO `orders` VALUES (3, 'XM202606032016430836', 3, 1078.50, 0, '江西师范大学\n', '', 0, '2026-06-03 20:16:43', '2026-06-03 20:16:43');
INSERT INTO `orders` VALUES (4, 'XM202606032148328817', 3, 32.50, 0, '测试', '', 0, '2026-06-03 21:48:32', '2026-06-03 21:48:32');
INSERT INTO `orders` VALUES (5, 'XM202606032149000912', 4, 285.00, 1, 'ceshi1', '', 0, '2026-06-03 21:49:00', '2026-06-03 21:49:00');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '角色: admin/user/employee',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$LZ5LLjK1rS70UYaPAFndVu6CpfEKBD.kOZPvrNHSW/H0tXLcLyYFW', '系统管理员', '13800138000', NULL, 'admin', NULL, 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `sys_user` VALUES (2, 'employee1', '$2a$10$LZ5LLjK1rS70UYaPAFndVu6CpfEKBD.kOZPvrNHSW/H0tXLcLyYFW', '员工张三', '13800138001', NULL, 'employee', NULL, 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `sys_user` VALUES (3, 'test', '$2a$10$LZ5LLjK1rS70UYaPAFndVu6CpfEKBD.kOZPvrNHSW/H0tXLcLyYFW', '测试用户', '13800138002', NULL, 'user', NULL, 0, '2026-06-03 00:01:32', '2026-06-03 00:01:32');
INSERT INTO `sys_user` VALUES (4, 'test1', '$2a$10$aU/NLra6LIIWA/1P3qaZ8uk7p0nxZ/cL7vmENNZoaDpvBMROgoOMO', '弦子', '', NULL, 'user', NULL, 0, '2026-06-03 21:48:02', '2026-06-03 21:48:02');

SET FOREIGN_KEY_CHECKS = 1;
