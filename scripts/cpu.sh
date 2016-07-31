#!/bin/bash
mpstat 1 1 | grep -A 5 "%idle" | tail -n 1 | awk -F " " '{print 100 -  $ 12}'
