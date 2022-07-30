package com.hzh.gatheringproject.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DAHUANG
 * @date 2022/7/19
 */
@Slf4j
public class RegisterException {

    public static boolean registerResult(int res){
        boolean result=true;
        try {
            if (res<=0){
                throw new RuntimeException("完善失败");
            }
        }catch (Exception e){
            result=false;
        }
        return result;
    }

    public static boolean followResult(int res){
        boolean result=true;
        try {
            if (res<=0){
                throw new RuntimeException("完善失败");
            }
        }catch (Exception e){
            result=false;
        }
        return result;
    }

}
