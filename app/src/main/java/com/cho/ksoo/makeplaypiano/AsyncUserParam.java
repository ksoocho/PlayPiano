package com.cho.ksoo.makeplaypiano;

public class AsyncUserParam {

    String userId;
    String userCode;
    String userPwd;
    String userName;

    public AsyncUserParam(
            String userId
            ,String userCode
            ,String userPwd
            ,String userName) {

        this.userId = userId;
        this.userCode = userCode;
        this.userPwd = userPwd;
        this.userName = userName;

    }
}