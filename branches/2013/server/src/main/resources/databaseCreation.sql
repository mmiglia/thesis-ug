SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `thesisug`
--
DROP DATABASE IF EXISTS `thesisug`;
CREATE DATABASE `thesisug` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `thesisug`;

-- --------------------------------------------------------

--
-- Struttura della tabella `Event`
--
select "Starting creation of table Event" as " ";

DROP TABLE IF EXISTS `Event`;
CREATE TABLE IF NOT EXISTS `Event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `User` varchar(50) DEFAULT NULL,
  `dueDate` varchar(50) DEFAULT NULL,
  `startTime` varchar(50) DEFAULT NULL,
  `endTime` varchar(50) DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL,
  `ReminderId` int(11) DEFAULT NULL,
  `Done` int(1) DEFAULT 0,
  `DoneTime` DATETIME DEFAULT NULL,
  `DoneLatitude` varchar(30) DEFAULT NULL,
  `DoneLongitude` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

select "End creation of table Event" as " ";
-- --------------------------------------------------------

--
-- Struttura della tabella `ExternalService`
--
select "Starting creation of table ExternalService" as " ";
DROP TABLE IF EXISTS `ExternalService`;
CREATE TABLE IF NOT EXISTS `ExternalService` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `Description` varchar(250) DEFAULT NULL,
  `ServiceType` enum('EventService','TaskService') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;
select "End creation of table ExternalService" as " ";

-- --------------------------------------------------------

--
-- Struttura della tabella `ExternalServiceCredentials`
--
select "Starting creation of table ExternalServiceCredentials" as " ";
DROP TABLE IF EXISTS `ExternalServiceCredentials`;
CREATE TABLE IF NOT EXISTS `ExternalServiceCredentials` (
  `User` varchar(50) DEFAULT NULL,
  `ExternalServiceID` int(11) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table ExternalServiceCredentials" as " ";

-- --------------------------------------------------------

--
-- Struttura della tabella `Reminder`
--
select "Starting creation of table Reminder" as " ";
DROP TABLE IF EXISTS `Reminder`;
CREATE TABLE IF NOT EXISTS `Reminder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `priority` int(11) DEFAULT NULL,
  `description` varchar(350) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `latitude` varchar(30) DEFAULT NULL,
  `longitude` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;
select "End creation of table Reminder" as " ";

-- --------------------------------------------------------

--
-- Struttura della tabella `Task`
--
select "Starting creation of table Task" as " ";
DROP TABLE IF EXISTS `Task`;
CREATE TABLE IF NOT EXISTS `Task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `User` varchar(50) DEFAULT NULL,
  `dueDate` varchar(50) DEFAULT NULL,
  `notifyTimeStart` varchar(50) DEFAULT NULL,
  `notifyTimeEnd` varchar(50) DEFAULT NULL,
  `ReminderId` int(11) DEFAULT NULL,
  `UserGroup` int(11) DEFAULT 0,
  `Done` int(1) DEFAULT 0,
  `DoneTime` DATETIME DEFAULT NULL,
  `DoneLatitude` varchar(30) DEFAULT NULL,
  `DoneLongitude` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;
select "End creation of table Task" as " ";

-- --------------------------------------------------------
--
-- Struttura della tabella `User`
--
select "Starting creation of table User" as " ";
DROP TABLE IF EXISTS `User`;
CREATE TABLE IF NOT EXISTS `User` (
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `firstname` varchar(50) NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `sessionKey` varchar(50) DEFAULT NULL,
  `active` int(1) DEFAULT '0',
  `verified` tinyint(1) DEFAULT '0',
  `verification_code` varchar(32) NOT NULL,
  `trial_login` int(11) NOT NULL DEFAULT '5',
  `rank` double NOT NULL DEFAULT '0.1' COMMENT 'rank per la proporzione del
voto dell”utente nell”ontologia'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table User" as " ";

-- --------------------------------------------------------

--
-- Struttura della tabella `Group`
--
select "Starting creation of table Group" as " ";
DROP TABLE IF EXISTS `UserGroup`;
CREATE TABLE IF NOT EXISTS `UserGroup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(50) NOT NULL,
  `owner` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

select "End creation of table Group" as " ";
-- --------------------------------------------------------

