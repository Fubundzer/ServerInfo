/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serversinfo;

import com.zabbix4j.ZabbixApi;
import com.zabbix4j.ZabbixApiException;
import com.zabbix4j.host.HostCreateRequest;
import com.zabbix4j.host.HostCreateResponse;
import com.zabbix4j.hostinteface.HostInterfaceObject;
import com.zabbix4j.usermacro.Macro;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.unix4j.Unix4j;

/**
 *
 * @author michalr
 */
public class ServerInfo {
    /*public static void main(String[] args) {
    String s,lines="",test="",test2="";
    double d,sum=0.0;
     Pattern pat;
        Process p;
        try{
            //Runtime.getRuntime().exec("ps -aux");
            p=Runtime.getRuntime().exec("top -bn1");
            BufferedReader br = new BufferedReader(
            new InputStreamReader(p.getInputStream()));
            while((s=br.readLine())!=null)
                //System.out.println("line: "+s);
                lines+=s+"\n";*/
            //test = Unix4j.fromString(lines).grep("Cpu").sed("s/.*, *\\([0-9.]*\\)%* id.*/\\1/").toStringResult();
            /*test2=Unix4j.fromString(lines).grep("KiB Mem").toStringResult();
                pat = Pattern.compile("(\\d,\\d)|(\\d\\d,\\d)");
            Matcher m = pat.matcher(test);
            while(m.find()) {
                //double d = Double.parseDouble(m.group(1));
                
                if(m.group(1)!=null){
                d= Double.parseDouble(m.group(1).replace(",", "."));
                sum+=d;
                System.out.println(d);
                }else if(m.group(2)!=null){
                d= Double.parseDouble(m.group(2).replace(",","."));
                System.out.println(d);
                sum+=d;
                }
            }
            pat = Pattern.compile("((\\d+) used)");
            m = pat.matcher(test2);
            while(m.find()) {
                d=Double.parseDouble(m.group(2));
                System.out.println(d/1000+"MB of mem used");
            }
            
            System.out.println(test);
            System.out.println(test2);
            System.out.println(sum+"% of CPU used");
            p.waitFor();
            System.out.println("exit: "+p.exitValue());
            p.destroy();
        }catch(Exception e){}
    }*/
    
    public static final String ZBX_URL = "http://192.168.1.145/api_jsonrpc.php";
    public static final String USERNAME = "zabbix";
    public static final String PASSWORD = "K9GtWt2aT5uy53q9";

    public static void main(String[] args) {

        try {
            // login to Zabbix
            ZabbixApi zabbixApi = new ZabbixApi(ZBX_URL);
            zabbixApi.login(USERNAME, PASSWORD);

            // request paramter
            HostCreateRequest request = new HostCreateRequest();
            HostCreateRequest.Params params = request.getParams();

            // set tempalte
            List<Integer> templates = new ArrayList<Integer>();
            templates.add(10093);
            params.setTemplates(templates);

            // attached to group
            params.addGroupId(12);

            // set macro
            List<Macro> macros = new ArrayList<Macro>();
            Macro macro1 = new Macro();
            macro1.setMacro("{$MACRO1}");
            macro1.setValue("value1");
            macros.add(macro1);
            params.setMacros(macros);

            // host interface
            List<HostInterfaceObject> interfaces = new ArrayList<HostInterfaceObject>();
            HostInterfaceObject hostInterface = new HostInterfaceObject();
            hostInterface.setIp("192.168.255.255");
            interfaces.add(hostInterface);
            params.setInterfaces(interfaces);

            // host
            params.setHost("test host created1");

            // host name
            params.setName("test host created1 name");

            // send create request
            HostCreateResponse response = zabbixApi.host().create(request);

            Integer hostId = response.getResult().getHostids().get(0);
            System.out.println(hostId);

        } catch (ZabbixApiException e) {
            e.printStackTrace();
        }
}
}
