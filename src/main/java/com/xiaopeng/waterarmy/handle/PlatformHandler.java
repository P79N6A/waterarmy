package com.xiaopeng.waterarmy.handle;

import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.PraiseInfo;
import com.xiaopeng.waterarmy.model.dao.PublishInfo;
import com.xiaopeng.waterarmy.model.mapper.CommentInfoMapper;
import com.xiaopeng.waterarmy.model.mapper.PraiseInfoMapper;
import com.xiaopeng.waterarmy.model.mapper.PublishInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class PlatformHandler implements  RequestHandler{

    @Autowired
    private CommentInfoMapper commentInfoMapper;

    @Autowired
    private PublishInfoMapper publishInfoMapper;

    @Autowired
    private PraiseInfoMapper praiseInfoMapper;

    @Autowired
    private LoginResultPool loginResultPool;

    @Override
    public Result<HandlerResultDTO> handle(RequestContext requestContext) {

        //这里做一次请求处理路由
        switch (requestContext.getHandleType()) {
            case POSIED:
                Result<HandlerResultDTO> result = publish(requestContext);
                if (!result.getSuccess()) {
                    loginResultPool.removeLoginResult(String.valueOf(requestContext.getUserId()));
                }
                return result;
            case COMMENT:
                Result<HandlerResultDTO> result1 = comment(requestContext);
                if (!result1.getSuccess()) {
                    loginResultPool.removeLoginResult(String.valueOf(requestContext.getUserId()));
                }
                return result1;
            case LIKE:
                Result<HandlerResultDTO> result2 =  praise(requestContext);
                if (!result2.getSuccess()) {
                    loginResultPool.removeLoginResult(String.valueOf(requestContext.getUserId()));
                }
                return result2;
            case READ:
                return read(requestContext);
            case PLAY:
                return play(requestContext);
                default:
                    return new Result<HandlerResultDTO>(null,false,ResultCodeEnum.HANDLER_NOT_FOUND.getIndex(),ResultCodeEnum.HANDLER_NOT_FOUND.getDesc());
        }
    }





    @Override
    public Result save(final SaveContext saveContext) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        if (saveContext.getData() != null) {
                            Object object = saveContext.getData();
                            if (object instanceof CommentInfo) {
                                try {
                                    CommentInfo commentInfo = (CommentInfo) object;
                                    commentInfoMapper.save(commentInfo);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            if (object instanceof PublishInfo) {
                                try {
                                    PublishInfo publishInfo = (PublishInfo)object;
                                    publishInfoMapper.save(publishInfo);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            if (object instanceof PraiseInfo) {
                                try {
                                    PraiseInfo praiseInfo = (PraiseInfo)object;
                                    praiseInfoMapper.save(praiseInfo);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
        );
        return new Result(true);
    }


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
     * 播放
     * @param requestContext
     * @return
     */
    public abstract Result<HandlerResultDTO> play(RequestContext requestContext);

    /**
     * 点赞
     * @param requestContext
     * @return
     */
    public abstract Result<HandlerResultDTO> praise(RequestContext requestContext);
}
