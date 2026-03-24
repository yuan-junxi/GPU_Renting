package main.service;

import main.dao.*;
import main.model.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataService {
    private static DataService instance;

    // DAO 实例
    private UserDao userDao;
    private GpuServerDao serverDao;
    private GpuCardDao cardDao;
    private PricingRuleDao pricingRuleDao;
    private RentalApplicationDao rentalDao;
    private UsageRecordDao usageRecordDao;
    private BillDao billDao;
    private RechargeRecordDao rechargeDao;

    private DataService() {
        // 初始化 DAO
        userDao = new UserDao();
        serverDao = new GpuServerDao();
        cardDao = new GpuCardDao();
        pricingRuleDao = new PricingRuleDao();
        rentalDao = new RentalApplicationDao();
        usageRecordDao = new UsageRecordDao();
        billDao = new BillDao();
        rechargeDao = new RechargeRecordDao();
    }

    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    // ========== 用户相关方法 ==========
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public User getUserById(Integer userId) {
        return userDao.findById(userId);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public boolean updateUserStatus(Integer userId, String status) {
        boolean result = userDao.updateStatus(userId, status);
        return result;
    }

    // ========== 显卡相关方法 ==========
    public List<GpuCard> getAllGpuCards() {
        // 每次都从数据库获取最新数据
        return cardDao.findAll();
    }

    public List<GpuCard> getAvailableGpuCards() {
        return cardDao.findAvailable();
    }

    public List<GpuCard> getGpuCardsByModel(String model) {
        return cardDao.findByModel(model);
    }

    public boolean addGpuCard(GpuCard card) {
        int result = cardDao.insert(card);
        return result > 0;
    }

    public boolean updateGpuCard(GpuCard card) {
        int result = cardDao.update(card);
        return result > 0;
    }

    public boolean deleteGpuCard(Integer cardId) {
        int result = cardDao.delete(cardId);
        return result > 0;
    }

    // DataService.java
    public boolean updateGpuCardStatus(Integer cardId, String status) {
        // 转换状态值（中文转英文）
        String dbStatus = status;
        if ("空闲".equals(status)) {
            dbStatus = "idle";
        } else if ("已租".equals(status)) {
            dbStatus = "rented";
        } else if ("维护".equals(status)) {
            dbStatus = "maintenance";
        }

        // 直接传递英文状态给 DAO
        int result = cardDao.updateStatusDirect(cardId, dbStatus);
        return result > 0;
    }

    // ========== 服务器相关方法 ==========
    public List<GpuServer> getAllServers() {
        return serverDao.findAll();
    }

    public boolean addServer(GpuServer server) {
        int result = serverDao.insert(server);
        return result > 0;
    }

    public boolean updateServer(GpuServer server) {
        int result = serverDao.update(server);
        return result > 0;
    }

    public boolean deleteServer(Integer serverId) {
        // 检查服务器下是否有显卡
        List<GpuCard> cards = cardDao.findAll();
        boolean hasCards = cards.stream().anyMatch(c -> c.getServerId().equals(serverId));
        if (hasCards) {
            return false;
        }
        int result = serverDao.delete(serverId);
        return result > 0;
    }

    // ========== 计费规则相关方法 ==========
    public List<PricingRule> getAllPricingRules() {
        return pricingRuleDao.findAll();
    }

    public boolean addPricingRule(PricingRule rule) {
        // 添加规则
        int result = pricingRuleDao.insert(rule);
        return result > 0;
    }

    public boolean updatePricingRule(PricingRule rule) {
        // 获取旧规则（用于比较价格是否变化）
        PricingRule oldRule = pricingRuleDao.findById(rule.getRuleId());

        if (oldRule == null) {
            return false;
        }

        // 更新规则
        int result = pricingRuleDao.update(rule);

        if (result > 0) {
            // 如果价格发生了变化，同步更新该型号所有显卡的价格
            if (oldRule.getPriceHourly().compareTo(rule.getPriceHourly()) != 0) {
                updateGpuCardPricesByModel(rule.getGpuModel(), rule.getPriceHourly());
            }
            return true;
        }
        return false;
    }

    /**
     * 更新指定型号所有显卡的价格
     */
    private void updateGpuCardPricesByModel(String model, BigDecimal newPrice) {
        List<GpuCard> cards = cardDao.findAll();
        List<GpuCard> targetCards = cards.stream()
                .filter(c -> c.getModel().equals(model))
                .collect(java.util.stream.Collectors.toList());

        for (GpuCard card : targetCards) {
            card.setPriceHourly(newPrice);
            cardDao.update(card);
        }

        if (!targetCards.isEmpty()) {
            System.out.println("已更新 " + targetCards.size() + " 张 " + model + " 显卡的价格为 ¥" + newPrice);
        }
    }

    public boolean deletePricingRule(Integer ruleId) {
        PricingRule rule = pricingRuleDao.findById(ruleId);
        if (rule == null) {
            return false;
        }

        // 检查是否存在该型号的显卡
        List<GpuCard> cards = cardDao.findAll();
        boolean hasCards = cards.stream()
                .anyMatch(c -> c.getModel().equals(rule.getGpuModel()));

        if (hasCards) {
            System.out.println("无法删除规则：存在型号为 " + rule.getGpuModel() + " 的显卡");
            return false;
        }

        int result = pricingRuleDao.delete(ruleId);
        return result > 0;
    }

    public BigDecimal getPriceByModel(String model) {
        return pricingRuleDao.findPriceByModel(model);
    }

    // ========== 租赁相关方法 ==========
    public List<RentalApplication> getAllRentals() {
        return rentalDao.findAll();
    }

    public List<RentalApplication> getRentalsByUserId(Integer userId) {
        return rentalDao.findByUserId(userId);
    }

    public List<RentalApplication> getActiveRentals() {
        return rentalDao.findActive();
    }

    public boolean startRental(RentalApplication rental) {
        // 检查用户余额
        User user = getUserById(rental.getUserId());
        if (user == null || user.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("开始租赁失败：用户余额不足或用户不存在");
            return false;
        }

        // 检查显卡是否可用 - 使用中文判断
        GpuCard card = cardDao.findById(rental.getCardId());
        if (card == null || !"空闲".equals(card.getStatus())) {
            System.out.println("开始租赁失败：显卡不可用，当前状态=" + (card != null ? card.getStatus() : "null"));
            return false;
        }

        // 检查显卡是否已被租用
        if (rentalDao.isCardRented(rental.getCardId())) {
            System.out.println("开始租赁失败：显卡已被租用");
            return false;
        }

        // 设置开始时间和状态 - 使用中文
        rental.setStartTime(LocalDateTime.now());
        rental.setStatus("进行中");  // 改为中文
        rental.setCreateTime(LocalDateTime.now());

        // 插入租用记录
        int result = rentalDao.insert(rental);

        if (result > 0) {
            // 更新显卡状态 - 使用中文
            cardDao.updateStatus(rental.getCardId(), "已租");  // 改为中文
            System.out.println("开始租赁成功：用户=" + rental.getUserId() + ", 显卡=" + rental.getCardId());
            return true;
        }
        System.out.println("开始租赁失败：插入租用记录失败");
        return false;
    }

    public boolean endRental(Integer rentalId) {
        System.out.println("=== 开始结束租赁 ===");
        System.out.println("租赁ID: " + rentalId);

        RentalApplication rental = rentalDao.findById(rentalId);
        if (rental == null) {
            System.out.println("错误：未找到租赁记录，ID=" + rentalId);
            return false;
        }

        System.out.println("找到租赁记录:");
        System.out.println("  状态: " + rental.getStatus());
        System.out.println("  用户ID: " + rental.getUserId());
        System.out.println("  显卡ID: " + rental.getCardId());
        System.out.println("  开始时间: " + rental.getStartTime());
        System.out.println("  创建时间: " + rental.getCreateTime());

        // 检查开始时间，如果为null则使用创建时间（仅在内存中处理，不更新数据库）
        LocalDateTime actualStartTime = rental.getStartTime();
        if (actualStartTime == null) {
            System.out.println("警告：开始时间为空，使用创建时间作为开始时间");
            actualStartTime = rental.getCreateTime();

            // 如果创建时间也为空，则使用当前时间减去1小时作为默认值
            if (actualStartTime == null) {
                System.out.println("警告：创建时间也为空，使用当前时间减去1小时作为开始时间");
                actualStartTime = LocalDateTime.now().minusHours(1);
            }
        }

        // 使用中文状态判断
        if (!"进行中".equals(rental.getStatus())) {
            System.out.println("错误：租赁状态不是进行中，当前状态=" + rental.getStatus());
            return false;
        }

        // 计算使用时长和费用
        LocalDateTime endTime = LocalDateTime.now();
        double hours = java.time.Duration.between(actualStartTime, endTime).toMinutes() / 60.0;
        BigDecimal totalHours = new BigDecimal(String.format("%.2f", hours));
        BigDecimal totalCost = totalHours.multiply(rental.getPriceHourly());

        System.out.println("计算费用:");
        System.out.println("  开始时间: " + actualStartTime);
        System.out.println("  结束时间: " + endTime);
        System.out.println("  使用时长: " + totalHours + " 小时");
        System.out.println("  总费用: " + totalCost);

        // 更新租用记录
        int result = rentalDao.endRental(rentalId, endTime, totalHours, totalCost);
        System.out.println("更新租用记录结果: " + result);

        if (result > 0) {
            // 更新显卡状态（使用中文）
            int updateCardResult = cardDao.updateStatus(rental.getCardId(), "空闲");
            System.out.println("更新显卡状态结果: " + updateCardResult);

            // 创建使用记录和账单
            User user = userDao.findById(rental.getUserId());
            if (user != null && totalCost.compareTo(BigDecimal.ZERO) > 0) {
                // 创建使用记录
                UsageRecord usageRecord = new UsageRecord();
                usageRecord.setRentalId(rentalId);
                usageRecord.setUserId(rental.getUserId());
                usageRecord.setCardId(rental.getCardId());
                usageRecord.setHours(totalHours);
                usageRecord.setAmount(totalCost);
                usageRecord.setBalanceBefore(user.getBalance());
                usageRecord.setBalanceAfter(user.getBalance().subtract(totalCost));
                int usageResult = usageRecordDao.insert(usageRecord);
                System.out.println("创建使用记录结果: " + usageResult);

                // 创建账单
                Bill bill = new Bill();
                bill.setUserId(rental.getUserId());
                bill.setRentalId(rentalId);
                bill.setAmount(totalCost);
                bill.setBillType("rent");
                bill.setStatus("paid");
                bill.setPayTime(LocalDateTime.now());
                int billResult = billDao.insert(bill);
                System.out.println("创建账单结果: " + billResult);

                // 更新用户余额
                boolean updateBalanceResult = userDao.updateBalance(rental.getUserId(), user.getBalance().subtract(totalCost));
                System.out.println("更新用户余额结果: " + updateBalanceResult);
            }

            System.out.println("=== 结束租赁成功 ===");
            return true;
        }

        System.out.println("=== 结束租赁失败 ===");
        return false;
    }

    // ========== 使用记录相关方法 ==========
    public List<UsageRecord> getUsageRecordsByUserId(Integer userId) {
        return usageRecordDao.findByUserId(userId);
    }

    public List<UsageRecord> getAllUsageRecords() {
        return usageRecordDao.findAll();
    }

    // ========== 账单相关方法 ==========
    public List<Bill> getBillsByUserId(Integer userId) {
        return billDao.findByUserId(userId);
    }

    public List<Bill> getAllBills() {
        return billDao.findAll();
    }

    public BigDecimal getTotalIncome() {
        return billDao.getTotalRentalIncome();
    }

    public Map<String, BigDecimal> getIncomeByPeriod(LocalDateTime start, LocalDateTime end) {
        return billDao.getIncomeByPeriod(start, end);
    }

    // ========== 充值记录相关方法 ==========
    public List<RechargeRecord> getRechargesByUserId(Integer userId) {
        return rechargeDao.findByUserId(userId);
    }

    public boolean recharge(Integer userId, BigDecimal amount, String paymentMethod) {
        User user = getUserById(userId);
        if (user == null) {
            return false;
        }

        BigDecimal balanceBefore = user.getBalance();
        BigDecimal balanceAfter = user.getBalance().add(amount);

        // 创建充值记录
        RechargeRecord record = new RechargeRecord();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setPaymentMethod(paymentMethod);
        record.setStatus("success");
        record.setCreateTime(LocalDateTime.now());
        record.setUsername(user.getUsername());

        // 创建账单
        Bill bill = new Bill();
        bill.setUserId(userId);
        bill.setAmount(amount);
        bill.setBillType("recharge");
        bill.setStatus("paid");
        bill.setCreateTime(LocalDateTime.now());
        bill.setPayTime(LocalDateTime.now());
        bill.setUsername(user.getUsername());

        // 更新用户余额
        boolean updateBalanceResult = userDao.updateBalance(userId, balanceAfter);

        if (updateBalanceResult) {
            int recordResult = rechargeDao.insert(record);
            int billResult = billDao.insert(bill);

            return recordResult > 0 && billResult > 0;
        }
        return false;
    }
}