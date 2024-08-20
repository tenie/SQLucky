 CREATE TABLE `CONNECTION_INFO` ( 
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,  
   `CONN_NAME` VARCHAR(1000)   NOT NULL, 
   `USER` VARCHAR(1000)   NOT NULL, 
   `PASS_WORD` VARCHAR(1000)   NOT NULL, 
   `HOST` VARCHAR(200) , 
   `PORT` VARCHAR(10) ,  
   `JDBC_URL` VARCHAR(500) ,  
   `DRIVER` VARCHAR(200) , 
   `VENDOR` VARCHAR(100)  , 
   `SCHEMA` VARCHAR(200)  , 
   `DB_NAME` VARCHAR(200)  , 
   `COMMENT` VARCHAR(200) DEFAULT NULL,  
   `AUTO_CONNECT` INT(1) DEFAULT '0', 
   `CREATED_AT` DATETIME DEFAULT NULL, 
   `UPDATED_AT` DATETIME DEFAULT NULL, 
   `RECORD_VERSION` INT(11) DEFAULT '0', 
   `ORDER_TAG` DOUBLE(11) DEFAULT '99' 
 );
CREATE INDEX idx_conn_info on CONNECTION_INFO (`ID`,`CONN_NAME`);
 
  CREATE TABLE `SCRIPT_ARCHIVE` ( 
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,  
   `TITLE_NAME` VARCHAR(1000)   NOT NULL,  
   `SQL_TEXT` CLOB,  
   `FILE_NAME` VARCHAR(1000) ,  
   `ENCODE` VARCHAR(100) ,  
   `PARAGRAPH` INT(11) DEFAULT '0' ,
   `TAB_POSITION` INT(11) DEFAULT '0' ,  -- tab 在哪个tabPane显示, 0 : 在mainTabPane, 1: rightTabPane
   `IS_ACTIVATE` INT(1) DEFAULT '0' ,    -- 是否激活 1:表示激活状态
   `OPEN_STATUS` INT(1) DEFAULT '0'      --打开状态 1: 打开, 0:未打开

 )  ;
 
CREATE TABLE `APP_CONFIG` ( 
	`NAME` VARCHAR(1000)   NOT NULL, 
	`VAL`  VARCHAR(1000),
	`PLUGIN_NAME`  VARCHAR(200),
	PRIMARY KEY (`NAME`) 
)  ;


 CREATE TABLE `DATA_MODEL_INFO` ( 
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,  
   `NAME` VARCHAR(200)   NOT NULL,  
   `DESCRIBE` VARCHAR(300)  ,  
   `AVATAR` VARCHAR(200)   ,  
   `VERSION` VARCHAR(100)   ,  
			
   `CREATEDTIME` VARCHAR(100)    ,  
   `UPDATEDTIME` VARCHAR(100)    ,  
			
   `ORDER_TAG` INT(11) DEFAULT '99'
 ) ;
CREATE INDEX idx_mod_info_id_name on DATA_MODEL_INFO ( `ID`, `NAME`);
 
 CREATE TABLE `DATA_MODEL_TABLE` ( 
   `ITEM_ID` INTEGER PRIMARY KEY AUTOINCREMENT,  
   `MODEL_ID` INT(11)   ,  		
   `ID` VARCHAR(100) ,  
   `DEF_KEY` VARCHAR(200)   NOT NULL,  
   `DEF_NAME` VARCHAR(300)  ,  
   `COMMENT` VARCHAR(1000)  ,  
	 
   `CREATED_TIME` DATETIME  ,  
   `UPDATED_TIME` DATETIME  
 )  ;
 
CREATE INDEX idx_mod_tab_id_key on  DATA_MODEL_TABLE (`ITEM_ID`, `DEF_KEY`);
 
 
 CREATE TABLE `DATA_MODEL_TABLE_FIELDS` ( 
   `ITEM_ID` INTEGER PRIMARY KEY AUTOINCREMENT,  
   `TABLE_ID` INT(11) NOT NULL , 
   `MODEL_ID` INT(11)   ,  	
   `ID` VARCHAR(100)  ,   
   `ROW_NO` INT(11) ,  
   `DEF_KEY` VARCHAR(200)    ,      -- 字段名称
   `DEF_NAME` VARCHAR(300)  ,  
   `COMMENT` VARCHAR(1000)  ,  
			
   `DOMAIN` VARCHAR(200)  ,  
   `TYPE` VARCHAR(200)  ,  
   `LEN` INT(11) ,  
   `SCALE` VARCHAR(100)  ,  
			
   `PRIMARY_KEY` VARCHAR(10) ,  
   `NOT_NULL` VARCHAR(10) ,  
   `AUTO_INCREMENT` VARCHAR(10) ,  
   `DEFAULT_VALUE` VARCHAR(500)  ,   
   `HIDE_IN_GRAPH` VARCHAR(10) ,  
			
   `TYPE_FULL_NAME` VARCHAR(500)  ,   
   `PRIMARY_KEY_NAME` VARCHAR(500)  ,   
   `NOT_NULL_NAME` VARCHAR(500)  ,   
   `AUTO_INCREMENT_NAME` VARCHAR(500)  ,   
   `REF_DICT` VARCHAR(500)  ,   
			
   `CREATED_TIME` DATETIME ,  
   `UPDATED_TIME` DATETIME 
 ) ;
 