--
-- Struttura della tabella `Group_Member`
--
select "Starting creation of table Group_Member" as " ";
DROP TABLE IF EXISTS `GroupMember`;
CREATE TABLE IF NOT EXISTS `GroupMember` (
  `UserGroup` int(11) NOT NULL,
  `User` varchar(50)  NOT NULL,
  `TimeStamp` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

select "End creation of table Group_Member" as " ";
-- --------------------------------------------------------

--
-- Struttura della tabella `Group_Request`
--
select "Starting creation of table GroupRequest" as " ";
DROP TABLE IF EXISTS `GroupRequest`;
CREATE TABLE IF NOT EXISTS `GroupRequest` (
  `id` int(15) NOT NULL AUTO_INCREMENT,
  `Sender` varchar(50)  NOT NULL,
  `UserGroup` int(11) NOT NULL,
  `User` varchar(50)  NOT NULL,
  `Message` varchar(150)  NOT NULL,
  `TimeStamp` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

select "End creation of table Group_Request" as " ";
-- ---------------Modified by Alberto Servetti 14/03/2013---------------------------

--
-- Struttura della tabella `Item_foundIn_Loc`
--
select "Starting creation of table Item_foundIn_Loc" as " ";
DROP TABLE IF EXISTS `Item_foundIn_Loc`;
CREATE TABLE IF NOT EXISTS `Item_foundIn_Loc` (
`Item` varchar(30) NOT NULL,
`Location` varchar(30) NOT NULL,
`Username` varchar(30) NOT NULL,
`N_views` int(11) NOT NULL,
`N_votes` int(11) NOT NULL,
`N_votes_neg` int(11) NOT NULL DEFAULT '0',
`Vote` double NOT NULL DEFAULT '0',
`AddedDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`Cancel` int(1) NOT NULL DEFAULT '0',
`CancellationDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
`Promotion` int(1) NOT NULL DEFAULT '0',
`PromotionDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
PRIMARY KEY (`Item`,`Location`) )ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Item_foundIn_Loc" as " ";
-- ---------------------------------------------------------------------------

--
-- Struttura della tabella `Item_voted`
--
select "Starting creation of table Item_voted" as " ";
DROP TABLE IF EXISTS `Item_voted`;
CREATE TABLE IF NOT EXISTS `Item_voted` (
`Item` varchar(30) NOT NULL,
`Location` varchar(30) NOT NULL,
`Username` varchar(30) NOT NULL COMMENT 'nome dell”utente che ha votato',
`Vote` int(1) NOT NULL DEFAULT '1' COMMENT '1-voto, 0-voto cancellato',
`Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`CancellationDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
PRIMARY KEY (`Item`,`Location`,`Username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Item_voted" as " ";
-- ----------------------------------------------------------------------------

--
-- Struttura della tabella `Item_voted_historical`
--
select "Starting creation of table Item_voted" as " ";
DROP TABLE IF EXISTS `Item_voted_historical`;
CREATE TABLE IF NOT EXISTS `Item_voted_historical` (
`Item` varchar(30) NOT NULL,
`Location` varchar(30) NOT NULL,
`Username` varchar(50) NOT NULL,
`Vote` int(1) NOT NULL COMMENT '0:voto cancellato, 1:votato',
`Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`Item`,`Location`,`Username`,`Vote`,`Date`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Item_voted_historical" as " ";
-- ----------------------------------------------------------------------------

--
-- Struttura della tabella `Action_foundIn_Loc`
--
select "Starting creation of table Action_foundIn_Loc" as " ";
DROP TABLE IF EXISTS `Action_foundIn_Loc`;
CREATE TABLE IF NOT EXISTS `Action_foundIn_Loc` (
`Action` varchar(30) NOT NULL,
`Location` varchar(30) NOT NULL,
`Username` varchar(30) NOT NULL, 
`N_views` int(11) NOT NULL,
`N_votes` int(11) NOT NULL, `Vote` double NOT NULL DEFAULT '0',
`AddedDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`Cancel` int(1) NOT NULL DEFAULT '0',
`CancellationDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
`Promotion` int(1) NOT NULL DEFAULT '0',
`PromotionDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
PRIMARY KEY (`Action`,`Location`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Action_foundIn_Loc" as " ";
-- ------------------------------------------------------------------------------

--
-- Struttura della tabella `Action_voted`
--
select "Starting creation of table Action_voted" as " ";
DROP TABLE IF EXISTS `Action_voted`;
CREATE TABLE IF NOT EXISTS `Action_voted` (
`Action` varchar(30) NOT NULL,
`Location` varchar(30) NOT NULL,
`Username` varchar(30) NOT NULL COMMENT 'nome dell”utente che ha votato',
`Vote` int(1) NOT NULL DEFAULT '1' COMMENT '1-voto, 0-voto cancellato',
`VoteDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`CancellationDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
PRIMARY KEY (`Action`,`Location`,`Username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Action_voted" as " ";
-- ------------------------------------------------------------------------------

--
-- Struttura della tabella `Action_voted_historical`
--
select "Starting creation of table Action_voted_historical" as " ";
DROP TABLE IF EXISTS `Action_voted_historical`;
CREATE TABLE IF NOT EXISTS `Action_voted_historical` (
`Action` varchar(30) NOT NULL,
`Location` varchar(30) NOT NULL,
`Username` varchar(50) NOT NULL,
`Vote` int(1) NOT NULL,
`Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`Action`,`Location`,`Username`,`Vote`,`Date`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Action_voted_historical" as " ";
-- ---------------------------------------------------------------------------------

--
-- Struttura della tabella `Location`
--
select "Starting creation of table Location" as " ";
DROP TABLE IF EXISTS `Location`;
CREATE TABLE IF NOT EXISTS `Location` (
`title` varchar(200) NOT NULL,
`location` varchar(30) NOT NULL,
`username` varchar(50) NOT NULL,
PRIMARY KEY (`title`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Location" as " ";
-- ----------------------------------------------------------------------------------

--
-- Struttura della tabella `Voted`
--
select "Starting creation of table Voted" as " ";
DROP TABLE IF EXISTS `Voted`;
CREATE TABLE IF NOT EXISTS `Voted` (
`object` varchar(30) NOT NULL,
`username` varchar(30) NOT NULL,
PRIMARY KEY (`object`,`username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Voted" as " ";
-- -----------------------------------------------------------------------------------

--
-- Struttura della tabella `Place`
--
select "Starting creation of table Place" as " ";
DROP TABLE IF EXISTS `Place`;
CREATE TABLE IF NOT EXISTS `Place` (
`title` varchar(100) NOT NULL,
`lat` varchar(20) NOT NULL,
`lng` varchar(20) NOT NULL,
`streetAddress` varchar(100) NOT NULL,
`streetNumber` varchar(10) NOT NULL,
`cap` varchar(6) NOT NULL,
`city` varchar(100) NOT NULL,
`user` varchar(50) NOT NULL,
`userGroup` int(11) NOT NULL DEFAULT '-1' COMMENT '-1 pubblico, 0 privato',
`addedDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`title`,`lat`,`lng`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Place" as " ";
-- -----------------------------------------------------------------------------------

--
-- Struttura della tabella `PlacePrivate`
--
select "Starting creation of table PlacePrivate" as " ";
DROP TABLE IF EXISTS `PlacePrivate`;
CREATE TABLE IF NOT EXISTS `PlacePrivate` (
`title` varchar(100) NOT NULL,
`lat` varchar(20) NOT NULL,
`lng` varchar(20) NOT NULL,
`streetAddress` varchar(100) NOT NULL,
`streetNumber` varchar(10) NOT NULL,
`cap` varchar(6) NOT NULL,
`city` varchar(100) NOT NULL,
`user` varchar(50) NOT NULL,
`userGroup` int(11) NOT NULL,
`addedDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON
UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`title`,`lat`,`lng`,`user`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table PlacePrivate" as " ";
-- -----------------------------------------------------------------------------------

--
-- Struttura della tabella `PlacePrivate_category`
--
select "Starting creation of table PlacePrivate_category" as " ";
DROP TABLE IF EXISTS `PlacePrivate_category`;
CREATE TABLE IF NOT EXISTS `PlacePrivate_category` (
`title` varchar(100) NOT NULL,
`lat` varchar(20) NOT NULL,
`lng` varchar(20) NOT NULL,
`category` varchar(50) NOT NULL,
`username`varchar(50)NOT NULL,
PRIMARY KEY(`title`,`lat`,`lng`,`category`,`username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table PlacePrivate_category" as " ";
-- -----------------------------------------------------------------------------------

--
-- Struttura della tabella `Place_category`
--
select "Starting creation of table Place_category" as " ";
DROP TABLE IF EXISTS `Place_category`;
CREATE TABLE IF NOT EXISTS `Place_category` (
`title` varchar(100) NOT NULL,
`lat` varchar(20) NOT NULL,
`lng` varchar(20) NOT NULL,
`category` varchar(50) NOT NULL,
`username` varchar(50) NOT NULL COMMENT 'nome dell”utente che ha inserito la categoria',
PRIMARY KEY (`title`,`lat`,`lng`,`category`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Place_category" as " ";
-- ------------------------------------------------------------------------------------

--
-- Struttura della tabella `Place_voted`
--
select "Starting creation of table Place_voted" as " ";
DROP TABLE IF EXISTS `Place_voted`;
CREATE TABLE IF NOT EXISTS `Place_voted` (
`title` varchar(100) NOT NULL,
`lat` varchar(20) NOT NULL,
`lng` varchar(20) NOT NULL,
`category` varchar(50) NOT NULL,
`username` varchar(50) NOT NULL,
`vote` int(11) NOT NULL COMMENT '1 voto positivo, 2 voto negativo',
`date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`title`,`lat`,`lng`,`category`,`username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Place_voted" as " ";
-- ------------------------------------------------------------------------------------

--
-- Struttura della tabella `Place_voted_historical`
--
select "Starting creation of table Place_voted_historical" as " ";
DROP TABLE IF EXISTS `Place_voted_historical`;
CREATE TABLE IF NOT EXISTS `Place_voted_historical` (
`title` varchar(100) NOT NULL,
`lat` varchar(20) NOT NULL,
`lng` varchar(20) NOT NULL,
`category` varchar(50) NOT NULL,
`username` varchar(50) NOT NULL,
`vote` int(11) NOT NULL COMMENT '1 voto positivo, 2 voto negativo',
`date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`title`,`lat`,`lng`,`category`,`username`,`vote`,`date`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table Place_voted_historical" as " ";
-- ------------------------------------------------------------------------------------

--
-- Struttura della tabella `ChachingGoogle`
--

select "Starting creation of table ChachingGoogle" as " ";
DROP TABLE IF EXISTS `ChachingGoogle`;
CREATE TABLE IF NOT EXISTS `CachingGoogle` (
`title` varchar(100) NOT NULL,
`url` varchar(400) NOT NULL,
`content` varchar(100) NOT NULL,
`titleNoFormatting` varchar(100) NOT NULL,
`lat` varchar(20) NOT NULL,
`lng` varchar(20) NOT NULL,
`streetAddress` varchar(100) NOT NULL,
`city` varchar(100) NOT NULL,
`ddUrl` varchar(400) NOT NULL,
`ddUrlToHere` varchar(400) NOT NULL,
`ddUrlFromHere` varchar(400) NOT NULL,
`staticMapUrl` varchar(400) NOT NULL,
`listingType` varchar(400) NOT NULL,
`region` varchar(100) NOT NULL,
`country` varchar(100) NOT NULL,
`insertionDate` date NOT NULL,
`sentence` varchar(30) NOT NULL COMMENT 'query con cui è stato interrogato Google',
`user` varchar(30) NOT NULL COMMENT 'username dell''utente che ha fatto la query',
PRIMARY KEY (`title`,`lat`,`lng`,`sentence`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table ChachingGoogle" as " ";
-- ------------------------------------------------------------------------------------

--
-- Struttura della tabella `ChachingGoogleAddressLines`
--

select "Starting creation of table CachingGoogleAddressLines" as " ";
DROP TABLE IF EXISTS `CachingGoogleAddressLines`;
CREATE TABLE IF NOT EXISTS `CachingGoogleAddressLines` (
`title` varchar(100) NOT NULL,
`lat` varchar(20) NOT NULL,
`lng` varchar(20) NOT NULL,
`addressLine` varchar(100) NOT NULL,
`insertionDate` date NOT NULL,
PRIMARY KEY (`title`,`lat`,`lng`,`addressLine`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table CachingGoogleAddressLines" as " ";
-- ------------------------------------------------------------------------------------

--
-- Struttura della tabella `CachingGooglePhoneNumber`
--

select "Starting creation of table CachingGooglePhoneNumber" as " ";
DROP TABLE IF EXISTS `CachingGooglePhoneNumber`;
CREATE TABLE IF NOT EXISTS `CachingGooglePhoneNumber` (
`title` varchar(100) NOT NULL,
`lat` varchar(20) NOT NULL,
`lng` varchar(20) NOT NULL,
`number` varchar(20) NOT NULL,
`type` varchar(30) NOT NULL,
`insertionDate` date NOT NULL,
PRIMARY KEY (`title`,`lat`,`lng`,`number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
select "End creation of table CachingGooglePhoneNumber" as " ";
-- ------------------------------------------------------------------------------------
