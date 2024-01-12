
-- -----------------------------------------------------
-- Table `web_practicafinal`.`genre`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`genre` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`nationality`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`nationality` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`distributor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`distributor` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`director`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`director` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`age_classification`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`age_classification` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `age` SMALLINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`movie` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `web` VARCHAR(255) NOT NULL,
  `original_title` VARCHAR(255) NOT NULL,
  `duration` SMALLINT UNSIGNED NOT NULL,
  `year` SMALLINT UNSIGNED NOT NULL,
  `synopsis` TEXT NOT NULL DEFAULT '',
  `genre_id` INT UNSIGNED NOT NULL,
  `nationality_id` INT UNSIGNED NOT NULL,
  `distributor_id` INT UNSIGNED NOT NULL,
  `director_id` INT UNSIGNED NOT NULL,
  `age_classification_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_movie_genre_idx` (`genre_id` ASC),
  INDEX `fk_movie_nationality1_idx` (`nationality_id` ASC),
  INDEX `fk_movie_distributor1_idx` (`distributor_id` ASC),
  INDEX `fk_movie_director1_idx` (`director_id` ASC),
  INDEX `fk_movie_age_classification1_idx` (`age_classification_id` ASC),
  CONSTRAINT `fk_movie_genre`
    FOREIGN KEY (`genre_id`)
    REFERENCES `web_practicafinal`.`genre` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_movie_nationality1`
    FOREIGN KEY (`nationality_id`)
    REFERENCES `web_practicafinal`.`nationality` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_movie_distributor1`
    FOREIGN KEY (`distributor_id`)
    REFERENCES `web_practicafinal`.`distributor` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_movie_director1`
    FOREIGN KEY (`director_id`)
    REFERENCES `web_practicafinal`.`director` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_movie_age_classification1`
    FOREIGN KEY (`age_classification_id`)
    REFERENCES `web_practicafinal`.`age_classification` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`room`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`room` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `depth` SMALLINT UNSIGNED NOT NULL,
  `seats` SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`actor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`actor` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`session`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`session` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `datetime` DATETIME NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `room_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_sessions_room1_idx` (`room_id` ASC),
  INDEX `fk_sessions_movie1_idx` (`movie_id` ASC),
  CONSTRAINT `fk_sessions_room1`
    FOREIGN KEY (`room_id`)
    REFERENCES `web_practicafinal`.`room` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_sessions_movie1`
    FOREIGN KEY (`movie_id`)
    REFERENCES `web_practicafinal`.`movie` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`role` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`user` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role_id` INT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  INDEX `fk_user_role1_idx` (`role_id` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  CONSTRAINT `fk_user_role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `web_practicafinal`.`role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`payment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`payment` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `reference` VARCHAR(255) NOT NULL,
  `amount` DECIMAL(8,2) UNSIGNED NOT NULL,
  `card_title` VARCHAR(255) NOT NULL,
  `card_number` BIGINT(16) NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_payments_user1_idx` (`user_id` ASC),
  UNIQUE INDEX `reference_UNIQUE` (`reference` ASC),
  CONSTRAINT `fk_payments_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `web_practicafinal`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`ticket`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`ticket` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `depth` SMALLINT UNSIGNED NOT NULL,
  `seat` SMALLINT UNSIGNED NOT NULL,
  `code` VARCHAR(255) NOT NULL,
  `session_id` INT UNSIGNED NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  `payment_id` INT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_ticket_session1_idx` (`session_id` ASC),
  INDEX `fk_ticket_user1_idx` (`user_id` ASC),
  UNIQUE INDEX `code_UNIQUE` (`code` ASC),
  INDEX `fk_ticket_payment1_idx` (`payment_id` ASC),
  CONSTRAINT `fk_ticket_session1`
    FOREIGN KEY (`session_id`)
    REFERENCES `web_practicafinal`.`session` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ticket_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `web_practicafinal`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ticket_payment1`
    FOREIGN KEY (`payment_id`)
    REFERENCES `web_practicafinal`.`payment` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`comment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`comment` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `content` TEXT NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_comment_user1_idx` (`user_id` ASC),
  INDEX `fk_comment_movie1_idx` (`movie_id` ASC),
  CONSTRAINT `fk_comment_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `web_practicafinal`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comment_movie1`
    FOREIGN KEY (`movie_id`)
    REFERENCES `web_practicafinal`.`movie` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`movie_actor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`movie_actor` (
  `movie_id` INT UNSIGNED NOT NULL,
  `actor_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`movie_id`, `actor_id`),
  INDEX `fk_movie_has_actor_actor1_idx` (`actor_id` ASC),
  INDEX `fk_movie_has_actor_movie1_idx` (`movie_id` ASC),
  CONSTRAINT `fk_movie_has_actor_movie1`
    FOREIGN KEY (`movie_id`)
    REFERENCES `web_practicafinal`.`movie` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_movie_has_actor_actor1`
    FOREIGN KEY (`actor_id`)
    REFERENCES `web_practicafinal`.`actor` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`card`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`card` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `card_number` BIGINT(16) UNSIGNED NOT NULL,
  `expiration` DATE NOT NULL,
  `cvv` VARCHAR(255) NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_card_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_card_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `web_practicafinal`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `web_practicafinal`.`favorite`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `web_practicafinal`.`favorite` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `movie_id` INT UNSIGNED NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_favorite_user1_idx` (`user_id` ASC),
  INDEX `fk_favorite_movie1_idx` (`movie_id` ASC),
  CONSTRAINT `fk_favorite_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `web_practicafinal`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_favorite_movie1`
    FOREIGN KEY (`movie_id`)
    REFERENCES `web_practicafinal`.`movie` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;