CREATE INDEX idx_mod_id_key on DATA_MODEL_TABLE_FIELDS ( `ITEM_ID`, `TABLE_ID`,`DEF_KEY`);
 
 CREATE TABLE `PLUGIN_INFO` ( 
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,   
   `PLUGIN_NAME` VARCHAR(200)    ,       
   `PLUGIN_CODE` VARCHAR(200)    ,
   `FILE_NAME` VARCHAR(200)    ,
   `PLUGIN_DESCRIBE` VARCHAR(1000)  ,
   `COMMENT` VARCHAR(1000)  ,  
   `DOWNLOAD_STATUS` INT(1) ,             -- 下载状态, 0:未安装, 1: 以安装
   `RELOAD_STATUS` INT(1) DEFAULT '1',    -- 是否需要加载, 0: 不加载, 1: 加载
   `VERSION` VARCHAR(30)    ,             --  版本
			
   `CREATED_TIME` DATETIME ,  
   `UPDATED_TIME` DATETIME 
 )  ;
CREATE INDEX idx_plugin_id_name on PLUGIN_INFO (`ID`, `PLUGIN_NAME`);
	
 CREATE TABLE `SQLUCKY_USER` ( 
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,   
   `USER_NAME`  VARCHAR(300)   ,       
   `EMAIL` VARCHAR(300)    ,      
   `PASSWORD` VARCHAR(300)  ,  
			
   `CREATED_TIME` DATETIME ,  
   `UPDATED_TIME` DATETIME 
 ) ;
 
 CREATE TABLE `KEYS_BINDING` ( 
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,   
   `ACTION_NAME`  VARCHAR(300)   ,       
   `BINDING` VARCHAR(300)    ,      
   `CODE` VARCHAR(300)  ,  
			
   `CREATED_TIME` DATETIME ,  
   `UPDATED_TIME` DATETIME 
 ) ;
 
CREATE INDEX idx_keys_id_name on KEYS_BINDING (`ID`, `ACTION_NAME`);



 CREATE TABLE `IMPORT_FIELD_MAP` (   
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,   
   `TABLE_NAME`  VARCHAR(300)   ,         
   `TYPE` VARCHAR(30)  ,  
			
   `CREATED_TIME` DATETIME ,  
   `UPDATED_TIME` DATETIME 
 ) ;
 
CREATE INDEX IDX_TABLE_IMPORT_FIELD_ID_NAME on IMPORT_FIELD_MAP (`ID`, `TABLE_NAME`);


 CREATE TABLE `IMPORT_FIELD_MAP_DETAIL` ( 
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,   
   `TABLE_ID` INTEGER ,   
   `TABLE_FILED_NAME`  VARCHAR(300)   ,       
   `EXCEL_FILED_IDX` INT(4)   ,      
   `FIXED_VALUE` VARCHAR(300)  ,  
			
   `CREATED_TIME` DATETIME ,  
   `UPDATED_TIME` DATETIME 
 ) ;
 
CREATE INDEX IDX_IMPORT_FIELD_DETAIL ON IMPORT_FIELD_MAP_DETAIL (`TABLE_ID`);

 CREATE TABLE `SQLUCKY_APPEND_SQL` (
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,
   `REMARK` VARCHAR(500)    DEFAULT '',
   `SQL_VAL` VARCHAR(500)  DEFAULT '',  -- 要插入的sql
   `IS_EXECUTE` int(1)  DEFAULT '0'  ,  -- 是否执行过, 默认没有执行过, 值为1表示执行过
   `VERSION` VARCHAR(30)    ,             --  版本

   `CREATED_TIME` DATETIME ,
   `UPDATED_TIME` DATETIME
 )  ;
CREATE INDEX idx_SQLUCKY_APPEND_SQL_REMARK on SQLUCKY_APPEND_SQL ( `REMARK`);
CREATE INDEX idx_SQLUCKY_APPEND_SQL_VAL on SQLUCKY_APPEND_SQL ( `SQL_VAL`);

-- 保存收集的字符串, 为自动补全使用
CREATE TABLE `AUTO_COMPLETE_TEXT` (
   `TEXT`  VARCHAR(300) PRIMARY KEY
 ) ;