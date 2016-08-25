package com.onyx.cloud.db.query;

import java.util.List;

import com.onyx.cloud.model.BaseObject;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

public class DbQueryBase<T extends BaseObject>{
	protected Where<T> where;
	
    DbQueryBase(Class<T> clazz){
		where = SQLite.select().from(clazz).where();
	}
    
    protected String likeTerm(String want){
		return "%" + want + "%";
	}
	
	public List<T> startQueryList(){
		return where.queryList();
	}
	
	public T startQuerySingle(){
		return where.querySingle();
	}
	
	public Where<T> getWhere(){
		return where;
	}
	
	public DbQueryBase<T> offset(int offset){
		where.offset(offset);
		return this;
	}
	
	public DbQueryBase<T> limit(int limit){
		where.limit(limit);
		return this;
	}
}
