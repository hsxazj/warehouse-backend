package org.homework;

public class SqlTest {
    static String sql = "INSERT INTO material (name, specification, unit, stock, remark) VALUES ('测试物品%d', '测试物品%d说明', '吨', %d, '测试物品%d备注');";

    public static void main(String[] args) {
        for (int i = 20; i < 30; i++) {
            System.out.println(String.format(sql, i, i, i, i, i));
        }

    }
}
