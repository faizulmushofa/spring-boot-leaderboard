
test-player:
	mvn -Dtest=com.faizul.leaderboard.Player.PlayerServiceImpTest -Dsurefire.useFile=false test

test-redis:
	mvn -Dtest=com.faizul.leaderboard.redis.PlayerCacheServiceTest -Dsurefire.useFile=false test

test-all:
	mvn -Dsurefire.useFile=false test
