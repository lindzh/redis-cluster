#!/bin/bash
echo "slaveof 127.0.0.1 6666" | redis-cli -h 127.0.0.1 -p 7772
