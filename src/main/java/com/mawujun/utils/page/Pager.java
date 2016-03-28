package com.mawujun.utils.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pager<T> {
	protected int page = -1;//当前第几页
	protected int limit = 50;// 默认是每页50条
	protected int start = 0;
	protected int total = -1;//总共有几页
	private Boolean success=true;
	private String message;
	protected List<T> root = null;

	// 这个优先级较高，如果设置了这个值，传递到后台的话，就使用这个值作为参数
	// mybatis时候用的较多
	protected Object params;// 具体的参数形式，可能是Map也可能是Bean
	
	//protected List result = null;
	//protected int total = -1;
	/**
	 * PageParam.getInstance().setPageSize(1).setStart(1).setParams("");
	 * @author mawujun email:160649888@163.com qq:16064988
	 * @return
	 */
//	public static Pager<T> getInstance(){
//		
//		return new Pager<T>();
//	}
//	public static Pager<T> getInstance(int start,int limit){
//		Pager<T> param = new Pager<T>();
//		param.setStart(start);
//		param.setLimit(limit);
//		return param;
//	}
	/**
	 * 获得当前页,如果参数没有传递过来获取 第几页的话，这里将会自动计算当前是第几页
	 * @author mawujun qq:16064988 mawujun1234@163.com
	 * @return
	 */
	public int getPage() {
		if(this.page==-1){
			return getPage_cmpt();
		}
		return page;
	}
	/**
	 * 通过计算获得当前页
	 * @author mawujun qq:16064988 mawujun1234@163.com
	 * @return
	 */
	public int getPage_cmpt() {
		//开始计算pageNo。看getFirst()
		this.page=Double.valueOf(Math.ceil(new Double(start)/new Double(limit))).intValue()+1;
		return page;
	}


	public int getStart() {
		return start;
	}
	public Pager<T> setStart(int start) {
		this.start = start;
		return this;
	}
	public Object getParams() {
		return params;
	}
	/**
	 * 设置参数，一般是作为where条件的，可以是map，bean等各种类型
	 * 和addParams是冲突的，只能选一个
	 * @author mawujun email:160649888@163.com qq:16064988
	 * @param params
	 */
	public Pager setParams(Object params) {
		this.params = params;
		return this;
	}
	/**
	 * 调用这个方法，就默认参数Map类型的,
	 * 默认null和空字符串不会添加进去
	 * 和setParams是冲突的，只能选一个
	 * @author mawujun email:160649888@163.com qq:16064988
	 * @param key
	 * @param params
	 * @return
	 */
	public Pager addParam(String key,Object value) {
		if(value==null || "".equals(value)){
			return this;
		}
		if(this.params==null || !(this.params instanceof Map)){
			this.params=new HashMap<String,Object>();
		}
		((Map<String,Object>)this.params).put(key, value);
		return this;
	}

	public void setPage(int page) {
		this.page = page;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<T> getRoot() {
		return root;
	}
	public void setRoot(List<T> root) {
		this.root = root;
	}


	
	/**
	 * 返回结果集中的数量
	 * @author mawujun email:160649888@163.com qq:16064988
	 * @return
	 */
	public int getRootSize() {
		if(root==null){
			return 0;
		}
		return root.size();
	}
	/**
	 * 返回页数
	 * 根据pageSize与totalItems计算总页数.
	 */
	public int getTotalPages() {
		return (int) Math.ceil((double) total / (double) getLimit());

	}

	/**
	 * 是否还有下一页.
	 */
	public boolean hasNextPage() {
		return (getPage() + 1 <= getTotalPages());
	}

	/**
	 * 是否最后一页.
	 */
	public boolean isLastPage() {
		return !hasNextPage();
	}

	/**
	 * 取得下页的页号, 序号从1开始.
	 * 当前页为尾页时仍返回尾页序号.
	 */
	public int getNextPageNo() {
		if (hasNextPage()) {
			return getPage() + 1;
		} else {
			return getPage();
		}
	}

	/**
	 * 是否还有上一页.
	 */
	public boolean hasPrePage() {
		return (getPage() > 1);
	}

	/**
	 * 是否第一页.
	 */
	public boolean isFirstPage() {
		return !hasPrePage();
	}

	/**
	 * 取得上页的页号, 序号从1开始.
	 * 当前页为首页时返回首页序号.
	 */
	public int getPrePageNo() {
		if (hasPrePage()) {
			return getPage() - 1;
		} else {
			return getPage();
		}
	}
	
	
}
