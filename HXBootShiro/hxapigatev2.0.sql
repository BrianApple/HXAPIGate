/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1-mysql
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : 127.0.0.1:3306
 Source Schema         : hxapigate

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 21/08/2022 11:46:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for auth_account_log
-- ----------------------------
DROP TABLE IF EXISTS `auth_account_log`;
CREATE TABLE `auth_account_log` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '用户账户操作日志主键',
  `LOG_NAME` varchar(255) DEFAULT NULL COMMENT '日志名称(login,register,logout)',
  `USER_ID` varchar(32) DEFAULT NULL COMMENT '用户id',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `SUCCEED` int(3) DEFAULT NULL COMMENT '是否执行成功(0失败1成功)',
  `MESSAGE` varchar(255) DEFAULT NULL COMMENT '具体消息',
  `IP` varchar(255) DEFAULT NULL COMMENT '登录ip',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COMMENT='登录注册登出记录';

-- ----------------------------
-- Records of auth_account_log
-- ----------------------------
BEGIN;
INSERT INTO `auth_account_log` VALUES (1, '用户登录日志', 'admin', '2022-08-10 15:06:53', 1, '登录成功', NULL);
INSERT INTO `auth_account_log` VALUES (40, '用户登录日志', 'admin', '2022-08-20 23:32:17', 1, '登录成功', NULL);
INSERT INTO `auth_account_log` VALUES (41, '用户登录日志', 'admin', '2022-08-20 23:46:46', 1, '登录成功', NULL);
INSERT INTO `auth_account_log` VALUES (42, '用户登录日志', 'admin', '2022-08-20 23:47:04', 1, '登录成功', NULL);
INSERT INTO `auth_account_log` VALUES (43, '用户登录日志', 'admin', '2022-08-21 00:00:22', 1, '登录成功', NULL);
INSERT INTO `auth_account_log` VALUES (44, '用户登录日志', 'admin', '2022-08-21 00:21:02', 1, '登录成功', '0:0:0:0:0:0:0:1,localhost');
INSERT INTO `auth_account_log` VALUES (45, '用户登录日志', 'admin', '2022-08-21 11:25:41', 1, '登录成功', '0:0:0:0:0:0:0:1,localhost');
INSERT INTO `auth_account_log` VALUES (46, '用户登录日志', 'admin', '2022-08-21 11:29:23', 1, '登录成功', NULL);
INSERT INTO `auth_account_log` VALUES (47, '用户登录日志', 'admin', '2022-08-21 11:38:38', 1, '登录成功', '0:0:0:0:0:0:0:1,localhost');
COMMIT;

-- ----------------------------
-- Table structure for auth_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `auth_operation_log`;
CREATE TABLE `auth_operation_log` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '用户操作日志主键',
  `LOG_NAME` varchar(255) DEFAULT NULL COMMENT '日志名称',
  `USER_ID` varchar(32) DEFAULT NULL COMMENT '用户id',
  `API` varchar(255) DEFAULT NULL COMMENT 'api名称',
  `METHOD` varchar(255) DEFAULT NULL COMMENT '方法名称',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `SUCCEED` int(1) DEFAULT NULL COMMENT '是否执行成功(0失败1成功)',
  `MESSAGE` varchar(255) DEFAULT NULL COMMENT '具体消息备注',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1425 DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

