CREATE TABLE `KEYS_BINDING` ( 
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,   
   `ACTION_NAME`  VARCHAR(300)   ,       
   `BINDING` VARCHAR(300)    ,      
   `CODE` VARCHAR(300)  ,  
			
   `CREATED_TIME` DATETIME ,  
   `UPDATED_TIME` DATETIME 
 ) ;
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) values("Line Comment", "Ctrl + Slash");
