/*User(ID,username,password,sessionKey,active)*/
drop table User;
 create table User (
		username varchar(50),
		password varchar(50),
		sessionKey varchar(50),
		active int(1) default 0
       )TYPE=innodb;



/*ExternalService(ID,Name,ServiceType,Description)*/
drop table ExternalService;
 create table ExternalService (
		id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		name varchar(100),
		Description varchar(250),
		ServiceType ENUM('EventService', 'TaskService')
       )TYPE=innodb;


/*ExternalServiceCredentials(UserID,ExternalServiceID,username,password)*/
drop table ExternalServiceCredentials;
 create table ExternalServiceCredentials (
		User varchar(50),
		ExternalServiceID INT,
		username varchar(50),
		password varchar(50)
       )TYPE=innodb;


/*Task(ID,userId,dueDate,notifyTimeStart,notifyTimeEnd,Reminder.ID)*/
drop table Task;
 create table Task (
		id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		User varchar(50),
		dueDate varchar(50),
		notifyTimeStart varchar(50),
		notifyTimeEnd varchar(50),
		ReminderId INT
       )TYPE=innodb;

/*Event(ID,userId,startTime,endTime,latitude,longitude,Reminder.ID)*/
drop table Event;
 create table Event (
		id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		User varchar(50),
		dueDate varchar(50),
		startTime varchar(50),
		endTime varchar(50),
		location varchar(200),
		ReminderId INT
       )TYPE=innodb;
       
/*Reminder(ID,priority,description,title,type,latitude,longitude)*/
drop table Reminder;
 create table Reminder (
		id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		priority INT,
		description varchar(350),
		title varchar(200),
		type INT,
		latitude varchar(30),
		longitude varchar(30)
       )TYPE=innodb;



