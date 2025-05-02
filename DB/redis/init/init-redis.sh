#!/bin/sh
redis-server &

sleep 2

redis-cli set hello "world"
redis-cli hset user:1 name "admin" email "admin@example.com"

redis-cli MONITOR