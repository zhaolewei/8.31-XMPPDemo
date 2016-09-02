#OpenFire + Smack 实现安卓端即时通讯应用开发 
        开发中使用spark 进行测试
        下载地址：http://www.igniterealtime.org/downloads/
        
##项目演示




##项目搭建


1.配置项目

 (1). 在 app.iml </component>中添加如下代码
    
        <orderEntry type="library" exported="" name="smack-resolver-minidns-4.1.4" level="project" />
           <orderEntry type="library" exported="" 
       name="smack-im-4.1.4" level="project" />
           <orderEntry type="library" exported="" name="support-annotations-22.2.0" level="project" />
       <orderEntry type="library" exported="" name="smack-android-4.1.4" level="project" />
           <orderEntry type="library" exported="" name="smack-
       android-extensions-4.1.4" level="project" />
           <orderEntry type="library" exported="" name="minidns-0.1.7" level="project" />
           <orderEntry 
       type="library" exported="" name="jxmpp-util-cache-0.4.2" level="project" />
           <orderEntry type="library" exported="" name="smack-extensions-
       4.1.4" level="project" />
           <orderEntry type="library" exported="" name="smack-core-4.1.4" level="project" />
           <orderEntry type="library" 
       exported="" name="support-v4-22.2.0" level="project" />
           <orderEntry type="library" exported="" name="appcompat-v7-22.2.0" level="project" />
         <orderEntry type="library" exported="" name="jxmpp-core-0.4.2" level="project" />
           <orderEntry type="library" exported="" name="smack-tcp-
       4.1.4" level="project" />
           <orderEntry type="library" exported="" name="smack-sasl-provided-4.1.4" level="project" />
       
   
   
  (2). build.gradle
  
       compile 'org.igniterealtime.smack:smack-android-extensions:4.1.4'
       compile 'org.igniterealtime.smack:smack-tcp:4.1.4'
  (3). 编译代码 在扩展库中多出smack类库,则导入成功    

2.项目实现：
   (1).链接服务器 
        
     
    public XMPPTCPConnection getConnection() {
        String server = "192.168.56.1";
        int port = 5222;
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setHost(server);
        builder.setPort(port);
        builder.setServiceName(""); //使用域名  ， 此处使用计算机名
        builder.setCompressionEnabled(false); //是否压缩
        builder.setDebuggerEnabled(true);  //是否显示通讯日志
        builder.setSendPresence(true);

        //登陆安全认证：SASL安全机制
        SASLAuthentication.blacklistSASLMechanism("DIGSET-MD5");
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        XMPPTCPConnection connection = new XMPPTCPConnection(builder.build());

        return connection;
    }
    
    //开始连接
        connection = getConnection();
        connection.connect();
     
    
 (2). 注册用户
 
        if (connection.isConnected()) {
        connection.disconnect();
        }
        connection.connect();
        AccountManager accountManager = AccountManager.getInstance(connection);
        if (accountManager.supportsAccountCreation()) {
        Map<String, String> map = new Hashtable<>();
        map.put("email", "demo@qq.com");
        map.put("android", "createUser_android");
        accountManager.createAccount(username, password);
        }
    
 (3). 用户登陆
 
         if (connection.isConnected()) {
             connection.disconnect();
         }
         connection.connect();
         connection.login(username, password);
         Presence presence = new Presence(Presence.Type.available);
         presence.setStatus("我在线");
         connection.sendStanza(presence); // 修改状态

 (4).添加好友
 
         Roster roster = Roster.getInstanceFor(connection);
           //添加好友
         roster.createEntry(friendId, friendId, null);
         
 (5).发送消息       
 
         ChatManager chatManager = ChatManager.getInstanceFor(connection);
         Chat mChat = chatManager.createChat(FriendID);
         try {
             mChat.sendMessage(msg);
         } catch (SmackException.NotConnectedException e) {
             e.printStackTrace();
         }