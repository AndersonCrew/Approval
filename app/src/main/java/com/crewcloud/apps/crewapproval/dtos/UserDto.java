package com.crewcloud.apps.crewapproval.dtos;


import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.util.PreferenceUtilities;


public class UserDto {
    public int Id;
    public int CompanyNo;
    public int PermissionType;//0 normal, 1 admin
    public String userID;
    public String FullName = "";
    public String MailAddress = "";
    public String session;
    public String avatar;
    public String NameCompany = "";
    public PreferenceUtilities prefs;

    public UserDto() {
        prefs = CrewCloudApplication.getInstance().getPreferenceUtilities();
    }
}
