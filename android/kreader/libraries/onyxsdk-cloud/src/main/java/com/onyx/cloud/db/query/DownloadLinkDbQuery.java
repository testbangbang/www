package com.onyx.cloud.db.query;

import com.onyx.cloud.model.DownloadLink;
import com.onyx.cloud.model.DownloadLink_Table;

public class DownloadLinkDbQuery extends DbQueryBase<DownloadLink>{

	public DownloadLinkDbQuery(){
		super(DownloadLink.class);
	}
	
	public DownloadLinkDbQuery andExt(String ext){
		where.and(DownloadLink_Table.ext.eq(ext));
		return this;
	}
	
	public DownloadLinkDbQuery andMd5(String md5){
		where.and(DownloadLink_Table.md5.eq(md5));
		return this;
	}
	
	public DownloadLinkDbQuery andId(long id){
		where.and(DownloadLink_Table.id.eq(id));
		return this;
	}
	
	public DownloadLinkDbQuery orExt(String ext){
		where.or(DownloadLink_Table.ext.eq(ext));
		return this;
	}
	
	public DownloadLinkDbQuery orMd5(String md5){
		where.or(DownloadLink_Table.md5.eq(md5));
		return this;
	}
	
	public DownloadLinkDbQuery orId(long id){
		where.or(DownloadLink_Table.id.eq(id));
		return this;
	}
				
}
