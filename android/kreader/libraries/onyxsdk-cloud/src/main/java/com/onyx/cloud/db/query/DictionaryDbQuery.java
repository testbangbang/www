package com.onyx.cloud.db.query;

import com.onyx.cloud.model.Dictionary;
import com.onyx.cloud.model.Dictionary_Table;

public class DictionaryDbQuery extends DbQueryBase<Dictionary>{

	public DictionaryDbQuery() {
		super(Dictionary.class);		
	}
	
	public DictionaryDbQuery and_eqSourceLanguage(String sourceLanguage){
		where.and(Dictionary_Table.sourceLanguage.eq(sourceLanguage));
		return this;
	}
	
	public DictionaryDbQuery and_likeSourceLanguage(String sourceLanguage){
		where.and(Dictionary_Table.sourceLanguage.like(likeTerm(sourceLanguage)));
		return this;
	}
	
	public DictionaryDbQuery and_eqTargetLanguage(String targetLanguage){
		where.and(Dictionary_Table.targetLanguage.eq(targetLanguage));
		return this;
	}
	
	public DictionaryDbQuery and_likeTargetLanguage(String targetLanguage){
		where.and(Dictionary_Table.targetLanguage.like(likeTerm(targetLanguage)));
		return this;
	}
	
	public DictionaryDbQuery or_eqSourceLanguage(String sourceLanguage){
		where.or(Dictionary_Table.sourceLanguage.eq(sourceLanguage));
		return this;
	}
	
	public DictionaryDbQuery or_likeSourceLanguage(String sourceLanguage){
		where.or(Dictionary_Table.sourceLanguage.like(likeTerm(sourceLanguage)));
		return this;
	}
	
	public DictionaryDbQuery or_eqTargetLanguage(String targetLanguage){
		where.or(Dictionary_Table.targetLanguage.eq(targetLanguage));
		return this;
	}
	
	public DictionaryDbQuery or_likeTargetLanguage(String targetLanguage){
		where.or(Dictionary_Table.targetLanguage.like(likeTerm(targetLanguage)));
		return this;
	}

}
