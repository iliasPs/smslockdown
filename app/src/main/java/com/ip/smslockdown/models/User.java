package com.ip.smslockdown.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Builder
@Data
@Entity
@AllArgsConstructor
public class User implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int uid=0;
    @With
    @ColumnInfo(name = "full_name")
    private String fullName;
    @With
    @ColumnInfo(name = "address")
    private String address;
    @With
    @ColumnInfo(name = "last_used")
    private boolean lastUsed;
    @With
    @ColumnInfo(name = "to_edit")
    private boolean toBeEdited;


    public User(String fullName, String address, boolean lastUsed) {
        this.fullName = fullName;
        this.address = address;
        this.lastUsed = lastUsed;
    }

}
