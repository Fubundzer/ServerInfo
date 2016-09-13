/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serversinfo;

/**
 *
 * @author michalr
 */
public class ServerInfo {
    
    public static void main(String[] args) throws Exception {
        
        GetServerInfo info = new GetServerInfo(10084);
        GetServerInfo info2 = new GetServerInfo();
        System.out.println(info.getPrettyString(info.servInfo()));
        System.out.println(info.getPrettyString(info.rawServInfo()));
        info2.getHost();
    }
}