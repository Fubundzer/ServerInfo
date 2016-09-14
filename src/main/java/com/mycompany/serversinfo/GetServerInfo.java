/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serversinfo;

import com.goebl.david.Webb;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author michalr
 */
public class GetServerInfo {
    
    private String ZBX_URL = "http://192.168.1.145/api_jsonrpc.php";
    private String USERNAME = "zabbix";
    private String PASSWORD = "K9GtWt2aT5uy53q9";

    private int hostid;
    private String auth;
    private Webb webb = Webb.create();
    
    public GetServerInfo(){
        auth=login();
    };
    
    /**
     * Konstruktor klasy wymagajacy wporwadzenia ID hosta, jezeli
     * nie znamy ID mozliwe jest uzycie konstruktora bezparametrowego,
     * a nastepnie wywolanie funkcji getHost(), ktora zwroci liste
     * nazw i ID wszystkich hostow.
     * @param hostid Id hosta, ktorego dane chcemy uzyskac
     */
    public GetServerInfo(int hostid){
       this.hostid=hostid;
       auth=login();
    };
    
    /**
     * Koncstruktor, ktory umozliwia zmiane podstawowych informacji (url, login, password)
     * @param hostid
     * @param ZBX_URL
     * @param USERNAME
     * @param PASSWORD 
     */
    public GetServerInfo(int hostid,String ZBX_URL, String USERNAME, String PASSWORD){
       this.hostid=hostid;
       this.ZBX_URL=ZBX_URL;
       this.USERNAME=USERNAME;
       this.PASSWORD=PASSWORD;
       auth=login();
    }; 
    
    public GetServerInfo(String ZBX_URL, String USERNAME, String PASSWORD){
       this.ZBX_URL=ZBX_URL;
       this.USERNAME=USERNAME;
       this.PASSWORD=PASSWORD;
       auth=login();
    }; 
   /**
    * Prosta funkcja przeprowadzajaca logowanie do zabbixa i
    * zwracajaca token.
    * @return 
    */
    public String login(){
        JSONObject mainJObj = new JSONObject();
        JSONObject paramJObj = new JSONObject();
            mainJObj.put("jsonrpc", "2.0");
            mainJObj.put("method", "user.login");

            paramJObj.put("user", USERNAME);
            paramJObj.put("password", PASSWORD);

            mainJObj.put("params", paramJObj);
            mainJObj.put("id", "1");

            JSONObject result = webb.post(ZBX_URL)
                                    .header("Content-Type", "application/json")
                                    .useCaches(false)
                                    .body(mainJObj)
                                    .ensureSuccess()
                                    .asJsonObject()
                                    .getBody();
       return result.getString("result");
    }
    
