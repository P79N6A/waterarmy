package com.xiaopeng.waterarmy.handle;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;

public abstract class PlatformHandler implements  RequestHandler{

    @Override
    public Result<HandlerResultDTO> handle(RequestContext requestContext) {

        //这里做一次请求处理路由
        switch (requestContext.getHandleType()) {
            case POSIED:
                return publish(requestContext);
            case COMMENT:
                return comment(requestContext);
            case LIKE:
                return praise(requestContext);
            case READ:
                return read(requestContext);
                default:
                    return new Result<HandlerResultDTO>(null,false,ResultCodeEnum.HANDLER_NOT_FOUND.getIndex(),ResultCodeEnum.HANDLER_NOT_FOUND.getDesc());
        }
    }

    @Override
    public abstract Result save(SaveContext saveContext);


    /**
     * 发帖
     * @param requestContext
     * @return
     */
    public abstract Result<HandlerResultDTO> publish(RequestContext requestContext);

    /**
     * 评论
     * @param requestContext
     * @return
     */
    public abstract Result<HandlerResultDTO> comment(RequestContext requestContext);


    /**
     * 浏览
     * @param requestContext
     * @return
     */
    public abstract Result<HandlerResultDTO> read(RequestContext requestContext);

    /**
     * 点赞
     * @param requestContext
     * @return
     */
    public abstract Result<HandlerResultDTO> praise(RequestContext requestContext);
}
