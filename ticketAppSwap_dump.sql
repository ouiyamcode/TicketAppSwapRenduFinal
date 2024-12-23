-- MySQL dump 10.13  Distrib 8.0.40, for Linux (x86_64)
--
-- Host: localhost    Database: TicketAppSwap
-- ------------------------------------------------------
-- Server version	8.0.40-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `poste_info`
--

DROP TABLE IF EXISTS `poste_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `poste_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `configuration` varchar(255) NOT NULL,
  `etat` varchar(50) NOT NULL,
  `utilisateur_affecte` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_utilisateur_affecte` (`utilisateur_affecte`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `poste_info`
--

LOCK TABLES `poste_info` WRITE;
/*!40000 ALTER TABLE `poste_info` DISABLE KEYS */;
INSERT INTO `poste_info` VALUES (1,'Core i5, 8GB RAM, 256GB SSD','EN_FONCTION',1),(2,'Core i7, 16GB RAM, 512GB SSD','EN_MAINTENANCE',2),(3,'Core i5, 8GB RAM, 256GB SSD','EN_MAINTENANCE',3),(4,'Core i7, 16GB RAM, 512GB SSD','EN_FONCTION',4);
/*!40000 ALTER TABLE `poste_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `description` text,
  `impact` smallint DEFAULT NULL,
  `titre` varchar(255) DEFAULT NULL,
  `dateHeureCreation` datetime DEFAULT NULL,
  `dateHeureMiseAJour` datetime DEFAULT NULL,
  `typeDemande` varchar(255) DEFAULT NULL,
  `utilisateur_createur` int DEFAULT NULL,
  `poste_info` int DEFAULT NULL,
  `etatTicket` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_utilisateur_createur` (`utilisateur_createur`),
  KEY `fk_poste_info` (`poste_info`),
  CONSTRAINT `fk_poste_info` FOREIGN KEY (`poste_info`) REFERENCES `poste_info` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_utilisateur_createur` FOREIGN KEY (`utilisateur_createur`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
INSERT INTO `ticket` VALUES (1,NULL,'Impossible de se connecter au réseau de l\'entreprise depuis le poste 101.',0,'Problème de connexion réseau','2024-12-12 12:00:00','2024-12-12 12:00:00','Incident',1,1,'Ouvert'),(2,NULL,'Poste tombé par terre',0,'Poste cassé','2024-12-19 12:00:00','2024-12-19 12:00:00','Incident',2,2,'Ouvert'),(3,NULL,'L\'écran ne s\'allume plus',1,'Ecran noir','2024-12-20 10:00:00','2024-12-20 12:00:00','Incident',3,3,'Ouvert'),(5,NULL,'La connexion réseau se déconnecte fréquemment',0,'Connexion réseau instable','2024-12-21 09:00:00','2024-12-21 11:00:00','Problème réseau',4,4,'EnCours');
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utilisateur` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `mdp` varchar(255) NOT NULL,
  `date_derniere_cnx` datetime DEFAULT NULL,
  `statut_actif` tinyint(1) NOT NULL DEFAULT '1',
  `role` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utilisateur`
--

LOCK TABLES `utilisateur` WRITE;
/*!40000 ALTER TABLE `utilisateur` DISABLE KEYS */;
INSERT INTO `utilisateur` VALUES (1,'Jean Dupont','jean.dupont@example.com','password123','2024-12-23 15:30:00',1,'ADMIN'),(2,'Marie Curry','marie.curry@example.com','securePass456','2024-12-22 10:15:00',0,'Intervenant'),(3,'Albert Einstein','albert.einstein@example.com','relativity123','2024-12-22 11:30:00',1,'ADMIN'),(4,'Isaac Newton','isaac.newton@example.com','gravityRules','2024-12-23 08:45:00',1,'Intervenant');
/*!40000 ALTER TABLE `utilisateur` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-23 17:23:59
