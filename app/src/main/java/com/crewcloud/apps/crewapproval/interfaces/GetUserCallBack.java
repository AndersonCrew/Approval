package com.crewcloud.apps.crewapproval.interfaces;

import com.crewcloud.apps.crewapproval.dtos.Profile;

public interface GetUserCallBack {

    void onGetUserSuccess(Profile profile);
    void onError();
}
