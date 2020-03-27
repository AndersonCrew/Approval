package com.crewcloud.apps.crewapproval.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dazone on 5/9/2017.
 */

public class Profile {

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public String getEntranceDate() {
        return entranceDate;
    }

    public List<BelongDepartmentDTO> getBelongs() {
        return belongs;
    }

    public String getAvatar() {
        return avatar;
    }

    @SerializedName("UserNo")
    public int userNo;

    @SerializedName("ModUserNo")
    public int modUserNo;

    @SerializedName("ModDate")
    public String modDate;

    @SerializedName("UserId")
    public String userId;

    @SerializedName("Password")
    public String password;

    @SerializedName("PasswordChangeDate")
    private String passwordChangeDate;

    @SerializedName("Name_Default")
    public String name_default;

    @SerializedName("Name_EN")
    public String name_En;

    @SerializedName("Name")
    public String name;

    @SerializedName("MailAddress")
    public String mailAddress;

    @SerializedName("Sex")
    public int sex;

    @SerializedName("CellPhone")
    public String cellPhone;

    @SerializedName("ExtensionNumber")
    public String extensionNumber;

    @SerializedName("EntranceDate")
    public String entranceDate;

    @SerializedName("BirthDate")
    public String birthDay;

    @SerializedName("Belongs")
    public List<BelongDepartmentDTO> belongs;

    @SerializedName("NameAndUserID")
    public String nameAndUserId;

    @SerializedName("AvatarUrl")
    public String avatar;

    @SerializedName("CompanyPhone")
    private String CompanyPhone;

    public String getCompanyPhone() {
        return CompanyPhone;
    }

    public String getBirthDate() {
        return birthDay;
    }

}
