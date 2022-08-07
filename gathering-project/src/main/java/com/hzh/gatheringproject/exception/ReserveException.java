package com.hzh.gatheringproject.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DAHUANG
 * @date 2022/7/31
 */
@Slf4j
public class ReserveException {
    public static boolean sendReserveResult(int res){
        boolean result=true;
        try {
            if (res<=0){
                throw new RuntimeException("发送失败");
            }
        }catch (Exception e){
            result=false;
        }
        return result;
    }
}
