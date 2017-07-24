package com.onyx.android.sdk.data.model;

import java.util.List;

/**
 * Created by ming on 2017/7/22.
 */

public class CloudBackupData {

    private String updatedAt;

    private String createdAt;

    private String deviceMAC;

    private String name;

    private List<CloudBackupFile> dataFiles ;

    public void setUpdatedAt(String updatedAt){
        this.updatedAt = updatedAt;
    }
    public String getUpdatedAt(){
        return this.updatedAt;
    }
    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }
    public String getCreatedAt(){
        return this.createdAt;
    }
    public void setDeviceMAC(String deviceMAC){
        this.deviceMAC = deviceMAC;
    }
    public String getDeviceMAC(){
        return this.deviceMAC;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setDataFiles(List<CloudBackupFile> dataFiles){
        this.dataFiles = dataFiles;
    }
    public List<CloudBackupFile> getDataFiles(){
        return this.dataFiles;
    }
}
