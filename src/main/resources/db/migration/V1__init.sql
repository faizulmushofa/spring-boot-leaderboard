CREATE SCHEMA IF NOT EXISTS "public";

CREATE  TABLE "public".game ( 
	id                   uuid  NOT NULL  ,
	name                 varchar(100)  NOT NULL  ,
	created_at           timestamp  NOT NULL  ,
	deleted_at           timestamp    ,
	CONSTRAINT pk_game PRIMARY KEY ( id )
 );

CREATE  TABLE "public".player ( 
	id                   uuid  NOT NULL  ,
	name                 varchar(100)  NOT NULL  ,
	email                varchar(255)  NOT NULL  ,
	created_at           timestamp  NOT NULL  ,
	last_login           timestamp    ,
	profile_image        varchar(500)    ,
	location             varchar(255)  NOT NULL  ,
	game_id              uuid  NOT NULL  ,
	CONSTRAINT pk_tbl PRIMARY KEY ( id )
 );

CREATE  TABLE "public".friends ( 
	id                   uuid  NOT NULL  ,
	friend_fk            uuid  NOT NULL  ,
	player_fk            uuid  NOT NULL  ,
	CONSTRAINT pk_tbl_0 PRIMARY KEY ( id )
 );

CREATE  TABLE "public".leaderboard ( 
	id                   uuid  NOT NULL  ,
	score                integer  NOT NULL  ,
	created_at           timestamp  NOT NULL  ,
	game_id              uuid  NOT NULL  ,
	player_id            integer  NOT NULL  ,
	CONSTRAINT pk_tbl_1 PRIMARY KEY ( id )
 );

ALTER TABLE "public".friends ADD CONSTRAINT fk_friends_player FOREIGN KEY ( player_fk ) REFERENCES "public".player( id );

ALTER TABLE "public".friends ADD CONSTRAINT fk_friend_player FOREIGN KEY ( friend_fk ) REFERENCES "public".player( id );

ALTER TABLE "public".leaderboard ADD CONSTRAINT fk_leaderboard_game FOREIGN KEY ( game_id ) REFERENCES "public".game( id );

ALTER TABLE "public".leaderboard ADD CONSTRAINT fk_leaderboard_player FOREIGN KEY ( player_id ) REFERENCES "public".player( id );

ALTER TABLE "public".player ADD CONSTRAINT fk_player_game FOREIGN KEY ( game_id ) REFERENCES "public".game( id );