-- ----------------------------
-- Records of auth_operation_log
-- ----------------------------
BEGIN;
INSERT INTO `auth_operation_log` VALUES (1262, '业务操作日志', 'admin', '/inner/api/islogin', 'POST', '2022-08-20 23:59:02', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1263, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-20 23:59:03', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1264, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-20 23:59:03', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1265, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-20 23:59:03', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1266, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-20 23:59:03', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1267, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-20 23:59:03', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1268, '业务操作日志', 'admin', '/inner/role/initRole', 'POST', '2022-08-20 23:59:04', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1269, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-20 23:59:05', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1270, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-20 23:59:05', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1271, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-20 23:59:05', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1272, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-20 23:59:09', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1273, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-20 23:59:09', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1274, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-20 23:59:33', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1275, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-20 23:59:33', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1276, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-20 23:59:41', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1277, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-20 23:59:41', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1278, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-20 23:59:43', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1279, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-20 23:59:43', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1280, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-20 23:59:51', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1281, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-20 23:59:51', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1282, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-20 23:59:53', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1283, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-20 23:59:54', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1284, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-20 23:59:58', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1285, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-20 23:59:58', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1286, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:00:02', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1287, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 00:00:02', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1288, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 00:00:06', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1289, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:06', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1290, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:00:08', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1291, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 00:00:08', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1292, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 00:00:12', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1293, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:12', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1294, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:00:14', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1295, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 00:00:14', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1296, '业务操作日志', 'admin', '/inner/api/islogin', 'POST', '2022-08-21 00:00:25', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1297, '业务操作日志', 'admin', '/inner/role/initRole', 'POST', '2022-08-21 00:00:25', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1298, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:00:25', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1299, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 00:00:25', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1300, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:00:25', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1301, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:25', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1302, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 00:00:25', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1303, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:00:30', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1304, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 00:00:30', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1305, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 00:00:34', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1306, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:34', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1307, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:00:36', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1308, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 00:00:36', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1309, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 00:00:40', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1310, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:40', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1311, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:41', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1312, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:00:43', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1313, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 00:00:43', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1314, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 00:00:47', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1315, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:48', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1316, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:51', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1317, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:00:53', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1318, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 00:00:53', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1319, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 00:00:57', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1320, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:57', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1321, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:00:59', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1322, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:01:01', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1323, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 00:01:01', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1324, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 00:01:05', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1325, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:01:05', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1326, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:01:15', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1327, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 00:01:15', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1328, '业务操作日志', 'admin', '/inner/api/islogin', 'POST', '2022-08-21 00:07:32', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1329, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:07:33', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1330, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 00:07:33', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1331, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:07:33', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1332, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:07:33', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1333, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 00:07:33', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1334, '业务操作日志', 'admin', '/inner/role/initRole', 'POST', '2022-08-21 00:07:33', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1335, '业务操作日志', 'admin', '/inner/api/islogin', 'POST', '2022-08-21 00:22:26', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1336, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 00:22:26', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1337, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:22:26', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1338, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 00:22:26', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1339, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 00:22:26', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1340, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 00:22:26', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1341, '业务操作日志', 'admin', '/inner/role/initRole', 'POST', '2022-08-21 00:22:27', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1342, '业务操作日志', 'admin', '/inner/api/islogin', 'POST', '2022-08-21 11:29:20', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1343, '业务操作日志', 'admin', '/inner/api/islogin', 'POST', '2022-08-21 11:29:26', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1344, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:29:31', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1345, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 11:29:31', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1346, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 11:29:31', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1347, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:29:35', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1348, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:29:49', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1349, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:29:49', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1350, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:29:55', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1351, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:29:55', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1352, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:30:01', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1353, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:30:03', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1354, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:30:03', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1355, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:30:11', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1356, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:30:15', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1357, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:30:15', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1358, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:30:17', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1359, '业务操作日志', 'admin', '/inner/api/islogin', 'POST', '2022-08-21 11:30:21', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1360, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:30:21', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1361, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:30:21', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1362, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 11:30:21', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1363, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:30:21', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1364, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 11:30:21', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1365, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:30:23', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1366, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:30:26', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1367, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:30:26', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1368, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:30:28', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1369, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:31:07', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1370, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:31:07', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1371, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:31:09', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1372, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:31:09', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1373, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:31:12', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1374, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:31:13', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1375, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:31:14', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1376, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:31:14', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1377, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:31:18', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1378, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:31:18', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1379, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:31:21', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1380, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:31:21', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1381, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:31:24', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1382, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:31:24', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1383, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:31:25', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1384, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:31:25', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1385, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:31:27', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1386, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:31:27', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1387, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:31:28', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1388, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:31:28', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1389, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:31:32', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1390, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 11:31:32', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1391, '业务操作日志', 'admin', '/inner/api/initApiList', 'POST', '2022-08-21 11:31:32', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1392, '业务操作日志', 'admin', '/inner/role/initRole', 'POST', '2022-08-21 11:31:34', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1393, '业务操作日志', 'admin', '/inner/role/getUserListByRoleId', 'POST', '2022-08-21 11:31:37', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1394, '业务操作日志', 'admin', '/inner/role/getRestApiExtendByRoleId', 'POST', '2022-08-21 11:31:39', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1395, '业务操作日志', 'admin', '/inner/role/getUserListByRoleId', 'POST', '2022-08-21 11:31:41', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1396, '业务操作日志', 'admin', '/inner/role/getUserListExtendByRoleId', 'POST', '2022-08-21 11:31:42', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1397, '业务操作日志', 'admin', '/inner/role/getUserListExtendByRoleId', 'POST', '2022-08-21 11:31:46', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1398, '业务操作日志', 'admin', '/inner/role/getRestApiExtendByRoleId', 'POST', '2022-08-21 11:31:50', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1399, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:31:51', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1400, '业务操作日志', 'admin', '/inner/role/getRestApiByRoleId', 'POST', '2022-08-21 11:31:51', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1401, '业务操作日志', 'admin', '/inner/api/initApiByItemIdAndRID', 'POST', '2022-08-21 11:31:54', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1402, '业务操作日志', 'admin', '/inner/api/initApiByItemIdAndRID', 'POST', '2022-08-21 11:31:57', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1403, '业务操作日志', 'admin', '/inner/api/initApiByItemIdAndRID', 'POST', '2022-08-21 11:32:02', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1404, '业务操作日志', 'admin', '/inner/api/initApiByItemIdAndRID', 'POST', '2022-08-21 11:32:06', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1405, '业务操作日志', 'admin', '/inner/api/initApiByItemIdAndRID', 'POST', '2022-08-21 11:32:08', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1406, '业务操作日志', 'admin', '/inner/api/initApiByItemIdAndRID', 'POST', '2022-08-21 11:32:10', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1407, '业务操作日志', 'admin', '/inner/role/getRestApiExtendByRoleId', 'POST', '2022-08-21 11:32:15', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1408, '业务操作日志', 'admin', '/inner/role/getRestApiByRoleId', 'POST', '2022-08-21 11:32:16', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1409, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:32:16', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1410, '业务操作日志', 'admin', '/inner/api/initApiByItemIdAndRID', 'POST', '2022-08-21 11:32:20', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1411, '业务操作日志', 'admin', '/inner/api/initApiByItemIdAndRID', 'POST', '2022-08-21 11:32:21', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1412, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:36:30', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1413, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:36:34', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1414, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:36:34', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1415, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:36:38', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1416, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:36:38', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1417, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:37:13', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1418, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:37:13', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1419, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:37:24', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1420, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:37:24', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1421, '业务操作日志', 'admin', '/inner/api/initApiType', 'POST', '2022-08-21 11:37:52', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1422, '业务操作日志', 'admin', '/inner/api/initApiByApiId', 'POST', '2022-08-21 11:37:52', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1423, '业务操作日志', 'admin', '/inner/api/updateApiResource', 'POST', '2022-08-21 11:38:01', 1, NULL);
INSERT INTO `auth_operation_log` VALUES (1424, '业务操作日志', 'admin', '/inner/api/initApiByItemId', 'POST', '2022-08-21 11:38:01', 1, NULL);
COMMIT;

-- ----------------------------
-- Table structure for auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_resource`;
CREATE TABLE `auth_resource` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `CODE` varchar(32) DEFAULT NULL COMMENT '资源名称',
  `NAME` varchar(128) DEFAULT NULL COMMENT '资源描述',
  `PARENT_ID` int(32) DEFAULT NULL COMMENT '父资源编码->菜单',
  `URI` varchar(128) DEFAULT NULL COMMENT '访问地址URL',
  `VERSION` varchar(20) DEFAULT NULL COMMENT '资源版本信息',
  `TYPE` int(3) DEFAULT NULL COMMENT '类型 0:内部资源（不走API网关）, 1:菜单 ,  2:资源element(rest-api) 3:资源分类',
  `METHOD` varchar(8) DEFAULT NULL COMMENT '访问方式 GET POST PUT DELETE PATCH',
  `NEED_AUTH` int(3) DEFAULT NULL COMMENT '网关是否鉴权',
  `ROUTE_INFO` text COMMENT '路由信息（json串）',
  `ICON` varchar(128) DEFAULT NULL COMMENT '图标',
  `STATUS` int(1) DEFAULT NULL COMMENT '状态   1:正常、9：禁用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=231 DEFAULT CHARSET=utf8mb4 COMMENT='资源信息表(菜单,资源)';

-- ----------------------------
-- Records of auth_resource
-- ----------------------------
BEGIN;
INSERT INTO `auth_resource` VALUES (103, 'GROUP_ACCOUNT', 'HXBS系列', 110, '', 'v1.0.0', 3, 'POST', 0, '{}', NULL, 1, '2020-04-02 15:10:03', '2022-08-21 03:31:25');
INSERT INTO `auth_resource` VALUES (110, 'CATEGORY_GROUP', '默认资源类型(API类别请放入此集合)', -1, NULL, 'v1.0.0', 3, NULL, NULL, NULL, NULL, 1, '2018-04-07 14:27:58', '2018-04-07 14:27:58');
INSERT INTO `auth_resource` VALUES (120, 'USER_LIST', '用户-获取用户列表', 103, '/user/list/*/*', 'v1.0.0', 2, 'GET', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2018-04-12 03:08:30', '2022-08-20 15:59:41');
INSERT INTO `auth_resource` VALUES (121, 'USER_AUTHORITY_ROLE', '用户-给用户授权添加角色', 103, '/user/authority/role', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2018-04-12 03:15:56', '2022-08-20 15:59:51');
INSERT INTO `auth_resource` VALUES (122, 'USER_AUTHORITY_ROLE', '用户-删除已授权用户角色', 103, '/user/authority/role', 'v1.0.0', 2, 'DELETE', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2018-04-12 03:29:03', '2022-08-20 15:59:58');
INSERT INTO `auth_resource` VALUES (167, 'PASSWORD_UPDATE', '用户-密码修改', 103, '/user/accountupdate', 'v1.0.0', 2, 'PUT', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2020-04-02 15:10:03', '2022-08-20 16:00:06');
INSERT INTO `auth_resource` VALUES (171, NULL, '用户-登录', 103, '/account/login', 'v1.0.0', 2, 'POST', 0, '{\"all_tps\":\"19\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"0\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"2\"}', NULL, 1, '2021-01-05 21:31:48', '2022-08-21 03:38:01');
INSERT INTO `auth_resource` VALUES (219, NULL, '网关内部API', 110, NULL, NULL, 3, NULL, 0, '{}', NULL, 1, '2022-08-18 07:05:24', '2022-08-21 03:31:28');
INSERT INTO `auth_resource` VALUES (220, NULL, '账户登录', 219, '/inner/user/**', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"10\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"\",\"rout_order1\":\"\",\"rout_port1\":\"\",\"rout_tps1\":\"\",\"rout_weight1\":\"\"}', NULL, 1, '2022-08-18 07:07:36', '2022-08-21 03:31:07');
INSERT INTO `auth_resource` VALUES (221, NULL, 'API资源管理', 219, '/inner/api/**', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"10\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"\",\"rout_order1\":\"\",\"rout_port1\":\"\",\"rout_tps1\":\"\",\"rout_weight1\":\"\"}', NULL, 1, '2022-08-18 07:08:39', '2022-08-21 03:31:12');
INSERT INTO `auth_resource` VALUES (222, NULL, '角色/资源管理', 219, '/inner/role/**', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"10\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"\",\"rout_order1\":\"\",\"rout_port1\":\"\",\"rout_tps1\":\"\",\"rout_weight1\":\"\"}', NULL, 1, '2022-08-18 07:09:18', '2022-08-21 03:31:18');
INSERT INTO `auth_resource` VALUES (226, NULL, '用户-注册', 103, '/account/register', 'v1.0.0', 2, 'POST', 0, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"0\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-19 09:32:03', '2022-08-20 16:00:34');
INSERT INTO `auth_resource` VALUES (227, NULL, '用户-获取当前用户角色', 103, '/user/role/*', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-19 09:59:34', '2022-08-20 16:00:40');
INSERT INTO `auth_resource` VALUES (228, NULL, '用户-退出登录', 103, '/user/exit', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-19 10:04:55', '2022-08-20 16:00:47');
INSERT INTO `auth_resource` VALUES (229, NULL, '日志-登录日志', 103, '/log/accountLog/**', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-20 14:47:51', '2022-08-20 16:00:57');
INSERT INTO `auth_resource` VALUES (230, NULL, '日志-用户操作日志查询', 103, '/log/operationLog/**', 'v1.0.0', 2, 'POST', 1, '{\"all_tps\":\"20\",\"api_version\":\"v1.0.0\",\"api_version_balance\":\"1\",\"balance\":\"1\",\"isAuth\":\"1\",\"pType\":\"http\",\"rout_ipAddr1\":\"127.0.0.1\",\"rout_order1\":\"1\",\"rout_port1\":\"18080\",\"rout_tps1\":\"10\",\"rout_weight1\":\"1\"}', NULL, 1, '2022-08-20 14:48:56', '2022-08-20 16:01:05');
COMMIT;

-- ----------------------------
-- Table structure for auth_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_role`;
CREATE TABLE `auth_role` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `CODE` varchar(32) NOT NULL COMMENT '角色编码',
  `NAME` varchar(32) DEFAULT NULL COMMENT '角色名称',
  `STATUS` int(1) DEFAULT NULL COMMENT '状态   1:正常、9：禁用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ----------------------------
-- Records of auth_role
-- ----------------------------
BEGIN;
INSERT INTO `auth_role` VALUES (100, 'role_admin', '管理员角色', 1, NULL, NULL);
INSERT INTO `auth_role` VALUES (102, 'role_user', '用户角色', 1, NULL, NULL);
INSERT INTO `auth_role` VALUES (103, 'role_guest', '访客角色（只有查询权限）', 1, NULL, NULL);
INSERT INTO `auth_role` VALUES (104, 'role_anon', '非角色（不需要任何权限就可访问）', 1, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for auth_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_role_resource`;
CREATE TABLE `auth_role_resource` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ROLE_ID` int(32) NOT NULL COMMENT '角色ID',
  `RESOURCE_ID` int(32) NOT NULL COMMENT '资源ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=286 DEFAULT CHARSET=utf8mb4 COMMENT='资源角色关联表';

-- ----------------------------
-- Records of auth_role_resource
-- ----------------------------
BEGIN;
INSERT INTO `auth_role_resource` VALUES (266, 105, 101, '2022-08-17 22:57:29', '2022-08-17 22:57:29');
INSERT INTO `auth_role_resource` VALUES (267, 100, 101, '2022-08-17 23:07:06', '2022-08-17 23:07:06');
INSERT INTO `auth_role_resource` VALUES (280, 100, 220, '2022-08-18 16:58:09', '2022-08-18 16:58:09');
INSERT INTO `auth_role_resource` VALUES (281, 100, 221, '2022-08-18 16:58:14', '2022-08-18 16:58:14');
INSERT INTO `auth_role_resource` VALUES (282, 100, 222, '2022-08-18 18:55:33', '2022-08-18 18:55:33');
INSERT INTO `auth_role_resource` VALUES (283, 102, 125, '2022-08-19 11:22:24', '2022-08-19 11:22:24');
INSERT INTO `auth_role_resource` VALUES (284, 102, 124, '2022-08-19 11:32:05', '2022-08-19 11:32:05');
INSERT INTO `auth_role_resource` VALUES (285, 102, 119, '2022-08-19 12:33:42', '2022-08-19 12:33:42');
COMMIT;

-- ----------------------------
-- Table structure for auth_user
-- ----------------------------
DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE `auth_user` (
  `UID` varchar(32) NOT NULL COMMENT 'uid,用户账号,主键',
  `USERNAME` varchar(32) NOT NULL COMMENT '用户名(nick_name)',
  `PASSWORD` varchar(64) NOT NULL COMMENT '密码(MD5(密码+盐))',
  `SALT` varchar(32) DEFAULT NULL COMMENT '盐',
  `REAL_NAME` varchar(32) DEFAULT NULL COMMENT '用户真名',
  `AVATAR` varchar(128) DEFAULT NULL COMMENT '头像',
  `PHONE` varchar(32) DEFAULT NULL COMMENT '电话号码(唯一)',
  `EMAIL` varchar(64) DEFAULT NULL COMMENT '邮件地址(唯一)',
  `SEX` int(1) DEFAULT NULL COMMENT '性别(1.男 2.女)',
  `STATUS` int(3) DEFAULT NULL COMMENT '账户状态(1.正常 2.锁定 3.删除 4.非法)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `CREATE_WHERE` int(3) DEFAULT NULL COMMENT '创建来源(1.web 2.android 3.ios 4.win 5.macos 6.ubuntu)',
  PRIMARY KEY (`UID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- Records of auth_user
-- ----------------------------
BEGIN;
INSERT INTO `auth_user` VALUES ('admin', '超级管理员', '7ABAF42833062862D42D20E3A2CAE439', 'gfkqj9', '超级管理员', NULL, '', NULL, NULL, NULL, '2022-08-10 14:56:01', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_role`;
CREATE TABLE `auth_user_role` (
  `ID` int(32) NOT NULL AUTO_INCREMENT COMMENT '用户角色关联表主键',
  `USER_ID` varchar(32) NOT NULL COMMENT '用户UID',
  `ROLE_ID` int(32) NOT NULL COMMENT '角色ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- Records of auth_user_role
-- ----------------------------
BEGIN;
INSERT INTO `auth_user_role` VALUES (39, 'admin', 100, '2019-10-26 15:20:21', '2019-10-26 15:20:21');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
