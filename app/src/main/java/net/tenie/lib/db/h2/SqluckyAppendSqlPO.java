package net.tenie.lib.db.h2;

import java.util.Date;

public class SqluckyAppendSqlPO {
    private Date createdTime;
    private Date updatedTime;
    private Integer isExecute;
    private String version;
    private String sqlVal;
    private Integer id;
    private String remark;

    public void setCreatedTime(Date createdTime){
        this.createdTime=createdTime;
    }

    public Date getCreatedTime(){
        return this.createdTime;
    }

    public void setUpdatedTime(Date updatedTime){
        this.updatedTime=updatedTime;
    }

    public Date getUpdatedTime(){
        return this.updatedTime;
    }

    public void setIsExecute(Integer isExecute){
        this.isExecute=isExecute;
    }

    public Integer getIsExecute(){
        return this.isExecute;
    }

    public void setVersion(String version){
        this.version=version;
    }

    public String getVersion(){
        return this.version;
    }

    public void setSqlVal(String sqlVal){
        this.sqlVal=sqlVal;
    }

    public String getSqlVal(){
        return this.sqlVal;
    }

    public void setId(Integer id){
        this.id=id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setRemark(String remark){
        this.remark=remark;
    }

    public String getRemark(){
        return this.remark;
    }
}
