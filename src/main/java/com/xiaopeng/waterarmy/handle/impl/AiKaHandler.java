package com.xiaopeng.waterarmy.handle.impl;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import org.springframework.stereotype.Component;

@Component
public class AiKaHandler extends PlatformHandler {

    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {
        return null;
    }

    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {


        return null;
    }

    @Override
    public Result<HandlerResultDTO> read(RequestContext requestContext) {
        return null;
    }

    @Override
    public Result<HandlerResultDTO> praise(RequestContext requestContext) {
        return null;
    }
}
