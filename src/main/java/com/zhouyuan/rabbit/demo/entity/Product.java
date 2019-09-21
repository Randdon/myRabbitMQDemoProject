package com.zhouyuan.rabbit.demo.entity;

import java.util.Date;

public class Product {
    private Integer id;

    private String productNo;

    private Integer total;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo == null ? null : productNo.trim();
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productNo='" + productNo + '\'' +
                ", total=" + total +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public Product(Integer id, String productNo, Integer total) {
        this.id = id;
        this.productNo = productNo;
        this.total = total;
        this.createTime = new Date();
        this.updateTime = new Date();
    }
}