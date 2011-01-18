
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;
select "End creation of table Task" as " ";

-- --------------------------------------------------------
--
-- Struttura della tabella `User`
--

CREATE TABLE IF NOT EXISTS `User` (
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `firstname` varchar(50) NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `sessionKey` varchar(50) DEFAULT NULL,
  `active` int(1) DEFAULT '0',
  `verified` tinyint(1) DEFAULT NULL,
  `verification_code` varchar(32) NOT NULL,
  `trial_login` int(11) NOT NULL DEFAULT '5'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


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