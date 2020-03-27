package com.crewcloud.apps.crewapproval.interfaces;


import com.crewcloud.apps.crewapproval.dtos.ErrorDto;

public interface OnAutoLoginCallBack {
    void OnAutoLoginSuccess(String response);
    void OnAutoLoginFail(ErrorDto dto);
}
