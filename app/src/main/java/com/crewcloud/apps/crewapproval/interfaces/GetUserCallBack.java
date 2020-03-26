package com.crewcloud.apps.crewapproval.interfaces;

import com.crewcloud.apps.crewapproval.dtos.Profile;

/**
 * Created by dazone on 5/9/2017.
 */

public interface GetUserCallBack {

    void onGetUserSuccess(Profile profile);
    void onError();
}
