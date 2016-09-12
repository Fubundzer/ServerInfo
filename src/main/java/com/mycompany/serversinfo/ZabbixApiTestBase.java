/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serversinfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zabbix4j.ZabbixApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.fail;


/**
 * Created by Suguru Yajima on 2014/04/27.
 */
import com.zabbix4j.ZabbixApiException;
public class ZabbixApiTestBase {

    protected static Logger logger = LoggerFactory.getLogger(ZabbixApiTestBase.class);
    protected String user = "admin";
    protected String password = "zabbix";

    protected ZabbixApi zabbixApi;

    public ZabbixApiTestBase() {
        login(user, password);
    }

    protected void login(String user, String password) {
        try {
            zabbixApi = new ZabbixApi("http://zabbix.dev.corp.flamingo-inc.com/api_jsonrpc.php");
            zabbixApi.login(user, password);
        } catch (ZabbixApiException e) {
            fail(e.getMessage());
        }
    }

    protected Gson getGson() {

        return new GsonBuilder().setPrettyPrinting().create();
    }

}
