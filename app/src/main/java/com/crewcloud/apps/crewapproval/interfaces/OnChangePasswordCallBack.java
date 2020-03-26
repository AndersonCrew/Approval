package com.crewcloud.apps.crewapproval.interfaces;

import com.crewcloud.apps.crewapproval.dtos.ErrorDto;

public interface OnChangePasswordCallBack {
    void onSuccess(String response);
    void onFail(ErrorDto errorDto);
}