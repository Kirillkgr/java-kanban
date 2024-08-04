#!/bin/bash

echo "Start test and run"
docker compose stop;
docker system prune -af;
docker compose up;