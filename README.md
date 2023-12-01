# My university systems design group project

In this project we had to gather requirements and design a retail store kiosk application which interfaces with a database. Staff would use the system to manage store stock and perform business management operations, and customers would use it for browsing products and placing orders.

This app will not function without a MySQL database with a correct schema to interface with, we were required to configure this on university servers and to develop the app connected to our VPN.

## Project Info
 - Java version: 17
 - Gradle version: 8.2

## Building

This project uses gradle-wrapper, so nothing need be installed.

Just run `./gradlew jar` to output a standalone JAR file which can be run.

## Our Database DDL - An assessment requirement

### Person
```mysql
CREATE TABLE `Person` (
    `PersonId` int NOT NULL AUTO_INCREMENT,
    `forename` varchar(45) NOT NULL,
    `surname` varchar(45) NOT NULL,
    `email` varchar(60) NOT NULL,
    `password` varchar(255) NOT NULL,
    `houseName` varchar(20) NOT NULL,
    `postCode` varchar(45) NOT NULL,
    `paymentId` int DEFAULT NULL,

    PRIMARY KEY (`PersonId`),
    KEY `paymentId_idx` (`paymentId`),
    KEY `houseName_idx` (`houseName`),
    KEY `postCode_idx` (`postCode`),
    KEY `address` (`houseName`,`postCode`),
    
    CONSTRAINT `address` FOREIGN KEY (`houseName`, `postCode`) REFERENCES `Address` (`houseNumber`, `postCode`),
    CONSTRAINT `paymentId` FOREIGN KEY (`paymentId`) REFERENCES `BankDetails` (`paymentId`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB
```

### Address
```mysql
CREATE TABLE `Address` (
    `houseNumber` varchar(20) NOT NULL,
    `streetName` varchar(45) NOT NULL,
    `cityName` varchar(45) NOT NULL,
    `postCode` varchar(45) NOT NULL,
    PRIMARY KEY (`houseNumber`,`postCode`)
) ENGINE=InnoDB
```

### BankDetails
```mysql
CREATE TABLE `BankDetails` (
    `paymentId` int NOT NULL AUTO_INCREMENT,
    `cardName` varchar(45) NOT NULL,
    `cardNumber` text NOT NULL,
    `expiryDate` date NOT NULL,
    `securityCode` text NOT NULL,
    `cardHolderName` varchar(45) NOT NULL,
    PRIMARY KEY (`paymentId`)
) ENGINE=InnoDB
```

### Role
```mysql
CREATE TABLE `Role` (
    `personId` int NOT NULL,
    `role` varchar(15) NOT NULL,
    PRIMARY KEY (`personId`,`role`),
    CONSTRAINT `person` FOREIGN KEY (`personId`) REFERENCES `Person` (`PersonId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
```

### Order
```mysql
CREATE TABLE `Order` (
    `orderId` int NOT NULL AUTO_INCREMENT,
    `personId` int NOT NULL,
    `date` datetime NOT NULL,
    `status` varchar(20) NOT NULL,
    PRIMARY KEY (`orderId`),
    KEY `personId_idx` (`personId`),
    CONSTRAINT `personId` FOREIGN KEY (`personId`) REFERENCES `Person` (`PersonId`)
) ENGINE=InnoDB
```

### OrderLine
```mysql
CREATE TABLE `OrderLine` (
    `orderId` int NOT NULL,
    `productCode` varchar(20) NOT NULL,
    `quantity` int NOT NULL,
    PRIMARY KEY (`orderId`,`productCode`),
    KEY `productId_idx` (`productCode`),
    CONSTRAINT `orderId` FOREIGN KEY (`orderId`) REFERENCES `Order` (`orderId`) ON DELETE CASCADE,
    CONSTRAINT `productCode` FOREIGN KEY (`productCode`) REFERENCES `Product` (`productCode`) ON DELETE CASCADE
) ENGINE=InnoDB
```

### Product
```mysql
CREATE TABLE `Product` (
    `productCode` varchar(20) NOT NULL,
    `name` varchar(45) NOT NULL,
    `stockLevel` int NOT NULL,
    `price` decimal(10,2) NOT NULL,
    PRIMARY KEY (`productCode`)
) ENGINE=InnoDB
```

### BoxedSetContent
```mysql
CREATE TABLE `BoxedSetContent` (
    `boxSetProductCode` varchar(20) NOT NULL,
    `contentProductCode` varchar(20) NOT NULL,
    `quantity` int NOT NULL DEFAULT '0',
    PRIMARY KEY (`boxSetProductCode`,`contentProductCode`),
    KEY `componentProductCode_idx` (`contentProductCode`),
    CONSTRAINT `boxedProductCode` FOREIGN KEY (`boxSetProductCode`) REFERENCES `Product` (`productCode`) ON DELETE CASCADE,
    CONSTRAINT `componentProductCode` FOREIGN KEY (`contentProductCode`) REFERENCES `Product` (`productCode`) ON DELETE CASCADE
) ENGINE=InnoDB
```

### Component
```mysql
CREATE TABLE `Component` (
    `productCode` varchar(20) NOT NULL,
    `brand` varchar(45) NOT NULL,
    `era` varchar(8) NOT NULL,
    `gauge` varchar(45) NOT NULL,
    PRIMARY KEY (`productCode`),
    CONSTRAINT `product` FOREIGN KEY (`productCode`) REFERENCES `Product` (`productCode`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
```

### Locomotive
```mysql
CREATE TABLE `Locomotive` (
    `productCode` varchar(20) NOT NULL,
    `priceBracket` varchar(30) NOT NULL,
    PRIMARY KEY (`productCode`),
    CONSTRAINT `component` FOREIGN KEY (`productCode`) REFERENCES `Component` (`productCode`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
```

### Track
```mysql
CREATE TABLE `Track` (
    `productCode` varchar(20) NOT NULL,
    `curvature` varchar(45) NOT NULL,
    PRIMARY KEY (`productCode`),
    CONSTRAINT `component3` FOREIGN KEY (`productCode`) REFERENCES `Component` (`productCode`) ON DELETE CASCADE
) ENGINE=InnoDB
```

### Controller
```mysql
CREATE TABLE `Controller` (
    `productCode` varchar(20) NOT NULL,
    `controlType` varchar(45) NOT NULL,
    PRIMARY KEY (`productCode`),
    CONSTRAINT `component2` FOREIGN KEY (`productCode`) REFERENCES `Component` (`productCode`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
```
