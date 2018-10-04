package com.xiaopeng.waterarmy.handle;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;

public interface RequestHandler {

    /**
     * 请求处理
     * @param requestContext
     * @return
     */
    public Result<HandlerResultDTO> handle(RequestContext requestContext);

    /**
     * 存储请求结果
     * @param saveContext
     * @return
     */
    public Result save(SaveContext saveContext);
}
