package com.mossle.api.store;

import javax.activation.DataSource;

public class StoreDTO {
    private String model;
    private String key;
    private DataSource dataSource;
    private String displayName;
    private String type;
    private long size;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取文件MIME类型
     * @return
     */
	public String getType() {
		return type;
	}

	/**
	 * 设置文件MIME类型
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取文件大小
	 * @return
	 */
	public long getSize() {
		return size;
	}

	/**
	 * 设置文件大小
	 * @param size
	 */
	public void setSize(long size) {
		this.size = size;
	}
}
