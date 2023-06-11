CREATE TABLE `KEYS_BINDING` ( 
   `ID` INTEGER PRIMARY KEY AUTOINCREMENT,   
   `ACTION_NAME`  VARCHAR(300)   ,       
   `BINDING` VARCHAR(300)    ,      
   `CODE` VARCHAR(300)  ,  
			
   `CREATED_TIME` DATETIME ,  
   `UPDATED_TIME` DATETIME 
 ) ;
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Line Comment", "Ctrl /");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Run SQL", "Ctrl Enter");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Run SQL Current Line", "Alt R");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Add New Edit Page", "Ctrl T");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Save", "Ctrl S");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Format", "Ctrl Shift F");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Find", "Ctrl F");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Replace", "Ctrl R");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Open", "Ctrl O");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Exit", "Ctrl Q");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Close Data Table", "Alt W");
 
insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Upper Case", "Ctrl Shift X");

insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Lower Case", "Ctrl Shift Y");

insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Underscore To Hump", "Ctrl Shift R");

insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Hump To Underscore", "Ctrl Shift T");

insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Hide/Show All Panels", "Ctrl H");

insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Font Size +", "Ctrl =");

insert into KEYS_BINDING (ACTION_NAME, BINDING) 
values("Font Size -", "Ctrl -");

