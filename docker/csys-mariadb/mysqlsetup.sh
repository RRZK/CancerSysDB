#!/bin/bash
mysql -u root cancersys < /tmp/cancersysload.sql
rm /tmp/cancersysload.sql