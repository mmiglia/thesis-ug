SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `thesisug`
--
CREATE DATABASE `thesisug` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `thesisug`;

-- --------------------------------------------------------

--
-- Struttura della tabella `Event`
--

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


-- --------------------------------------------------------

--
-- Struttura della tabella `ExternalService`
--

DROP TABLE IF EXISTS `ExternalService`;
CREATE TABLE IF NOT EXISTS `ExternalService` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `Description` varchar(250) DEFAULT NULL,
  `ServiceType` enum('EventService','TaskService') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;


-- --------------------------------------------------------

--
-- Struttura della tabella `ExternalServiceCredentials`
--

DROP TABLE IF EXISTS `ExternalServiceCredentials`;
CREATE TABLE IF NOT EXISTS `ExternalServiceCredentials` (
  `User` varchar(50) DEFAULT NULL,
  `ExternalServiceID` int(11) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Struttura della tabella `Reminder`
--

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


-- --------------------------------------------------------

--
-- Struttura della tabella `Task`
--

DROP TABLE IF EXISTS `Task`;
CREATE TABLE IF NOT EXISTS `Task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `User` varchar(50) DEFAULT NULL,
  `dueDate` varchar(50) DEFAULT NULL,
  `notifyTimeStart` varchar(50) DEFAULT NULL,
  `notifyTimeEnd` varchar(50) DEFAULT NULL,
  `ReminderId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;


-- --------------------------------------------------------

--
-- Struttura della tabella `User`
--

DROP TABLE IF EXISTS `User`;
CREATE TABLE IF NOT EXISTS `User` (
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `sessionKey` varchar(50) DEFAULT NULL,
  `active` int(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;