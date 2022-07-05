package com.hzh.gatheringproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author DAHUANG
 * @date 2022/7/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Boolean success;
    private int status;
    private String errorMsg;
    private Object data;
    private Long total;

    public static Result ok(){
        return new Result(true, 200,null, null, null);
    }
    public static Result ok(Object data){
        return new Result(true,200, null, data, null);
    }
    public static Result ok(List<?> data, Long total){
        return new Result(true,200, null, data, total);
    }
    public static Result fail(String errorMsg){
        return new Result(false,404, errorMsg, null, null);
    }
    public static Result fail(String errorMsg,int status){
        return new Result(false,status,errorMsg,null,null);
    }
}