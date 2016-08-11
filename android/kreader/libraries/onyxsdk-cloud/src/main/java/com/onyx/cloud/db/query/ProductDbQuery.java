package com.onyx.cloud.db.query;

import com.onyx.cloud.model.Product;
import com.onyx.cloud.model.Product_Table;

public class ProductDbQuery extends DbQueryBase<Product> {

    public ProductDbQuery() {
        super(Product.class);
    }

    public ProductDbQuery and_eqName(String name) {
        where.and(Product_Table.name.eq(name));
        return this;
    }

    public ProductDbQuery and_likeName(String name) {
        where.and(Product_Table.name.like(likeTerm(name)));
        return this;
    }

    public ProductDbQuery and_likeCategory(String category) {
        where.and(Product_Table.categoryString.like(likeTerm(category)));
        return this;
    }

    public ProductDbQuery and_eqRating(float rating) {
        where.and(Product_Table.rating.eq(rating));
        return this;
    }

    public ProductDbQuery and_greaterThanRating(float rating) {
        where.and(Product_Table.rating.greaterThan(rating));
        return this;
    }

    public ProductDbQuery and_lessThanRating(float rating) {
        where.and(Product_Table.rating.lessThan(rating));
        return this;
    }

    public ProductDbQuery and_eqAuthor(String author) {
        where.and(Product_Table.authorString.eq(author));
        return this;
    }

    public ProductDbQuery and_likeAuthor(String author) {
        where.and(Product_Table.authorString.like(likeTerm(author)));
        return this;
    }

    public ProductDbQuery and_eqStorage(String storage) {
        where.and(Product_Table.storageString.eq(storage));
        return this;
    }

    public ProductDbQuery and_likeStorage(String storage) {
        where.and(Product_Table.storageString.like(likeTerm(storage)));
        return this;
    }

    public ProductDbQuery or_eqName(String name) {
        where.or(Product_Table.name.eq(name));
        return this;
    }

    public ProductDbQuery or_likeName(String name) {
        where.or(Product_Table.name.like(likeTerm(name)));
        return this;
    }

    public ProductDbQuery or_likeCategory(String category) {
        where.or(Product_Table.categoryString.like(likeTerm(category)));
        return this;
    }

    public ProductDbQuery or_eqRating(float rating) {
        where.or(Product_Table.rating.eq(rating));
        return this;
    }

    public ProductDbQuery or_greaterThanRating(float rating) {
        where.or(Product_Table.rating.greaterThan(rating));
        return this;
    }

    public ProductDbQuery or_lessThanRating(float rating) {
        where.or(Product_Table.rating.lessThan(rating));
        return this;
    }

    public ProductDbQuery or_eqAuthor(String author) {
        where.or(Product_Table.authorString.eq(author));
        return this;
    }

    public ProductDbQuery or_likeAuthor(String author) {
        where.or(Product_Table.authorString.like(likeTerm(author)));
        return this;
    }

    public ProductDbQuery or_eqStorage(String storage) {
        where.or(Product_Table.storageString.eq(storage));
        return this;
    }

    public ProductDbQuery or_likeStorage(String storage) {
        where.or(Product_Table.storageString.like(likeTerm(storage)));
        return this;
    }
}
