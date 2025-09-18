package org.framegen.core.db;

/**
 * 支持的数据库产品
 */
public enum DatabaseProduct {
    MYSQL("MySQL");

    private final String productName;

    DatabaseProduct(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public static DatabaseProduct fromProductName(String productName) {
        for (DatabaseProduct product : values()) {
            if (product.productName.equals(productName)) {
                return product;
            }
        }
        throw new IllegalArgumentException("不支持的数据库类型: " + productName);
    }
}
