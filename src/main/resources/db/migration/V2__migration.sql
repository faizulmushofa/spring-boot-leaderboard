ALTER TABLE "public".friends ADD CONSTRAINT uq_friends_friend_fk UNIQUE (friend_fk);

ALTER TABLE "public".friends ADD CONSTRAINT uq_friends_player_fk UNIQUE (player_fk);

ALTER TABLE "public".player ADD CONSTRAINT uq_player_email UNIQUE (email);

