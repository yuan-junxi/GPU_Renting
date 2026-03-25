-- =====================================================
-- 清空所有表数据（注意外键约束顺序）
-- =====================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE bill;
TRUNCATE TABLE usage_record;
TRUNCATE TABLE recharge_record;
TRUNCATE TABLE rental_application;
TRUNCATE TABLE gpu_card;
TRUNCATE TABLE pricing_rule;
TRUNCATE TABLE gpu_server;
TRUNCATE TABLE User;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 1. 插入用户数据
-- =====================================================

INSERT INTO `User` (`user_id`, `username`, `password`, `phone`, `balance`, `role`, `status`, `create_time`) VALUES
                                                                                                                (1, 'admin', 'admin123', '13800000000', 0.00, 0, 'active', DATE_SUB(NOW(), INTERVAL 60 DAY)),
                                                                                                                (2, '张三', '123456', '13912345678', 1250.00, 1, 'active', DATE_SUB(NOW(), INTERVAL 45 DAY)),
                                                                                                                (3, '李四', '123456', '13987654321', 580.50, 1, 'active', DATE_SUB(NOW(), INTERVAL 40 DAY)),
                                                                                                                (4, '王五', '123456', '13812345678', 2100.00, 1, 'active', DATE_SUB(NOW(), INTERVAL 35 DAY)),
                                                                                                                (5, '赵六', '123456', '13712345678', 320.00, 1, 'active', DATE_SUB(NOW(), INTERVAL 30 DAY)),
                                                                                                                (6, '小明', '123456', '13612345678', 890.00, 1, 'active', DATE_SUB(NOW(), INTERVAL 25 DAY)),
                                                                                                                (7, '小红', '123456', '13512345678', 150.00, 1, 'disabled', DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                                                (8, '小刚', '123456', '13412345678', 450.00, 1, 'active', DATE_SUB(NOW(), INTERVAL 15 DAY)),
                                                                                                                (9, '小丽', '123456', '13312345678', 670.00, 1, 'active', DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                (10, '测试用户', '123456', '13212345678', 100.00, 1, 'active', DATE_SUB(NOW(), INTERVAL 5 DAY));

-- =====================================================
-- 2. 插入GPU服务器（只有在线和维护两种状态）
-- =====================================================

INSERT INTO `gpu_server` (`server_id`, `server_no`, `server_name`, `location`, `ip_address`, `status`, `create_time`) VALUES
                                                                                                                          (1, 'SVR-BJ-A-001', '北京A区-计算节点1', '北京A区', '10.0.1.11', '在线', DATE_SUB(NOW(), INTERVAL 90 DAY)),
                                                                                                                          (2, 'SVR-BJ-A-002', '北京A区-计算节点2', '北京A区', '10.0.1.12', '在线', DATE_SUB(NOW(), INTERVAL 90 DAY)),
                                                                                                                          (3, 'SVR-BJ-B-001', '北京B区-训练节点1', '北京B区', '10.0.2.11', '在线', DATE_SUB(NOW(), INTERVAL 80 DAY)),
                                                                                                                          (4, 'SVR-BJ-B-002', '北京B区-训练节点2', '北京B区', '10.0.2.12', '维护', DATE_SUB(NOW(), INTERVAL 80 DAY)),
                                                                                                                          (5, 'SVR-CQ-A-001', '重庆A区-渲染节点1', '重庆A区', '10.0.3.11', '在线', DATE_SUB(NOW(), INTERVAL 70 DAY)),
                                                                                                                          (6, 'SVR-CQ-A-002', '重庆A区-渲染节点2', '重庆A区', '10.0.3.12', '在线', DATE_SUB(NOW(), INTERVAL 70 DAY)),
                                                                                                                          (7, 'SVR-NM-B-001', '内蒙B区-推理节点1', '内蒙B区', '10.0.4.11', '在线', DATE_SUB(NOW(), INTERVAL 60 DAY)),
                                                                                                                          (8, 'SVR-NM-B-002', '内蒙B区-推理节点2', '内蒙B区', '10.0.4.12', '维护', DATE_SUB(NOW(), INTERVAL 60 DAY)),
                                                                                                                          (9, 'SVR-XB-B-001', '西北B区-大模型节点', '西北B区', '10.0.5.11', '在线', DATE_SUB(NOW(), INTERVAL 50 DAY)),
                                                                                                                          (10, 'SVR-FS-001', '佛山区-边缘节点', '佛山区', '10.0.6.11', '在线', DATE_SUB(NOW(), INTERVAL 40 DAY));

-- =====================================================
-- 3. 插入定价规则
-- =====================================================

INSERT INTO `pricing_rule` (`rule_id`, `gpu_model`, `price_hourly`, `effective_date`, `expire_date`, `create_time`) VALUES
                                                                                                                        (1, 'RTX 5090', 28.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (2, 'RTX PRO 6000', 35.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (3, 'vGPU-32GB', 12.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (4, 'vGPU-48GB', 18.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (5, 'H800', 45.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (6, 'RTX 4090D', 22.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (7, 'RTX 4090', 24.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (8, 'RTX 3090', 15.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (9, 'RTX 3080x2', 20.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (10, 'CPU-close-HT', 5.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (11, 'vGPU-48GB-350W', 22.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (12, 'RTX 3060', 8.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (13, 'GTX 1080 Ti', 6.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (14, 'CPU', 2.50, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (15, 'RTX 3080 Ti', 18.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY)),
                                                                                                                        (16, 'RTX A4000', 14.00, '2024-01-01', NULL, DATE_SUB(NOW(), INTERVAL 100 DAY));

-- =====================================================
-- 4. 插入GPU卡（状态：空闲、已租、维护）
-- =====================================================

INSERT INTO `gpu_card` (`card_id`, `server_id`, `model`, `memory_gb`, `price_hourly`, `status`, `create_time`) VALUES
-- 北京A区服务器
(1, 1, 'RTX 5090', 32, 28.00, '空闲', DATE_SUB(NOW(), INTERVAL 60 DAY)),
(2, 1, 'RTX 5090', 32, 28.00, '已租', DATE_SUB(NOW(), INTERVAL 60 DAY)),
(3, 2, 'RTX PRO 6000', 48, 35.00, '空闲', DATE_SUB(NOW(), INTERVAL 60 DAY)),
(4, 2, 'RTX PRO 6000', 48, 35.00, '维护', DATE_SUB(NOW(), INTERVAL 60 DAY)),

-- 北京B区服务器
(5, 3, 'vGPU-32GB', 32, 12.00, '已租', DATE_SUB(NOW(), INTERVAL 55 DAY)),
(6, 3, 'vGPU-48GB', 48, 18.00, '空闲', DATE_SUB(NOW(), INTERVAL 55 DAY)),
(7, 4, 'H800', 80, 45.00, '维护', DATE_SUB(NOW(), INTERVAL 55 DAY)),
(8, 4, 'H800', 80, 45.00, '维护', DATE_SUB(NOW(), INTERVAL 55 DAY)),

-- 重庆A区服务器
(9, 5, 'RTX 4090D', 24, 22.00, '空闲', DATE_SUB(NOW(), INTERVAL 50 DAY)),
(10, 5, 'RTX 4090', 24, 24.00, '已租', DATE_SUB(NOW(), INTERVAL 50 DAY)),
(11, 6, 'RTX 3090', 24, 15.00, '空闲', DATE_SUB(NOW(), INTERVAL 50 DAY)),
(12, 6, 'RTX 3080x2', 20, 20.00, '空闲', DATE_SUB(NOW(), INTERVAL 50 DAY)),

-- 内蒙B区服务器
(13, 7, 'CPU-close-HT', 64, 5.00, '已租', DATE_SUB(NOW(), INTERVAL 45 DAY)),
(14, 7, 'vGPU-48GB-350W', 48, 22.00, '空闲', DATE_SUB(NOW(), INTERVAL 45 DAY)),
(15, 8, 'RTX 3060', 12, 8.00, '维护', DATE_SUB(NOW(), INTERVAL 45 DAY)),
(16, 8, 'GTX 1080 Ti', 11, 6.00, '维护', DATE_SUB(NOW(), INTERVAL 45 DAY)),

-- 西北B区服务器
(17, 9, 'CPU', 32, 2.50, '已租', DATE_SUB(NOW(), INTERVAL 40 DAY)),
(18, 9, 'RTX 3080 Ti', 12, 18.00, '空闲', DATE_SUB(NOW(), INTERVAL 40 DAY)),
(19, 9, 'RTX A4000', 16, 14.00, '空闲', DATE_SUB(NOW(), INTERVAL 40 DAY)),
(20, 9, 'RTX 4090', 24, 24.00, '空闲', DATE_SUB(NOW(), INTERVAL 40 DAY)),

-- 佛山区服务器
(21, 10, 'RTX 3060', 12, 8.00, '空闲', DATE_SUB(NOW(), INTERVAL 35 DAY)),
(22, 10, 'RTX 3060', 12, 8.00, '空闲', DATE_SUB(NOW(), INTERVAL 35 DAY)),
(23, 10, 'vGPU-32GB', 32, 12.00, '空闲', DATE_SUB(NOW(), INTERVAL 35 DAY)),
(24, 10, 'RTX 5090', 32, 28.00, '空闲', DATE_SUB(NOW(), INTERVAL 35 DAY));

-- =====================================================
-- 5. 插入租用记录（进行中和已结束）
-- =====================================================

-- 进行中的租用
INSERT INTO `rental_application` (`rental_id`, `user_id`, `card_id`, `start_time`, `end_time`, `total_hours`, `total_cost`, `status`, `create_time`) VALUES
                                                                                                                                                         (1, 2, 2, DATE_SUB(NOW(), INTERVAL 3 HOUR), NULL, NULL, NULL, 'ongoing', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
                                                                                                                                                         (2, 3, 5, DATE_SUB(NOW(), INTERVAL 8 HOUR), NULL, NULL, NULL, 'ongoing', DATE_SUB(NOW(), INTERVAL 8 HOUR)),
                                                                                                                                                         (3, 4, 10, DATE_SUB(NOW(), INTERVAL 24 HOUR), NULL, NULL, NULL, 'ongoing', DATE_SUB(NOW(), INTERVAL 24 HOUR)),
                                                                                                                                                         (4, 6, 13, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NULL, NULL, 'ongoing', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
                                                                                                                                                         (5, 8, 17, DATE_SUB(NOW(), INTERVAL 12 HOUR), NULL, NULL, NULL, 'ongoing', DATE_SUB(NOW(), INTERVAL 12 HOUR)),

-- 已结束的租用
                                                                                                                                                         (6, 2, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 48.00, 1344.00, 'ended', DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                                                         (7, 3, 6, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 48.00, 864.00, 'ended', DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                                                                                         (8, 4, 11, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 48.00, 720.00, 'ended', DATE_SUB(NOW(), INTERVAL 5 DAY)),
                                                                                                                                                         (9, 5, 14, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 48.00, 1056.00, 'ended', DATE_SUB(NOW(), INTERVAL 4 DAY)),
                                                                                                                                                         (10, 9, 18, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 48.00, 864.00, 'ended', DATE_SUB(NOW(), INTERVAL 3 DAY));

-- =====================================================
-- 6. 插入使用记录（扣费记录）
-- =====================================================

-- 为进行中的租用创建使用记录（模拟自动扣费）
INSERT INTO `usage_record` (`record_id`, `rental_id`, `user_id`, `card_id`, `hours`, `amount`, `balance_before`, `balance_after`, `create_time`) VALUES
-- 租用1（张三，RTX 5090，3小时）
(1, 1, 2, 2, 1.00, 28.00, 1250.00, 1222.00, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 1, 2, 2, 1.00, 28.00, 1222.00, 1194.00, DATE_SUB(NOW(), INTERVAL 1 HOUR)),

-- 租用2（李四，vGPU-32GB，8小时）
(3, 2, 3, 5, 1.00, 12.00, 580.50, 568.50, DATE_SUB(NOW(), INTERVAL 7 HOUR)),
(4, 2, 3, 5, 1.00, 12.00, 568.50, 556.50, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(5, 2, 3, 5, 1.00, 12.00, 556.50, 544.50, DATE_SUB(NOW(), INTERVAL 5 HOUR)),

-- 租用3（王五，RTX 4090，24小时）
(6, 3, 4, 10, 1.00, 24.00, 2100.00, 2076.00, DATE_SUB(NOW(), INTERVAL 23 HOUR)),
(7, 3, 4, 10, 1.00, 24.00, 2076.00, 2052.00, DATE_SUB(NOW(), INTERVAL 22 HOUR)),

-- 租用4（小明，CPU-close-HT，2小时）
(8, 4, 6, 13, 1.00, 5.00, 890.00, 885.00, DATE_SUB(NOW(), INTERVAL 1 HOUR)),

-- 租用5（小刚，CPU，12小时）
(9, 5, 8, 17, 1.00, 2.50, 450.00, 447.50, DATE_SUB(NOW(), INTERVAL 11 HOUR)),
(10, 5, 8, 17, 1.00, 2.50, 447.50, 445.00, DATE_SUB(NOW(), INTERVAL 10 HOUR)),

-- 已结束租用的使用记录
(11, 6, 2, 1, 48.00, 1344.00, 2000.00, 656.00, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(12, 7, 3, 6, 48.00, 864.00, 1500.00, 636.00, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(13, 8, 4, 11, 48.00, 720.00, 2100.00, 1380.00, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(14, 9, 5, 14, 48.00, 1056.00, 1200.00, 144.00, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(15, 10, 9, 18, 48.00, 864.00, 800.00, -64.00, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- =====================================================
-- 7. 插入充值记录
-- =====================================================

INSERT INTO `recharge_record` (`recharge_id`, `user_id`, `amount`, `balance_before`, `balance_after`, `payment_method`, `status`, `create_time`) VALUES
                                                                                                                                                     (1, 2, 500.00, 100.00, 600.00, '微信支付', 'success', DATE_SUB(NOW(), INTERVAL 40 DAY)),
                                                                                                                                                     (2, 2, 300.00, 600.00, 900.00, '支付宝', 'success', DATE_SUB(NOW(), INTERVAL 30 DAY)),
                                                                                                                                                     (3, 2, 350.00, 900.00, 1250.00, '银行卡', 'success', DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                                                                                     (4, 3, 200.00, 80.50, 280.50, '微信支付', 'success', DATE_SUB(NOW(), INTERVAL 35 DAY)),
                                                                                                                                                     (5, 3, 300.00, 280.50, 580.50, '支付宝', 'success', DATE_SUB(NOW(), INTERVAL 25 DAY)),
                                                                                                                                                     (6, 4, 1000.00, 100.00, 1100.00, '银行卡', 'success', DATE_SUB(NOW(), INTERVAL 45 DAY)),
                                                                                                                                                     (7, 4, 1000.00, 1100.00, 2100.00, '微信支付', 'success', DATE_SUB(NOW(), INTERVAL 30 DAY)),
                                                                                                                                                     (8, 5, 500.00, 50.00, 550.00, '支付宝', 'success', DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                                                                                     (9, 5, 200.00, 550.00, 750.00, '微信支付', 'success', DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                                                     (10, 6, 300.00, 100.00, 400.00, '银行卡', 'success', DATE_SUB(NOW(), INTERVAL 15 DAY)),
                                                                                                                                                     (11, 6, 500.00, 400.00, 900.00, '支付宝', 'success', DATE_SUB(NOW(), INTERVAL 5 DAY)),
                                                                                                                                                     (12, 8, 200.00, 50.00, 250.00, '微信支付', 'success', DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                                                     (13, 8, 200.00, 250.00, 450.00, '支付宝', 'success', DATE_SUB(NOW(), INTERVAL 3 DAY)),
                                                                                                                                                     (14, 9, 500.00, 100.00, 600.00, '银行卡', 'success', DATE_SUB(NOW(), INTERVAL 15 DAY)),
                                                                                                                                                     (15, 9, 200.00, 600.00, 800.00, '微信支付', 'success', DATE_SUB(NOW(), INTERVAL 7 DAY));

-- =====================================================
-- 8. 插入账单（没有逾期状态）
-- =====================================================

-- 租用账单（对应已结束的租用，全部已支付）
INSERT INTO `bill` (`bill_id`, `user_id`, `rental_id`, `record_id`, `amount`, `bill_type`, `status`, `create_time`, `pay_time`) VALUES
                                                                                                                                    (1, 2, 6, 11, 1344.00, '租用', '已支付', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
                                                                                                                                    (2, 3, 7, 12, 864.00, '租用', '已支付', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
                                                                                                                                    (3, 4, 8, 13, 720.00, '租用', '已支付', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
                                                                                                                                    (4, 5, 9, 14, 1056.00, '租用', '已支付', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
                                                                                                                                    (5, 9, 10, 15, 864.00, '租用', '已支付', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 充值账单
                                                                                                                                    (6, 2, NULL, NULL, 500.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY)),
                                                                                                                                    (7, 2, NULL, NULL, 300.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
                                                                                                                                    (8, 2, NULL, NULL, 350.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                                                                    (9, 3, NULL, NULL, 200.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY)),
                                                                                                                                    (10, 3, NULL, NULL, 300.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
                                                                                                                                    (11, 4, NULL, NULL, 1000.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 45 DAY)),
                                                                                                                                    (12, 4, NULL, NULL, 1000.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
                                                                                                                                    (13, 5, NULL, NULL, 500.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                                                                    (14, 5, NULL, NULL, 200.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                                    (15, 6, NULL, NULL, 300.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
                                                                                                                                    (16, 6, NULL, NULL, 500.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
                                                                                                                                    (17, 8, NULL, NULL, 200.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                                                                    (18, 8, NULL, NULL, 200.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
                                                                                                                                    (19, 9, NULL, NULL, 500.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
                                                                                                                                    (20, 9, NULL, NULL, 200.00, '充值', '已支付', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),

-- 进行中租用的临时账单（待支付）
                                                                                                                                    (21, 2, 1, NULL, 56.00, '租用', '待支付', NOW(), NULL),
                                                                                                                                    (22, 3, 2, NULL, 36.00, '租用', '待支付', NOW(), NULL),
                                                                                                                                    (23, 4, 3, NULL, 48.00, '租用', '待支付', NOW(), NULL),
                                                                                                                                    (24, 6, 4, NULL, 5.00, '租用', '待支付', NOW(), NULL),
                                                                                                                                    (25, 8, 5, NULL, 5.00, '租用', '待支付', NOW(), NULL);

-- =====================================================
-- 9. 修复用户余额（确保余额与实际扣费一致）
-- =====================================================

-- 更新用户9余额（小丽，充值800 - 租用864 = -64，设置为0并标记为欠费状态）
UPDATE User SET balance = 0.00, status = 'disabled' WHERE user_id = 9;

-- =====================================================
-- 10. 验证数据
-- =====================================================

SELECT 'User' as table_name, COUNT(*) as count FROM User
UNION ALL
SELECT 'gpu_server', COUNT(*) FROM gpu_server
UNION ALL
SELECT 'gpu_card', COUNT(*) FROM gpu_card
UNION ALL
SELECT 'pricing_rule', COUNT(*) FROM pricing_rule
UNION ALL
SELECT 'rental_application', COUNT(*) FROM rental_application
UNION ALL
SELECT 'usage_record', COUNT(*) FROM usage_record
UNION ALL
SELECT 'recharge_record', COUNT(*) FROM recharge_record
UNION ALL
SELECT 'bill', COUNT(*) FROM bill;

-- 查看各状态统计
SELECT '服务器状态' as type, status, COUNT(*) FROM gpu_server GROUP BY status
UNION ALL
SELECT '显卡状态', status, COUNT(*) FROM gpu_card GROUP BY status
UNION ALL
SELECT '租用状态', CASE WHEN status = 'ongoing' THEN '进行中' WHEN status = 'ended' THEN '已结束' END, COUNT(*) FROM rental_application GROUP BY status
UNION ALL
SELECT '账单状态', status, COUNT(*) FROM bill GROUP BY status;