package com.onyx.cloud.db.query;

import com.onyx.cloud.model.ProductContainer;
import com.onyx.cloud.model.ProductContainer_Table;

public class ProductContainerDbQuery extends DbQueryBase<ProductContainer>{

	public ProductContainerDbQuery() {
		super(ProductContainer.class);	
	}
	
	public ProductContainerDbQuery and_eqName(String name){
		where.and(ProductContainer_Table.name.eq(name));
		return this;
	}
	
	public ProductContainerDbQuery and_likeName(String name){
		where.and(ProductContainer_Table.name.like(likeTerm(name)));
		return this;
	}
	
	public ProductContainerDbQuery and_eqValue(String value){
		where.and(ProductContainer_Table.value.eq(value));
		return this;
	}
	
	public ProductContainerDbQuery and_likeValue(String value){
		where.and(ProductContainer_Table.value.like(likeTerm(value)));
		return this;
	}
	
	public ProductContainerDbQuery and_eqCount(long count){
		where.and(ProductContainer_Table.count.eq(count));
		return this;
	}
	
	public ProductContainerDbQuery and_greaterThanCount(long count){
		where.and(ProductContainer_Table.count.greaterThan(count));
		return this;
	}
	
	public ProductContainerDbQuery and_lessThanCount(long count){
		where.and(ProductContainer_Table.count.lessThan(count));
		return this;
	}
	
	public ProductContainerDbQuery and_eqParentId(long parentId){
		where.and(ProductContainer_Table.parentId.eq(parentId));
		return this;
	}
	
	public ProductContainerDbQuery or_eqName(String name){
		where.or(ProductContainer_Table.name.eq(name));
		return this;
	}
	
	public ProductContainerDbQuery or_likeName(String name){
		where.or(ProductContainer_Table.name.like(likeTerm(name)));
		return this;
	}
	
	public ProductContainerDbQuery or_eqValue(String value){
		where.or(ProductContainer_Table.value.eq(value));
		return this;
	}
	
	public ProductContainerDbQuery or_likeValue(String value){
		where.or(ProductContainer_Table.value.like(likeTerm(value)));
		return this;
	}
	
	public ProductContainerDbQuery or_eqCount(long count){
		where.or(ProductContainer_Table.count.eq(count));
		return this;
	}
	
	public ProductContainerDbQuery or_greaterThanCount(long count){
		where.or(ProductContainer_Table.count.greaterThan(count));
		return this;
	}
	
	public ProductContainerDbQuery or_lessThanCount(long count){
		where.or(ProductContainer_Table.count.lessThan(count));
		return this;
	}
	
	public ProductContainerDbQuery or_eqParentId(long parentId){
		where.or(ProductContainer_Table.parentId.eq(parentId));
		return this;
	}

}
