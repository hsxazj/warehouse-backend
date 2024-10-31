/*
 Navicat Premium Dump SQL

 Source Server         : docker-mysql8
 Source Server Type    : MySQL
 Source Server Version : 80020 (8.0.20)
 Source Host           : 1.117.62.42:3307
 Source Schema         : warehouse-management-cloud

 Target Server Type    : MySQL
 Target Server Version : 80020 (8.0.20)
 File Encoding         : 65001

 Date: 29/10/2024 16:26:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for material
-- ----------------------------
DROP TABLE IF EXISTS `material`;
CREATE TABLE `material`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `specification` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明',
  `unit` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '单位',
  `stock` bigint NOT NULL DEFAULT 0 COMMENT '库存',
  `remark` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `material_name_unique`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8815 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `admin_id` bigint NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0 COMMENT '0为未审核 1为已审批 2为拒绝',
  `submit_time` datetime NOT NULL,
  `confirm_time` datetime NULL DEFAULT NULL,
  `in_out` int NOT NULL DEFAULT 0 COMMENT '0为入库，1为出库',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 133 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for order_material
-- ----------------------------
DROP TABLE IF EXISTS `order_material`;
CREATE TABLE `order_material`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `material_id` bigint NOT NULL,
  `quantity` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 111 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `permission_pk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (12, 'log:operation', '操作日志相关操作');
INSERT INTO `permission` VALUES (13, 'log:api', 'api日志相关操作');
INSERT INTO `permission` VALUES (15, 'material:check_stock', '查看物料库存');
INSERT INTO `permission` VALUES (16, 'material:type:get', '获取物料品类列表');
INSERT INTO `permission` VALUES (17, 'material:add', '添加物料品类');
INSERT INTO `permission` VALUES (18, 'material:update', '修改物料');
INSERT INTO `permission` VALUES (19, 'material:delete', '删除物料');
INSERT INTO `permission` VALUES (20, 'material:report:get', '获取年度报表');
INSERT INTO `permission` VALUES (21, 'order:submit', '提交订单');
INSERT INTO `permission` VALUES (22, 'order:audit', '审核订单');
INSERT INTO `permission` VALUES (23, 'permission:manage', '权限管理');
INSERT INTO `permission` VALUES (24, 'role:manage', '角色管理');
INSERT INTO `permission` VALUES (25, 'user:manage', '用户管理');
INSERT INTO `permission` VALUES (26, 'statistic:permission', '统计与打印');
INSERT INTO `permission` VALUES (27, 'log:error:send', '发送错误日志');
INSERT INTO `permission` VALUES (28, 'report:bug', 'bug反馈');
INSERT INTO `permission` VALUES (29, 'report:suggestion', '优化建议反馈');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `description` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_pk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'super_admin', '超级管理员');
INSERT INTO `role` VALUES (18, 'user', '普通用户');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL,
  `permission_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 165 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (52, 1, 12);
INSERT INTO `role_permission` VALUES (53, 1, 13);
INSERT INTO `role_permission` VALUES (54, 1, 15);
INSERT INTO `role_permission` VALUES (55, 1, 16);
INSERT INTO `role_permission` VALUES (56, 1, 17);
INSERT INTO `role_permission` VALUES (57, 1, 18);
INSERT INTO `role_permission` VALUES (58, 1, 19);
INSERT INTO `role_permission` VALUES (59, 1, 20);
INSERT INTO `role_permission` VALUES (60, 1, 21);
INSERT INTO `role_permission` VALUES (61, 1, 22);
INSERT INTO `role_permission` VALUES (62, 1, 23);
INSERT INTO `role_permission` VALUES (63, 1, 24);
INSERT INTO `role_permission` VALUES (64, 1, 25);
INSERT INTO `role_permission` VALUES (68, 1, 26);
INSERT INTO `role_permission` VALUES (111, 18, 15);
INSERT INTO `role_permission` VALUES (122, 18, 21);
INSERT INTO `role_permission` VALUES (123, 18, 16);
INSERT INTO `role_permission` VALUES (124, 18, 21);
INSERT INTO `role_permission` VALUES (125, 18, 16);
INSERT INTO `role_permission` VALUES (126, 18, 21);
INSERT INTO `role_permission` VALUES (127, 18, 16);
INSERT INTO `role_permission` VALUES (128, 18, 21);
INSERT INTO `role_permission` VALUES (144, 1, 27);
INSERT INTO `role_permission` VALUES (147, 18, 28);
INSERT INTO `role_permission` VALUES (148, 18, 29);
INSERT INTO `role_permission` VALUES (149, 1, 28);
INSERT INTO `role_permission` VALUES (150, 1, 29);
INSERT INTO `role_permission` VALUES (164, 18, 24);

-- ----------------------------
-- Table structure for system_version
-- ----------------------------
DROP TABLE IF EXISTS `system_version`;
CREATE TABLE `system_version`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `version` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '版本',
  `file_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统版本号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_version
-- ----------------------------
INSERT INTO `system_version` VALUES (1, '1.0.4', 'warehouseNewVersion_1.0.4.exe');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `phone_number` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电话',
  `password` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `identity` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '身份证号',
  `real_name` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `gender` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '性别：0为女性，1为男性',
  `address` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '地址',
  `birth` date NOT NULL COMMENT '出生日期',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识：0为未删除1为删除',
  `is_root` tinyint NOT NULL DEFAULT 0 COMMENT '是否为超管，0为不是，1为是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `admin_phone_number_uindex`(`phone_number` ASC) USING BTREE,
  UNIQUE INDEX `identity`(`identity` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '11111111111', '$2a$10$UA/r0AVUW7FJEUEjGHF6wOZtX/1oviW3XtmAMU3ANDG7NwdguYHIm', '350322004022874136', '张小明', '男', '福建省福州市', '2005-04-12', '2024-09-19 11:20:04', '2024-10-22 14:12:31', 0, 1);
INSERT INTO `user` VALUES (31, '2222222222', '$2a$10$r2ASqAvbH0JVcdyeShGt6.u/0MlOw3OlOK9nlAMhcY33GYFzIRs/K', '371324198404020015', '胡伟', '男', '陕西省宝鸡市', '2009-06-22', '2024-10-22 13:59:16', '2024-10-22 13:59:16', 0, 0);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1, 1);
INSERT INTO `user_role` VALUES (14, 31, 18);

SET FOREIGN_KEY_CHECKS = 1;
