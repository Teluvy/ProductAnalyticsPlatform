package com.example.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ProductCatalog {

    private final List<Product> products;

    public ProductCatalog(List<Product> products) {
        this.products = products;
    }

    public static ProductCatalog demoCatalog() {

        String[] categories = {
                "electronics",
                "books",
                "games",
                "clothes",
                "fitness"
        };

        List<Product> products = new ArrayList<>();

        for (int i = 1; i <= 10_000; i++) {

            String category = categories[
                    ThreadLocalRandom.current()
                            .nextInt(categories.length)
                    ];

            BigDecimal price = BigDecimal.valueOf(
                    ThreadLocalRandom.current()
                            .nextDouble(10, 5000)
            );

            double popularity =
                    ThreadLocalRandom.current()
                            .nextDouble(0.1, 1.0);

            products.add(
                    new Product(
                            i,
                            category,
                            price,
                            popularity
                    )
            );
        }

        return new ProductCatalog(products);
    }

    public Product getRandomPopularProduct() {

        int index = ThreadLocalRandom.current()
                .nextInt(products.size());

        return products.get(index);
    }
}