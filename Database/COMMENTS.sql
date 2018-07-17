SET time_zone = "+00:00";

CREATE TABLE `COMMENTS` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `comment` varchar(30000) NOT NULL,
 `url` varchar(10000) NOT NULL,
 `android_id` varchar(20000) NOT NULL,
 `usr` varchar(11) NOT NULL,
 `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1