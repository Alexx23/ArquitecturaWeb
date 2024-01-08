/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web.practicafinal.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import web.practicafinal.enums.RoleEnum;
import web.practicafinal.exceptions.SessionException;
import web.practicafinal.exceptions.UnauthorizedException;
import web.practicafinal.models.User;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class Middleware {
    
    public static void authRoute(HttpServletRequest httpRequest) throws SessionException {
        Request.getUser(httpRequest);
    }
    
    public static void adminRoute(HttpServletRequest httpRequest) throws SessionException, UnauthorizedException {
        User userSession = Request.getUser(httpRequest);
        
        if (userSession.getRole().getId() != RoleEnum.ADMIN.getId()) {
            throw new UnauthorizedException();
        }
    }
    
}
