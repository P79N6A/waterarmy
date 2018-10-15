package com.xiaopeng.waterarmy.handle;

import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.mapper.AccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class LoginResultPool {
    private static Logger logger = LoggerFactory.getLogger(LoginResultPool.class);

    private static final int MAX_SIZE = 1000;

    @Autowired
    private AccountMapper accountMapper;


    private HashMap<String, LoginResultDTO> loginResultMap = new HashMap<>();


    public LoginResultDTO getLoginResult(String userid) {
        return loginResultMap.get(userid);
    }


    public void removeLoginResult(String userid) {
        loginResultMap.remove(userid);
    }

    public void putToLoginResultMap(String key,LoginResultDTO loginResultDTO) {
        if (loginResultMap.size() > MAX_SIZE) {
            //移除一个再添加
            Set<Map.Entry<String, LoginResultDTO>> set = loginResultMap.entrySet();
            Iterator<Map.Entry<String, LoginResultDTO>> iterator = set.iterator();
            Map.Entry entry = iterator.next();
            loginResultMap.remove(entry.getKey());
        }
        loginResultMap.put(key,loginResultDTO);
    }

}
