package com.crewcloud.apps.crewapproval.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dazone on 5/9/2017.
 */

public class Profile {

    public String positionDepartName = "";

    public String getPositionDepartName() {
        return positionDepartName;
    }

    public void setPositionDepartName(String positionDepartName) {
        this.positionDepartName = positionDepartName;
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public int getModUserNo() {
        return modUserNo;
    }

    public void setModUserNo(int modUserNo) {
        this.modUserNo = modUserNo;
    }

    public String getModDate() {
        return modDate;
    }

    public void setModDate(String modDate) {
        this.modDate = modDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordChangeDate() {
        return passwordChangeDate;
    }

    public void setPasswordChangeDate(String passwordChangeDate) {
        this.passwordChangeDate = passwordChangeDate;
    }

    public String getName_default() {
        return name_default;
    }

    public void setName_default(String name_default) {
        this.name_default = name_default;
    }

    public String getName_En() {
        return name_En;
    }

    public void setName_En(String name_En) {
        this.name_En = name_En;
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

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public String getEntranceDate() {
        return entranceDate;
    }

    public void setEntranceDate(String entranceDate) {
        this.entranceDate = entranceDate;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public List<BelongDepartmentDTO> getBelongs() {
        return belongs;
    }

    public void setBelongs(List<BelongDepartmentDTO> belongs) {
        this.belongs = belongs;
    }

    public String getNameAndUserId() {
        return nameAndUserId;
    }

    public void setNameAndUserId(String nameAndUserId) {
        this.nameAndUserId = nameAndUserId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public void setCompanyPhone(String companyPhone) {
        CompanyPhone = companyPhone;
    }

    public String getBirthDate() {
        return birthDay;
    }

    public void setBirthDate(String birthDate) {
        birthDate = birthDate;
    }


}
