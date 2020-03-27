package com.crewcloud.apps.crewapproval.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BelongDepartmentDTO implements Serializable {
    @SerializedName("DbId")
    public int DbId;

    @SerializedName("BelongNo")
    public int BelongNo;

    @SerializedName("UserNo")
    public int UserNo;

    @SerializedName("DepartNo")
    public int DepartNo;

    @SerializedName("PositionNo")
    public int PositionNo;

    @SerializedName("DutyNo")
    public int DutyNo;

    @SerializedName("IsDefault")
    public boolean IsDefault;

    @SerializedName("DepartName")
    public String DepartName;

    @SerializedName("DepartSortNo")
    public int DepartSortNo;

    @SerializedName("PositionName")
    public String PositionName;

    @SerializedName("PositionSortNo")
    public int PositionSortNo;

    @SerializedName("DutyName")
    public String DutyName;

    @SerializedName("DutySortNo")
    public int DutySortNo;

    @Override
    public String toString() {
        return "BelongDepartmentDTO {" +
                "DbId=" + DbId +
                ", UserNo=" + UserNo +
                ", DepartNo=" + DepartNo +
                ", PositionNo=" + PositionNo +
                ", DutyNo=" + DutyNo +
                ", IsDefault=" + IsDefault +
                ", DepartName='" + DepartName + '\'' +
                ", DepartSortNo=" + DepartSortNo +
                ", PositionName='" + PositionName + '\'' +
                ", PositionSortNo=" + PositionSortNo +
                ", DutyName='" + DutyName + '\'' +
                ", DutySortNo=" + DutySortNo +
                '}';
    }

    public boolean isDefault() {
        return IsDefault;
    }

    public String getDepartName() {
        return DepartName;
    }

    public String getPositionName() {
        return PositionName;
    }

    public String getDutyName() {
        return DutyName;
    }

}