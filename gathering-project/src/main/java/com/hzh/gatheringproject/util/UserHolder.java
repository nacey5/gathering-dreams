package com.hzh.gatheringproject.util;

import com.hzh.gatheringproject.dto.UserDTO;

/**
 * @author DAHUANG
 * @date 2022/7/11
 */
public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    public static UserDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
