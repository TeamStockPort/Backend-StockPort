package com.stockport.server.global.apipayload.handler;

import com.stockport.server.global.apipayload.code.BaseCode;
import com.stockport.server.global.apipayload.code.status.ErrorStatus;
import com.stockport.server.global.exception.GeneralException;

public class TempHandler extends GeneralException {
    public TempHandler(BaseCode errorCode) { super((ErrorStatus) errorCode); }
}