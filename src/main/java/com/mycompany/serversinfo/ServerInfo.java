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
        
        //GetServerInfo info = new GetServerInfo(10084);
        //GetServerInfo info2 = new GetServerInfo();
        GetServerInfo info3 = new GetServerInfo(10084,"http://192.168.1.145/api_jsonrpc.php","zabbix","K9GtWt2aT5uy53q9");
        System.out.println(info3.getPrettyString(info3.servInfo()));
        System.out.println(info3.getPrettyString(info3.rawServInfo()));
        info3.getHost();
    }
}