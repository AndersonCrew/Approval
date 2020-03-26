package com.crewcloud.apps.crewapproval.interfaces;


import com.crewcloud.apps.crewapproval.dtos.ErrorDto;

public interface OnHasUpdateAppCallBack {
    void hasApp(String url);
    void noHas(ErrorDto dto);
}
