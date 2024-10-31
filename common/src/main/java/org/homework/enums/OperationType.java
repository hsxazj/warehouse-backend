package org.homework.enums;

public enum OperationType {

    PERMISSION {
        @Override
        public String getTypeString() {
            return "操作员相关";
        }
    },

    PERSON {
        @Override
        public String getTypeString() {
            return "人事相关";
        }
    },

    MATERIALS {
        @Override
        public String getTypeString() {
            return "物料（订单）相关";
        }
    },

    OTHER {
        @Override
        public String getTypeString() {
            return "其他";
        }
    };

    public static void main(String[] args) {
        String eumStr = "PERSON";
        // 使用valueOf来获取枚举
        OperationType operationType = Enum.valueOf(OperationType.class, eumStr);
        String typeString = operationType.getTypeString();
        System.out.println(typeString);
    }

    public static String getTypeStringFromStr(String str) {
        try {
            // 使用valueOf来获取枚举
            OperationType operationType = Enum.valueOf(OperationType.class, str);
            return operationType.getTypeString();
        } catch (Exception e) {
            return null;
        }
    }

    public abstract String getTypeString();
}