    /**
     * Metoda zwracajaca wszystkie techniczne informacje na temat hosta
     * w postaci tablicy obiektow typu JSON
     * @return 
     */
    private JSONArray getJson(){
        JSONObject hostJObj = new JSONObject();
        JSONObject paramJObj2 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray resultArray = new JSONArray();
        
        try{  
            hostJObj.put("jsonrpc", "2.0");
            hostJObj.put("method","host.get");
            paramJObj2.put("output", "extend");
            paramJObj2.put("hostids",hostid);
            paramJObj2.put("selectInventory", "true");
            paramJObj2.put("expandDescription", "1");
            paramJObj2.put("expandData","1");
            jsonArray.put("name");
            jsonArray.put("lastvalue");
            jsonArray.put("units");
            jsonArray.put("itemid");
            jsonArray.put("lastclock");
            jsonArray.put("value_type");
            jsonArray.put("itemid");
            paramJObj2.put("selectItems", jsonArray);
            hostJObj.put("params",paramJObj2);
            hostJObj.put("auth", auth);
            hostJObj.put("id", "2");
       
            JSONObject result2 = webb.post(ZBX_URL)
                    .header("Content-Type", "application/json")
                    .useCaches(false)
                    .body(hostJObj)
                    .ensureSuccess()
                    .asJsonObject()
                    .getBody();  
       
            resultArray= (JSONArray)result2.get("result");
         
            resultArray = resultArray.getJSONObject(0).getJSONArray("items"); 
        } catch (JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
        return resultArray;
    }
    
    /**
     * Metoda zwracajaca obiekt typu JSON, ktory zawiera przetworzone
     * informacje na temat zuzycia CPU, pamięci RAM oraz lacza internetowego
     * @return 
     */
    public JSONObject servInfo(){
        
        JSONArray resultArray=getJson();
        long availableMemory=0,totalMemory=0,incNetTraf=0,outNetTraf=0,usedMemory=0;
        double cpuUsage=0.0;
        
        try{
            int j=0;
            for(int i=0;i<resultArray.length();i++){
                if(resultArray.getJSONObject(i).getString("name").equals("Available memory")){
                availableMemory=Long.parseLong(resultArray.getJSONObject(i).getString("lastvalue"));
                }else if(resultArray.getJSONObject(i).getString("name").equals("Total memory")){
                    totalMemory=Long.parseLong(resultArray.getJSONObject(i).getString("lastvalue"));
                }else if(resultArray.getJSONObject(i).getString("name").startsWith("Incoming network traffic")) {
                    incNetTraf+=Long.parseLong(resultArray.getJSONObject(i).getString("lastvalue"));
                }else if(resultArray.getJSONObject(i).getString("name").startsWith("Outgoing network traffic")) {
                    outNetTraf+=Long.parseLong(resultArray.getJSONObject(i).getString("lastvalue"));
                }else if(resultArray.getJSONObject(i).getString("name").startsWith("CPU")) {
                    j++;
                    if(j!=1)
                    cpuUsage+=Double.parseDouble(resultArray.getJSONObject(i).getString("lastvalue"));            
                }
             }
            usedMemory = totalMemory-availableMemory;
        }catch (JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
        
        JSONObject fin = new JSONObject();
        JSONObject cpu = new JSONObject();
        cpu.put("cpuUsage",cpuUsage);
        cpu.put("units", "%");
        JSONObject mem = new JSONObject();
        mem.put("availableMemory",availableMemory);
        mem.put("totalMemory",totalMemory);
        mem.put("usedMemory", usedMemory);
        mem.put("units","B");
        JSONObject net = new JSONObject();
        net.put("incNetTraf", incNetTraf);
        net.put("outNetTraf",outNetTraf);
        net.put("units", "bps");
        fin.put("CPU",cpu);
        fin.put("MEMORY", mem);
        fin.put("NETWORK", net);
        return fin;
    }
    
    /**
     * Metoda zwracajaca informacje na temat zuzycia CPU, pamieci RAM, lacza
     * w postaci nieprzetworzonej. Bez obliczania lacznych wartosci podanych danych
     * @return 
     */
    public JSONObject rawServInfo(){
        
        JSONArray resultArray=getJson();
        JSONArray finalArray=new JSONArray();

        try{
            int j=0;
            for(int i=0;i<resultArray.length();i++){
               if(resultArray.getJSONObject(i).getString("name").equals("Available memory")){
               finalArray.put(resultArray.getJSONObject(i));
               }else if(resultArray.getJSONObject(i).getString("name").equals("Total memory")){
                   finalArray.put(resultArray.getJSONObject(i));
               }else if(resultArray.getJSONObject(i).getString("name").startsWith("Incoming network traffic")) {
                 finalArray.put(resultArray.getJSONObject(i));
               }else if(resultArray.getJSONObject(i).getString("name").startsWith("Outgoing network traffic")) {
                   finalArray.put(resultArray.getJSONObject(i));
               }else if(resultArray.getJSONObject(i).getString("name").startsWith("CPU")) {
                   finalArray.put(resultArray.getJSONObject(i));
               }
            }
        }catch (JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
        
        JSONObject fin = new JSONObject();
        fin.put("result", finalArray);
        return fin;
    }
    
    /**
     * Funkcja zwracajaca liste ID oraz nazw wszstkich hostow
     */
    public void getHost(){
        
        this.auth=login();
        JSONObject hostJObj = new JSONObject();
        JSONObject paramJObj2 = new JSONObject();
        JSONArray jsonArray;
        
        try{     
            hostJObj.put("jsonrpc", "2.0");
            hostJObj.put("method","host.get");
            paramJObj2.put("output", "extend");
            hostJObj.put("params",paramJObj2);
            hostJObj.put("auth", auth);
            hostJObj.put("id", "3");
            
        JSONObject result2 = webb.post(ZBX_URL)
               .header("Content-Type", "application/json")
               .useCaches(false)
               .body(hostJObj)
               .ensureSuccess()
               .asJsonObject()
               .getBody();

        jsonArray = result2.getJSONArray("result");
        for(int i=0;i<jsonArray.length();i++){
            System.out.println("Host id: "+jsonArray.getJSONObject(i).getString("hostid")+"\t Host name: "+jsonArray.getJSONObject(i).getString("name"));
            }
        }catch(JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
    }
     /**
      * Funkcja, ktora wypisuje zawartosc JSONa w przystepnej postaci
      * @param json
      * @return 
      */
    public String getPrettyString(JSONObject json){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json.toString());
        return gson.toJson(je);
    }
    
    public void setZBX_URL(String ZBX_URL){
        this.ZBX_URL=ZBX_URL;
    }
    
    public String getZBX_URL(){
        return ZBX_URL;
    }
    
    public void setUSERNAME(String username){
        this.USERNAME=username;
    }
    
    public String getUSERNAME(){
        return USERNAME;
    }
    
    public void setPASSWORD(String password){
        this.PASSWORD=password;
    }
    
    public String getPASSWORD(){
        return PASSWORD;
    }
}