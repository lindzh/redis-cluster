#!/bin/bash
echo "slaveof 127.0.0.1 8000" | redis-cli -h 127.0.0.1 -p 8101
